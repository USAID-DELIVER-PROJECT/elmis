package org.openlmis.core.dto;

import lombok.Data;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
@Data
public class GeoZoneMapDTO extends BaseModel implements Importable{

    @ImportField(mandatory = true, name = "Code")
    private String code;

    @ImportField(mandatory = true, name = "Map Code")
    private String mapCode;

    private Long geographicZoneId;
}
