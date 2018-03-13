package org.openlmis.rnr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentTestTypeOperationalStatus extends BaseModel{

    Integer equipmentLineItemId;
    Integer functionalTestypeId;
    Boolean nonFunctional;
    Integer daysOutOfuse;
    String functionalTestName;
}
