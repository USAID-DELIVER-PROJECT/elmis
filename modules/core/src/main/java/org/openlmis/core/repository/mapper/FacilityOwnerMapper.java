/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */


package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOwner;
import org.openlmis.core.domain.Owner;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FacilityOwnerMapper {
    @Insert("INSERT INTO facility_owners(\n" +
            "             facilityid, ownerid, createdby, createddate, modifiedby, \n" +
            "            modifieddate, description,active)\n" +
            "    VALUES ( #{facility}, #{owner.id}, #{createdBy}, #{createdDate}, #{modifiedBy}, \n" +
            "            #{modifiedDate}, #{description},#{active});\n")
    @Options(useGeneratedKeys = true)
     Long insert(FacilityOwner facilityOwner);
    @Delete("DELETE FROM facility_owners\n" +
            " WHERE  facilityid= #{id};")
     Long deleteOwners(Facility facility);
    @Select("select * FROM facility_owners fo " +
            " inner join facilities f on f.id =fo.facilityid" +
            " inner join facility_operators fop on fop.id=fo.ownerid \n" +
            " WHERE  facilityid= #{id};")
    @Results(value = {@Result(property = "owner.id", column = "ownerid"),
            @Result(property = "owner.code", column = "code"),
            @Result(property = "owner.text", column = "text")})


    List<FacilityOwner> loadFacilityOwners(Facility facility);
    @Select("SELECT * FROM owners ORDER BY displayOrder")
    List<Owner> allOwners();
}
