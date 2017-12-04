/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.EquipmentEnergyType;
import org.openlmis.equipment.domain.EquipmentModel;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentModelMapper {

    @Select("select * from equipment_model")
    @Results({
            @Result(
                    property = "equipmentType", column = "equipmenttypeid", javaType = EquipmentType.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById"))
    })
    List<EquipmentModel> getAll();

    @Select("select * from equipment_model where id = #{id}")
    EquipmentModel getById(Long id);

    @Select("select * from equipment_model where id = #{id}")
    EquipmentModel getEquipmentModelById(Long id);

    @Delete("delete from equipment_model where id = #{id}")
    void deleteEquipmentModel(Long id);

    @Update("update equipment_model set equipmenttypeid=#{equipmentTypeId}, name=#{name}, code=#{code}, modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} where id=#{id}")
    void updateEquipmentModel(EquipmentModel model);

    @Insert("insert into equipment_model (equipmenttypeid, name, code, createdBy, createdDate) values (#{equipmentTypeId}, #{name}, #{code}, #{createdBy}, #{createdDate})")
    void insertEquipmentModel(EquipmentModel model);

    @Select("select * from equipment_model where equipmenttypeid = #{equipmentTypeId}")
    List<EquipmentModel> getByEquipmentTypeId(Long equipmentTypeId);
}
