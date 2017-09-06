package org.openlmis.vaccine.dto;

import lombok.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Created by hassan on 7/5/17.
 */

@Data
@NoArgsConstructor
public class LogTagDTO extends BaseModel implements Importable{

    @ImportField(name = "Date",type = "String")
    private String logDate;

    @ImportField(name = "Time",type = "String")
    private String logTime;

    /*private String sampleId;*/
    @ImportField(name = "Temperature",type = "String")
    private String temperature;

    @ImportField(name = "Events", type = "String")
    private String events;

    private Long facilityId;

    private String serialNumber;

    private Date createdDate;

    private String route;

    public void validateMandatoryFields() {
        if (isBlank(this.temperature)  || isBlank(this.temperature)) {
            throw new DataException("error.mandatory.fields.missing");
        }
    }

}
