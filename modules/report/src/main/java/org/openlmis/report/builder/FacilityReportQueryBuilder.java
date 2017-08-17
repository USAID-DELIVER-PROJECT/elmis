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

import org.openlmis.report.model.params.FacilityReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class FacilityReportQueryBuilder {

    public static String getExportQuery(Map params) {

        FacilityReportParam filter = (FacilityReportParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        String reportType = filter.getStatusList() != null && !filter.getStatusList().isEmpty() ?
                filter.getStatusList().replaceAll(",", "','").replaceAll("AC", "t").replaceAll("IN", "f") : "f";
        BEGIN();
        SELECT("DISTINCT F.id, F.code, F.name, F.active as active, " +
                "FT.name as facilityType, GZ.district_name as district, GZ.region_name as province," +
                "FO.code as owner," +
                "F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates," +
                "F.mainphone as phoneNumber," +
                " F.fax as fax ," +
                "ps.id supportprogramid,ps.active activeprogram,ps.startdate,  p.id programid, p.code programcode, p.name programname");
        FROM("facilities F");
        JOIN("facility_types FT on FT.id = F.typeid");
        LEFT_OUTER_JOIN("programs_supported ps on ps.facilityid = F.id");
      LEFT_OUTER_JOIN("programs p on ps.programid=p.id");
        LEFT_OUTER_JOIN("vw_districts GZ on GZ.district_id = F.geographiczoneid");
        LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");
        LEFT_OUTER_JOIN("facility_owners FS on FS.facilityid = F.id");
        WHERE("F.geographicZoneId in (select distinct district_id from vw_user_facilities where user_id = " + userId + " )");
        WHERE(facilityStatusFilteredBy("F.active", reportType));
        if (filter.getZone() != 0) {
            WHERE("( F.geographicZoneId = #{filterCriteria.zone} or GZ.region_id = #{filterCriteria.zone} or GZ.zone_id = #{filterCriteria.zone} or GZ.parent = #{filterCriteria.zone} ) ");
        }
        if (filter.getFacilityType() != 0) {
            WHERE(facilityTypeIsFilteredBy("F.typeId"));
        }
        if (filter.getPeriodStart() != null && !filter.getPeriodStart().trim().isEmpty()) {
            WHERE(startDateFilteredBy("ps.startdate", filter.getPeriodStart().trim()));
        }
        if (filter.getPeriodEnd() != null && !filter.getPeriodEnd().trim().isEmpty()) {
            WHERE(endDateFilteredBy("ps.startdate", filter.getPeriodEnd().trim()));
        }
        if (filter.getFacilityOwner() != 0) {
            WHERE(facilityOwnerIsFilteredBy("FS.ownerid"));
        }
        if (filter.getProgram() != 0) {
            WHERE(programIsFilteredBy("ps.programId"));
            WHERE("F.id in (select facility_id from vw_user_facilities" +
                    " where user_id = cast( #{userId} as int4) and program_id = cast(#{filterCriteria.program} as int4))");
            WHERE("F.id in (select m.facilityid from requisition_group_members m where m.requisitionGroupId in (select rpgs.requisitionGroupId from requisition_group_program_schedules rpgs where rpgs.programId = #{filterCriteria.program}) )");
            WHERE("ps.active = true");
        }
        String query = SQL();
        return query;
    }
  public static String getQuery(Map params) {

    FacilityReportParam filter1 = (FacilityReportParam) params.get("filterCriteria");
    Long userId = (Long) params.get("userId");
    String reportType = filter1.getStatusList() != null && !filter1.getStatusList().isEmpty() ?
            filter1.getStatusList().replaceAll(",", "','").replaceAll("AC", "t").replaceAll("IN", "f") : "f";
    BEGIN();
    SELECT("DISTINCT F.id, F.code, F.name, F.active as active, FT.name as facilityType, GZ.district_name as region, FO.code as owner,F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates,F.mainphone as phoneNumber, F.fax as fax ");
    FROM("facilities F");
    JOIN("facility_types FT on FT.id = F.typeid");
    LEFT_OUTER_JOIN("programs_supported ps on ps.facilityid = F.id");
    LEFT_OUTER_JOIN("vw_districts GZ on GZ.district_id = F.geographiczoneid");
    LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");
    LEFT_OUTER_JOIN("facility_owners FS on FS.facilityid = F.id");
    WHERE("F.geographicZoneId in (select distinct district_id from vw_user_facilities where user_id = " + userId + " )");
    WHERE(facilityStatusFilteredBy("F.active", reportType));
    if (filter1.getZone() != 0) {
      WHERE("( F.geographicZoneId = #{filterCriteria.zone} or GZ.region_id = #{filterCriteria.zone} or GZ.zone_id = #{filterCriteria.zone} or GZ.parent = #{filterCriteria.zone} ) ");
    }
    if (filter1.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("F.typeId"));
    }
    if (filter1.getPeriodStart() != null && !filter1.getPeriodStart().trim().isEmpty()) {
      WHERE(startDateFilteredBy("ps.startdate", filter1.getPeriodStart().trim()));
    }
    if (filter1.getPeriodEnd() != null && !filter1.getPeriodEnd().trim().isEmpty()) {
      WHERE(endDateFilteredBy("ps.startdate", filter1.getPeriodEnd().trim()));
    }
    if (filter1.getFacilityOwner() != 0) {
      WHERE(facilityOwnerIsFilteredBy("FS.ownerid"));
    }
    if (filter1.getProgram() != 0) {
      WHERE(programIsFilteredBy("ps.programId"));
      WHERE("F.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast(#{filterCriteria.program} as int4))");
      WHERE("F.id in (select m.facilityid from requisition_group_members m where m.requisitionGroupId in (select rpgs.requisitionGroupId from requisition_group_program_schedules rpgs where rpgs.programId = #{filterCriteria.program}) )");
      WHERE("ps.active = true");
    }
    String query = SQL();
    return query;
  }
    public static String getProgramSupportedQuery(Map params) {
    /*
    select ps.id,ps.active,ps.startdate,
  p.id programid, p.code, p.name
from programs_supported ps
INNER JOIN programs p on ps.programid=p.id
where ps.facilityid=#{filterCriteria.facilityId} and ps.active=true
     */
        FacilityReportParam filter = (FacilityReportParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        BEGIN();
        SELECT("ps.id,ps.active,ps.startdate,  p.id programid, p.code, p.name");
        FROM("programs_supported ps");
        INNER_JOIN("programs p on ps.programid=p.id");
        WHERE("ps.active=true");
        if (filter.getPeriodStart() != null && !filter.getPeriodStart().trim().isEmpty()) {
            WHERE(startDateFilteredBy("ps.startdate", filter.getPeriodStart().trim()));
        }
        if (filter.getPeriodEnd() != null && !filter.getPeriodEnd().trim().isEmpty()) {
            WHERE(endDateFilteredBy("ps.startdate", filter.getPeriodEnd().trim()));
        }
        WHERE(facilityIsFilteredBy(" ps.facilityid"));
        String query = SQL();
        return query;

    }
}
