/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.vaccine.repository.mapper.reports.builder;


import java.util.Date;
import java.util.Map;

public class ClassificationVaccineUtilizationPerformanceQueryBuilder {

    public static String  selectClassificationVaccineUtilizationPerformanceByZone(Map params) {

        Long zoneId = (Long) params.get("zoneId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        Long productId = (Long) params.get("productId");


        String sql = "" +
                "with temp as (\n" +
                "select\n" +
                "geographic_zone_name,\n" +
                "geographic_zone_id,\n" +
                "period_name,\n" +
                "period_start_date,\n" +
                "facility_name,\n" +
                "usage_denominator\n" +
                "from\n" +
                " vw_vaccine_class\n" +
                " where\n" +
                "product_id = "+productId+" \n" +
                " and period_start_date >= '"+startDate+"' \n" +
                " and period_start_date <= '"+endDate+"' \n" +
                ") \n" +
                " select \n" +
                "  vd.region_name, \n" +
                "  t.geographic_zone_name, \n" +
                "  t.period_name, \n" +
                "  period_start_date, \n" +
                "  count(t.facility_name) facility_count, \n" +
                "  \n" +
                "    case when sum(t.usage_denominator) between 1 \n" +
                "    and 1999 then 'Class_A' when sum(t.usage_denominator) between 2000 \n" +
                "    and 3999 then 'Class_B' when sum(t.usage_denominator) between 4000 \n" +
                "    and 4999 then 'Class_C' else 'Class_D' end\n" +
                "  classification \n" +
                " from \n" +
                "  temp t \n" +
                "  join vw_districts vd on t.geographic_zone_id = vd.district_id \n"
                + writePredicate(zoneId) +
                " group by \n" +
                "  1, \n" +
                "  2, \n" +
                "  3, \n" +
                "  4 \n" +
                "order by \n" +
                "  geographic_zone_name, \n" +
                "  period_start_date";



        return sql;

    }



    private static String writePredicate(Long zoneId) {

        String predicate = "";
        if (zoneId != 0 && zoneId != null) {

            predicate = " where (vd.district_id = "+zoneId+" or vd.region_id = "+zoneId+" or vd.parent = "+zoneId+")";

        }
        return predicate;
    }
}