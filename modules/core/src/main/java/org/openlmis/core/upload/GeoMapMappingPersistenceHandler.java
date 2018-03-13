package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.dto.GeoZoneMapDTO;
import org.openlmis.core.service.GeographicZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeoMapMappingPersistenceHandler extends AbstractModelPersistenceHandler{

    @Autowired
    private GeographicZoneService service;
    @Override
    protected BaseModel getExisting(BaseModel record) {
        return service.getGeoMapMapping((GeoZoneMapDTO)record);
    }

    @Override
    protected void save(BaseModel record) {
       service.uploadGeoMapMapping((GeoZoneMapDTO)record);
    }


    @Override
    public String getMessageKey() {
        return "error.duplicate.program.supported";
    }
}
