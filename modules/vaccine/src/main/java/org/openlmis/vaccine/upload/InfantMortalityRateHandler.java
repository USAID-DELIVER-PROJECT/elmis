package org.openlmis.vaccine.upload;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.openlmis.vaccine.repository.inventory.InfantMortalityRateRepository;
import org.openlmis.vaccine.service.inventory.InfantMortalityRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class InfantMortalityRateHandler extends AbstractModelPersistenceHandler {

    @Autowired
    private InfantMortalityRateRepository repository;

    @Autowired
    private InfantMortalityRateService service;

    @Override
    protected BaseModel getExisting(BaseModel record) {
        InfantMortalityRateDTO mortalityRateDTO = (InfantMortalityRateDTO)record;
        return repository.getByCode(mortalityRateDTO.getGeographicZone().getCode());
    }

    @Override
    protected void save(BaseModel record) {
      service.insert((InfantMortalityRateDTO) record);
    }

    @Override
    public String getMessageKey() {
        return "error.duplicate.infant.mortality.rate";
    }

}
