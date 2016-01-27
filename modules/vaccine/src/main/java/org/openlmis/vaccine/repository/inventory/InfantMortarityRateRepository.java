package org.openlmis.vaccine.repository.inventory;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.openlmis.vaccine.repository.mapper.inventory.InfantMortalityRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by hassan on 1/6/16.
 */
@Repository
public class InfantMortarityRateRepository {

    @Autowired
    private InfantMortalityRateMapper mapper;

    public InfantMortalityRateDTO getByCode(String code) {

        return mapper.getByCode(code);
    }
}
