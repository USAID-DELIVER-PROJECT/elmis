package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;

import org.joda.time.DateTime;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.service.*;
import org.openlmis.stockmanagement.repository.mapper.StockCardMapper;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionStatusChange;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;
import org.openlmis.vaccine.dto.StockRequirementsDTO;
import org.openlmis.vaccine.dto.VaccineOnTimeInFullDTO;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineOrderRequisitionRepository;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineOrderRequisitionStatusChangeRepository;
import org.openlmis.vaccine.service.StockRequirementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;


@Service
public class VaccineOrderRequisitionService {
    @Autowired
    VaccineOrderRequisitionRepository orderRequisitionRepository;

    @Autowired
    ProgramProductService programProductService;

    @Autowired
    VaccineOrderRequisitionStatusChangeRepository statusChangeRepository;

    @Autowired
    SupervisoryNodeService supervisoryNodeService;
    @Autowired
    FacilityService facilityService;
    @Autowired
    ProgramService programService;

    @Autowired
    ProcessingPeriodRepository periodService;

    @Autowired
    VaccineOrderRequisitionsColumnService columnService;

    @Autowired
    FacilityProgramProductService facilityProgramProductService;

    @Autowired
    StockRequirementsService stockRequirementsService;


    @Autowired
    StockCardMapper stockCardMapper;

    @Autowired
    ProductService service;

    @Autowired
    VaccineNotificationService notificationService;

    @Autowired
    ConfigurationSettingService configurationSettingService;

    public static String  getCommaSeparatedIds(List<Long> idList){

        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }


    @Transactional
    public VaccineOrderRequisition initialize(Long periodId, Long programId, Long facilityId, Long userId) {
        VaccineOrderRequisition orderRequisition = orderRequisitionRepository.getByFacilityProgram(periodId, programId, facilityId);
        if (orderRequisition != null) {
            return orderRequisition;
        }

        orderRequisition = createNewOrderRequisition(periodId, programId, facilityId, userId);
        orderRequisitionRepository.Insert(orderRequisition);
        VaccineOrderRequisitionStatusChange change = new VaccineOrderRequisitionStatusChange(orderRequisition, VaccineOrderStatus.DRAFT, userId);
        statusChangeRepository.insert(change);
        return orderRequisition;

    }
    @Transactional
    public VaccineOrderRequisition initializeEmergency(Long periodId, Long programId, Long facilityId, Long userId) {
        VaccineOrderRequisition orderRequisition;
        orderRequisition = createNewOrderRequisition(periodId, programId, facilityId, userId);
        orderRequisition.setEmergency(true);
        orderRequisitionRepository.Insert(orderRequisition);
        VaccineOrderRequisitionStatusChange change = new VaccineOrderRequisitionStatusChange(orderRequisition, VaccineOrderStatus.DRAFT, userId);
        statusChangeRepository.insert(change);
        return orderRequisition;

    }

    private VaccineOrderRequisition createNewOrderRequisition(Long periodId, Long programId, Long facilityId, Long userId) {

        VaccineOrderRequisition orderRequisition;
        SimpleDateFormat form = new SimpleDateFormat("MM-dd-YYYY");

        Facility facility = facilityService.getById(facilityId);

        Date date = new Date();
        SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(facilityService.getFacilityById(facilityId), programService.getById(programId));
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(Calendar.YEAR);

        //List<StockRequirementsDTO> stockRequirements = stockRequirementsService.getStockRequirements(facilityId, programId);
        List<StockRequirementsDTO>stockRequirements2 = stockRequirementsService.getAllForOrderRequisition(programId,facilityId,year);
        orderRequisition = new VaccineOrderRequisition();
        orderRequisition.setPeriodId(periodId);
        orderRequisition.setProgramId(programId);
        orderRequisition.setStatus(VaccineOrderStatus.DRAFT);
        orderRequisition.setSupervisoryNodeId(supervisoryNode.getId());
        orderRequisition.setFacilityId(facilityId);
        orderRequisition.setOrderDate(form.format(date));
        orderRequisition.setCreatedBy(userId);
        orderRequisition.setModifiedBy(userId);
        if(facility !=null)
        orderRequisition.setFacility(facility);
        System.out.println(stockRequirements2);
        if(stockRequirements2 != null)
        orderRequisition.initiateOrder(stockRequirements2,service,stockCardMapper);

        return orderRequisition;
    }

    @Transactional
    public void save(VaccineOrderRequisition orderRequisition) {
        orderRequisitionRepository.Update(orderRequisition);
    }

