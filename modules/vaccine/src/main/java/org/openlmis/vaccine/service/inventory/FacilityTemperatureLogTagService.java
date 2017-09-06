package org.openlmis.vaccine.service.inventory;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.vaccine.domain.inventory.FacilityTemperatureLogTag;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.openlmis.vaccine.repository.inventory.FacilityTemperatureLogTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hassan on 8/29/17.
 */

@Service
public class FacilityTemperatureLogTagService {
    @Autowired
    FacilityTemperatureLogTagRepository repository;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private GeographicZoneService zoneService;

    public void insert(FacilityTemperatureLogTag logTag){

        if(null != logTag.getZoneId()) {
            Long levelId = 0L;
            GeographicZone geographicZone = zoneService.getById(logTag.getZoneId());
            if (geographicZone != null) {
                levelId = geographicZone.getLevel().getId();
            }
            Facility facility = facilityService.getByGeographicZoneId(logTag.getZoneId(), levelId);
            logTag.setFacilityId(facility.getId());

            if (logTag.getId() == null)
                repository.insert(logTag);
            else
                repository.update(logTag);
        }
    }
    
    public FacilityTemperatureLogTag getById(Long id){
      return repository.getById(id);
    }
    public List<FacilityTemperatureLogTag> getAll(){
        return repository.getAll();
    }

    public BaseModel getByCode(LogTagDTO logTagDTO) {
        //System.out.println(logTagDTO.getCreatedBy());
        return logTagDTO;
    }

    public void save(LogTagDTO logTagDTO) {
        repository.insertLogTag(logTagDTO);
    }

    public void addFacilitySent(AuditFields auditFields) {
        Facility facility = facilityService.getHomeFacility(auditFields.getUser());
        repository.updateInserted(facility.getId(),auditFields.getCurrentTimestamp(),null);

    }
}
