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
public class EquipmentTestItemTests extends BaseModel {

    Integer equipmentLineItemId;
    Long    testItemId;
    Integer numberOfTestes;
    String testItemName;
    String testTypeName;
}
