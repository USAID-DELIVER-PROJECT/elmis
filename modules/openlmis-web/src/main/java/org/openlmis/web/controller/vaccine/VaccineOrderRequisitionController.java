package org.openlmis.web.controller.vaccine;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderStatus;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionLineItemService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionService;
import org.openlmis.vaccine.service.VaccineOrderRequisitionServices.VaccineOrderRequisitionsColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
@RequestMapping(value = "/vaccine/orderRequisition/")
public class VaccineOrderRequisitionController extends BaseController {
    public static final String VaccineOrderRequisition = "orderRequisition";
    public static final String OrderRequisitionColumns = "columns";
    private static final String PROGRAM_PRODUCT_LIST = "programProductList";

    @Autowired
    VaccineOrderRequisitionService service;

    @Autowired
    FacilityService facilityService;
    @Autowired
    VaccineOrderRequisitionLineItemService lineItemService;
    private static final String PRINT_OR = "test";
    @Autowired
    TemplateService templateService;
    @Autowired
    VaccineOrderRequisitionsColumnService columnService;
    @Autowired
    ProgramService programService;
    @Autowired
    private ProgramProductService programProductService;

    @Autowired
    private JasperReportsViewFactory jasperReportsViewFactory;
    @Autowired
    UserService userService;


    @RequestMapping(value = "periods/{facilityId}/{programId}", method = RequestMethod.GET)
   //TODO// @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
        return response("periods", service.getPeriodsFor(facilityId, programId, new Date()));
    }

    @RequestMapping(value = "view-periods/{facilityId}/{programId}", method = RequestMethod.GET)
   //TODO// @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getViewPeriods(@PathVariable Long facilityId, @PathVariable Long programId, HttpServletRequest request){
        return response("periods", service.getReportedPeriodsFor(facilityId, programId));
    }



    @RequestMapping(value = "initialize/{periodId}/{programId}/{facilityId}")
   //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> initialize(
            @PathVariable Long periodId,
            @PathVariable Long programId,
            @PathVariable Long facilityId,
            HttpServletRequest request
    ){
        return response("report", service.initialize(periodId, programId, facilityId, loggedInUserId(request)));
    }

    @RequestMapping(value = "initializeEmergency/{periodId}/{programId}/{facilityId}")
   //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION)")
    public ResponseEntity<OpenLmisResponse> initializeEmergency(
            @PathVariable Long periodId,
            @PathVariable Long programId,
            @PathVariable Long facilityId,
            HttpServletRequest request
    ){
        return response("report", service.initializeEmergency(periodId, programId, facilityId, loggedInUserId(request)));
    }

    @RequestMapping(value = "submit")
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> submit(@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request){
        service.submit(orderRequisition, loggedInUserId(request));
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "save")
   //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request){
        service.save(orderRequisition);
        return response("report", orderRequisition);
    }

    @RequestMapping(value = "lastReport/{facilityId}/{programId}", method = RequestMethod.GET)
   //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse>
    getLastReport(@PathVariable  Long facilityId,@PathVariable Long programId,HttpServletRequest request){

        return response("lastReport", service.getLastReport(facilityId, programId));
    }

    @RequestMapping(value = "get/{id}.json", method = RequestMethod.GET)
   //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getReport(@PathVariable Long id, HttpServletRequest request){
        return response("report", service.getAllDetailsById(id));
    }

    @RequestMapping(value = "userHomeFacility.json", method = RequestMethod.GET)
        public ResponseEntity<OpenLmisResponse> getUserHomeFacilities(HttpServletRequest request){
            return  response("homeFacility", facilityService.getHomeFacility(loggedInUserId(request)));
        }


    @RequestMapping(value = "orderRequisitionTest/{id}/print", method = RequestMethod.GET, headers = ACCEPT_PDF)
    @PostAuthorize("@vaccineOrderRequisitionPermissionService.hasPermission(principal, returnObject.model.get(\"orderRequisition\"), 'VIEW_REQUISITION')")
    public ModelAndView printOrders(@PathVariable Long id)  throws JRException, IOException, ClassNotFoundException {

        ModelAndView modelAndView = new ModelAndView("vaccineOrderRequisition");

        org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition requisition = service.getAllDetailsById(id);

        modelAndView.addObject(VaccineOrderRequisition, requisition);
        modelAndView.addObject(OrderRequisitionColumns,columnService.getAllColumns());

        return modelAndView;
    }


    @RequestMapping(value = "orderRequisition/{id}/print", method = GET, headers = ACCEPT_PDF)
    public ModelAndView print(@PathVariable Long id) throws JRException, IOException, ClassNotFoundException {
        Template podPrintTemplate = templateService.getByName(PRINT_OR);
        JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(podPrintTemplate);
        Map<String, Object> map = new HashMap<>();
        map.put("format", "pdf");

        Locale currentLocale = messageService.getCurrentLocale();
        map.put(JRParameter.REPORT_LOCALE, currentLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
        map.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);

        Resource reportResource = new ClassPathResource("reports");
        Resource imgResource = new ClassPathResource("images");

        String separator = System.getProperty("file.separator");
        map.put("subreport_dir", reportResource.getFile().getAbsolutePath() + separator);
       // map.put("image_dir", imgResource.getFile().getAbsolutePath() + separator);
        map.put("order_id", id.intValue());
        return new ModelAndView(jasperView, map);
    }


    @RequestMapping(value = "getPendingRequest/{facilityId}/{programId}", method = RequestMethod.GET,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getPendingRequest(@PathVariable  Long facilityId,@PathVariable Long programId,HttpServletRequest request){

        return response("pendingRequest", service.getPendingRequest(loggedInUserId(request), facilityId, programId));
    }

    @RequestMapping(value = "getAllBy/{programId}/{periodId}/{facilityId}", method = RequestMethod.GET,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getAllBy(@PathVariable Long programId ,@PathVariable Long periodId,@PathVariable  Long facilityId,HttpServletRequest request){
        return response("requisitionList", service.getAllBy(programId, periodId, facilityId));
    }


    @RequestMapping(value = "updateOrderRequest/{orderId}", method = RequestMethod.PUT,headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_ORDER_REQUISITION')")
    public ResponseEntity<OpenLmisResponse>
    updateORStatus(@PathVariable  Long orderId,HttpServletRequest request){
    try{
        service.updateORStatus(orderId);
        return success("Saved Successifully");

    } catch (DataException e) {
        return error(e, HttpStatus.BAD_REQUEST);
    }

    }

    @RequestMapping(value = "updateOrderRequisition/{orderId}", method = RequestMethod.PUT,headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> update(@PathVariable  Long orderId,@RequestBody org.openlmis.vaccine.domain.VaccineOrderRequisition.VaccineOrderRequisition orderRequisition, HttpServletRequest request){
        orderRequisition.setId(orderId);
        orderRequisition.setStatus(VaccineOrderStatus.ISSUED);
        service.save(orderRequisition);
        return response("report", orderRequisition);
    }



/*

    @RequestMapping(value = "/orderRequisitions", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REQUISITION')")
    public ResponseEntity<OpenLmisResponse> getRequisitionsForView(RequisitionSearchCriteria criteria, HttpServletRequest request) {
        criteria.setUserId(loggedInUserId(request));
        return response("Order_list", prepareForView(requisitionService.get(criteria)));
    }
*/


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


}
