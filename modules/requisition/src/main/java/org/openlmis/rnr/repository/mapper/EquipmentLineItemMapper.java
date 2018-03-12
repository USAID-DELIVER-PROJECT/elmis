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
import org.openlmis.rnr.domain.EquipmentTestItemTests;
import org.openlmis.rnr.domain.EquipmentTestTypeOperationalStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

  @Select("SELECT esli.id as id , \n" +
          " (select statusid from equipment_inventory_statuses where id = esli.inventoryStatusId limit 1) as operationalStatusId, \n" +
          " sq.id as programEquipmentId, inv.equipmentId as equipmentId, \n" +
          " esli.rnrid, esli.code, esli.equipmentname, esli.equipmentcategory equipmentCategory, esli.equipmentserial, \n" +
          " esli.equipmentinventoryid, esli.testcount, esli.totalcount, esli.daysoutofuse, esli.remarks, \n" +
          " esli.createdby, esli.createddate, esli.modifiedby, esli.modifieddate, esli.inventorystatusid, \n" +
          " esli.electrolytesdaysoutofuse, esli.analytesdaysoutofuse, cat.code eqipmentCategory, et.isbiochemistry as isBioChemistryEquipment, " +
          " cat.code  as equipmentTypeCategory\n" +
          "FROM equipment_status_line_items esli  \n" +
          " JOIN equipment_inventories inv ON esli.equipmentInventoryId = inv.id  \n" +
          "        LEFT JOIN (SELECT etp.*  \n" +
          "          , e.id AS equipmentId  \n" +
          "        FROM equipment_type_programs etp  \n" +
          "          JOIN equipments e ON etp.equipmentTypeId = e.equipmentTypeId  \n" +
          "        WHERE etp.programId IN (SELECT max(programId) from requisitions WHERE id = #{rnrId})) sq ON sq.equipmentId = inv.equipmentId  \n" +
          "        LEFT JOIN equipment_types et ON et.id = sq.equipmenttypeid  \n" +
          "        LEFT JOIN equipment_category cat on cat.id = et.categoryid \n" +
          "      WHERE rnrId = #{rnrId} order by cat.code, esli.equipmentname")
  @Results(
      value = {
          @Result(property = "id", column = "id"),
          @Result(property = "operationalStatusId", column = "operationalStatusId"),
          @Result(property = "relatedProducts", javaType = List.class, column = "id",
                  many = @Many(select = "org.openlmis.rnr.repository.mapper.EquipmentLineItemMapper.getRelatedRnrLineItems")),
          @Result(property = "bioChemistryTestes", javaType = List.class, column = "id",
                  many = @Many(select = "getBioChemistryTestLineItem")),
          @Result(property = "operationalStatus", javaType = EquipmentOperationalStatus.class, column = "operationalStatusId",
                      one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentOperationalStatusMapper.getById")),

          @Result(property = "nonFunctionalDaysOutOfUse", javaType = List.class, column = "id",
                      many = @Many(select = "getEquipmentLineItemOperationalStatus")),
          @Result(property = "testDone", javaType = List.class, column = "id",
                      many = @Many(select = "getEquipmentsTestDone"))

  })
  List<EquipmentLineItem> getEquipmentLineItemsByRnrId(@Param("rnrId") Long rnrId);


  @Select("select status.*, types.name as functionalTestName\n" +
          "from equipment_test_type_operational_status status\n" +
          "join equipment_functional_test_types types ON types.id = status.functionaltestypeid\n" +
          "where equipmentlineitemid  = #{linetItemId}")
  List<EquipmentTestTypeOperationalStatus> getEquipmentLineItemOperationalStatus(Long linetItemId);

  @Select(" select tests.*, item.name testItemName, testTypes.name testTypeName\n" +
          "from equipment_test_item_tests  tests  \n" +
          "join equipment_test_items item \n" +
          "join equipment_functional_test_types testTypes ON testTypes.id = item.functionaltesttypeid\n" +
          "on item.id = tests.testitemid where equipmentlineitemid = #{linetItemId}\n")
  List<EquipmentTestItemTests> getEquipmentsTestDone(Long linetItemId);


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

  @Insert("INSERT INTO equipment_test_type_operational_status (equipmentlineitemid, functionaltestypeid, nonfunctional, daysoutofuse, createdby)\n" +
          "   SELECT lineitem.id equipmentlineitemid, type.id functionalTestTypeId, false nonFunctional, 0 daysOutOfUse, #{createdBy} as createdBy\n" +
          "    from requisitions r\n" +
          "    join equipment_status_line_items lineitem ON r.id = lineitem.rnrid \n" +
          "    join equipment_inventories inventory ON lineitem.equipmentinventoryid = inventory.id\n" +
          "    join equipments e ON inventory.equipmentid = e.id\n" +
          "    join equipment_types t ON e.equipmenttypeid = t.id\n" +
          "    join equipment_category cat on cat.id = t.categoryid \n" +
          "    join equipment_functional_test_types type ON cat.id = type.equipmentcategoryid  \n" +
          "    where r.id = #{rnrId}")
  void generateOperationalStatus(@Param("rnrId") Long rnrId, @Param("createdBy") Long createdBy);

  @Insert("INSERT INTO equipment_test_item_tests (testitemid, numberoftestes, equipmentlineitemid, createdby)\n" +
          "SELECT items.id testitemid, 0 as numberoftestes, lineitem.id equipmentlineitemid, #{createdBy} as createdby\n" +
          "from requisitions r\n" +
          "join equipment_status_line_items lineitem ON r.id = lineitem.rnrid \n" +
          "join equipment_inventories inventory ON lineitem.equipmentinventoryid = inventory.id\n" +
          "join equipments e ON inventory.equipmentid = e.id\n" +
          "join equipment_types t ON e.equipmenttypeid = t.id\n" +
          "join equipment_category cat on cat.id = t.categoryid \n" +
          "join equipment_functional_test_types type ON cat.id = type.equipmentcategoryid  \n" +
          "join equipment_test_items items ON items.functionaltesttypeid = type.id\n" +
          "where r.id = #{rnrId}\n")
  void generateTestCounts(@Param("rnrId") Long rnrId, @Param("createdBy") Long createdBy);

  @Update("UPDATE equipment_test_type_operational_status\n" +
          " SET  functionaltestypeid=#{functionalTestypeId}, nonfunctional=#{nonFunctional}, daysoutofuse=#{daysOutOfuse}, \n" +
          "       equipmentlineitemid=#{equipmentLineItemId}, modifiedby=#{modifiedBy}, \n" +
          "       modifieddate=#{modifiedDate}" +
          " WHERE id = #{id}")
  void updateNonFunctionalDaysOutOfUse(EquipmentTestTypeOperationalStatus operationalStatus);

  @Update("UPDATE public.equipment_test_item_tests\n" +
          "   SET  testitemid=#{testItemId}, numberoftestes=#{numberOfTestes}, equipmentlineitemid=#{equipmentLineItemId}, \n" +
          "       modifiedby=#{modifiedBy},  modifieddate=#{modifiedDate} \n" +
          " WHERE id = #{id}")
  void updateTestDone(EquipmentTestItemTests test);
}
