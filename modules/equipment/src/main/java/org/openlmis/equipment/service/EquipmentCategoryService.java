package org.openlmis.equipment.service;


import java.util.List;

import org.openlmis.equipment.domain.EquipmentCategory;
import org.openlmis.equipment.repository.EquipmentCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipmentCategoryService {

    @Autowired
    private EquipmentCategoryRepository repository;

    public List<EquipmentCategory> getAllEquipmentCategory() {

        return repository.getAllEquipmentCategory();
    }

    public EquipmentCategory getEquipmentCategoryById(Long id) {
        return repository.getEquipmentCategoryById(id);
    }

    public void deleteEquipmentCategory(Long id) {
        repository.deleteEquipmentCategory(id);
    }

    public void updateEquipmentCategory(EquipmentCategory obj) {
        repository.updateEquipmentCategory(obj);
    }

    public void insertEquipmentCategory(EquipmentCategory obj) {
        repository.insertEquipmentCategory(obj);
    }

    public void associateEquipmentTypes(List<EquipmentCategory> categoryList) { repository.associateEquipmentTypes(categoryList);
    }
}