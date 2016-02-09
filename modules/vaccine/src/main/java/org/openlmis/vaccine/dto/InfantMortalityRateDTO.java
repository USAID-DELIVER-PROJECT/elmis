package org.openlmis.vaccine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Program;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * Created by hassan on 1/6/16.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfantMortalityRateDTO extends BaseModel implements Importable {

    @ImportField(mandatory = true, name = "Geographic Zone Code", nested = "code")
    private GeographicZone geographicZone;

    @ImportField(name = "Infant Mortality Rate")
    private Integer value;

    private Long districtId;

}
