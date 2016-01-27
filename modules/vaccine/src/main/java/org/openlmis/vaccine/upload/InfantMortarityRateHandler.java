package org.openlmis.vaccine.upload;

import com.sun.prism.impl.Disposer;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.openlmis.vaccine.repository.inventory.InfantMortarityRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hassan on 1/6/16.
 */
@Component
@NoArgsConstructor
public class InfantMortarityRateHandler extends AbstractModelPersistenceHandler {

    @Autowired
    private InfantMortarityRateRepository repository;

    @Override
    protected BaseModel getExisting(BaseModel record) {

        InfantMortalityRateDTO mortalityRateDTO = (InfantMortalityRateDTO)record;

        return repository.getByCode(mortalityRateDTO.getGeographicZone().getCode());
    }

    @Override
    protected void save(BaseModel record) {

    }
}
