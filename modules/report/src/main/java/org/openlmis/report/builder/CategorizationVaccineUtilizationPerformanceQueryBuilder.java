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

package org.openlmis.report.builder;


import org.openlmis.report.model.params.CategorizationVaccineUtilizationPerformanceReportParam;

import java.util.Date;
import java.util.Map;

public class CategorizationVaccineUtilizationPerformanceQueryBuilder {


    public String getDistrictReport(Map map){

        CategorizationVaccineUtilizationPerformanceReportParam params  = (CategorizationVaccineUtilizationPerformanceReportParam)map.get("filterCriteria");

        String sql =
                getDroupOutWithDemographicEstimatesQuery(map)
                        +
                ",vaccine_dropout_with_faility_count as (\n" +
                        "select " +
                        "    vd.region_id, vd.district_id, region_name, district_name, period_id, period_name , month_number, year_number, period_start_date,\n" +
                        "    demographics.population, demographics.denominator, facility_count, startdate, sum(vaccinated) vaccinated, sum(bcg_1) bcg_1, sum(mr_1) mr_1, sum(dtp_1) dtp_1, sum(dtp_3) dtp_3\n" +
                        "from vaccine_dropout vd\n" +
                        "    JOIN (\n" +
                                "select region_name rname, district_name dname, count(distinct facility_name) facility_count\n" +
                                "from vaccine_dropout group by 1, 2\n" +
                        "    ) as fcount ON fcount.dname = vd.district_name\n" +
                        "    JOIN (\n" +
                                "select vd.region_id, vd.district_id, coalesce(sum(denominator),0) denominator,\n" +
                                "coalesce(sum(population),0) population\n" +
                                "from vw_vaccine_population_denominator vd\n" +
                                "join vw_districts d ON vd.district_id = d.district_id\n" +
                                "where programid = fn_get_vaccine_program_id()\n" +
                                "and (productid = "+ params.getProduct()+")\n" +
                                "and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                                "and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "+params.getDistrict()+" or d.parent = "+params.getDistrict()+")\n" +
                                "group by 1,2\n" +
                                "order by 2,1\n" +
                        "    ) demographics on demographics.district_id = vd.district_id\n" +
                        "    \n" +
                        "    group by 1,2,3,4,5,6,7,8,9,10,11,12,13\n" +
                        "    order by 1,5\n" +
                " )\n" +

                ", progressive_total as (\n" +
                    "select\n" +
                        " *,\n" +
                        " (select case when population_tot = 0 then 0 \n" +
                        "      when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V001' then bcg_1_tot / (population_tot * 0.1) \n" +
                        "      else  dtp_1_tot / (population_tot * 0.1) end) as coverage_rate,\n" +
                        " \n" +
                        " (select case when dtp_1_tot = 0 then 0 \n" +
                        "       when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V001' and bcg_1_tot <> 0 then ((bcg_1_tot - mr_1_tot) / bcg_1_tot) * 100 \n" +
                        "       when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V010' and dtp_1_tot <> 0 then ((dtp_1_tot - dtp_3_tot) / dtp_1_tot) * 100 \n" +
                        "       else 0 end) as dropout_rate \n" +
                        " from  (\n" +
                        " select *,\n" +
                            "  sum(population) OVER (PARTITION BY district_id order by district_id, month_number) population_tot,\n" +
                            "  sum(bcg_1) OVER (PARTITION BY district_id order by district_id, month_number) bcg_1_tot,\n" +
                            "  sum(mr_1) OVER (PARTITION BY district_id order by district_id, month_number) mr_1_tot, \n" +
                            "  sum(dtp_1) OVER (PARTITION BY district_id order by district_id, month_number) dtp_1_tot,\n" +
                            "  sum(dtp_3) OVER (PARTITION BY district_id order by district_id, month_number) dtp_3_tot,\n" +
                            "  coverage min_coverage, dropout min_dropout, wastage\n" +
                        " from \n" +
                        " vaccine_dropout_with_faility_count\n" +
                        " join ( \n" +
                            "  select coalesce(whoratio,0) coverage,  coalesce(dropout,0) dropout , coalesce(wastagefactor,0) wastage\n" +
                            "  from program_products pp  \n" +
                            "  join isa_coefficients c on pp.isacoefficientsid = c.id \n" +
                            "  join vaccine_inventory_product_configurations pc on pc.productid=pp.productid\n" +
                            "  where pp.productid = "+ params.getProduct()+" limit 1\n" +
                        " ) as isa_coffecients ON 1=1\n" +
                        " order by region_name, district_name, period_start_date\n" +
                    ") temp\n" +
                ")\n" +