    @Transactional
    public void submit(VaccineOrderRequisition orderRequisition, Long userId) {
        orderRequisition.setCreatedBy(userId);
        orderRequisition.setModifiedBy(userId);
        orderRequisition.setStatus(VaccineOrderStatus.SUBMITTED);
        orderRequisitionRepository.Update(orderRequisition);
        VaccineOrderRequisitionStatusChange change = new VaccineOrderRequisitionStatusChange(orderRequisition, VaccineOrderStatus.SUBMITTED, userId);
        statusChangeRepository.insert(change);
        notificationService.sendOrderRequisitionStatusChangeNotification(orderRequisition,userId);
    }

    public VaccineOrderRequisition getLastReport(Long facilityId, Long programId) {
        return orderRequisitionRepository.getLastOrder(facilityId, programId);
    }

    public VaccineOrderRequisition getAllDetailsById(Long id) {
        VaccineOrderRequisition requisition;
        requisition = orderRequisitionRepository.getAllDetailsById(id);

        return requisition;
    }


    public List<OrderRequisitionDTO> getPeriodsFor(Long facilityId, Long programId, Date endDate) {
        Date startDate = programService.getProgramStartDate(facilityId, programId);

        // find out which schedule this facility is in?
        Long scheduleId = orderRequisitionRepository.getScheduleFor(facilityId, programId);
        VaccineOrderRequisition lastRequest = orderRequisitionRepository.getLastOrder(facilityId, programId);

        if (lastRequest != null) {
            lastRequest.setPeriod(periodService.getById(lastRequest.getPeriodId()));
            startDate = lastRequest.getPeriod().getStartDate();
        }

        Long lastPeriodId = lastRequest == null ? null : lastRequest.getPeriodId();
        List<OrderRequisitionDTO> results = new ArrayList<>();
        // find all periods that are after this period, and before today.

        List<ProcessingPeriod> periods = periodService.getAllPeriodsForDateRange(scheduleId, startDate, endDate);

        if (lastRequest != null && lastRequest.getStatus().equals(VaccineOrderStatus.DRAFT)) {
            OrderRequisitionDTO reportStatusDTO = new OrderRequisitionDTO();
            reportStatusDTO.setPeriodName(lastRequest.getPeriod().getName());
            reportStatusDTO.setPeriodId(lastRequest.getPeriod().getId());
            reportStatusDTO.setStatus(lastRequest.getStatus().toString());
            reportStatusDTO.setProgramId(programId);
            reportStatusDTO.setFacilityId(facilityId);
            reportStatusDTO.setId(lastRequest.getId());
            reportStatusDTO.setEmergency(lastRequest.isEmergency());

            results.add(reportStatusDTO);
        }


        for (ProcessingPeriod period : emptyIfNull(periods)) {

            if (lastRequest == null || !lastRequest.getPeriodId().equals(period.getId())) {
                OrderRequisitionDTO reportStatusDTO = new OrderRequisitionDTO();
                reportStatusDTO.setPeriodName(period.getName());
                reportStatusDTO.setPeriodId(period.getId());
                reportStatusDTO.setStatus(VaccineOrderStatus.DRAFT.toString());
                reportStatusDTO.setProgramId(programId);
                reportStatusDTO.setFacilityId(facilityId);
                reportStatusDTO.setEmergency(false);
                results.add(reportStatusDTO);
            }

        }
        return results;
    }

    public VaccineOrderRequisition getById(Long id) {
        VaccineOrderRequisition report = orderRequisitionRepository.getAllDetailsById(id);
        DateTime periodStartDate = new DateTime(report.getPeriod().getStartDate());
        return report;
    }

