package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;

import org.openlmis.equipment.domain.EquipmentFunctionalTestTypes;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentFunctionalTestTypesMapper {

    @Select("select types.*, cat.name equipmentCategoryName  from equipment_functional_test_types types " +
            "join equipment_category cat ON cat.id = types.equipmentcategoryid order by cat.name, types.name ")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "equipmentTestItemIds", javaType = List.class, column = "id",
                    many = @Many(select = "org.openlmis.equipment.repository.mapper.EquipmentTestItemsMapper.getEquipmentTestItemIdsByTestTypeId")),
    })
    List<EquipmentFunctionalTestTypes> getAllEquipmentFunctionalTestTypes();

    @Select("select * from equipment_functional_test_types where id = #{id}")
    EquipmentFunctionalTestTypes getEquipmentFunctionalTestTypesById(Long id);

    @Delete("delete from equipment_functional_test_types where id = #{id}")
    void deleteEquipmentFunctionalTestTypes(Long id);

    @Update("update equipment_functional_test_types set code= #{code} , name= #{name} , equipmentcategoryid= #{equipmentcategoryid} where id=#{id}")
    void updateEquipmentFunctionalTestTypes(EquipmentFunctionalTestTypes obj);

    @Insert("insert into equipment_functional_test_types (code, name, equipmentcategoryid) values (#{code} , #{name} , #{equipmentcategoryid})")
    void insertEquipmentFunctionalTestTypes(EquipmentFunctionalTestTypes obj);

    @Select("select id from equipment_functional_test_types where equipmentcategoryid = #{id}")
    List<Long> getByEquipmentCategoryId(Long id);

}