package org.openlmis.equipment.domain;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class EquipmentTestItems extends BaseModel{

    String code;

    String name;

    Long displayorder;

    Long functionaltesttypeid;

    String functionalTestTypeName;

}
