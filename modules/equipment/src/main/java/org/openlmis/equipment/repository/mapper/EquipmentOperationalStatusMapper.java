/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.EquipmentOperationalStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentOperationalStatusMapper {

  @Select("select * from equipment_operational_status order by displayOrder, name")
  List<EquipmentOperationalStatus> getAll();

  @Select("SELECT *" +
      " FROM equipment_operational_status" +
      " WHERE id = #{id}" +
      " ORDER BY displayOrder, name")
  EquipmentOperationalStatus getById(@Param("id")Long id);

  @Insert(" INSERT INTO equipment_operational_status(\n" +
          "            name, displayOrder, createdby, createddate, modifiedBy, modifiedDate, \n" +
          "            category, isBad, needSparePart)\n" +
          "    VALUES ( #{name}, #{displayOrder}, #{createdBy}, NOW(), #{modifiedBy}, NOW(), \n" +
          "            #{category}, #{isBad}, #{needSparePart}) ")
    @Options(useGeneratedKeys = true)
    Integer Insert(EquipmentOperationalStatus operationalStatus);

    @Update(" UPDATE equipment_operational_status\n" +
            "   SET name=#{name}, displayOrder=#{displayOrder},modifiedby= #{modifiedBy}, \n" +
            "       modifiedDate=#{modifiedDate}, category= #{category}, isbad= #{isBad}, needsparepart= #{needSparePart}\n" +
            " WHERE id = #{id} ")
    void update(EquipmentOperationalStatus operationalStatus);

    @Delete(" DELETE FROM equipment_operational_status WHERE id = #{id} ")
    void remove(@Param("id") Long id);

    @Select(" SELECT id, name, displayorder, modifiedby, modifieddate, \n" +
            "       category, isbad, needsparepart\n" +
            "  FROM equipment_operational_status\n" +
            "  WHERE ID = #{Id} ")
    EquipmentOperationalStatus getStatusById(Long Id);

  @Select(" SELECT id, name, displayorder, modifiedby, modifieddate, \n" +
          "       category, isbad, needsparepart\n" +
          "  FROM equipment_operational_status\n" +
          "  WHERE code = #{code}")
  EquipmentOperationalStatus getByCode(String code);

    @Select("select * from equipment_operational_status where category like #{category} || '%'")
    List<EquipmentOperationalStatus> getOperationalStatusByCategory(String category);
}
