package org.openlmis.equipment.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentModel extends BaseModel {

    EquipmentType equipmentType;
    Long equipmentTypeId;
    String name;
    String code;
}
