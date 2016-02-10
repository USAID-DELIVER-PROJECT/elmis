package org.openlmis.vaccine.service.inventory;

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.demographics.service.AnnualDistrictDemographicEstimateService;
import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.openlmis.vaccine.repository.inventory.InfantMortalityRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class InfantMortalityRateService {

    @Autowired
    private AnnualDistrictDemographicEstimateService estimateService;

    @Autowired
    private InfantMortalityRateRepository repository;

    @Autowired
    private GeographicZoneRepository zoneRepository;

    public void insert(InfantMortalityRateDTO mortalityRateDTO){
        if(mortalityRateDTO.getId() != null){
            repository.update(mortalityRateDTO);
        }else {
            GeographicZone zone = zoneRepository.getByCode(mortalityRateDTO.getGeographicZone().getCode());
            mortalityRateDTO.setDistrictId(zone.getId());
            insertDistrictDemographic(2016,82L,mortalityRateDTO.getCreatedBy());
            repository.insert(mortalityRateDTO);
        }
    }

    private void insertDistrictDemographic(Integer year, Long programId,Long userId) {
     estimateService.getEstimateForm(year,programId,userId);

    }

}
