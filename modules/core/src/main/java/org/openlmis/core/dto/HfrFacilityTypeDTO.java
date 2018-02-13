package org.openlmis.core.dto;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
public class HfrFacilityTypeDTO extends BaseModel  implements Importable {

    @ImportField(mandatory = true, name = "code")
    private String vimsCode;

    @ImportField(mandatory = true, name = "HFR Facility Type")
    private String hfrFacilityType;

}
