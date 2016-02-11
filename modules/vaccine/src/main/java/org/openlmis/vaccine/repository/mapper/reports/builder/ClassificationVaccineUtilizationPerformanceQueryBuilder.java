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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class ClassificationVaccineUtilizationPerformanceQueryBuilder {


    public static String selectClassficationUtilizationPerformanceForFacility(Map params) {

        String query = "" +
              " select stock.region_name,  \n" +
                "stock.district_name geographic_zone_name,\n" +
                "stock.facility_name,       \n" +
                "to_char(stock.period_start_date, 'Mon YYYY')  period_name ," +
                "stock.period_start_date::date period, \n" +
                "extract( month from stock.period_start_date) month_number,\n" +
                "extract( year from stock.period_start_date) year_number," +
                "cov.target_population population,\n" +
                "cov.vaccinated vaccinated,\n" +
                "stock.used\n" +
                "from (\n" +
                "select    \n" +
                "            facility_id,\n" +
                "            period_start_date::date period_start_date,\n" +
                "            sum(coalesce(denominator,0)) target_population,            \n" +
                "            sum(coalesce(within_outside_total, 0)) vaccinated\n" +
                "            from vw_vaccine_coverage   \n" +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id  \n" +
                writePredicate(params) +
                "            group by 1,2) cov\n" +
                "join (\n" +
                "select      vd.region_name,\n" +
                "            vd.district_id ,\n" +
                "            vd.district_name ,\n" +
                "            facility_id,\n" +
                "            facility_name,            \n" +
                "            period_start_date,\n" +
                "            coalesce(usage_denominator,0)::numeric used \n" +
                "            from vw_vaccine_stock_status  \n" +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id \n" +
                writePredicate(params) +
                "            ) stock\n" +
                "on cov.facility_id = stock.facility_id and cov.period_start_date::date =  stock.period_start_date::date\n" +
                "order by 1,2,3,5\n";

        return query;
    }
    public static String selectClassficationUtilizationPerformanceForDistrict(Map params) {

        String query = " with temp as ( select " +
                "stock.region_name,  \n" +
                "stock.district_name geographic_zone_name,\n" +
                "stock.facility_name,       \n" +
                "to_char(stock.period_start_date, 'Mon YYYY')  period_name ," +
                "stock.period_start_date::date period, \n" +
                "extract( month from stock.period_start_date) month_number,\n" +
                "extract( year from stock.period_start_date) year_number," +
                "cov.target_population population,\n" +
                "cov.vaccinated vaccinated,\n" +
                "stock.used\n" +
                "from (\n" +
                "select    \n" +
                "            facility_id,\n" +
                "            period_start_date::date period_start_date,\n" +
                "            sum(coalesce(denominator,0)) target_population,            \n" +
                "            sum(coalesce(within_outside_total, 0)) vaccinated\n" +
                "            from vw_vaccine_coverage   \n" +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id  \n" +
                writePredicate(params) +
                "            group by 1,2) cov\n" +
                "join (\n" +
                "select      vd.region_name,\n" +
                "            vd.district_id ,\n" +
                "            vd.district_name ,\n" +
                "            facility_id,\n" +
                "            facility_name,            \n" +
                "            period_start_date,\n" +
                "            coalesce(usage_denominator,0)::numeric used \n" +
                "            from vw_vaccine_stock_status  \n" +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id \n" +
                writePredicate(params) +
                "            ) stock\n" +
                "on cov.facility_id = stock.facility_id and cov.period_start_date::date =  stock.period_start_date::date)" +
                " select " +
                "region_name,  " +
                "geographic_zone_name," +
                "period_name ," +
                "period, " +
                "month_number," +
                "year_number," +
                " count(facility_name) facility_count,       " +
                " sum(population) population," +
                " sum(vaccinated) vaccinated," +
                "sum(used) used" +
                " from temp " +
                "group by 1,2,3,4 ,5,6" +
                "order by 1,2,4,5";

        return query;
    }
    public static String selectClassficationUtilizationPerformanceForRegion(Map params) {

        String query = " with temp as ( select " +
                "stock.region_name,  \n" +
                "stock.district_name geographic_zone_name,\n" +
                "stock.facility_name,       \n" +
                "to_char(stock.period_start_date, 'Mon YYYY')  period_name ," +
                "stock.period_start_date::date period, " +
                "extract( month from stock.period_start_date) month_number,\n" +
                "extract( year from stock.period_start_date) year_number," +
                "cov.target_population population,\n" +
                "cov.vaccinated vaccinated,\n" +
                "stock.used\n" +
                "from (\n" +
                "select    \n" +
                "            facility_id,\n" +
                "            period_start_date::date period_start_date,\n" +
                "            sum(coalesce(denominator,0)) target_population,            \n" +
                "            sum(coalesce(within_outside_total, 0)) vaccinated\n" +
                "            from vw_vaccine_coverage   \n" +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id  \n" +
                writePredicate(params) +
                "            group by 1,2) cov\n" +
                "join (\n" +
                "select      vd.region_name,\n" +
                "            vd.district_id ,\n" +
                "            vd.district_name ,\n" +
                "            facility_id,\n" +
                "            facility_name,            \n" +
                "            period_start_date,\n" +
                "            coalesce(usage_denominator,0)::numeric used \n" +
                "            from vw_vaccine_stock_status  \n" +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id \n" +
                writePredicate(params) +
                "            ) stock\n" +
                "on cov.facility_id = stock.facility_id and cov.period_start_date::date =  stock.period_start_date::date)" +
                " select " +
                "region_name,  " +
                "period_name ," +
                "period," +
                " month_number," +
                "year_number, " +
                "count(geographic_zone_name) district_count," +
                " count(facility_name) facility_count,       " +
                " sum(population) population," +
                " sum(vaccinated) vaccinated," +
                "sum(used) used" +
                " from temp " +
                "group by 1,2,3,4,5" +
                "order by 1,3,4,5";

        return query;
    }
    private static String writePredicate(Map params) {
        Long zone = (Long) params.get("zoneId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        Long productId = (Long) params.get("productId");
        String predicate = "            where product_id =" + productId   +
                "            and period_start_date::date >= '" + startDate + "' and " +
                "period_end_date <= '" + endDate +
                "'           and extract(year from period_end_date) = extract(year from '" + startDate + "'::date)             \n" +
                "            and (vd.parent = " + zone + " or vd.district_id = " + zone + " or vd.region_id = " + zone + "or 0=" + zone+" ) ";
        return predicate;
    }

}