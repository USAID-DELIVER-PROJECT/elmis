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

import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.equipment.domain.EquipmentModel;
import org.openlmis.equipment.service.EquipmentModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value="equipment/model/")
public class EquipmentModelController extends BaseController {

    public static final String MODELS = "models";
    @Autowired
    EquipmentModelService service;

    @RequestMapping(value="models",method= GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS, MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> getAllEquipmentModels(){
        return OpenLmisResponse.response(MODELS, service.getAll());
    }

    @RequestMapping(value = "models/{id}", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS, MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity getEquipmentModelById(@PathVariable("id") Long id) {

        return OpenLmisResponse.response(service.getEquipmentModelById(id));
    }

    @RequestMapping(value = "models", method = RequestMethod.POST)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS, MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> insertEquipmentModel(@RequestBody @Validated EquipmentModel obj) {

        try {
            service.insertEquipmentModel(obj);
        } catch (DuplicateKeyException ex) {
            return OpenLmisResponse.error("equipmentModel.save.error.duplicate.code", HttpStatus.BAD_REQUEST);
        }

        return OpenLmisResponse.success("equipmentModel.saved.successfully");
    }

    @RequestMapping(value = "models", method = RequestMethod.PUT)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS, MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> updateEquipmentModel(@RequestBody @Validated EquipmentModel obj) {

        try {
            service.updateEquipmentModel(obj);
        } catch (DuplicateKeyException ex) {
            return OpenLmisResponse.error("equipmentModel.save.error.duplicate.code", HttpStatus.BAD_REQUEST);
        }

        return OpenLmisResponse.success("equipmentModel.saved.successfully");
    }

    @RequestMapping(value = "models/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS, MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity<?> deleteEquipmentModel(@PathVariable("id") Long id) {

        try {
            service.deleteEquipmentModel(id);
        } catch (DataIntegrityViolationException ex) {
            return OpenLmisResponse.error("equipmentModel.data.already.inuse", HttpStatus.BAD_REQUEST);
        }

        return OpenLmisResponse.success("equipmentModel.delete.success");
    }

    @RequestMapping(value = "models/equipmenttype/{id}", method = RequestMethod.GET)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_EQUIPMENT_SETTINGS, MANAGE_EQUIPMENT_INVENTORY')")
    public ResponseEntity getByEquipmentTypeId(@PathVariable("id") Long id) {

        return OpenLmisResponse.response(service.getByEquipmentTypeId(id));
    }
}
