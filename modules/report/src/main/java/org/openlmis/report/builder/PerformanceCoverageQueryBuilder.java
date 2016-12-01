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


import java.util.Date;
import java.util.Map;

public class PerformanceCoverageQueryBuilder {

    private static String sql;

    public static String  selectPerformanceCoverageMainReportDataByRegionAggregate(Map params){

        sql = "SELECT\n" +
                "d.region_name,\n" +
                "sum(i.denominator) target,\n" +
                "i.period_name,\n" +
                "sum(i.within_outside_total) vaccinated,\n" +
                "round((case when sum(denominator) > 0 then (sum(i.within_outside_total) / sum(denominator)::numeric) else 0 end) * 100,2) coverage,\n" +
                "extract(month from i.period_start_date) \"month\",\n" +
                "extract(year from i.period_start_date) \"year\"" +
                "FROM\n" +
                "  vw_vaccine_coverage i\n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
                "JOIN program_products pp ON pp.programid = vr.programid\n" +
                "AND pp.productid = i.product_id\n" +
                "JOIN product_categories pg ON pp.productcategoryid = pg.ID\n" +
                "WHERE\n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p.enableivdform = TRUE )\n" +
                " AND "+
                writePredicate(params)+
                " group by d.region_name, i.period_name, i.period_start_date\n" +
                " ORDER BY\n" +
                " d.region_name,\n" +
                "i.period_start_date";

        return sql;
    }

    public static String selectPerformanceCoverageSummaryReportDataByRegionAggregate(Map params){

         sql = "SELECT  row_number() over (order by period_start_date nulls last) \"row\",\n" +
                "                                        period_name period,  \n" +
                "                                        coverageGroup \"group\" ,  \n" +
                "                                        COUNT(coverageGroup) total,  \n" +
                "                                        extract(month from period_start_date) \"month\",\n" +
                "                                        extract(year from period_start_date)  \"year\"\n" +
                "                                FROM    ( SELECT    period_start_date ,  \n" +
                "                                                    period_name ,  \n" +
                "                                                    CASE WHEN coverage > 90 THEN 'G1'  \n" +
                "                                                         WHEN coverage >= 80 THEN 'G2'  \n" +
                "                                                         ELSE 'G4'  \n" +
                "                                                    END coverageGroup  \n" +
                "                                          FROM      ( SELECT    i.period_start_date ,  \n" +
                "                                                                i.period_name ,  \n" +
                "                                                                d.region_id, \n" +
                "                                                                trunc(( CASE WHEN SUM(denominator) > 0  \n" +
                "                                                                             THEN ( Sum(i.within_outside_total)  \n" +
                "                                                                                    / Sum(denominator::numeric) )  \n" +
                "                                                                             ELSE 0  \n" +
                "                                                                        END ) * 100, 2) coverage  \n" +
                "                                                      FROM      vw_vaccine_coverage i  \n" +
                "                                                                JOIN vw_districts d ON i.geographic_zone_id = d.district_id  \n" +
                "                                                                JOIN vaccine_reports vr ON i.report_id = vr.ID  \n" +
                "                                                                JOIN program_products pp ON pp.programid = vr.programid  \n" +
                "                                                                                            AND pp.productid = i.product_id  \n" +
                "                                                                JOIN product_categories pg ON pp.productcategoryid = pg.ID  \n" +
                "                                                      WHERE     i.program_id = ( SELECT id  \n" +
                "                                                                                 FROM   programs p  \n" +
                "                                                                                 WHERE  p.enableivdform = TRUE  \n" +
                "                                                                               )  \n" +
                "                                                         AND                   "+
                                                                                    writePredicate(params)+
                "                                                GROUP BY  d.region_id, i.period_start_date, i.period_name\n" +
                "                                                ORDER BY  i.period_start_date  \n" +
                "                                                    ) a  \n" +
                "                                        ) b  \n" +
                "                                GROUP BY period_name , period_start_date , coverageGroup  \n" +
                "                                ORDER BY period_start_date ASC";

        return sql;
    }

