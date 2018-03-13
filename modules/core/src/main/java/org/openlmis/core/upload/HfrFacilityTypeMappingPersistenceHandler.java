package org.openlmis.core.upload;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.dto.HfrFacilityTypeDTO;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.FacilityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class HfrFacilityTypeMappingPersistenceHandler extends AbstractModelPersistenceHandler{

    private FacilityService facilityService;

    private FacilityTypeService service;

    @Autowired
    HfrFacilityTypeMappingPersistenceHandler (FacilityService facilityService){
        this.facilityService = facilityService;
    }

    @Override
    protected BaseModel getExisting(BaseModel record) {
        return facilityService.getAllByFacilityType((HfrFacilityTypeDTO) record);
    }

    @Override
    protected void save(BaseModel record) {
     facilityService.insertHfrFacilityTypeMapping((HfrFacilityTypeDTO)record);
    }
}
