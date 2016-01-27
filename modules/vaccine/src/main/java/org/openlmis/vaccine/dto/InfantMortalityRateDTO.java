package org.openlmis.vaccine.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Program;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * Created by hassan on 1/6/16.
 */

@Data
@NoArgsConstructor
public class InfantMortalityRateDTO extends BaseModel implements Importable {

    @ImportField(name = "Facility Code", type = "String", nested = "code", mandatory = true)
    private Facility facility;

    @ImportField(name = "Geographic Zone Code",type = "String", nested = "code", mandatory = true)
    private GeographicZone geographicZone;

    @ImportField(name = "Infant Mortality Rate", type = "Integer", mandatory = true)
    Integer infantMortalityRate;

}
