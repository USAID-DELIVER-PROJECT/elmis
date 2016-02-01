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

import org.openlmis.report.model.params.DistrictSummaryReportParam;

import java.util.Map;


public class DistrictFinancialSummaryQueryBuilder {

    public static String getQuery(Map params) {

        DistrictSummaryReportParam filter = (DistrictSummaryReportParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");

        return "  WITH temp as (select facilitycode,facility,facilitytype,region,\n" +
                "  coalesce (sum(fullsupplyitemssubmittedcost)+sum(nonfullsupplyitemssubmittedcost),0) totalCost  \n" +
                "  from vw_district_financial_summary \n" +
                 " join vw_districts gz on gz.district_id = vw_district_financial_summary.zoneId \n"+
                   writePredicates(filter, userId) +
                "  Group by region,facilitycode,facility,facilitytype order by region)   \n" +
                "  select t.facilitycode,t.facility,t.facilitytype ,totalcost ,t.region                            \n" +
                "  from temp t INNER JOIN (select region from temp GROUP BY region order by region) temp2 ON \n" +
                "  t.region= temp2.region\n" +
                "  where totalcost > 0 ";
    }

    private static String writePredicates(DistrictSummaryReportParam filter, Long userId) {
        String predicate = "";

        if (filter != null) {

            predicate = "where periodId =  " + filter.getPeriod() + " and ";
            predicate = predicate + " facility_Id in (select facility_id from vw_user_facilities where user_id = " + userId + " and program_id = " + filter.getProgram() + ")";
            predicate = predicate + " and status in ('IN_APPROVAL','APPROVED','RELEASED') ";

            if (filter.getZone() != 0) {
                predicate = predicate + " and ( zone_id = " + filter.getZone() + " or parent = " + filter.getZone() + " or region_id = " + filter.getZone() + " or district_id = " + filter.getZone() + ") ";
            }
            if (filter.getSchedule() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " scheduleId= #{filterCriteria.scheduleId}";
            }

            if (filter.getProgram() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " programId = #{filterCriteria.programId}";
            }
        }
        return predicate;
    }
}
