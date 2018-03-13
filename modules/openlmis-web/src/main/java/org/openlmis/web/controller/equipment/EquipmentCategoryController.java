package org.openlmis.web.controller.equipment;


import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.equipment.domain.EquipmentCategory;
import org.openlmis.equipment.domain.EquipmentFunctionalTestTypes;
import org.openlmis.equipment.service.EquipmentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RestController
public class EquipmentCategoryController extends BaseController {

    @Autowired
    private EquipmentCategoryService service;

        @RequestMapping(value = "/equipmentCategory", method = RequestMethod.GET)
    public ResponseEntity getAllEquipmentCategory() {

        return OpenLmisResponse.response(service.getAllEquipmentCategory());
    }

    @RequestMapping(value = "/equipmentCategory/{id}", method = RequestMethod.GET)
    public ResponseEntity getEquipmentCategoryById(@PathVariable("id") Long id) {

        return OpenLmisResponse.response(service.getEquipmentCategoryById(id));
    }

    @RequestMapping(value = "/equipmentCategory", method = RequestMethod.POST)
    public ResponseEntity insertEquipmentCategory(@RequestBody @Validated EquipmentCategory obj, HttpServletRequest request) {

        try {
            obj.setCreatedBy(loggedInUserId(request));
            service.insertEquipmentCategory(obj);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentCategory.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentCategory.saved.successfull");
    }

    @RequestMapping(value = "/equipmentCategory", method = RequestMethod.PUT)
    public ResponseEntity updateEquipmentCategory(@RequestBody @Validated EquipmentCategory obj) {

        try {
            service.updateEquipmentCategory(obj);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentCategory.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentCategory.saved.successfull");
    }

    @RequestMapping(value = "/equipmentCategory/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteEquipmentCategory(@PathVariable("id") Long id) {

        try {
            service.deleteEquipmentCategory(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataException("EquipmentCategory.data.already.inuse");
        }

        return OpenLmisResponse.success("EquipmentCategory.deleted.successfull");
    }


    @RequestMapping(value = "/equipmentCategories/associate/equipmentTypes", method = RequestMethod.PUT)
    public ResponseEntity associateEquipmentTypes(@RequestBody ArrayList<EquipmentCategory> categoryList) {

        try {
            service.associateEquipmentTypes(categoryList);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentCategory.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentCategory.saved.successfull");
    }


}
