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

package org.openlmis.rnr.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Product;
import org.openlmis.equipment.domain.EquipmentOperationalStatus;
import org.openlmis.equipment.domain.NonFunctionalTestProducts;
import org.openlmis.rnr.domain.EquipmentLineItem;
import org.openlmis.rnr.domain.EquipmentLineItemBioChemistryTests;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentLineItemMapper {

  @Insert("INSERT INTO equipment_status_line_items (rnrId, code, equipmentName, equipmentCategory, equipmentSerial," +
      " equipmentInventoryId, inventoryStatusId, testCount, totalCount, daysOutOfUse, remarks, createdBy," +
      " createdDate, modifiedBy, modifiedDate)" +
      " values" +
      " (#{rnrId}, #{code}, #{equipmentName}, #{equipmentCategory}, #{equipmentSerial}, #{equipmentInventoryId}," +
      " #{inventoryStatusId}, #{testCount}, #{totalCount}, #{daysOutOfUse}, #{remarks}, #{createdBy}," +
      " #{createdDate}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insert(EquipmentLineItem item);

  @Update("UPDATE equipment_status_line_items " +
      " SET" +
      " code = #{code}, equipmentName = #{equipmentName}, equipmentCategory = #{equipmentCategory}," +
      " equipmentSerial = #{equipmentSerial}, equipmentInventoryId = #{equipmentInventoryId}," +
      " inventoryStatusId = #{inventoryStatusId}, testCount = #{testCount}, totalCount = #{totalCount}," +
      " daysOutOfUse = #{daysOutOfUse}, remarks = #{remarks}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()," +
      " electrolytesDaysOutOfUse = #{electrolytesDaysOutOfUse}, analytesDaysOutOfUse = #{analytesDaysOutOfUse}" +
      " where id = #{id}")
  Integer update(EquipmentLineItem item);

  @Select("SELECT esli.id " +
          " , (select statusid from equipment_inventory_statuses where id = esli.inventoryStatusId limit 1) as operationalStatusId " +
      "  , sq.id as programEquipmentId " +
      "  , esli.*, et.isbiochemistry as isBioChemistryEquipment " +
      "FROM equipment_status_line_items esli " +
      "  JOIN equipment_inventories inv ON esli.equipmentInventoryId = inv.id " +
      "  LEFT JOIN (SELECT etp.* " +
      "    , e.id AS equipmentId " +
      "  FROM equipment_type_programs etp " +
      "    JOIN equipments e ON etp.equipmentTypeId = e.equipmentTypeId " +
      "  WHERE etp.programId IN (SELECT max(programId) from requisitions WHERE id = #{rnrId})) sq ON sq.equipmentId = inv.equipmentId " +
      "  LEFT JOIN equipment_types et ON et.id = sq.equipmenttypeid " +
      "WHERE rnrId = #{rnrId} ")
  @Results(
      value = {
          @Result(property = "id", column = "id"),
          @Result(property = "operationalStatusId", column = "operationalStatusId"),
          @Result(property = "relatedProducts", javaType = List.class, column = "id",
                  many = @Many(select = "org.openlmis.rnr.repository.mapper.EquipmentLineItemMapper.getRelatedRnrLineItems")),
          @Result(property = "bioChemistryTestes", javaType = List.class, column = "id",
                  many = @Many(select = "org.openlmis.rnr.repository.mapper.EquipmentLineItemMapper.getBioChemistryTestLineItem")),
          @Result(property = "operationalStatus", javaType = EquipmentOperationalStatus.class, column = "operationalStatusId",
                      one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentOperationalStatusMapper.getById"))
  })
  List<EquipmentLineItem> getEquipmentLineItemsByRnrId(@Param("rnrId") Long rnrId);

  @Select("SELECT tests.id, status.id equipmentLineItemId, products.id as productId, tests.numberoftestes, " +
          "       types.name as testTypeName, products.name as productName, types.code as testTypeCode  " +
          "FROM  equipment_status_line_items status " +
          "JOIN  equipment_bio_chemistry_tests tests ON status.id = tests.equipmentlineitemid " +
          "JOIN  equipment_bio_chemistry_products products ON tests.productid = products.id " +
          "JOIN  equipment_bio_chemistry_test_types types on products.testtypeid = types.id " +
          "WHERE status.id = #{id} ")
  List<EquipmentLineItemBioChemistryTests> getBioChemistryTestLineItem(@Param("id") Long id);


  @Select("select rli.id, p.primaryName, p.code from " +
                " requisitions r " +
      "         JOIN requisition_line_items rli on r.id = rli.rnrId " +
      "         JOIN products p on p.code::text = rli.productCode::text " +
      "         JOIN equipment_status_line_items esli on esli.rnrId = r.id " +
      "         JOIN equipment_inventories ei on ei.id = esli.equipmentInventoryId " +
      "         JOIN equipment_products ep on ei.equipmentId = ep.equipmentId " +
      "               and p.id = ep.productId " +
      " WHERE " +
      "       esli.id = #{id}")

   List<Product> getRelatedRnrLineItems(@Param("id") Long id);

  @Select("select * from equipment_status_line_items where id = #{id}")
  EquipmentLineItem getById( @Param("id") Long id);

  @Insert("INSERT INTO equipment_bio_chemistry_tests (productid, numberoftestes, equipmentlineitemid," +
          " createdBy, createdDate, modifiedBy, modifiedDate )" +
          " VALUES " +
          " (#{productId},#{numberOfTestes},#{equipmentLineItemId},#{createdBy}," +
          " #{createdDate}, #{modifiedBy}, #{modifiedDate})")
  @Options(useGeneratedKeys = true)
  Integer insertEquipmentLineItemBioChemistryTests(EquipmentLineItemBioChemistryTests test);

  @Update("update equipment_bio_chemistry_tests set productid=#{productId}, numberoftestes=#{numberOfTestes}, " +
          "equipmentlineitemid=#{equipmentLineItemId}, modifiedBy=#{modifiedBy}, modifiedDate=#{modifiedDate} " +
          " where id=#{id}")
  Integer updateEquipmentLineItemBioChemistryTests(EquipmentLineItemBioChemistryTests test);

  @Select("select id as productId, null as equipmentLineItemId, null as numberOfTestes from equipment_bio_chemistry_products;")
  List<EquipmentLineItemBioChemistryTests> getEmptyBioChemistryEquipmentTestWithProducts();
}
