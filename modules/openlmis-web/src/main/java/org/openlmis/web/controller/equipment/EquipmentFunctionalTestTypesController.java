package org.openlmis.web.controller.equipment;


import org.openlmis.core.exception.DataException;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.equipment.domain.EquipmentFunctionalTestTypes;
import org.openlmis.equipment.service.EquipmentFunctionalTestTypesService;
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
@RequestMapping("/EquipmentFunctionalTestTypes/")
public class EquipmentFunctionalTestTypesController extends BaseController {

    @Autowired
    private EquipmentFunctionalTestTypesService service;

    @RequestMapping(value = "/EquipmentFunctionalTestTypes", method = RequestMethod.GET)
    public ResponseEntity getAllEquipmentFunctionalTestTypes() {

        return OpenLmisResponse.response(service.getAllEquipmentFunctionalTestTypes());
    }

    @RequestMapping(value = "/EquipmentFunctionalTestTypes/{id}", method = RequestMethod.GET)
    public ResponseEntity getEquipmentFunctionalTestTypesById(@PathVariable("id") Long id) {

        return OpenLmisResponse.response(service.getEquipmentFunctionalTestTypesById(id));
    }

    @RequestMapping(value = "/EquipmentFunctionalTestTypes", method = RequestMethod.POST)
    public ResponseEntity insertEquipmentFunctionalTestTypes(@RequestBody @Validated EquipmentFunctionalTestTypes obj) {

        try {
            service.insertEquipmentFunctionalTestTypes(obj);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentFunctionalTestTypes.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentFunctionalTestTypes.saved.successfull");
    }

    @RequestMapping(value = "/EquipmentFunctionalTestTypes", method = RequestMethod.PUT)
    public ResponseEntity updateEquipmentFunctionalTestTypes(@RequestBody @Validated EquipmentFunctionalTestTypes obj) {

        try {
            service.updateEquipmentFunctionalTestTypes(obj);
        } catch (DuplicateKeyException ex) {
            throw new DataException("EquipmentFunctionalTestTypes.save.error.duplicate.code");
        }

        return OpenLmisResponse.success("EquipmentFunctionalTestTypes.saved.successfull");
    }

    @RequestMapping(value = "/EquipmentFunctionalTestTypes/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteEquipmentFunctionalTestTypes(@PathVariable("id") Long id) {

        try {
            service.deleteEquipmentFunctionalTestTypes(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataException("EquipmentFunctionalTestTypes.data.already.inuse");
        }

        return OpenLmisResponse.success("EquipmentFunctionalTestTypes.deleted.successfull");
    }
}