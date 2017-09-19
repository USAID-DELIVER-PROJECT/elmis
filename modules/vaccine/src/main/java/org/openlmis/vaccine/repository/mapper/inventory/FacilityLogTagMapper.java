package org.openlmis.vaccine.repository.mapper.inventory;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.vaccine.domain.inventory.FacilityTemperatureLogTag;
import org.openlmis.vaccine.dto.LogTagDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by hassan on 8/29/17.
 */

@Repository
public interface FacilityLogTagMapper {

    @Select("select * from log_tag_facility_mappings")
    @Results(value = {
            @Result(property = "facility", column = "facilityId", javaType = Long.class,
                    one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
     List<FacilityTemperatureLogTag> getAll();

    @Select("SELECT * FROM log_tag_facility_mappings where id=#{id} ")
    @Results(value = {
    @Result(property = "facility", column = "facilityId", javaType = Long.class,
            one = @One(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))})
    FacilityTemperatureLogTag getById(@Param("id")Long id);

    @Insert("INSERT INTO log_tag_facility_mappings(\n" +
            "             facilityid, serialNumber,description, createddate)\n" +
            "    VALUES (#{facilityId}, #{serialNumber},#{description}, NOW());\n ")
    @Options(useGeneratedKeys = true)
    Integer insert(FacilityTemperatureLogTag logTag);

    @Update("UPDATE log_tag_facility_mappings\n" +
            "   SET facilityid=#{facilityId}, serialNumber=#{serialNumber}, createdDate=NOW()," +
            " description = #{description}\n" +
            " WHERE id = #{id};")
    void update(FacilityTemperatureLogTag logTag);

    @Select("SELECT * FROM facilities WHERE id = #{id}")
    public Facility getFacilityById(Long id);

    @Insert(" INSERT INTO log_tags(\n" +
            "            logdate, logtime, temperature, facilityId,route, createddate)\n" +
            "    VALUES ( #{logDate}, #{logTime}, #{temperature}, #{facilityId},#{route}, NOW()) ")
    void insertLogData(LogTagDTO logTagDTO);

    @Update("UPDATE log_tags\n" +
            "   SET  logdate=#{logDate}, logtime=#{logTime}, temperature=#{temperature}, facilityId=#{facilityId}\n" +
            " WHERE id = #{id};")
    void updateLogTag(LogTagDTO dto);

    @Select("select * from log_tags ")
    LogTagDTO  getLogTag();

    @Update("UPDATE log_tags\n" +
            "   SET facilityId=#{id} ,route=#{route}\n" +
            " WHERE facilityId is null ")
    void updateLastUploaded(@Param("id")Long id, Date currentTimestamp,@Param("route") String route);
}