                "select \n" +
                        "  region_name regionName,\n" +
                        "  district_name districtName,\n" +
                        "  period_name periodName, \n" +
                        "  startdate startDate,\n" +
                        " max(facility_count) over (partition by district_id) facilityCount, \n" +
                        "  coalesce(population,0) population,\n" +
                        "  case \n" +
                        "  when coverage_rate is null then '' \n" +
                        "  when coverage_rate >= min_coverage and dropout_rate <= min_dropout then 'Cat_1'\n" +
                        "  when coverage_rate < min_coverage and dropout_rate <= min_dropout then 'Cat_3'\n" +
                        "  when coverage_rate >= min_coverage and dropout_rate > min_dropout then 'Cat_2'\n" +
                        "  else 'Cat_4' \n" +
                        "  end as classification\n" +
                " from  progressive_total";

            return sql;
    }

    public String getRegionReport(Map map){

        CategorizationVaccineUtilizationPerformanceReportParam params  = (CategorizationVaccineUtilizationPerformanceReportParam)map.get("filterCriteria");
        
        String sql =
                getDroupOutWithDemographicEstimatesQuery(map)
                        +
                ",vaccine_dropout_with_faility_count as (\n" +
                        "select \n" +
                            "vd.region_id, region_name, period_id, period_name , month_number, year_number, period_start_date,\n" +
                            "demographics.population, demographics.denominator, facility_count, startdate, sum(vaccinated) vaccinated, sum(bcg_1) bcg_1, sum(mr_1) mr_1, sum(dtp_1) dtp_1, sum(dtp_3) dtp_3\n" +
                        "from \n" +
                            "vaccine_dropout vd\n" +
                            "JOIN (\n" +
                                "select region_name rname, count(distinct facility_name) facility_count\n" +
                                "from vaccine_dropout group by 1\n" +
                            ") as fcount ON fcount.rname = vd.region_name\n" +
                            "JOIN (\n" +
                                "select vd.region_id, coalesce(sum(denominator),0) denominator,\n" +
                                "coalesce(sum(population),0) population\n" +
                                "from vw_vaccine_population_denominator vd\n" +
                                "join vw_districts d ON vd.district_id = d.district_id\n" +
                                "where programid = fn_get_vaccine_program_id()\n" +
                                "and (productid = "+ params.getProduct()+")\n" +
                                "and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                                "and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "+params.getDistrict()+" or d.parent = "+params.getDistrict()+")\n" +
                                "group by 1\n" +
                                "order by 2,1\n" +
                            ") demographics on demographics.region_id = vd.region_id\n" +
                        "group by 1,2,3,4,5,6,7,8,9,10,11\n" +
                        "order by 1,5\n" +
                ")" +

                ", progressive_total as (\n" +
                        "select *,\n" +
                        " (select case when population_tot = 0 then 0 \n" +
                        "      when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V001' then bcg_1_tot / (population_tot * 0.1) \n" +
                        "      else  dtp_1_tot / (population_tot * 0.1) end) as coverage_rate,\n" +
                        " (select case when dtp_1_tot = 0 then 0 \n" +
                        "       when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V001' and bcg_1_tot <> 0 then ((bcg_1_tot - mr_1_tot) / bcg_1_tot) * 100 \n" +
                        "       when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V010' and dtp_1_tot <> 0 then ((dtp_1_tot - dtp_3_tot) / dtp_1_tot) * 100 \n" +
                        "       else 0 end) as dropout_rate \n" +
                        " from  (\n" +
                            " select *,\n" +
                                "  sum(population) OVER (PARTITION BY region_id order by region_id, month_number) population_tot,\n" +
                                "  sum(bcg_1) OVER (PARTITION BY region_id order by region_id, month_number) bcg_1_tot,\n" +
                                "  sum(mr_1) OVER (PARTITION BY region_id order by region_id, month_number) mr_1_tot, \n" +
                                "  sum(dtp_1) OVER (PARTITION BY region_id order by region_id, month_number) dtp_1_tot,\n" +
                                "  sum(dtp_3) OVER (PARTITION BY region_id order by region_id, month_number) dtp_3_tot,\n" +
                                "  coverage min_coverage, dropout min_dropout, wastage\n" +
                            " from \n" +
                            " vaccine_dropout_with_faility_count\n" +
                            " join ( \n" +
                                "  select coalesce(whoratio,0) coverage,  coalesce(dropout,0) dropout , coalesce(wastagefactor,0) wastage\n" +
                                "  from program_products pp  \n" +
                                "  join isa_coefficients c on pp.isacoefficientsid = c.id \n" +
                                "  join vaccine_inventory_product_configurations pc on pc.productid=pp.productid\n" +
                                "  where pp.productid = "+ params.getProduct()+" limit 1\n" +
                            " ) as isa_coffecients ON 1=1\n" +
                            " order by region_name, period_start_date\n" +
                            ") temp\n" +
                ")\n" +

                "select \n" +
                        "  region_name regionName,\n" +
                        "  period_name periodName, \n" +
                        "  startdate startDate,\n" +
                        " max(facility_count) over (partition by region_id) facilityCount, \n" +
                        "  coalesce(population,0) population,\n" +
                        "  case \n" +
                        "  when coverage_rate is null then '' \n" +
                        "  when coverage_rate >= min_coverage and dropout_rate <= min_dropout then 'Cat_1'\n" +
                        "  when coverage_rate < min_coverage and dropout_rate <= min_dropout then 'Cat_3'\n" +
                        "  when coverage_rate >= min_coverage and dropout_rate > min_dropout then 'Cat_2'\n" +
                        "  else 'Cat_4' \n" +
                        "  end as classification\n" +
                " from  progressive_total";

        return sql;
    }

    public String getFacilityReport(Map map) {

        CategorizationVaccineUtilizationPerformanceReportParam params  = (CategorizationVaccineUtilizationPerformanceReportParam)map.get("filterCriteria");

        String sql =
                getDroupOutWithDemographicEstimatesQuery(map)
                        +
                ",vaccine_dropout_with_faility_count as ( \n" +
                        "select \n" +
                             "vd.region_id, vd.region_name, vd.district_id, district_name, period_id,facility_name, facility_id, period_name , month_number, year_number, period_start_date,\n" +
                             "demographics.population, demographics.denominator, startdate, sum(vaccinated) vaccinated, sum(bcg_1) bcg_1, sum(mr_1) mr_1, sum(dtp_1) dtp_1, sum(dtp_3) dtp_3\n" +
                        "from " +
                            "vaccine_dropout vd " +
                            "join (\n" +
                                "select vd.region_id, vd.district_id, facilityid, denominator, population\n" +
                                "from vw_vaccine_population_denominator vd\n" +
                                "join vw_districts d ON vd.district_id = d.district_id\n" +
                                "where programid = fn_get_vaccine_program_id()\n" +
                                "and (productid = "+ params.getProduct()+")\n" +
                                "and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                                "and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "+params.getDistrict()+" or d.parent = "+params.getDistrict()+")\n" +
                            "order by 2,1\n" +
                            ") demographics on demographics.facilityid = facility_id\n" +
                        "group by 1,2,3,4,5,6,7,8,9,10,11,12,13,14\n" +
                        "order by 1,5 \n" +
                 ")"  +

                ", progressive_total as (\n" +
                        "select\n" +
                        "  *,\n" +
                        "  (select case when population_tot = 0 then 0 \n" +
                        "        when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V001' then bcg_1_tot / (population_tot * 0.1) \n" +
                        "        else  dtp_1_tot / (population_tot * 0.1) end) as coverage_rate,\n" +
                        "  (select case when dtp_1_tot = 0 then 0 \n" +
                        "         when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V001' and bcg_1_tot <> 0 then ((bcg_1_tot - mr_1_tot) / bcg_1_tot) * 100 \n" +
                        "         when (select code from products where id = "+ params.getProduct()+" limit 1) = 'V010' and dtp_1_tot <> 0 then ((dtp_1_tot - dtp_3_tot) / dtp_1_tot) * 100 \n" +
                        "         else 0 end) as dropout_rate  \n" +
                        " from  (\n" +
                        "  select *,\n" +
                        "    sum(population) OVER (PARTITION BY facility_id order by facility_id, month_number) population_tot,\n" +
                        "    sum(bcg_1)      OVER (PARTITION BY facility_id order by facility_id, month_number) bcg_1_tot,\n" +
                        "    sum(mr_1)       OVER (PARTITION BY facility_id order by facility_id, month_number) mr_1_tot, \n" +
                        "    sum(dtp_1)      OVER (PARTITION BY facility_id order by facility_id, month_number) dtp_1_tot,\n" +
                        "    sum(dtp_3)      OVER (PARTITION BY facility_id order by facility_id, month_number) dtp_3_tot,\n" +
                        "    coverage min_coverage, dropout min_dropout, wastage\n" +
                        "  from vaccine_dropout_with_faility_count\n" +
                        "  join (  \n" +
                        "    select coalesce(whoratio,0) coverage,  coalesce(dropout,0) dropout , coalesce(wastagefactor,0) wastage\n" +
                        "    from program_products pp  \n" +
                        "    join isa_coefficients c on pp.isacoefficientsid = c.id \n" +
                        "    join vaccine_inventory_product_configurations pc on pc.productid=pp.productid\n" +
                        "    where pp.productid = "+ params.getProduct()+" limit 1\n" +
                        "  ) as isa_coffecients ON 1=1\n" +
                        "\n" +
                        "  order by region_name, period_start_date\n" +
                        ") temp)\n" +

                        "select \n" +
                            "  region_name regionName,\n" +
                            "  district_name districtName,\n" +
                            "  facility_name facilityName, \n" +
                            "  period_name periodName, \n" +
                            "  startdate startDate,\n" +
                            "  coalesce(population,0) population,\n" +
                            "  case \n" +
                            "  when coverage_rate is null then '' \n" +
                            "  when coverage_rate >= min_coverage and dropout_rate <= min_dropout then 'Cat_1'\n" +
                            "  when coverage_rate < min_coverage and dropout_rate <= min_dropout then 'Cat_3'\n" +
                            "  when coverage_rate >= min_coverage and dropout_rate > min_dropout then 'Cat_2'\n" +
                            "  else 'Cat_4' \n" +
                            "  end as classification\n" +
                        " from  progressive_total";

        return sql;
    }

    public String getDroupOutWithDemographicEstimatesQuery(Map map){

        CategorizationVaccineUtilizationPerformanceReportParam params  = (CategorizationVaccineUtilizationPerformanceReportParam)map.get("filterCriteria");

        String sql =
                "-- Get population target\n" +
                "with vaccine_dropout as (\n" +
                    "   select \n" +
                        "    vd.region_id,\n" +
                        "    vd.district_id,\n" +
                        "    period_id,\n" +
                        "    vd.region_name, \n" +
                        "    vd.district_name, \n" +
                        "    facility_id, \n" +
                        "    facility_name,\n" +
                        "    period_start_date startdate, \n" +
                        "    to_char(period_start_date, 'Mon YYYY')  period_name , \n" +
                        "    extract( month from period_start_date) month_number, \n" +
                        "    extract( year from period_start_date) year_number, \n" +
                        "    period_start_date::date period_start_date, \n" +
                        "    coalesce(within_outside_total, 0) vaccinated,\n" +
                        "    bcg_1, \n" +
                        "    mr_1, \n" +
                        "    dtp_1,\n" +
                        "    dtp_3\n" +
                    "    from vw_vaccine_dropout\n" +
                    "    inner join vw_districts vd on vd.district_id = geographic_zone_id\n" +
                    "    where \n" +
                    "      product_id ="+ params.getProduct()+" and \n" +
                    "      period_start_date::date >= '"+ params.getPeriodStart()+"'::date and\n" +
                    "      period_end_date::date <= '"+ params.getPeriodEnd()+"'::date\n" +
                    "      and (vd.parent = "+params.getDistrict()+" or vd.district_id = "+params.getDistrict()+" or vd.region_id = "+params.getDistrict()+" or "+params.getDistrict()+"=0) \n" +
                ")";

        return sql;
    }


   // The following query needs to be deprecated in favour of the above query

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