package org.openlmis.web.controller.equipment;

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.ColdChainEquipmentDesignation;
import org.openlmis.equipment.service.ColdChainStatusDataService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
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
@RequestMapping(value="/equipment/cold-chain/")
public class ColdChainStatusDataController extends BaseController {
  public static final String DESIGNATION = "designations";
  public static final String DESIGNATION_GET_BY_ID = "designationsById";


  @Autowired
  ColdChainStatusDataService service;

  @RequestMapping(method = GET, value = "designations")
  public ResponseEntity<OpenLmisResponse> getDesignations( ){
    return OpenLmisResponse.response("designations",service.getAllDesignations());
  }

  @RequestMapping(method = GET, value = "pqsStatus")
  public ResponseEntity<OpenLmisResponse> getPqsStatus( ){
    return OpenLmisResponse.response("pqs_status",service.getAllPqsStatus());
  }

  @RequestMapping(value="insert.json",method=POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ColdChainEquipmentDesignation designation, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;
    designation.setCreatedBy(loggedInUserId(request));
    designation.setModifiedBy(loggedInUserId(request));
    try {
      service.save(designation);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success(String.format("Equipment Designation '%s' has been successfully saved", designation.getName()));
    successResponse.getBody().addData(DESIGNATION, designation);
     return  successResponse;
  }

  @RequestMapping(value="designation/{id}",method = GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable(value="id") Long id){
    return OpenLmisResponse.response(DESIGNATION_GET_BY_ID,service.getById(id));
  }





}
