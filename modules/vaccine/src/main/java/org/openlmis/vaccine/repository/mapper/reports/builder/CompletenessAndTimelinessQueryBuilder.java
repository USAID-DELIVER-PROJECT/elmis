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

package org.openlmis.vaccine.repository.mapper.reports.builder;

import java.util.Date;
import java.util.Map;

public class CompletenessAndTimelinessQueryBuilder {


    public static String  selectCompletenessAndTimelinessMainReportDataByDistrict(Map params){
        Long zone = (Long) params.get("districtId");
        Date startDate   = (Date) params.get("startDate");
        Date endDate     = (Date) params.get("endDate");
        Long productId   = (Long) params.get("productId");

        String sql = "SELECT \n" +
                "region_name, district_name, period_name, target, expected, reported, expected - reported AS late, \n" +
                "trunc((reported::numeric/expected::numeric)*100,2) percent_reported, \n" +
                "trunc(((expected::numeric - reported::numeric)/expected::numeric)*100,2) percent_late, \n" +
                "fixed, outreach, fixed+outreach session_total\n" +
                "  FROM (\n" +
                "       SELECT \n" +
                "                d.region_name, \n" +
                "                d.district_name,\n" +
                "                d.district_id,\n" +
                "                sum(i.denominator) target, \n" +
                "                i.period_name, \n" +
                " count(i.facility_id) reported,\n" +
                " SUM(i.fixed_immunization_session) fixed,\n" +
                " SUM(i.outreach_immunization_session) outreach\n" +
                "                FROM \n" +
                "                    vw_vaccine_coverage i \n" +
                "                JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                "                JOIN vaccine_reports vr ON i.report_id = vr.ID \n" +
                "                JOIN program_products pp ON pp.programid = vr.programid \n" +
                "                AND pp.productid = i.product_id \n" +
                "                JOIN product_categories pg ON pp.productcategoryid = pg.ID \n" +
                "                WHERE \n" +
                "                i.program_id = ( SELECT id FROM programs p WHERE p.enableivdform = TRUE ) \n" +
                "        AND i.period_start_date >= '"+startDate+"' and i.period_end_date <= '"+endDate+"'\n" +
                "                 and i.product_id =  " +productId+
                "\n" +
                                 writeDistrictPredicate(zone)  +
                "                 group by d.region_name, d.district_id, d.district_name, i.period_name, i.period_start_date \n" +
                "                 ORDER BY \n" +
                "                 d.region_name,\n" +
                "                 d.district_name,\n" +
                "                  i.period_start_date\n" +
                "      ) AS coverages\n" +
                "            JOIN (SELECT d.district_id, count(*) expected FROM facilities f\n" +
                "              JOIN vw_districts d ON f.geographiczoneid = d.district_id \n" +
                "              GROUP BY d.district_id\n" +
                "            ) reporting_facilities ON coverages.district_id = reporting_facilities.district_id\n";
        return sql;
    }
    public static String  selectCompletenessAndTimelinessSummaryReportDataByDistrict(Map params){
        Long zone = (Long) params.get("districtId");
        Date startDate   = (Date) params.get("startDate");
        Date endDate     = (Date) params.get("endDate");
        Long productId   = (Long) params.get("productId");

        String sql = "SELECT period_name, \n" +
                "       \"month\", \n" +
                "       \"year\", \n" +
                "       SUM(ontime)                                      ontime, \n" +
                "       Count(*)                                         reported, \n" +
                "       (SELECT Count(*) AS expected \n" +
                "        FROM   facilities f \n" +
                "               join vw_districts d \n" +
                "                 ON f.geographiczoneid = d.district_id WHERE 1=1 "+writeDistrictPredicate(zone)  +" ) AS expected \n" +
                "FROM   (SELECT d.district_id, \n" +
                "               i.period_name, \n" +
                "               i.period_start_date, \n" +
                "               Extract(month FROM i.period_start_date) \"month\", \n" +
                "               Extract(year FROM i.period_start_date)  \"year\", \n" +
                "               vr.id, \n" +
                "               i.facility_id, \n" +
                "               vr.submissiondate, \n" +
                "               CASE \n" +
                "                 WHEN ( Date_part('day', vr.submissiondate :: timestamp - \n" +
                "               i.period_start_date :: timestamp) ) :: NUMERIC <= \n" +
                "               conf.cutoff :: NUMERIC THEN 1 \n" +
                "                 ELSE 0 \n" +
                "               END                                     ontime \n" +
                "        FROM   vw_vaccine_coverage i \n" +
                "               join vw_districts d \n" +
                "                 ON i.geographic_zone_id = d.district_id \n" +
                "               join vaccine_reports vr \n" +
                "                 ON i.report_id = vr.id \n" +
                "               join program_products pp \n" +
                "                 ON pp.programid = vr.programid \n" +
                "                    AND pp.productid = i.product_id \n" +
                "               join product_categories pg \n" +
                "                 ON pp.productcategoryid = pg.id \n" +
                "               join (SELECT value AS cutoff, \n" +
                "                            KEY \n" +
                "                     FROM   configuration_settings) conf \n" +
                "                 ON conf.KEY = 'VACCINE_LATE_REPORTING_DAYS' \n" +
                "        WHERE  i.program_id = (SELECT id \n" +
                "                               FROM   programs p \n" +
                "                               WHERE  p.enableivdform = TRUE) \n" +
                "        AND i.period_start_date >= '"+startDate+"' and i.period_end_date <= '"+endDate+"'\n" +
                "                 and i.product_id =  " +productId+
                writeDistrictPredicate(zone)  +
                "        ORDER  BY i.period_start_date) AS timeliness \n" +
                "GROUP  BY period_name, \n" +
                "          \"month\", \n" +
                "          \"year\" \n" +
                "ORDER  BY \"year\", \n" +
                "          \"month\" ";
        return sql;
    }

    private static String writeDistrictPredicate(Long zone) {

        String predicate = "";
        if (zone != 0 && zone != null) {
            predicate = " AND (district_id = "+zone+" or zone_id = "+zone+" or region_id = "+zone+" or parent = "+zone+")";
        }
        return predicate;
    }
}
