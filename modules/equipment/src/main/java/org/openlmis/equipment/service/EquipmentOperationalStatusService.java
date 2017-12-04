package org.openlmis.equipment.service;

import lombok.NoArgsConstructor;
import org.openlmis.equipment.domain.EquipmentOperationalStatus;
import org.openlmis.equipment.repository.EquipmentOperationalStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class EquipmentOperationalStatusService {


    EquipmentOperationalStatusRepository repository;

    @Autowired
   public EquipmentOperationalStatusService(EquipmentOperationalStatusRepository repository){
        this.repository = repository;
    }

    public void save(EquipmentOperationalStatus operationalStatus){
        if(operationalStatus.getId() == null)
            repository.insert(operationalStatus);
        else
            repository.update(operationalStatus);
    }

    public void remove(Long id){
        repository.remove(id);
    }

    public EquipmentOperationalStatus getStatusById(Long id){
        return repository.getStatusById(id);
    }

    public List<EquipmentOperationalStatus>getAll(){
        return repository.getAll();
    }

    public EquipmentOperationalStatus getByCode(String code){
        return repository.getByCode(code);
    }

}
