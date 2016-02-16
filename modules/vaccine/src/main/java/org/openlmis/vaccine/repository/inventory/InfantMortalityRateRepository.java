package org.openlmis.vaccine.repository.inventory;

import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.openlmis.vaccine.repository.mapper.inventory.InfantMortalityRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class InfantMortalityRateRepository {

    @Autowired
    private InfantMortalityRateMapper mapper;

    public InfantMortalityRateDTO getByCode(String code) {

        return mapper.getByCode(code);
    }

    public void update(InfantMortalityRateDTO mortalityRateDTO) {
         mapper.update(mortalityRateDTO);
    }

    public void insert(InfantMortalityRateDTO mortalityRateDTO) {
         mapper.insert(mortalityRateDTO);
    }
}
