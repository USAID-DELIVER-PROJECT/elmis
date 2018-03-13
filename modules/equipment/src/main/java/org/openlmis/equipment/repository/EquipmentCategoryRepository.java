package org.openlmis.equipment.repository;


import org.openlmis.equipment.domain.EquipmentCategory;
import org.openlmis.equipment.domain.EquipmentFunctionalTestTypes;
import org.openlmis.equipment.repository.mapper.EquipmentCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EquipmentCategoryRepository {

    @Autowired
    private EquipmentCategoryMapper mapper;

    public List<EquipmentCategory> getAllEquipmentCategory() {

        return mapper.getAllEquipmentCategory();
    }

    public EquipmentCategory getEquipmentCategoryById(Long id) {
        return mapper.getEquipmentCategoryById(id);
    }

    public void deleteEquipmentCategory(Long id) {
        mapper.deleteEquipmentCategory(id);
    }

    public void updateEquipmentCategory(EquipmentCategory obj) {
        mapper.updateEquipmentCategory(obj);
    }

    public void insertEquipmentCategory(EquipmentCategory obj) {
        mapper.insertEquipmentCategory(obj);
    }

    public void associateEquipmentTypes(List<EquipmentCategory> categoryList) {

        //first reset the equipmenType -> equipmentCategory association
        mapper.resetEquipmentTypecategoryAssociation();

        //rest equipmentFunctionalTest -> equipmentCategory association
        mapper.resetEquipmentFunctionalTestTypeCategoryAssociation();

        categoryList.stream()
                .forEach(category -> {
                    category.getEquipmentTypeIds()
                            .stream()
                            .forEach(equipmentTypeId -> mapper.associateEquipmentTypes(category.getId(), equipmentTypeId));

                    category.getFunctionalTestTypeIds()
                            .stream()
                            .forEach(functionalTestTypeId -> mapper.associateFunctionalTestTypes(category.getId(), functionalTestTypeId));

                });
    }

}
