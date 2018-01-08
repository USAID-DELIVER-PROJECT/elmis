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

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.equipment.domain.DailyColdTraceStatus;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DailyColdTraceStatusMapper {

  @Insert("insert into equipment_daily_cold_trace_status " +
      "(" +
      " serialNumber, equipmentInventoryId, date, operationalStatusId, " +
      " minEpisodeTemp, maxEpisodeTemp, lowTemp, highTemp, " +
      " remarks, createdBy, createdDate" +
      ")" +
      "values " +
      "(" +
      " #{serialNumber}, #{equipmentInventory.id}, #{date}, #{operationalStatusId}, " +
      " #{minEpisodeTemp}, #{maxEpisodeTemp}, #{lowTemp}, #{highTemp} , " +
      " #{remarks}, #{createdBy}, NOW()" +
      ")")
  int insert(DailyColdTraceStatus status);

  @Insert("UPDATE equipment_daily_cold_trace_status SET " +
      "serialNumber = #{serialNumber}, " +
      "equipmentInventoryId = #{equipmentInventory.id}, " +
      "date = #{date}, " +
      "operationalStatusId = #{operationalStatusId}, " +
      "minEpisodeTemp = #{minEpisodeTemp}, " +
      "maxEpisodeTemp = #{maxEpisodeTemp}, " +
      "lowTemp = #{lowTemp}, " +
      "highTemp = #{highTemp}, " +
      "remarks = #{remarks}, " +
      "modifiedBy = #{modifiedBy}," +
      "modifiedDate = NOW()" +
      " WHERE id = #{id}")
  int update(DailyColdTraceStatus status);

  @Select("select * from equipment_daily_cold_trace_status where equipmentInventoryId = #{equipmentId} and date = #{date}")
  DailyColdTraceStatus getForEquipmentForDate(@Param("equipmentId") Long equipmentId, @Param("date") Date date);

  @Select("select s.* from equipment_daily_cold_trace_status s " +
      " JOIN equipment_inventories i on i.id = s.equipmentInventoryId " +
      " JOIN processing_periods p on p.id = #{periodId} and s.date >= p.startDate and s.date <= p.endDate " +
      " where i.facilityId = #{facilityId}")
  List<DailyColdTraceStatus> getForFacilityForPeriod(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);


}
