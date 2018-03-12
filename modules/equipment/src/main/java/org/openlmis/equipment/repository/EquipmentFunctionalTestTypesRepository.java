package org.openlmis.equipment.repository;


import org.openlmis.equipment.domain.EquipmentFunctionalTestTypes;
import org.openlmis.equipment.repository.mapper.EquipmentFunctionalTestTypesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class EquipmentFunctionalTestTypesRepository {

    @Autowired
    private EquipmentFunctionalTestTypesMapper mapper;

    public List<EquipmentFunctionalTestTypes> getAllEquipmentFunctionalTestTypes() {

        return mapper.getAllEquipmentFunctionalTestTypes();
    }

    public EquipmentFunctionalTestTypes getEquipmentFunctionalTestTypesById(Long id) {
        return mapper.getEquipmentFunctionalTestTypesById(id);
    }

    public void deleteEquipmentFunctionalTestTypes(Long id) {
        mapper.deleteEquipmentFunctionalTestTypes(id);
    }

    public void updateEquipmentFunctionalTestTypes(EquipmentFunctionalTestTypes obj) {
        mapper.updateEquipmentFunctionalTestTypes(obj);
    }

    public void insertEquipmentFunctionalTestTypes(EquipmentFunctionalTestTypes obj) {
        mapper.insertEquipmentFunctionalTestTypes(obj);
    }
}