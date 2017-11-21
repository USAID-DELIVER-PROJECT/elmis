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

    public static String getReportingFacilities(Map params) {
        NonReportingFacilityParam filterParam = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
        BEGIN();
        SELECT_DISTINCT("facilities.code, facilities.name");
        SELECT_DISTINCT("gz.district_name as location");
        SELECT_DISTINCT("gz.region_name as province");
        SELECT_DISTINCT("p.name as program");
        SELECT_DISTINCT("period.name period");
        SELECT_DISTINCT("ft.name as facilityType");
        SELECT_DISTINCT("'REPORTED' as reportingStatus");
        SELECT_DISTINCT("r.status as rnrStatus");
        SELECT_DISTINCT("r.id as rnrId");
        SELECT_DISTINCT("extract('epoch' from period.startdate)::bigint  AS epoch");
        SELECT_DISTINCT(" (select max(rs.createdDate) from requisition_status_changes rs where rs.rnrid = r.id and rs.status = 'AUTHORIZED') as createdDate");
        FROM("facilities");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
        INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
        INNER_JOIN("programs_supported ps on ps.facilityId = facilities.id and ps.programId = #{filterCriteria.program}");
        INNER_JOIN("programs p on p.id = ps.programId");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and ps.programId = rgps.programId");
        INNER_JOIN("processing_periods period on period.scheduleid = rgps.scheduleid ");
        INNER_JOIN("requisitions r on r.facilityId = facilities.id and r.programId = ps.programId and r.periodId = period.id and r.emergency = false");
        LEFT_OUTER_JOIN(" facility_owners fo on fo.facilityid=facilities.id");
        WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
        WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast( #{filterCriteria.program} as int4) )");
        WHERE("facilities.id in " +
                "( select r.facilityId from requisitions r " +
                "where " +
                "r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') " +
                "and r.emergency = false " +
                "and r.periodId  =any(#{filterCriteria.periodString}::int[]) " +
                "and r.programId = cast( #{filterCriteria.program} as int4) " +
                ")");
        WHERE("period.id = any(#{filterCriteria.periodString}::int[])");
        writePredicates(filterParam);
        ORDER_BY("name");
        String sqlStatment = SQL();
        return sqlStatment;
    }

    public static String getFacilitiesWithByReportingStatus(Map params) {
        NonReportingFacilityParam filterParam = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
        BEGIN();
        BEGIN();
        SELECT_DISTINCT("facilities.code, facilities.name");
        SELECT_DISTINCT("gz.district_name as location");
        SELECT_DISTINCT("gz.region_name as province");
        SELECT_DISTINCT("p.name as program");
        SELECT_DISTINCT("period.name period");
        SELECT_DISTINCT("ft.name as facilityType");
        SELECT_DISTINCT("extract('epoch' from period.startdate)::bigint  AS epoch");
        SELECT_DISTINCT("case when r.id is null then 'NON_REPORTING' else 'REPORTED' end as reportingStatus ");
        FROM("facilities");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
        INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.programId = #{filterCriteria.program}");
        INNER_JOIN("programs p on p.id = ps.programId");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        INNER_JOIN("processing_periods period on period.scheduleid = rgps.scheduleid ");
        LEFT_OUTER_JOIN(" requisitions r on r.facilityId = facilities.id and r.programId = ps.programId and r.periodId = period.id " +
                "and r.emergency = false AND r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') ");
        LEFT_OUTER_JOIN(" facility_owners fo on fo.facilityid=facilities.id");
        WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) " +
                "or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
        WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast( #{filterCriteria.program} as int4) )");
        WHERE("period.id = any(#{filterCriteria.periodString}::int[])");
        writePredicates(filterParam);
        ORDER_BY("name");
        String query = SQL();
        return query;
    }

    public static String getPeriodsTicksForChart(Map params){
        BEGIN();
        SELECT("name");
        SELECT("rank() OVER (ORDER BY startdate ASC)");
        FROM("processing_periods");
        WHERE("id = any(#{filterCriteria.periodString}::int[])");
        return SQL();
    }
    private static String getQueryString(NonReportingFacilityParam filterParam) {
        BEGIN();
        SELECT_DISTINCT("facilities.code, facilities.name");
        SELECT_DISTINCT("gz.district_name as location");
        SELECT_DISTINCT("gz.region_name as province");
        SELECT_DISTINCT("p.name as program");
        SELECT_DISTINCT("period.name period");
        SELECT_DISTINCT("ft.name as facilityType");
        SELECT_DISTINCT("'NON_REPORTING' as reportingStatus");
        SELECT_DISTINCT("extract('epoch' from period.startdate)::bigint  AS epoch");
        FROM("facilities");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
        INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id and ps.programId = #{filterCriteria.program}");
        INNER_JOIN("programs p on p.id = ps.programId");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        INNER_JOIN("processing_periods period on period.scheduleid = rgps.scheduleid ");
        LEFT_OUTER_JOIN(" requisitions r on r.facilityId = facilities.id and r.programId = ps.programId and r.periodId = period.id " +
                "and r.emergency = false AND r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') ");
        LEFT_OUTER_JOIN(" facility_owners fo on fo.facilityid=facilities.id");
        WHERE("( (facilities.active = true and ps.active = true and ps.startDate <= period.startDate) or (facilities.active = false and period.startDate <= facilities.modifiedDate) " +
                "or (facilities.active = true and ps.active = false and ps.modifiedDate >= period.startDate and ps.startDate <= period.startDate) )");
        WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast( #{filterCriteria.program} as int4) )");
        WHERE("period.id = any(#{filterCriteria.periodString}::int[])");
        WHERE(" r.id is null");
        writePredicates(filterParam);
        ORDER_BY("name");
        String query = SQL();
        return query;
    }

    private static void writePredicates(NonReportingFacilityParam filterParams) {

        WHERE(programIsFilteredBy("ps.programId"));
        if (filterParams.getZone() != 0) {
            WHERE(geoZoneIsFilteredBy("gz"));
        }

        if (filterParams.getFacilityType() != 0) {
            WHERE(facilityTypeIsFilteredBy("facilities.typeId"));
        }
        if (filterParams.getFacilityOwner() != 0) {
            WHERE(facilityOwnerIdFilteredBy("fo.ownerid"));
        }
    }

    public static String getPeriodListQueryString(NonReportingFacilityParam filterParam) {

        String queryString="SELECT  id from processing_periods p\n" +
                "WHERE p.startdate >=(select pp.startdate FROM  processing_periods pp where pp.id= #{filterCriteria.period})\n" +
                "and  p.startdate <=(select pp.startdate FROM  processing_periods pp where pp.id= #{filterCriteria.periodEnd})";;
        return queryString;
    }
}
