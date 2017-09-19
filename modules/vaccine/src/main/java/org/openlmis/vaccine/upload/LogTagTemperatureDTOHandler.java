package org.openlmis.vaccine.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.openlmis.vaccine.service.inventory.FacilityTemperatureLogTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hassan on 8/31/17.
 */
@Component
@NoArgsConstructor
public class LogTagTemperatureDTOHandler extends AbstractModelPersistenceHandler {

    @Autowired
    private FacilityTemperatureLogTagService tagService;

    @Override
    protected BaseModel getExisting(BaseModel record) {
        LogTagDTO logTagDTO = (LogTagDTO)record;
      //  System.out.println(record.getCreatedBy());
        return tagService.getByCode(logTagDTO);
    }

    @Override
    protected void save(BaseModel record) {
        System.out.println("recodred");
        System.out.println(record);
        LogTagDTO logTagDTO = (LogTagDTO)record;
        System.out.println("createdDate");
        System.out.println(record.getCreatedDate());
        System.out.println(logTagDTO.getCreatedDate());

        tagService.save(logTagDTO);
        //System.out.println(record.getCreatedBy());
    }

    @Override
    public void postProcess(AuditFields auditFields) {
        tagService.addFacilitySent(auditFields);
        System.out.println(auditFields.getUser());
        System.out.println(auditFields.getCurrentTimestamp());
       // programService.notifyProgramChange();
    }
}
