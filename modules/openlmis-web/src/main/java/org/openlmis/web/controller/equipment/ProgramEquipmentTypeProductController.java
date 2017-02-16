/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.equipment;

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.EquipmentProduct;
import org.openlmis.equipment.service.EquipmentProductService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RequestMapping(value="/equipment/program-equipment-product/")
@Controller
public class ProgramEquipmentTypeProductController extends BaseController{

  public static final String PROGRAM_EQUIPMENT_PRODUCT = "programEquipmentProduct";
  public static final String PRODUCTS = "products";

  @Autowired
  EquipmentProductService equipmentProductService;

  @RequestMapping(value="getByProgramEquipment/{programEquipmentId}",headers = ACCEPT_JSON,method = RequestMethod.GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> getByProgramEquipmentId(@PathVariable(value="programEquipmentId") Long programEquipmentId){
    return OpenLmisResponse.response("programEquipmentProducts", equipmentProductService.getByProgramEquipmentId(programEquipmentId));
  }

  @RequestMapping(value = "save", method = RequestMethod.POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody EquipmentProduct equipmentProduct, HttpServletRequest request){
    Date date = new Date();
    Long userId = loggedInUserId(request);
    ResponseEntity<OpenLmisResponse> successResponse;

    if(equipmentProduct.getId() == null){
      equipmentProduct.setCreatedDate(date);
      equipmentProduct.setCreatedBy(userId);
    }
    equipmentProduct.setModifiedDate(date);
    equipmentProduct.setModifiedBy(userId);

    try{
      equipmentProductService.Save(equipmentProduct);
    }
    catch (DataException e){
      return OpenLmisResponse.error(e, HttpStatus.BAD_REQUEST);
    }

    successResponse = OpenLmisResponse.success("message.equipment.association.pep.saved");
    successResponse.getBody().addData(PROGRAM_EQUIPMENT_PRODUCT, equipmentProduct);
    return successResponse;
  }

  @RequestMapping(value="remove/{programEquipmentId}")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS')")
  public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value = "programEquipmentId") Long programEquipmentId){
    ResponseEntity<OpenLmisResponse> successResponse;

    try{
      equipmentProductService.remove(programEquipmentId);
    }
    catch(DataException e){
      return OpenLmisResponse.error(e,HttpStatus.BAD_REQUEST);
    }

    successResponse = OpenLmisResponse.success("message.equipment.association.pep.removed");
    return successResponse;
  }


  @RequestMapping(value="possible-products",headers = ACCEPT_JSON,method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getProducts(@RequestParam("program") Long programId, @RequestParam(value = "equipment", defaultValue = "0") Long equipmentId ){
    return OpenLmisResponse.response(PRODUCTS, equipmentProductService.getAvailableProductsToLink(programId, equipmentId));
  }
}
