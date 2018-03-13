package org.openlmis.equipment.service;


import java.util.List;

import org.openlmis.equipment.domain.EquipmentFunctionalTestTypes;
import org.openlmis.equipment.repository.EquipmentFunctionalTestTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipmentFunctionalTestTypesService {

    @Autowired
    private EquipmentFunctionalTestTypesRepository repository;

    public List<EquipmentFunctionalTestTypes> getAllEquipmentFunctionalTestTypes() {

        return repository.getAllEquipmentFunctionalTestTypes();
    }

    public EquipmentFunctionalTestTypes getEquipmentFunctionalTestTypesById(Long id) {
        return repository.getEquipmentFunctionalTestTypesById(id);
    }

    public void deleteEquipmentFunctionalTestTypes(Long id) {
        repository.deleteEquipmentFunctionalTestTypes(id);
    }

    public void updateEquipmentFunctionalTestTypes(EquipmentFunctionalTestTypes obj) {
        repository.updateEquipmentFunctionalTestTypes(obj);
    }

    public void insertEquipmentFunctionalTestTypes(EquipmentFunctionalTestTypes obj) {
        repository.insertEquipmentFunctionalTestTypes(obj);
    }
}