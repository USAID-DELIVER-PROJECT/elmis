package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.dto.HfrMappingDTO;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class HfrFacilityMappingPersistenceHandler extends AbstractModelPersistenceHandler {


    private FacilityService facilityService;

    @Autowired
    public HfrFacilityMappingPersistenceHandler(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @Override
    protected BaseModel getExisting(BaseModel record) {
        return facilityService.getAllHfrMappingByCouncil((HfrMappingDTO) record);
    }

    @Override
    protected void save(BaseModel record) {
        facilityService.insertHfrMapping((HfrMappingDTO) record);
    }

    @Override
    public void postProcess(AuditFields auditFields) {

    }

    @Override
    public String getMessageKey() {
        return "error.duplicate.facility.code";
    }
}