    public static String selectPerformanceCoverageMainReportDataByRegion(Map params){

          sql = "SELECT\n" +
                "d.district_id,\n" +
                "d.region_name,\n" +
                "d.district_name,\n" +
                "sum(i.denominator) target,\n" +
                "i.period_name,\n" +
                "sum(i.within_outside_total) vaccinated,\n" +
                "extract(month from i.period_start_date) \"month\",\n" +
                "extract(year from i.period_start_date) \"year\"," +
                "round((case when sum(denominator) > 0 then (sum(i.within_outside_total) / sum(denominator)::numeric) else 0 end) * 100,2) coverage\n" +
                "FROM\n" +
                "  vw_vaccine_coverage i\n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
                "JOIN program_products pp ON pp.programid = vr.programid\n" +
                "AND pp.productid = i.product_id\n" +
                "JOIN product_categories pg ON pp.productcategoryid = pg.ID\n" +
                "WHERE\n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p.enableivdform = TRUE )\n" +
                " AND "+
                writePredicate(params)+
                "group by d.region_name,d.district_name, d.district_id, i.period_name, i.period_start_date\n" +
                "ORDER BY\n" +
                "d.region_name,\n" +
                "d.district_name,\n" +
                "i.period_start_date";

        return sql;
    }

    public static String selectPerformanceCoverageSummaryReportDataByRegion(Map params){

         sql = "SELECT  row_number() over (order by period_start_date nulls last) \"row\",\n" +
                "                        period_name period, \n" +
                "                        coverageGroup \"group\" , \n" +
                "                        COUNT(coverageGroup) total, \n" +
                "                        extract(month from period_start_date) \"month\",\n" +
                "                        extract(year from period_start_date)  \"year\"\n" +
                "                FROM    ( SELECT    period_start_date , \n" +
                "                                    period_name , \n" +
                "                                    CASE WHEN coverage > 90 THEN 'G1' \n" +
                "                                         WHEN coverage >= 80 THEN 'G2' \n" +
                "                                         ELSE 'G4' \n" +
                "                                    END coverageGroup \n" +
                "                          FROM      ( SELECT    i.period_start_date , \n" +
                "                                                i.period_name , \n" +
                "                                                d.district_id,\n" +
                "                                                trunc(( CASE WHEN SUM(denominator) > 0 \n" +
                "                                                             THEN ( Sum(i.within_outside_total) \n" +
                "                                                                    / Sum(denominator::numeric) ) \n" +
                "                                                             ELSE 0 \n" +
                "                                                        END ) * 100, 2) coverage \n" +
                "                                      FROM      vw_vaccine_coverage i \n" +
                "                                                JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                "                                                JOIN vaccine_reports vr ON i.report_id = vr.ID \n" +
                "                                                JOIN program_products pp ON pp.programid = vr.programid \n" +
                "                                                                            AND pp.productid = i.product_id \n" +
                "                                                JOIN product_categories pg ON pp.productcategoryid = pg.ID \n" +
                "                                      WHERE     i.program_id = ( SELECT id \n" +
                "                                                                 FROM   programs p \n" +
                "                                                                 WHERE  p.enableivdform = TRUE \n" +
                "                                                               ) \n" +
                "                           AND "+
                                writePredicate(params)
                +"                                GROUP BY  d.district_id, i.period_name, i.period_start_date " +
                "                                ORDER BY  i.period_start_date \n" +
                "                                    ) a \n" +
                "                        ) b \n" +
                "                GROUP BY period_name , period_start_date , coverageGroup \n" +
                "                ORDER BY period_start_date ASC";

        return sql;
    }

