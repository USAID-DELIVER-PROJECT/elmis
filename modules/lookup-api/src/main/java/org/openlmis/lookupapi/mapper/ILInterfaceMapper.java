package org.openlmis.lookupapi.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.lookupapi.model.HealthFacilityDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface ILInterfaceMapper {

    @Insert("INSERT INTO public.hfr_facilities(\n" +
            "             commfacname, council, createdat, district, facidnumber, facilitytype, \n" +
            "            facilitytypegroup, latitude, longitude, name, oschangeclosedtooperational, \n" +
            "            oschangeopenedtoclose, operatingstatus, ownership, ownershipgroup, \n" +
            "            postorupdate, region, registrationstatus, updatedat, villagemtaa, \n" +
            "            ward, zone,IlIDNumber)\n" +
            "    VALUES ( #{commFacName}, #{council}, #{createdAt}, #{district}, #{facIDNumber}, #{facilityType}, \n" +
            "            #{facilityTypeGroup},CAST(#{latitude} as double precision), CAST(#{longitude} as double precision), #{name}, #{oSchangeClosedtoOperational}, \n" +
            "            #{oSchangeOpenedtoClose}, #{operatingStatus}, #{ownership}, #{ownershipGroup}, \n" +
            "            #{postorUpdate}, #{region}, #{registrationStatus}, #{updatedAt}, #{villageMtaa}, \n" +
            "            #{ward}, #{zone},#{IlIDNumber}); ")
    @Options(useGeneratedKeys = true)
    Integer insert(HealthFacilityDTO dto);

    @Update("UPDATE public.hfr_facilities\n" +
            "   SET  commfacname=#{commFacName}, council=#{council}, createdat=#{createdAt}, district=#{district},facidNumber=#{facIDNumber}, \n" +
            "       facilitytype=#{facilityType}, facilitytypegroup= #{facilityTypeGroup}, latitude=CAST(#{latitude} as double precision), longitude=CAST(#{longitude} AS double precision), \n" +
            "       name=#{name}, oschangeclosedtooperational=#{oSchangeClosedtoOperational}, oschangeopenedtoclose=#{oSchangeOpenedtoClose}, \n" +
            "       operatingstatus=#{operatingStatus}, ownership=#{ownership}, ownershipgroup=#{ownershipGroup}, postorupdate=#{postorUpdate}, \n" +
            "       region=#{region}, registrationstatus= #{registrationStatus}, updatedat=#{updatedAt}, villagemtaa=#{villageMtaa}, ward=#{ward}, \n" +
            "       zone=#{zone}\n" +
            " WHERE IlIDNumber= #{IlIDNumber} ;")
    void update(HealthFacilityDTO dto);

    @Select("select * from hfr_facilities WHERE IlIDNumber = #{IlIDNumber} limit 1")
    HealthFacilityDTO getByTransactionId(@Param("IlIDNumber") String IlIDNumber);

    @Select("select * from hfr_facilities where id = #{id} limit 1  ")
    HealthFacilityDTO getById(@Param("id") Integer id);

    @Select("select * from hfr_facilities where facIDNumber = #{facIDNumber} limit 1")
    HealthFacilityDTO getByFacilityCode(@Param("facIDNumber") String facIDNumber);
}
