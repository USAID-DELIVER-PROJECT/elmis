package org.openlmis.equipment.service;


import java.util.List;

import org.openlmis.equipment.domain.EquipmentTestItems;
import org.openlmis.equipment.repository.EquipmentTestItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipmentTestItemsService {

    @Autowired
    private EquipmentTestItemsRepository repository;

    public List<EquipmentTestItems> getAllEquipmentTestItems() {

        return repository.getAllEquipmentTestItems();
    }

    public EquipmentTestItems getEquipmentTestItemsById(Long id) {
        return repository.getEquipmentTestItemsById(id);
    }

    public void deleteEquipmentTestItems(Long id) {
        repository.deleteEquipmentTestItems(id);
    }

    public void updateEquipmentTestItems(EquipmentTestItems obj) {
        repository.updateEquipmentTestItems(obj);
    }

    public void insertEquipmentTestItems(EquipmentTestItems obj) {
        repository.insertEquipmentTestItems(obj);
    }
}