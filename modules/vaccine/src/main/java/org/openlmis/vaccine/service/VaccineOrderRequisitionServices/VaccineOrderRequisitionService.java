package org.openlmis.vaccine.service.VaccineOrderRequisitionServices;

import org.joda.time.DateTime;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisitionStatusChange;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.openlmis.vaccine.domain.inventory.StockCard;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineOrderRequisitionRepository;
import org.openlmis.vaccine.repository.VaccineOrderRequisitions.VaccineOrderRequisitionStatusChangeRepository;
import org.openlmis.vaccine.service.Inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.System.out;
import static org.openlmis.vaccine.utils.ListUtil.emptyIfNull;

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
    VaccineInventoryService vaccineInventoryService;

    @Autowired
    ProcessingPeriodRepository periodService;

    @Autowired
    VaccineOrderRequisitionsColumnService columnService;


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
        SimpleDateFormat form = new SimpleDateFormat("dd-MM-YYYY");
        //List<StockCard> stockCards = stockCardService.getStockCards(facilityId,programId);
        Date date = new Date();
        List<ProgramProduct> programProducts = programProductService.getActiveByProgram(programId);
        SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(facilityService.getFacilityById(facilityId), programService.getById(programId));
        List<StockCard> stockCard = vaccineInventoryService.getStockCards(facilityId, programId);
        orderRequisition = new VaccineOrderRequisition();
        orderRequisition.setPeriodId(periodId);
        orderRequisition.setProgramId(programId);
        orderRequisition.setStatus(VaccineOrderStatus.DRAFT);
        orderRequisition.setSupervisoryNodeId(supervisoryNode.getId());
        orderRequisition.setFacilityId(facilityId);
        orderRequisition.setOrderDate(form.format(date));
        orderRequisition.setCreatedBy(userId);
        orderRequisition.setModifiedBy(userId);
        orderRequisition.viewOrderRequisitionLineItems(stockCard, programProducts);
        //orderRequisition.initiateOrderRequisitionLineItem(programProducts);
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
                results.add(reportStatusDTO);
            }

            if(results == null){
                out.print("Got It");
                OrderRequisitionDTO reportStatusDTO = new OrderRequisitionDTO();
                reportStatusDTO.setPeriodName("Period");
                reportStatusDTO.setPeriodId(2L);
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

    public List<OrderRequisitionDTO> getPendingRequest(Long userId, Long facilityId, Long programId) {
        return orderRequisitionRepository.getPendingRequest(userId, facilityId, programId);
    }

    public List<OrderRequisitionDTO> getAllBy(Long programId, Long periodId, Long facilityId) {
        return orderRequisitionRepository.getAllBy(programId, periodId, facilityId);
    }

   public Long updateORStatus(Long orderId){

       return orderRequisitionRepository.updateOFRStatus(orderId);
    }
}
