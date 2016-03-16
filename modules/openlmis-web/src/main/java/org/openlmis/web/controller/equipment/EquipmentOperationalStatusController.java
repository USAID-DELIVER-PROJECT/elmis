package org.openlmis.web.controller.equipment;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.equipment.domain.EquipmentOperationalStatus;
import org.openlmis.equipment.service.EquipmentOperationalStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.error;
import static org.openlmis.core.web.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value="/operational-status/")
public class EquipmentOperationalStatusController extends BaseController {
    public static final String OPERATIONAL_STATUS = "operationalStatus";
    public static final String OPERATIONAL_STATUSES = "operationalStatuses";


    @Autowired
    EquipmentOperationalStatusService service;

    @RequestMapping(value="insert.json",method=POST, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> save(@RequestBody EquipmentOperationalStatus status, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        status.setModifiedBy(loggedInUserId(request));
        try {
            service.save(status);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Operational '%s' has been successfully saved", status.getName()));
        successResponse.getBody().addData(OPERATIONAL_STATUS, status);
        return successResponse;
    }

    @RequestMapping(value="getDetails/{id}",method = GET,headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> getDetailsForEquipmentStatus(@PathVariable(value="id") Long id){
        return OpenLmisResponse.response(OPERATIONAL_STATUS,service.getStatusById(id));
    }

    @RequestMapping(value="remove/{id}")
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="id") Long statusId, HttpServletRequest request){
        ResponseEntity<OpenLmisResponse> successResponse;
        try {
            service.remove(statusId);
        } catch (DataException e) {
            return error(e, HttpStatus.BAD_REQUEST);
        }
        successResponse = success(String.format("Equipment Operational Status has been successfully removed"));
        return successResponse;
    }

    @RequestMapping(value="list",method= GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> getAll(){
        return OpenLmisResponse.response(OPERATIONAL_STATUSES,service.getAll());
    }

}
