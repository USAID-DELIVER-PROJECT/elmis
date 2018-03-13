package org.openlmis.web.controller.equipment;


import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.equipment.domain.EquipmentTestItems;
import org.openlmis.equipment.service.EquipmentTestItemsService;
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


@RestController
public class EquipmentTestItemsController extends BaseController {

    @Autowired
    private EquipmentTestItemsService service;

    @RequestMapping(value = "/equipmentTestItems", method = RequestMethod.GET)
    public ResponseEntity getAllEquipmentTestItems() {

        return OpenLmisResponse.response(service.getAllEquipmentTestItems());
    }

    @RequestMapping(value = "/equipmentTestItems/{id}", method = RequestMethod.GET)
    public ResponseEntity getEquipmentTestItemsById(@PathVariable("id") Long id) {

        return OpenLmisResponse.response(service.getEquipmentTestItemsById(id));
    }

    @RequestMapping(value = "/equipmentTestItems", method = RequestMethod.POST)
    public ResponseEntity insertEquipmentTestItems(@RequestBody @Validated EquipmentTestItems obj) {

        try {
            service.insertEquipmentTestItems(obj);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentTestItems.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentTestItems.saved.successfull");
    }

    @RequestMapping(value = "/equipmentTestItems", method = RequestMethod.PUT)
    public ResponseEntity updateEquipmentTestItems(@RequestBody @Validated EquipmentTestItems obj) {

        try {
            service.updateEquipmentTestItems(obj);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentTestItems.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentTestItems.saved.successfull");
    }

    @RequestMapping(value = "/equipmentTestItems/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteEquipmentTestItems(@PathVariable("id") Long id) {

        try {
            service.deleteEquipmentTestItems(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataException("EquipmentTestItems.data.already.inuse");
        }

        return OpenLmisResponse.success("EquipmentTestItems.deleted.successfull");
    }
}