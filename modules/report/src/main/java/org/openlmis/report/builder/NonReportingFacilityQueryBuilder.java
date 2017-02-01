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

package org.openlmis.report.builder;

import org.openlmis.report.model.params.NonReportingFacilityParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class NonReportingFacilityQueryBuilder {

  private static final String FILTER_CRITERIA = "filterCriteria";

  public static String getQuery(Map params) {
    NonReportingFacilityParam nonReportingFacilityParam = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
    return getQueryString(nonReportingFacilityParam);
  }

  public static String getReportingFacilities(Map params){
    NonReportingFacilityParam filterParam = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
    BEGIN();
    SELECT_DISTINCT("facilities.code, facilities.name");
    SELECT_DISTINCT("gz.district_name as location");
    SELECT_DISTINCT("ft.name as facilityType");
    SELECT_DISTINCT("'REPORTED' as reportingStatus");
    SELECT_DISTINCT("r.status as rnrStatus");
    SELECT_DISTINCT("r.id as rnrId");
    SELECT_DISTINCT(" (select max(rs.createdDate) from requisition_status_changes rs where rs.rnrid = r.id and rs.status = 'AUTHORIZED') as createdDate");
    FROM("facilities");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
    INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
    INNER_JOIN("programs_supported ps on ps.facilityId = facilities.id and ps.programId = #{filterCriteria.program}");
    INNER_JOIN("processing_periods period on period.id = #{filterCriteria.period}");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and ps.programId = rgps.programId");
    INNER_JOIN("requisitions r on r.facilityId = facilities.id and r.programId = ps.programId and r.periodId = period.id and r.emergency = false");
    WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast( #{filterCriteria.program} as int4) )");
    WHERE("facilities.id in (select r.facilityId from requisitions r where r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.emergency = false and r.periodId = cast (#{filterCriteria.period} as int4) and r.programId = cast( #{filterCriteria.program} as int4) )");
    writePredicates(filterParam);
    ORDER_BY("name");

    return SQL();
  }

  private static String getQueryString(NonReportingFacilityParam filterParam) {
    BEGIN();
    SELECT_DISTINCT("facilities.code, facilities.name");
    SELECT_DISTINCT("gz.district_name as location");
    SELECT_DISTINCT("ft.name as facilityType");
    SELECT_DISTINCT("'NON_REPORTING' as reportingStatus");
    FROM("facilities");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
    INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.programId = #{filterCriteria.program}");
    INNER_JOIN("processing_periods period on period.id = #{filterCriteria.period}");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
    WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast( #{filterCriteria.program} as int4) )");
    WHERE("facilities.id not in (select r.facilityid from requisitions r where r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.emergency = false and r.periodid = cast (#{filterCriteria.period} as int4) and r.programid = cast( #{filterCriteria.program} as int4) )");
    writePredicates(filterParam);
    ORDER_BY("name");
    return SQL();
  }

  private static void writePredicates(NonReportingFacilityParam filterParams) {

    WHERE(programIsFilteredBy("ps.programId"));
    if (filterParams.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("gz"));
    }

    if (filterParams.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facilities.typeId"));
    }

  }



  public static String getTotalFacilities(Map params) {
    NonReportingFacilityParam filterParams = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);

    BEGIN();
    SELECT("COUNT (distinct facilities.id)");
    FROM("facilities")
    ;
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.active = true and ps.programId = #{filterCriteria.program}");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid and rgps.scheduleid = (select max(scheduleid) from processing_periods where processing_periods.id =  #{filterCriteria.period})");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    writePredicates(filterParams);
    return SQL();
  }

  public static String getTotalNonReportingFacilities(Map params) {

    NonReportingFacilityParam filterParams = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
    BEGIN();
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.active = true and ps.programId = #{filterCriteria.program}");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and ps.programid = rgps.programid");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.periodid = cast( #{filterCriteria.period} as int4) and r.programid = cast(#{filterCriteria.program} as int4) )");
    writePredicates(filterParams);
    return SQL();
  }

  public static String getSummaryQuery(Map params) {
    NonReportingFacilityParam filterParams = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);

    BEGIN();
    SELECT("'Non Reporting Facilities' AS name");
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.programId = #{filterCriteria.program} and ps.active = true");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("processing_periods period on period.id = #{filterCriteria.period}");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    //ONLY consider facilities that are currently active and support the program in the selected period
    //IF facility is inactive, consider the facility expected to report until it was deactivated. The reason why we have to do this is because (deactivation date is not currently enforced.)
    WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
    WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.periodid = cast( #{filterCriteria.period} as int4) and r.programid = cast(#{filterCriteria.program} as int4) )");
    writePredicates(filterParams);

    String query = SQL();
    RESET();
    BEGIN();
    SELECT("'Facilities required to report for this program' AS name");
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.active = true and ps.programId = #{filterCriteria.program}");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("processing_periods period on period.id = #{filterCriteria.period}");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid ");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
    writePredicates(filterParams);
    query += " UNION " + SQL();
    return query;
  }
}
