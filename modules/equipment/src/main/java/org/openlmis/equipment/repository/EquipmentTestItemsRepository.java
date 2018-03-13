package org.openlmis.equipment.repository;


import org.openlmis.equipment.domain.EquipmentTestItems;
import org.openlmis.equipment.repository.mapper.EquipmentTestItemsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class EquipmentTestItemsRepository {

    @Autowired
    private EquipmentTestItemsMapper mapper;

    public List<EquipmentTestItems> getAllEquipmentTestItems() {

        return mapper.getAllEquipmentTestItems();
    }

    public EquipmentTestItems getEquipmentTestItemsById(Long id) {
        return mapper.getEquipmentTestItemsById(id);
    }

    public void deleteEquipmentTestItems(Long id) {
        mapper.deleteEquipmentTestItems(id);
    }

    public void updateEquipmentTestItems(EquipmentTestItems obj) {
        mapper.updateEquipmentTestItems(obj);
    }

    public void insertEquipmentTestItems(EquipmentTestItems obj) {
        mapper.insertEquipmentTestItems(obj);
    }
}