    public static String selectPerformanceCoverageMainReportDataByDistrict(Map params){

         sql = "SELECT\n" +
                "d.district_id,\n" +
                 "d.region_name,\n" +
                 "d.district_name,\n" +
                 "sum(i.denominator) target,\n" +
                 "i.facility_name,   \n" +
                 "i.period_name,\n" +
                 "sum(i.within_outside_total) vaccinated,\n" +
                 "extract(month from i.period_start_date) \"month\",\n" +
                 "extract(year from i.period_start_date) \"year\",\n" +
                 "round((case when sum(i.denominator) > 0 then (sum(i.within_outside_total) / sum(i.denominator)::numeric) else 0 end) * 100,2) coverage" +
                " FROM\n" +
                "  vw_vaccine_coverage i\n" +
                " JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                " JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
                " JOIN program_products pp ON pp.programid = vr.programid\n" +
                " AND pp.productid = i.product_id\n" +
                " JOIN product_categories pg ON pp.productcategoryid = pg.ID\n" +
                " WHERE\n" +
                " i.program_id = ( SELECT id FROM programs p WHERE p.enableivdform = TRUE )\n" +
                " AND "+
                writePredicate(params)
                +" GROUP BY d.district_id, d.region_name, d.district_name, i.facility_name,i.period_name,\"month\",\"year\",i.period_start_date" +
                 " ORDER BY\n" +
                "d.region_name,\n" +
                "d.district_name, "+
                "i.facility_name,\n" +
                "i.period_start_date";
        return sql;
    }

    public static String selectPerformanceCoverageSummaryReportDataByDistrict(Map params){

         sql =
                " SELECT  row_number() over (order by period_start_date nulls last) \"row\",\n" +
                "        period_name period,\n" +
                "        coverageGroup \"group\" ,\n" +
                "        COUNT(coverageGroup) total,\n" +
                "        extract(month from period_start_date) \"month\",\n" +
                "        extract(year from period_start_date)  \"year\"\n" +
                "FROM    ( SELECT    period_start_date ,\n" +
                "                    period_name ,\n" +
                "                    CASE WHEN coverage > 90 THEN 'G1'\n" +
                "                         WHEN coverage >= 80 THEN 'G2'\n" +
                "                         ELSE 'G4'\n" +
                "                    END coverageGroup\n" +
                "          FROM      ( SELECT    i.period_start_date ,\n" +
                "                                i.period_name ,\n" +
                "                                trunc(( CASE WHEN denominator > 0\n" +
                "                                             THEN ( i.within_outside_total\n" +
                "                                                    / denominator::numeric )\n" +
                "                                             ELSE 0\n" +
                "                                        END ) * 100, 2) coverage\n" +
                "                      FROM      vw_vaccine_coverage i\n" +
                "                                JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "                                JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
                "                                JOIN program_products pp ON pp.programid = vr.programid\n" +
                "                                                            AND pp.productid = i.product_id\n" +
                "                                JOIN product_categories pg ON pp.productcategoryid = pg.ID\n" +
                "                      WHERE     i.program_id = ( SELECT id\n" +
                "                                                 FROM   programs p\n" +
                "                                                 WHERE  p.enableivdform = TRUE\n" +
                "                                               )\n" +
                "                        AND "+
                        writePredicate(params)
                +"                      ORDER BY  i.period_start_date\n" +
                "                    ) a\n" +
                "        ) b\n" +
                "GROUP BY period_name ,\n" +
                "        period_start_date ,\n" +
                "        coverageGroup\n" +
                "ORDER BY period_start_date ASC";

        return sql;

    }

    private static String writePredicate(Map params) {

        Long zone = (Long) params.get("districtId");
        Date startDate   = (Date) params.get("startDate");
        Date endDate     = (Date) params.get("endDate");
        Long productId   = (Long) params.get("productId");
        Long doseId = (Long) params.get("doseId");
        Long userId = (Long) params.get("userId");

        String predicate = "";
        predicate = " geographic_zone_id in (select district_id from vw_user_facilities where user_id ="+userId+" and program_id = fn_get_vaccine_program_id())  ";
        predicate += " AND  i.period_start_date::date >= '"+startDate+"' and i.period_end_date::date <= '"+endDate+"'\n";
        predicate +=" AND  i.product_id = " +productId +" ";

        if (zone != 0 && zone != null) {
            predicate += " AND (district_id = "+zone+" or zone_id = "+zone+" or region_id = "+zone+" or parent = "+zone+")";
        }


            predicate += " AND dose_id = "+ doseId;

        return predicate;
    }

}
