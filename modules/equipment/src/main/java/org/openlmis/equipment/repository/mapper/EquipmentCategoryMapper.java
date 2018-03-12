package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentCategory;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentCategoryMapper {

    @Select("select *  from equipment_category")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "functionalTestTypeIds", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.equipment.repository.mapper.EquipmentFunctionalTestTypesMapper.getByEquipmentCategoryId")),
            @Result(property = "equipmentTypeIds", javaType = List.class, column = "id",
                    many = @Many(select = "getEquipmentTypeByCategoryId")),
    })
    List<EquipmentCategory> getAllEquipmentCategory();

    @Select("select * from equipment_category where id = #{id}")
    EquipmentCategory getEquipmentCategoryById(Long id);

    @Delete("delete from equipment_category where id = #{id}")
    void deleteEquipmentCategory(Long id);

    @Update("update equipment_category set code= #{code} , name= #{name} where id=#{id}")
    void updateEquipmentCategory(EquipmentCategory obj);

    @Insert("insert into equipment_category (code, name) values (#{code} , #{name})")
    void insertEquipmentCategory(EquipmentCategory obj);

    @Select("select id from equipment_types where categoryid = #{id}")
    List<Long> getEquipmentTypeByCategoryId(Long id);

    @Update("update equipment_types set categoryid = #{equipemntCategoryId} where id = #{equipmentTypeId}")
    void associateEquipmentTypes(@Param("equipemntCategoryId") Long equipemntCategoryId, @Param("equipmentTypeId") Long equipmentTypeId);

    @Update("update equipment_types set categoryid = null")
    void resetEquipmentTypecategoryAssociation();
}
