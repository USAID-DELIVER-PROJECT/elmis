package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.equipment.domain.EquipmentTestItems;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentTestItemsMapper {

    @Select("select items.*, type.name as functionalTestTypeName from equipment_test_items items join equipment_functional_test_types type on items.functionaltesttypeid = type.id order by type.name, items.name")
    List<EquipmentTestItems> getAllEquipmentTestItems();

    @Select("select * from equipment_test_items where id = #{id}")
    EquipmentTestItems getEquipmentTestItemsById(Long id);

    @Delete("delete from equipment_test_items where id = #{id}")
    void deleteEquipmentTestItems(Long id);

    @Update("update equipment_test_items set code= #{code} , name= #{name} , displayorder= #{displayorder} , functionaltesttypeid= #{functionaltesttypeid} where id=#{id}")
    void updateEquipmentTestItems(EquipmentTestItems obj);

    @Insert("insert into equipment_test_items (code, name, displayorder, functionaltesttypeid) values (#{code} , #{name} , #{displayorder} , #{functionaltesttypeid})")
    void insertEquipmentTestItems(EquipmentTestItems obj);


    @Select("select id from equipment_test_items where functionaltesttypeid = #{id}")
    List<Long> getEquipmentTestItemIdsByTestTypeId(Long id);

}
