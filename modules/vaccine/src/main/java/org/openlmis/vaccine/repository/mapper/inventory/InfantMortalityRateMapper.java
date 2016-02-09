package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.vaccine.dto.InfantMortalityRateDTO;
import org.springframework.stereotype.Repository;


@Repository
public interface InfantMortalityRateMapper {

    @Select(" select * from district_infant_mortality_rates imr  " +
            " join geographic_zones gz on imr.districtId = gz.id  " +
            " where gz.code = #{code} ")
    @Results({@Result(property = "geographicZone", column = "districtId", javaType = GeographicZone.class,
            one = @One(select = "org.openlmis.core.repository.mapper.GeographicZoneMapper.getWithParentById"))})
    InfantMortalityRateDTO getByCode(@Param("code") String code);


    @Select(" update district_infant_mortality_rates set value = #{value} where id = #{id} ")
    void update(InfantMortalityRateDTO mortalityRateDTO);


    @Insert(" INSERT INTO district_infant_mortality_rates (districtId, value)  " +
            " VALUES(#{districtId}, #{value})  ")
    @Options(useGeneratedKeys = true)
    Integer insert(InfantMortalityRateDTO mortalityRateDTO);

}
