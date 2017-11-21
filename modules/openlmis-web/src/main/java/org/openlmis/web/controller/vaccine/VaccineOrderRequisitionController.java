package org.openlmis.web.controller.vaccine;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.*;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.util.Constants;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.openlmis.vaccine.domain.inventory.VaccineDistribution;
import org.openlmis.vaccine.dto.OrderRequisitionDTO;
import org.openlmis.vaccine.dto.OrderRequisitionStockCardDTO;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineNotificationService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionLineItemService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionsColumnService;
import org.openlmis.vaccine.service.inventory.VaccineInventoryDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.openlmis.core.web.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping(value = "/vaccine/orderRequisition/")
@Api("Vaccine Order Requisition Rest APIs")
public class VaccineOrderRequisitionController extends BaseController {

    private static final String PROGRAM_PRODUCT_LIST = "programProductList";
    private static final String PRINT_ORDER_REQUISITION = "Vaccine Order Requisition";
    private static final String PRINT_ISSUE_STOCK = "vims_distribution";
    private static final String PRINT_CONSOLIDATED = "Print_Consolidated_list_report";
    private static final String ORDER_REQUISITION_SEARCH = "search";
    private static final String PENDING_CONSIGNMENT_FOR_LOWER_LEVEL = "pendingToReceiveLowerLevel";
    private static final String PENDING_CONSIGNMENT = "pendingToReceive";

    @Autowired
    VaccineOrderRequisitionService service;

    @Autowired
    FacilityService facilityService;
    @Autowired
    VaccineOrderRequisitionLineItemService lineItemService;
    @Autowired
    TemplateService templateService;
    @Autowired
    VaccineOrderRequisitionsColumnService columnService;
    @Autowired
    ProgramService programService;
    @Autowired
    UserService userService;
    @Autowired
    ConfigurationSettingService settingService;
    @Autowired
    SupervisoryNodeService supervisoryNodeService;
    @Autowired
    VaccineInventoryDistributionService inventoryDistributionService;
    @Autowired
    VaccineInventoryDistributionService distributionService;
    @Autowired
    private ProgramProductService programProductService;
    @Autowired
    private JasperReportsViewFactory jasperReportsViewFactory;
    @Autowired
    private VaccineNotificationService notificationService;

    public static String getCommaSeparatedIds(List<Long> idList) {
        return idList == null ? "{}" : idList.toString().replace("[", "{").replace("]", "}");
    }

