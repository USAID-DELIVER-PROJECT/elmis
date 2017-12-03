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
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Product;
import org.openlmis.equipment.domain.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface EquipmentMapper {

  @Select("SELECT equipments.*" +
      "   , COUNT(equipment_inventories.id) AS inventorycount" +
      " FROM equipments" +
      "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
      " GROUP BY equipments.id")
  @Results({
      @Result(
          property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
      @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
          @Result(
                  property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
          @Result(property = "energyTypeId", column = "energyTypeId")

  })
  List<Equipment> getAll();

    @Select("SELECT equipments.*" +
        "   , COUNT(equipment_inventories.id) AS inventorycount" +
        " FROM equipments" +
        "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
        " WHERE equipmentTypeId = #{equipmentTypeId}" +
        " GROUP BY equipments.id" +
        " ORDER BY id DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(
                    property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
            @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
            @Result(
                    property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
                    one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
            @Result(
                property = "relatedProducts", column = "id", javaType = List.class,
                many = @Many(select = "org.openlmis.equipment.repository.mapper.EquipmentMapper.getRelatedProducts")),
            @Result(property = "energyTypeId", column = "energyTypeId")
    })
    List<Equipment> getByType(@Param("equipmentTypeId") Long equipmentTypeId, RowBounds rowBounds);


    @Select("SELECT p.id, p.code, p.primaryName, p.strength, true as active  from products p " +
        " JOIN equipment_products etp on etp.productId = p.id " +
        " WHERE " +
        "  etp.equipmentId = #{equipmentId}")
    List<Product> getRelatedProducts(@Param("equipmentId") Long equipmentId);

  @Select("SELECT equipments.*" +
      "   , COUNT(equipment_inventories.id) AS inventorycount,  " +
          " equipment_cold_chain_equipments.*  " +
      " FROM equipments" +
      "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id " +
      "   LEFT JOIN equipment_cold_chain_equipments  ON equipment_cold_chain_equipments.equipmentId = equipments.ID " +
      " WHERE equipmentTypeId = #{equipmentTypeId}" +
      " GROUP BY equipments.id,equipment_cold_chain_equipments.equipmentid" +
      " ORDER BY name")
  @Results({
      @Result(
          property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
      @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
      @Result(
          property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
          one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
      @Result(property = "energyTypeId", column = "energyTypeId"),
          @Result(property = "designation", column = "designationId", javaType = ColdChainEquipmentDesignation.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentDesignationMapper.getById")),
                  @Result(property = "designationId", column = "designationId")


  })
  List<Equipment> getAllByType(@Param("equipmentTypeId") Long equipmentTypeId);

  /** old equipment data has a text based 'model' field so that get equipment
   *  by model we need to use both the 'model'::String and the new 'modelId'::Int fields
   **/
  @Select("SELECT equipments.*" +
          "   , COUNT(equipment_inventories.id) AS inventorycount,  " +
          " equipment_cold_chain_equipments.*  " +
          " FROM equipments" +
          "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id " +
          "   LEFT JOIN equipment_cold_chain_equipments  ON equipment_cold_chain_equipments.equipmentId = equipments.ID " +
          " WHERE equipmentTypeId = #{equipmentTypeId} AND manufacturer = #{manufacturer} " +
          " AND ((modelId = #{modelId} AND model is null) or (modelId  is null AND model = #{model}))" +
          " GROUP BY equipments.id,equipment_cold_chain_equipments.equipmentid" +
          " ORDER BY name LIMIT 1")
  @Results({
          @Result(
                  property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
          @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
          @Result(
                  property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
          @Result(property = "energyTypeId", column = "energyTypeId"),
          @Result(property = "designation", column = "designationId", javaType = ColdChainEquipmentDesignation.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.ColdChainEquipmentDesignationMapper.getById")),
          @Result(property = "designationId", column = "designationId"),
          @Result(
                  property = "equipmentModel", column = "modelId", javaType = EquipmentModel.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentModelMapper.getById"))


  })
  Equipment getByTypeManufacturerAndModel(@Param("equipmentTypeId")Long equipmentTypeId, @Param("manufacturer") String manufacturer,
                                      @Param("modelId") Long modelId, @Param("model") String model);

  @Select("SELECT COUNT(id) FROM equipments WHERE equipmentTypeId = #{equipmentTypeId} ")
  Integer getCountByType(@Param("equipmentTypeId") Long equipmentTypeId);

  @Select("SELECT equipments.*" +
      "   , COUNT(equipment_inventories.id) AS inventorycount" +
      " FROM equipments" +
      "   LEFT JOIN equipment_inventories ON equipment_inventories.equipmentid = equipments.id" +
      " WHERE equipments.id = #{id}" +
      " GROUP BY equipments.id")
  @Results({
          @Result(
                  property = "equipmentType", column = "equipmentTypeId", javaType = EquipmentType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentTypeMapper.getEquipmentTypeById")),
          @Result(property = "equipmentTypeId", column = "equipmentTypeId"),
          @Result(
                  property = "energyType", column = "energyTypeId", javaType = EquipmentEnergyType.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentEnergyTypeMapper.getById")),
          @Result(property = "energyTypeId", column = "energyTypeId"),
          @Result(
                  property = "equipmentModel", column = "modelId", javaType = EquipmentModel.class,
                  one = @One(select = "org.openlmis.equipment.repository.mapper.EquipmentModelMapper.getById"))

  })
  Equipment getById(@Param("id") Long id);

  @Select("SELECT et.*" +
      " FROM equipment_types et" +
      " JOIN equipment_type_programs etp ON et.id = etp.equipmenttypeid" +
      " JOIN programs p ON etp.programid = p.id" +
      " WHERE p.id = #{programId}")
  List<EquipmentType> getTypesByProgram(@Param("programId") Long programId);

  @Insert("INSERT into equipments (name, equipmentTypeId, createdBy, createdDate, modifiedBy, modifiedDate, manufacturer, model, modelId, energyTypeId) " +
      "values " +
      "(#{name}, #{equipmentType.id}, #{createdBy}, NOW(), #{modifiedBy}, NOW(), #{manufacturer},#{model}, #{equipmentModel.id}, #{energyTypeId})")
  @Options(useGeneratedKeys = true)
  void insert(Equipment equipment);

  @Update("UPDATE equipments " +
      "set " +
      " name = #{name}, equipmentTypeId = #{equipmentType.id}, modifiedBy = #{modifiedBy}, modifiedDate = NOW(), manufacturer = #{manufacturer}, model = #{model}, modelId = #{equipmentModel.id}, energyTypeId = #{energyTypeId} " +
      "WHERE id = #{id}")
  void update(Equipment equipment);


    @Delete("DELETE FROM equipments WHERE id = #{Id}")
    void remove(Long Id);

  @Select("select * from equipment_bio_chemistry_test_types")
  @Results({
          @Result(property = "id", column = "id"),
          @Result(
                  property = "testProducts", column = "id", javaType = List.class,
                  many = @Many(select = "getBioChemistryEquipmentTestProducts"))
  })
  List<NonFunctionalTestTypes> getBioChemistryEquipmentTestTypes();

  @Select("select * from equipment_bio_chemistry_products where testtypeid = #{id}")
  List<NonFunctionalTestProducts> getBioChemistryEquipmentTestProducts(Long id);

  @Select("select * from manual_test_types")
  List<ManualTestTypes> getManualTestTypes();
}
