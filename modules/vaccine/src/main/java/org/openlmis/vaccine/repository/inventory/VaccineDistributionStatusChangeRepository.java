package org.openlmis.vaccine.repository.inventory;

import org.openlmis.vaccine.domain.inventory.VaccineDistributionStatusChange;
import org.openlmis.vaccine.repository.mapper.inventory.VaccineInventoryDistributionStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VaccineDistributionStatusChangeRepository {
    @Autowired
    VaccineInventoryDistributionStatusMapper statusMapper;

    public void insert(VaccineDistributionStatusChange statusChange){
        statusMapper.Insert(statusChange);
    }

}
