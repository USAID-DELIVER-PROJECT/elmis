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

package org.openlmis.ivdform.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.ivdform.domain.reports.DiseaseLineItem;
import org.openlmis.ivdform.domain.reports.VaccineReport;
import org.openlmis.ivdform.dto.ReportStatusDTO;
import org.openlmis.ivdform.dto.RoutineReportDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IvdFormMapper {

  @Insert("INSERT into vaccine_reports (periodId, programId, facilityId, status, supervisoryNodeId, majorImmunizationActivities, fixedImmunizationSessions, outreachImmunizationSessions,outreachImmunizationSessionsCanceled, submissionDate, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values (#{periodId}, #{programId}, #{facilityId}, #{status}, #{supervisoryNodeId}, #{majorImmunizationActivities}, #{fixedImmunizationSessions}, #{outreachImmunizationSessions}, #{outreachImmunizationSessionsCanceled}, #{submissionDate}, #{createdBy}, NOW(), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  Integer insert(VaccineReport report);

  @Select("SELECT * from vaccine_reports where id = #{id}")
  VaccineReport getById(@Param("id") Long id);

  @Select("SELECT * from vaccine_reports where facilityId = #{facilityId} and programId = #{programId} and periodId = #{periodId}")
  VaccineReport getByPeriodFacilityProgram(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId, @Param("programId") Long programId);

  @Select("SELECT * from vaccine_reports where id = #{id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "facilityId", column = "facilityId"),
      @Result(property = "periodId", column = "periodId"),
      @Result(property = "programId", column = "programId"),
      @Result(property = "logisticsLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.LogisticsLineItemMapper.getLineItems")),
      @Result(property = "coverageLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.CoverageMapper.getLineItems")),
      @Result(property = "adverseEffectLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.AdverseEffectMapper.getLineItems")),
      @Result(property = "columnTemplate", javaType = List.class, column = "programId",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.LogisticsColumnTemplateMapper.getForProgram")),
      @Result(property = "coldChainLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.ColdChainMapper.getLineItems")),
      @Result(property = "campaignLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.CampaignLineItemMapper.getLineItems")),
      @Result(property = "diseaseLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.DiseaseLineItemMapper.getLineItems")),
      @Result(property = "vitaminSupplementationLineItems", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.VitaminSupplementationLineItemMapper.getLineItems")),
      @Result(property = "reportStatusChanges", javaType = List.class, column = "id",
          many = @Many(select = "org.openlmis.ivdform.repository.mapper.reports.StatusChangeMapper.getChangeLogByReportId")),
      @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId",
          many = @Many(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById")),
      @Result(property = "facility", javaType = Facility.class, column = "facilityId",
          many = @Many(select = "org.openlmis.core.repository.mapper.FacilityMapper.getById"))
  })
  VaccineReport getByIdWithFullDetails(@Param("id") Long id);

  @Update("UPDATE vaccine_reports" +
      " set" +
      " periodId = #{periodId}, " +
      " programId = #{programId}, " +
      " facilityId = #{facilityId}, " +
      " status = #{status}, " +
      " supervisoryNodeId = #{supervisoryNodeId}, " +
      " majorImmunizationActivities = #{majorImmunizationActivities}, " +
      " fixedImmunizationSessions = #{fixedImmunizationSessions}, " +
      " outreachImmunizationSessions = #{outreachImmunizationSessions}, " +
      " outreachImmunizationSessionsCanceled = #{outreachImmunizationSessionsCanceled}, " +
      " submissionDate = #{submissionDate}, " +
      " modifiedBy = #{modifiedBy}, " +
      " modifiedDate = NOW() " +
      "where id = #{id}")
  void update(VaccineReport report);

  @Select("select max(s.scheduleId) id from requisition_group_program_schedules s " +
      " join requisition_group_members m " +
      "     on m.requisitionGroupId = s.requisitionGroupId " +
      " where " +
      "   s.programId = #{programId} " +
      "   and m.facilityId = #{facilityId} ")
  Long getScheduleFor(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("select * from vaccine_reports " +
      "   where " +
      "     facilityId = #{facilityId} and programId = #{programId} order by id desc limit 1")
  @Results(value = {
    @Result(property = "periodId", column = "periodId"),
    @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId", one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById"))
  })
  VaccineReport getLastReport(@Param("facilityId") Long facilityId, @Param("programId") Long programId);


  @Select("select * from vaccine_reports " +
    "   where " +
    "     facilityId = #{facilityId} and programId = #{programId} and status = 'REJECTED'" +
    "order by id desc")
  @Results(value = {
    @Result(property = "periodId", column = "periodId"),
    @Result(property = "period", javaType = ProcessingPeriod.class, column = "periodId", one = @One(select = "org.openlmis.core.repository.mapper.ProcessingPeriodMapper.getById"))
  })
  List<VaccineReport> getRejectedReports(@Param("facilityId") Long facilityId, @Param("programId") Long programId);


  @Select("select r.id, p.name as periodName, r.facilityId, r.status, r.programId " +
      " from vaccine_reports r " +
      "   join processing_periods p on p.id = r.periodId " +
      " where r.facilityId = #{facilityId} and r.programId = #{programId}" +
      " order by p.startDate desc")
  List<ReportStatusDTO> getReportedPeriodsForFacility(@Param("facilityId") Long facilityId, @Param("programId") Long programId);

  @Select("Select id from vaccine_reports where facilityid = #{facilityId} and periodid = #{periodId}")
  Long getReportIdForFacilityAndPeriod(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

  @Select("select COALESCE(cases, 0) as cases, COALESCE(death, 0) as death, COALESCE(cum_cases, 0) as cumulative, disease_name as diseaseName \n" +
      "from vw_vaccine_disease_surveillance \n" +
      "where report_id = #{reportId}")
  List<DiseaseLineItem> getDiseaseSurveillance(@Param("reportId") Long reportId);

  @Select("SELECT id FROM vaccine_reports " +
      "WHERE " +
      "periodId < #{periodId} " +
      "AND facilityId = #{facilityId} " +
      "AND programId = #{programId} " +
      "ORDER BY " +
      "periodId DESC limit 1")
  Long findPreviousReport(@Param("facilityId") Long facilityId, @Param("programId") Long programId, @Param("periodId") Long periodId);


  @Select("SELECT " +
    "   r.id, f.name as facilityName, f.code as facilityCode, z.name as districtName, r.status, r.submissionDate, p.startDate periodStartDate, p.endDate periodEndDate, p.name periodName " +
    "from vaccine_reports r " +
    "join processing_periods p on p.id = r.periodId " +
    "join facilities f on f.id = r.facilityId " +
    "join geographic_zones z on z.id = f.geographicZoneId " +
    "where " +
    "r.status = 'SUBMITTED' " +
    "and facilityId = ANY( #{facilityIds}::INT[] )")
  List<RoutineReportDTO> getApprovalPendingReports(@Param("facilityIds") String facilityIds);
}