    @RequestMapping(value = {"periods/{facilityId}/{programId}", "/rest-api/orderRequisition/periods/{facilityId}/{programId}"}, method = RequestMethod.GET)
    @ApiOperation(position = 1, value = "Get Open Vaccine Order Requisition Periods for Facility")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION, VIEW_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request) {
        return response("periods", service.getPeriodsFor(facilityId, programId, new Date()));
    }

    @RequestMapping(value = {"view-periods/{facilityId}/{programId}", "/rest-api/orderRequisition/view-periods/{facilityId}/{programId}"}, method = RequestMethod.GET)
    @ApiOperation(position = 2, value = "Get Periods for Which facility has Order Requisition Submissions")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION, VIEW_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request) {
        return response("periods", service.getReportedPeriodsFor(facilityId, programId));
    }

    @RequestMapping(value = "initialize/{periodId}/{programId}/{facilityId}")
    @ApiOperation(position = 3, value = "Initiate and Order Requisition form")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION, VIEW_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> initialize(
            @PathVariable Long periodId,
            @PathVariable Long programId,
            @PathVariable Long facilityId,
            HttpServletRequest request
    ) {
        return response("report", service.initialize(periodId, programId, facilityId, loggedInUserId(request)));
    }

    @RequestMapping(value = "initializeEmergency/{periodId}/{programId}/{facilityId}")
    @ApiOperation(position = 4, value = "Initiate Emergence Order Requisition form")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> initializeEmergency(
            @PathVariable Long periodId,
            @PathVariable Long programId,
            @PathVariable Long facilityId,
            HttpServletRequest request
    ) {
        return response("report", service.initializeEmergency(periodId, programId, facilityId, loggedInUserId(request)));
    }

    @RequestMapping(value = "submit")
    @ApiOperation(position = 5, value = "Submit Order Requisition form")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> submit(@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request) {
        service.submit(orderRequisition, loggedInUserId(request));
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "save")
    @ApiOperation(position = 5, value = "Save Requisition form")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request) {
        service.save(orderRequisition);
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "lastReport/{facilityId}/{programId}", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse>
    getLastReport(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request) {

        return response("lastReport", service.getLastReport(facilityId, programId));
    }

    @RequestMapping(value = "get/{id}.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getReport(@PathVariable Long id, HttpServletRequest request) {
        return response("report", service.getAllDetailsById(id));
    }

    @RequestMapping(value = "userHomeFacility.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getUserHomeFacilities(HttpServletRequest request) {
        return response("homeFacility", facilityService.getHomeFacility(loggedInUserId(request)));
    }

    @RequestMapping(value = "getPendingRequest/{facilityId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getPendingRequest(@PathVariable Long facilityId, HttpServletRequest request) {

        ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("pendingRequest", service.getPendingRequest(loggedInUserId(request), facilityId));
        response.getBody().addData(PENDING_CONSIGNMENT_FOR_LOWER_LEVEL, inventoryDistributionService.getPendingReceivedAlert(facilityId));
        response.getBody().addData(PENDING_CONSIGNMENT, inventoryDistributionService.getPendingNotificationForLowerLevel(facilityId));
        return response;
    }

    @RequestMapping(value = "getAllBy/{programId}/{periodId}/{facilityId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAllBy(@PathVariable Long programId, @PathVariable Long periodId, @PathVariable Long facilityId, HttpServletRequest request) {
        return response("requisitionList", service.getAllBy(programId, periodId, facilityId));
    }

    @RequestMapping(value = "updateOrderRequest/{orderId}", method = RequestMethod.PUT, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>
    updateORStatus(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            service.updateORStatus(orderId);
            return success("Saved Successifully");

        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "updateOrderRequisition/{orderId}", method = RequestMethod.PUT, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> update(@PathVariable Long orderId, @RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request) {
        orderRequisition.setId(orderId);
        orderRequisition.setStatus(VaccineOrderStatus.ISSUED);
        service.save(orderRequisition);
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "programs.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration() {
        return response("programs", programService.getAllIvdPrograms());
    }

    @RequestMapping(value = "loggedInUserDetails.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getLoggedInUserProfiles(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        User user = userService.getById(userId);
        return response("userDetails", user);
    }

    @RequestMapping(value = "order-requisition/programs.json", method = RequestMethod.GET)
    public ResponseEntity<OpenLmisResponse> getProgramFormHomeFacility(HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        User user = userService.getById(userId);
        return response("programs", programService.getProgramsSupportedByUserHomeFacilityWithRights(user.getFacilityId(), userId, "CREATE_REQUISITION", "AUTHORIZE_REQUISITION"));
    }

    @RequestMapping(value = "/{programId}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = programProductService.getByProgram(new Program(programId));
        return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }

    @RequestMapping(value = "{id}/print", method = GET, headers = ACCEPT_JSON)
    public ModelAndView printOrder(@PathVariable Long id) throws JRException, IOException, ClassNotFoundException {
        Template orPrintTemplate = templateService.getByName(PRINT_ORDER_REQUISITION);

        JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(orPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");

        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        Resource reportResource = new ClassPathResource("report");
        Resource imgResource = new ClassPathResource("images");
        ConfigurationSetting configuration = settingService.getByKey(Constants.OPERATOR_NAME);
        map.put(Constants.OPERATOR_NAME, configuration.getValue());

        String separator = System.getProperty("file.separator");
        map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("ORDER_ID", id.intValue());

        return new ModelAndView(jasperView, map);
    }

    @RequestMapping(value = "issue/print/{id}", method = GET, headers = ACCEPT_JSON)
    public ModelAndView printIssueStock(@PathVariable Long id) throws JRException, IOException, ClassNotFoundException {
        Template orPrintTemplate = templateService.getByName(PRINT_ISSUE_STOCK);
        JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(orPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");

        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        Resource reportResource = new ClassPathResource("report");
        Resource imgResource = new ClassPathResource("images");
        ConfigurationSetting configuration = settingService.getByKey(Constants.OPERATOR_NAME);

        map.put(Constants.OPERATOR_NAME, configuration.getValue());

        configuration=  settingService.getByKey(Constants.REPORT_PROGRAM_TITLE);
        map.put(Constants.REPORT_PROGRAM_TITLE,configuration.getValue());

        String separator = System.getProperty("file.separator");
        map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("ISSUE_ID", id.intValue());
        return new ModelAndView(jasperView, map);
    }

    @RequestMapping(value = "search", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> searchUser(@RequestParam(value = "facilityId", required = false) Long facilityId,
                                                       @RequestParam(value = "dateRangeStart", required = false) String dateRangeStart,
                                                       @RequestParam(value = "dateRangeEnd", required = false) String dateRangeEnd,
                                                       @RequestParam(value = "programId", required = false) Long programId,

                                                       HttpServletRequest request
    ) {
        return response(ORDER_REQUISITION_SEARCH, service.getAllSearchBy(facilityId, dateRangeStart, dateRangeEnd, programId));

    }

    @RequestMapping(value = "facilities/{facilityId}/programs/{programId}/stockCards", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getStockCards(@PathVariable Long facilityId,
                                        @PathVariable Long programId) {

        List<OrderRequisitionStockCardDTO> stockCards = service.getStockCards(facilityId, programId);
        return OpenLmisResponse.response("stockCards", stockCards);
    }

    @RequestMapping(value = "supervisoryNodeByFacilityAndRequisition/{facilityId}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getSupervisoryNodeByFacilityAndRequisition(@PathVariable Long facilityId) {

        List<OrderRequisitionDTO> requisitionDTOs = service.getSupervisoryNodeByFacility(facilityId);
        SupervisoryNode supervisoryNode = supervisoryNodeService.getParent(requisitionDTOs.get(0).getId());
        return OpenLmisResponse.response("supervisoryNodes", supervisoryNode);
    }

    @RequestMapping(value = "getConsolidatedOrderList/{program}/{facilityId}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity getConsolidatedOrderList(
            @PathVariable Long program,
            @PathVariable List<Long> facilityId) {

        return OpenLmisResponse.response("consolidatedOrders", service.getConsolidatedList(program,facilityId));
    }

    @RequestMapping(value = "consolidate/print/{facilityId}", method = GET, headers = ACCEPT_JSON)
    public ModelAndView printConsolidatedList(@PathVariable  List<Long> facilityId) throws JRException, IOException, ClassNotFoundException {
        Template orPrintTemplate = templateService.getByName(PRINT_CONSOLIDATED);
        JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(orPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");
        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        Resource reportResource = new ClassPathResource("report");
        Resource imgResource = new ClassPathResource("images");
        ConfigurationSetting configuration = settingService.getByKey(Constants.OPERATOR_NAME);
        map.put(Constants.OPERATOR_NAME, configuration.getValue());

        String separator = System.getProperty("file.separator");
        map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("DISTRIBUTION_ID", getCommaSeparatedIds(facilityId));

        return new ModelAndView(jasperView, map);
    }


    @RequestMapping(value = "facilities/{id}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getFacility(@PathVariable(value = "id") Long id) {
        return response("facility", facilityService.getById(id));
    }


    @RequestMapping(value = "view-order-requisition", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> listForViewingOrderRequisition(HttpServletRequest request) {
        return response("facilities", facilityService.getHomeFacility(loggedInUserId(request)));
    }

    @RequestMapping(value = "updateVerify/{orderId}", method = RequestMethod.PUT, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> verifyOrderRequisition(@PathVariable(value = "orderId") Long orderId) {

        try {
            service.verifyVaccineOrderRequisition(orderId);
            return success("Saved Successifully");

        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "getTotalPendingRequest/{facilityId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getTotalPendingRequest(@PathVariable Long facilityId, HttpServletRequest request) {

        return response("totalPendingRequest", service.getTotalPendingRequest(loggedInUserId(request), facilityId));
    }

    @RequestMapping(value = "sendNotification/{distributionId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> sendNotification(@PathVariable Long distributionId, HttpServletRequest request) {
        VaccineDistribution distribution = null;
        if (!(distributionId == null)) {
            distribution = distributionService.getDistributionById(distributionId);
            notificationService.sendIssuingEmail(distribution);
        }
        return response("message", distribution);
    }



    @RequestMapping(value = "getOnTimeInFull", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getVaccineOnTimeInFull(
            @RequestParam(value = "facilityId", required = false) Long facilityId,
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "period", required = false) Long periodId, HttpServletRequest request
            ) {

        return response("OnTimeInFull", service.getOnTimeInFullData(facilityId,periodId,orderId));
    }


    @RequestMapping(value = "searchOnTimeReporting", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> searchOrderRequisitionForOIF(@RequestParam(value = "facility", required = false) Long facilityId,
                                                       @RequestParam(value = "periodStartDate", required = false) String dateRangeStart,
                                                       @RequestParam(value = "periodEndDate", required = false) String dateRangeEnd, HttpServletRequest request
    ) {
        return response(ORDER_REQUISITION_SEARCH, service.getSearchedDataForOnTimeReportingBy(facilityId, dateRangeStart, dateRangeEnd));

    }


    @RequestMapping(value = "receiveNotification", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReceiveNotification(HttpServletRequest request
    ) {
        Facility facility = facilityService.getHomeFacility(loggedInUserId(request));
        return response("receiveNotification", inventoryDistributionService.getReceiveNotification(facility.getId()));
    }

 @RequestMapping(value = "receiveDistributionAlert", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getReceiveDistributionAlert(HttpServletRequest request
    ) {
        Facility facility = facilityService.getHomeFacility(loggedInUserId(request));
        return response("receiveNotification", inventoryDistributionService.getReceiveDistributionAlert(facility.getId()));
    }
@RequestMapping(value = "getMinimumStock", method = RequestMethod.GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getMinimumStockNotification(HttpServletRequest request
    ) {
        Facility facility = facilityService.getHomeFacility(loggedInUserId(request));
        return response("minimumStock", inventoryDistributionService.getMinimumStockNotification(loggedInUserId(request), facility.getId()));
    }

}
