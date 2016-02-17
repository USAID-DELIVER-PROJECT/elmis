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

public class CategorizationVaccineUtilizationPerformanceQueryBuilder {


    public String selectCategorizationUtilizationPerformanceForFacility(Map params) {

        return "   select    " +
                "            vd.region_name," +
                "            vd.district_name geographic_zone_name," +
                "            facility_id," +
                "            facility_name," +
                "            to_char(period_start_date, 'Mon YYYY')  period_name ," +
                "            extract( month from period_start_date) month_number," +
                "            extract( year from period_start_date) year_number," +
                "            period_start_date::date period_start_date," +
                "            sum(coalesce(denominator,0)) population,            " +
                "            sum(coalesce(within_outside_total, 0)) vaccinated," +
                "            sum(bcg_1) bcg_1," +
                "            sum(mr_1) mr_1," +
                "            sum(dtp_1) dtp_1," +
                "            sum(dtp_3) dtp_3 " +
                "           from vw_vaccine_dropout   " +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id  " +
                writePredicate(params) +
                "            group by 1,2,3,4,5,6,7,8" +
                "            order by 1,2,4,8";


    }

    public String selectCategorizationUtilizationPerformanceForDistrict(Map params) {

        return "with temp as (" +
                "select    " +
                "            vd.region_name," +
                "            vd.district_name geographic_zone_name," +
                "            facility_id," +
                "            facility_name," +
                "            to_char(period_start_date, 'Mon YYYY')  period_name ," +
                "            extract( month from period_start_date) month_number," +
                "            extract( year from period_start_date) year_number," +
                "            period_start_date::date period_start_date," +
                "            sum(coalesce(denominator,0)) population,            " +
                "            sum(coalesce(within_outside_total, 0)) vaccinated," +
                "            sum(bcg_1) bcg_1," +
                "            sum(mr_1) mr_1," +
                "            sum(dtp_1) dtp_1," +
                "            sum(dtp_3) dtp_3 " +
                "           from vw_vaccine_dropout   " +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id  " +
                writePredicate(params) +
                "            group by 1,2,3,4,5,6,7,8)" +
                "            select " +
                "            region_name," +
                "            geographic_zone_name,             " +
                "             period_name ," +
                "            month_number," +
                "             year_number," +
                "             period_start_date," +
                "            count(facility_name) facility_count," +
                "            sum(population) population,            " +
                "            sum(vaccinated) vaccinated," +
                "            sum(bcg_1) bcg_1," +
                "            sum(mr_1) mr_1," +
                "            sum(dtp_1) dtp_1," +
                "            sum(dtp_3) dtp_3" +
                "            from temp" +
                "            group by 1,2,3,4,5,6" +
                "            order by 1,2,6";
    }

    public String selectCategorizationUtilizationPerformanceForRegion(Map params) {

        return "   with temp as (" +
                "select    " +
                "            vd.region_name," +
                "            vd.district_name geographic_zone_name," +
                "            facility_id," +
                "            facility_name," +
                "            to_char(period_start_date, 'Mon YYYY')  period_name ," +
                "            extract( month from period_start_date) month_number," +
                "            extract( year from period_start_date) year_number," +
                "            period_start_date::date period_start_date," +
                "            sum(coalesce(denominator,0)) population,            " +
                "            sum(coalesce(within_outside_total, 0)) vaccinated," +
                "            sum(bcg_1) bcg_1," +
                "            sum(mr_1) mr_1," +
                "            sum(dtp_1) dtp_1," +
                "            sum(dtp_3) dtp_3 " +
                "           from vw_vaccine_dropout   " +
                "            inner join vw_districts vd on vd.district_id = geographic_zone_id  " +
                writePredicate(params) +
                "            group by 1,2,3,4,5,6,7,8)" +
                "            select " +
                "            region_name,                      " +
                "             period_name ," +
                "            month_number," +
                "             year_number," +
                "             period_start_date," +
                "             count( geographic_zone_name) district_count,  " +
                "            count(facility_name) facility_count," +
                "            sum(population) population,            " +
                "            sum(vaccinated) vaccinated," +
                "            sum(bcg_1) bcg_1," +
                "            sum(mr_1) mr_1," +
                "            sum(dtp_1) dtp_1," +
                "            sum(dtp_3) dtp_3" +
                "            from temp" +
                "            group by 1,2,3,4,5" +
                "            order by 1,5";
    }

    private String writePredicate(Map params) {
        Long zone = (Long) params.get("zoneId");
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        Long productId = (Long) params.get("productId");
        String predicate = "                  where product_id =" + productId +
                "                  and period_start_date::date >= '" + startDate + "      ' and " +
                "      period_end_date::date <= '" + endDate + "      '" +
                "                  and (vd.parent = " + zone + "       or vd.district_id = " + zone + "       or vd.region_id = " + zone + "      or 0=" + zone + "       ) ";
        return predicate;
    }

}