    public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId) {
        return orderRequisitionRepository.getReportIdForFacilityAndPeriod(facilityId, periodId);
    }

    public List<OrderRequisitionDTO> getReportedPeriodsFor(Long facilityId, Long programId) {
        return orderRequisitionRepository.getReportedPeriodsForFacility(facilityId, programId);
    }

    public List<OrderRequisitionDTO> getPendingRequest(Long userId, Long facilityId) {

        List<Program> vaccineProgram = programService.getAllIvdPrograms();
        if (vaccineProgram != null) {
            Long programId = vaccineProgram.get(0).getId();
            return orderRequisitionRepository.getPendingRequest(userId, facilityId, programId);
        } else {
            return null;
        }
    }

    public List<OrderRequisitionDTO> getAllBy(Long programId, Long periodId, Long facilityId) {
        return orderRequisitionRepository.getAllBy(programId, periodId, facilityId);
    }

   public Long updateORStatus(Long orderId){

       return orderRequisitionRepository.updateOFRStatus(orderId);
    }

    public List<OrderRequisitionDTO>getAllSearchBy(Long facilityId,String dateRangeStart,String dateRangeEnd,Long programId){
        return orderRequisitionRepository.getAllSearchBy(facilityId,dateRangeStart,dateRangeEnd,programId);
    }

    public List<OrderRequisitionStockCardDTO> getStockCards(Long facilityId, Long programId) {
        return orderRequisitionRepository.getStockCards(facilityId, programId);
    }

    public List<OrderRequisitionStockCardDTO>getAllByFacility(Long facilityId,Long programId){
        return orderRequisitionRepository.getAllByFacility(facilityId,programId);
    }

    public List<OrderRequisitionDTO>getSupervisoryNodeByFacility(Long facilityId){
        return orderRequisitionRepository.getSupervisoryNodeByFacility(facilityId);
    }
    public List<OrderRequisitionDTO>getConsolidatedList(Long program,List<Long> facilityIds){
        return orderRequisitionRepository.getConsolidatedList(program,getCommaSeparatedIds(facilityIds));
    }

    public Long verifyVaccineOrderRequisition(Long orderId){
        return orderRequisitionRepository.verifyVaccineOrderRequisition(orderId);
    }

    public Integer getTotalPendingRequest(Long userId, Long facilityId) {
        Integer total = 0;
        List<Program> vaccineProgram = programService.getAllIvdPrograms();
        if (vaccineProgram != null) {
            Long programId = vaccineProgram.get(0).getId();
            total = orderRequisitionRepository.getTotalPendingRequest(userId, facilityId, programId);
        }
        return total;
    }

    public List<VaccineOnTimeInFullDTO>getOnTimeInFullData(Long facilityId, Long periodId, Long orderId){

        List<VaccineOnTimeInFullDTO> onTimeInFullDTOList = orderRequisitionRepository.getOnTimeInFullData(facilityId,periodId,orderId);
         List<VaccineOnTimeInFullDTO> arr = new ArrayList<>();
         VaccineOnTimeInFullDTO dto;
         for(VaccineOnTimeInFullDTO fullDTO : onTimeInFullDTOList) {
             dto = new VaccineOnTimeInFullDTO();
             Product prod = service.getById(fullDTO.getProductId());
             if (prod != null) {
                 dto.setProduct(prod);
                 ProgramProduct programProduct = programProductService.getByProgramAndProductId(programService.getAllIvdPrograms().get(0).getId(), prod.getId());
                 dto.setProductCategory(programProduct.getProductCategory());
                 dto.setProductName(fullDTO.getProductName());
                 dto.setQuantityRequested(fullDTO.getQuantityRequested());
                 dto.setQuantityReceived(fullDTO.getQuantityReceived());
                 dto.setRequestedDate(fullDTO.getRequestedDate());
                 dto.setReceivedDate(fullDTO.getReceivedDate());
                 dto.setGap(calculateGap(fullDTO));
                 dto.setOnFull(calculateOnFUll(fullDTO));
                 arr.add(dto);
             }

        }
        return arr;
    }

    private String calculateOnFUll(VaccineOnTimeInFullDTO fullDTO) {

        getTenPercentLess(fullDTO.getQuantityRequested());
        Double full = fullDTO.getQuantityRequested() - getTenPercentLess(fullDTO.getQuantityRequested());
        Double full2 = fullDTO.getQuantityRequested() + getTenPercentLess(fullDTO.getQuantityRequested());

        if(full <= fullDTO.getQuantityReceived() && fullDTO.getQuantityReceived() <= full2){
             return "Yes";
        }else {
             return  "No";
        }

    }

    private double getTenPercentLess(Integer quantityRequested) {
        return ( configurationSettingService.getConfigurationIntValue("ON_TIME_IN_FULL_CONF_NUMBER") * quantityRequested) / 100L;
    }

    private Integer calculateGap(VaccineOnTimeInFullDTO fullDTO) {
        return  fullDTO.getQuantityReceived() - fullDTO.getQuantityRequested();

    }

    public List<OrderRequisitionDTO>getSearchedDataForOnTimeReportingBy(Long facilityId,String dateRangeStart,String dateRangeEnd){

        Program  p =  programService.getAllIvdPrograms().get(0);
        return orderRequisitionRepository.getSearchedDataForOnTimeReportingBy(facilityId, dateRangeStart, dateRangeEnd, p.getId());
    }
}
