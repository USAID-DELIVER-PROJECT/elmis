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

    public String getVaccineProducts() {
        return "      select p.id, coalesce(p.primaryname,'') as name, p.code, pp.productcategoryid as categoryid,  " +
                "          CASE WHEN p.tracer = true THEN 'Indicator Product' ELSE 'Regular' END tracer " +
                "             from products p " +
                "       join program_products pp on p.id = pp.productid " +
                "       join product_categories pc on pp.productcategoryid = pc.id " +
                "       where  pc.code = (select value from configuration_settings where key = 'VACCINE_REPORT_VACCINE_CATEGORY_CODE') " +
                "       and pp.active = true " +
                "           order by pp.displayorder ";
    }

    public String selectClassficationUtilizationPerformanceForFacility(Map params) {

        return "       select stock.region_name,  " +
                "      stock.district_name geographic_zone_name," +
                "      stock.facility_name,       " +
                "      to_char(stock.period_start_date, 'Mon YYYY')  period_name ," +
                "      stock.period_start_date::date period, " +
                "      extract( month from stock.period_start_date) month_number," +
                "      extract( year from stock.period_start_date) year_number," +
                "      cov.target_population population," +
                "      cov.vaccinated vaccinated," +
                "      stock.used" +
                "      from (" +
                "      select    " +
                "                  facility_id," +
                "                  period_start_date::date period_start_date," +
                "                  sum(coalesce(denominator,0)) target_population,            " +
                "                  sum(coalesce(within_outside_total, 0)) vaccinated" +
                "                  from vw_vaccine_coverage   " +
                "                  inner join vw_districts vd on vd.district_id = geographic_zone_id " +
                "    " +
                writePredicate(params) +
                "                  group by 1,2) cov" +
                "      join (" +
                "      select      vd.region_name," +
                "                  vd.district_id ," +
                "                  vd.district_name ," +
                "                  facility_id," +
                "                  facility_name,            " +
                "                  period_start_date," +
                "                  coalesce(usage_denominator,0)::numeric used " +
                "                  from vw_vaccine_stock_status  " +
                "                  inner join vw_districts vd on vd.district_id = geographic_zone_id " +
                writePredicate(params) +
                "                  ) stock" +
                "      on cov.facility_id = stock.facility_id and cov.period_start_date::date =  stock.period_start_date::date" +
                "      order by 1,2,3,5";


    }

    public String selectClassficationUtilizationPerformanceForDistrict(Map params) {

        return "       with temp as ( select " +
                "      stock.region_name,  " +
                "      stock.district_name geographic_zone_name," +
                "      stock.facility_name,       " +
                "      to_char(stock.period_start_date, 'Mon YYYY')  period_name ," +
                "      stock.period_start_date::date period, " +
                "      extract( month from stock.period_start_date) month_number," +
                "      extract( year from stock.period_start_date) year_number," +
                "      cov.target_population population," +
                "      cov.vaccinated vaccinated," +
                "      stock.used" +
                "      from (" +
                "      select    " +
                "                  facility_id," +
                "                  period_start_date::date period_start_date," +
                "                  sum(coalesce(denominator,0)) target_population,            " +
                "                  sum(coalesce(within_outside_total, 0)) vaccinated" +
                "                  from vw_vaccine_coverage   " +
                "                  inner join vw_districts vd on vd.district_id = geographic_zone_id  " +
                writePredicate(params) +
                "                  group by 1,2) cov" +
                "      join (" +
                "      select      vd.region_name," +
                "                  vd.district_id ," +
                "                  vd.district_name ," +
                "                  facility_id," +
                "                  facility_name,            " +
                "                  period_start_date," +
                "                  coalesce(usage_denominator,0)::numeric used " +
                "                  from vw_vaccine_stock_status  " +
                "                  inner join vw_districts vd on vd.district_id = geographic_zone_id " +
                writePredicate(params) +
                "                  ) stock" +
                "      on cov.facility_id = stock.facility_id and cov.period_start_date::date =  stock.period_start_date::date)" +
                "       select " +
                "      region_name,  " +
                "      geographic_zone_name," +
                "      period_name ," +
                "      period, " +
                "      month_number," +
                "      year_number," +
                "       count(facility_name) facility_count,       " +
                "       sum(population) population," +
                "       sum(vaccinated) vaccinated," +
                "      sum(used) used" +
                "       from temp " +
                "      group by 1,2,3,4 ,5,6" +
                "      order by 1,2,4,5";
    }

    public String selectClassficationUtilizationPerformanceForRegion(Map params) {

        return "       with temp as ( select " +
                "      stock.region_name,  " +
                "      stock.district_name geographic_zone_name," +
                "      stock.facility_name,       " +
                "      to_char(stock.period_start_date, 'Mon YYYY')  period_name ," +
                "      stock.period_start_date::date period, " +
                "      extract( month from stock.period_start_date) month_number," +
                "      extract( year from stock.period_start_date) year_number," +
                "      cov.target_population population," +
                "      cov.vaccinated vaccinated," +
                "       stock.used" +
                "       from (" +
                "       select    " +
                "                  facility_id," +
                "                  period_start_date::date period_start_date," +
                "                  sum(coalesce(denominator,0)) target_population,            " +
                "                  sum(coalesce(within_outside_total, 0)) vaccinated" +
                "                  from vw_vaccine_coverage   " +
                "                  inner join vw_districts vd on vd.district_id = geographic_zone_id  " +
                writePredicate(params) +
                "                  group by 1,2) cov" +
                "       join (" +
                "       select      vd.region_name," +
                "                  vd.district_id ," +
                "                  vd.district_name ," +
                "                  facility_id," +
                "                  facility_name,            " +
                "                  period_start_date," +
                "                  coalesce(usage_denominator,0)::numeric used " +
                "                  from vw_vaccine_stock_status  " +
                "                  inner join vw_districts vd on vd.district_id = geographic_zone_id " +
                writePredicate(params) +
                "                  ) stock" +
                "      on cov.facility_id = stock.facility_id and cov.period_start_date::date =  stock.period_start_date::date)" +
                "       select " +
                "      region_name,  " +
                "      period_name ," +
                "      period," +
                "       month_number," +
                "      year_number, " +
                "      count(geographic_zone_name) district_count," +
                "       count(facility_name) facility_count,       " +
                "       sum(population) population," +
                "       sum(vaccinated) vaccinated," +
                "      sum(used) used" +
                "       from temp " +
                "      group by 1,2,3,4,5" +
                "      order by 1,3,4,5";
    }

    //////////////////
    public String getFacilityPopulationInformation(Map params) {
        Long productId = (Long) params.get("productId");
        Long zone = (Long) params.get("zoneId");
        return " select year, region_name, district_name, facility_name, " +
                " denominator, population from vw_vaccine_population_denominator vd " +
                " where programid = 82 and " +
                " productid = " + productId +
                "  and (vd.district_id = " + zone + "       or vd.region_id = " + zone + "      or 0=" + zone + "       ) ";


    }
        public String getDistrictPopulationInformation(Map params) {
                Long productId = (Long) params.get("productId");
                Long zone = (Long) params.get("zoneId");
                return " select year, region_name, district_name, " +
                        "coalesce(sum(denominator),0) denominator, " +
                        "coalesce(sum(population),0) population \n" +
                        "from vw_vaccine_population_denominator vd " +
                        " where programid = 82 and " +
                        " productid = " + productId +
                        "  and (vd.district_id = " + zone + "       or vd.region_id = " + zone + "      or 0=" + zone + "       ) " +
                        " group by 1,2,3";


        }
        public String getRegionPopulationInformation(Map params) {
                Long productId = (Long) params.get("productId");
                Long zone = (Long) params.get("zoneId");
                return " select year, region_name,  " +
                        "coalesce(sum(denominator),0) denominator, " +
                        "coalesce(sum(population),0) population \n" +
                        "from vw_vaccine_population_denominator vd " +
                        " where programid = 82 and " +
                        " productid = " + productId +
                        "  and ( vd.district_id = " + zone + "       or vd.region_id = " + zone + "      or 0=" + zone + "       ) " +
                        " group by 1,2";


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

        public String getYearQuery(){
                return  "select distinct extract (year from pr.startdate) id,  extract (year from pr.startdate) year_value from vaccine_reports r\n" +
                        "join processing_periods pr on r.periodid = pr.id order by year_value DESC";
        }

}