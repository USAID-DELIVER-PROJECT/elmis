package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

/**
 * Created by hassan on 8/29/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityTemperatureLogTag extends BaseModel {

    private Facility facility;
    private String serialNumber;
    private Long facilityId;
    private Long zoneId;
    private String description;

}
