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


import org.openlmis.report.model.params.ClassificationVaccineUtilizationPerformanceReportParam;

import java.util.Date;
import java.util.Map;

public class ClassificationVaccineUtilizationPerformanceQueryBuilder {


    public String getDistrictReport(Map map) {

        ClassificationVaccineUtilizationPerformanceReportParam params = (ClassificationVaccineUtilizationPerformanceReportParam) map.get("filterCriteria");
        String sql =
                
                getStockStatus(map) + 
                        
                ",  classification_with_facility_count as \n" +
                        "( \n" +
                        "         select   vd.region_id, \n" +
                        "                  vd.district_id, \n" +
                        "                  region_name, \n" +
                        "                  district_name, \n" +
                        "                  period_id, \n" +
                        "                  period_name , \n" +
                        "                  month_number, \n" +
                        "                  year_number, \n" +
                        "                  demographics.population, \n" +
                        "                  demographics.denominator, \n" +
                        "                  facility_count, \n" +
                        "                  startdate, \n" +
                        "                  sum(used) used,\n" +
                        "                  sum(vaccinated) vaccinated \n" +
                        "         from     stock_status vd \n" +
                        "         join \n" +
                        "                  ( \n" +
                        "                           select   region_name                   rname, \n" +
                        "                                    district_name                 dname, \n" +
                        "                                    count(distinct facility_name) facility_count \n" +
                        "                           from     stock_status \n" +
                        "                           group by 1, 2 " +
                        "                   ) as fcount on fcount.dname = vd.district_name \n" +
                        "         join \n" +
                        "                  ( \n" +
                        "                           select   vd.region_id, \n" +
                        "                                    vd.district_id, \n" +
                        "                                    coalesce(sum(denominator),0) denominator, \n" +
                        "                                    coalesce(sum(population),0)  population \n" +
                        "                           from     vw_vaccine_population_denominator vd \n" +
                        "                           join     vw_districts d \n" +
                        "                           on       vd.district_id = d.district_id \n" +
                        "                           where    programid = fn_get_vaccine_program_id() \n" +
                        "                           and      (productid = "+params.getProduct()+" ) \n" +
                        "                           and      year = extract(year from '"+params.getPeriodStart()+"'::date) \n" +
                        "                           and      ( 0 = "+params.getDistrict()+"  or d.district_id = "+params.getDistrict()+"  or " +
                        "                                       d.region_id = "+params.getDistrict()+" or d.parent = "+params.getDistrict()+" ) \n" +
                        "                           group by 1, \n" +
                        "                                    2 \n" +
                        "                           order by 2, 1 " +
                        "                    ) demographics on demographics.district_id = vd.district_id \n" +
                        "   group by 1,2,3,4,5,6,7,8,9,10,11,12\n" +
                        "   order by 1,5\n" +
                        "   )\n" +

                ", classification_with_progresive_total as (\n" +
                        "  select   *, \n" +
                        "  case when vaccinated_tot != 0 then vaccinated_tot / used_tot * 100 else 0 end as usage_rate,\n" +
                        "  case when coalesce(population_tot, 0) != 0 then vaccinated_tot/ population_tot *10 *0.1 else 0 end coverage_rate,\n" +
                        "  100 - (case when coalesce(population_tot, 0) != 0 then vaccinated_tot/ population_tot *10 *0.1 else 0 end) wastage_rate   \n" +
                        "  from (\n" +
                        "    select *,\n" +
                        "      sum(population) over (partition by district_id order by district_id, month_number) population_tot,\n" +
                        "      sum(vaccinated) over (partition by district_id order by district_id, month_number) vaccinated_tot,\n" +
                        "      sum(used)       over (partition by district_id order by district_id, month_number) used_tot\n" +
                        "    from \n" +
                        "       classification_with_facility_count\n" +
                        "    join ( \n" +
                        "      select coalesce(whoratio, 0)      mincoverage, \n" +
                        "             coalesce(dropout, 0)       mindropout, \n" +
                        "             coalesce(wastagefactor, 0) minwastage \n" +
                        "      from   program_products pp \n" +
                        "             join isa_coefficients c \n" +
                        "         on pp.isacoefficientsid = c.id \n" +
                        "             join vaccine_inventory_product_configurations pc \n" +
                        "         on pc.productid = pp.productid \n" +
                        "      where  pp.productid = "+params.getProduct()+"  \n" +
                        "      limit  1 \n" +
                        "     ) as isa_coffecients on true\n" +
                        "     order by region_name, district_name, startdate\n" +
                        "   ) as running_totals\n" +
                        ")\n" +

                "select \n" +
                        "  region_name regionName,\n" +
                        "  region_id regionId,\n" +
                        "  district_name districtName,\n" +
                        "  district_id districtId,\n" +
                        "  period_name periodName,\n" +
                        "  facility_count facilityCount,\n" +
                        "  coalesce(population,0) population,\n" +
                        "  startdate startDate,\n" +
                        "  case\n" +
                        "  when coverage_rate >= mincoverage and wastage_rate <= minwastage then 'A'\n" +
                        "  when coverage_rate < mincoverage and wastage_rate <= minwastage then 'C'\n" +
                        "  when coverage_rate >= mincoverage and wastage_rate > minwastage then 'B'\n" +
                        "  else 'D' end classification\n" +
                "from classification_with_progresive_total";
        return sql;
    }

    public String getRegionReport(Map map) {

        ClassificationVaccineUtilizationPerformanceReportParam params = (ClassificationVaccineUtilizationPerformanceReportParam) map.get("filterCriteria");

        String sql =
                getStockStatus(map) + 
            
             ",  classification_with_facility_count as \n" +
                     "( \n" +
                     "         select   vd.region_id,  \n" +
                     "                  region_name, \n" +
                     "                  period_id, \n" +
                     "                  period_name , \n" +
                     "                  month_number, \n" +
                     "                  year_number, \n" +
                     "                  demographics.population, \n" +
                     "                  demographics.denominator, \n" +
                     "                  facility_count, \n" +
                     "                  startdate, \n" +
                     "                  sum(used) used,\n" +
                     "                  sum(vaccinated) vaccinated \n" +
                     "         from     stock_status vd \n" +
                     "         join \n" +
                     "                  ( \n" +
                     "                           select   region_name                   rname, \n" +
                     "                                    count(distinct facility_name) facility_count \n" +
                     "                           from     stock_status \n" +
                     "                           group by 1) as fcount \n" +
                     "         on       fcount.rname = vd.region_name \n" +
                     "         join \n" +
                     "                  ( \n" +
                     "                           select   vd.region_id, \n" +
                     "                                    vd.district_id, \n" +
                     "                                    coalesce(sum(denominator),0) denominator, \n" +
                     "                                    coalesce(sum(population),0)  population \n" +
                     "                           from     vw_vaccine_population_denominator vd \n" +
                     "                           join     vw_districts d \n" +
                     "                           on       vd.district_id = d.district_id \n" +
                     "                           and      (productid = "+params.getProduct()+" ) \n" +
                     "                           and      year = extract(year from '"+params.getPeriodStart()+"'::date) \n" +
                     "                           and      ( 0 = "+params.getDistrict()+"  or d.district_id = "+params.getDistrict()+"  or " +
                     "                                       d.region_id = "+params.getDistrict()+" or d.parent = "+params.getDistrict()+" ) \n" +
                     "                           group by 1, \n" +
                     "                                    2 \n" +
                     "                           order by 2, \n" +
                     "                                    1 ) demographics \n" +
                     "         on       demographics.district_id = vd.district_id \n" +
                     "   group by 1,2,3,4,5,6,7,8,9,10\n" +
                     "   order by 1,5\n" +
                     "   )\n" +

             ", classification_with_progresive_total as (\n" +
                     "  select   *, \n" +
                     "  case when vaccinated_tot != 0 then vaccinated_tot / used_tot * 100 else 0 end as usage_rate,\n" +
                     "  case when coalesce(population_tot, 0) != 0 then vaccinated_tot/ population_tot *10 *0.1 else 0 end coverage_rate,\n" +
                     "  100 - (case when coalesce(population_tot, 0) != 0 then vaccinated_tot/ population_tot *10 *0.1 else 0 end) wastage_rate   \n" +
                     "  from (\n" +
                     "    select *,\n" +
                     "      sum(population) over (partition by region_id order by region_id, month_number) population_tot,\n" +
                     "      sum(vaccinated) over (partition by region_id order by region_id, month_number) vaccinated_tot,\n" +
                     "      sum(used)       over (partition by region_id order by region_id, month_number) used_tot\n" +
                     "    from \n" +
                     "       classification_with_facility_count\n" +
                     "    join ( \n" +
                     "      select coalesce(whoratio, 0)      mincoverage, \n" +
                     "             coalesce(dropout, 0)       mindropout, \n" +
                     "             coalesce(wastagefactor, 0) minwastage \n" +
                     "      from   program_products pp \n" +
                     "             join isa_coefficients c \n" +
                     "         on pp.isacoefficientsid = c.id \n" +
                     "             join vaccine_inventory_product_configurations pc \n" +
                     "         on pc.productid = pp.productid \n" +
                     "      where  pp.productid = "+params.getProduct()+"  \n" +
                     "      limit  1 \n" +
                     "     ) as isa_coffecients on true\n" +
                     "     order by region_name, startdate\n" +
                     "   ) as running_totals\n" +
             ")\n" +

             "select \n" +
                     "  region_name regionName,\n" +
                     "  region_id regionId,\n" +
                     "  period_name periodName,\n" +
                     "  facility_count facilityCount,\n" +
                     "  coalesce(population,0) population,\n" +
                     "  startdate startDate, \n" +
                     "  case\n" +
                     "  when coverage_rate >= mincoverage and wastage_rate <= minwastage then 'A'\n" +
                     "  when coverage_rate < mincoverage and wastage_rate <= minwastage then 'C'\n" +
                     "  when coverage_rate >= mincoverage and wastage_rate > minwastage then 'B'\n" +
                     "  else 'D' end classification\n" +
             "from classification_with_progresive_total";
        return sql;
    }
    public String getFacilityReport(Map map) {

        ClassificationVaccineUtilizationPerformanceReportParam params = (ClassificationVaccineUtilizationPerformanceReportParam) map.get("filterCriteria");

        String sql =
                getStockStatus(map) +

                ",  classification_with_facility_count as \n" +
                        "( \n" +
                        "         select   vd.region_id, \n" +
                        "                  vd.district_id, \n" +
                        "                  region_name, \n" +
                        "                  district_name, \n" +
                        "                  facility_name,\n" +
                        "                  facility_id,\n" +
                        "                  period_id, \n" +
                        "                  period_name , \n" +
                        "                  month_number, \n" +
                        "                  year_number, \n" +
                        "                  demographics.population, \n" +
                        "                  demographics.denominator, \n" +
                        "                  startdate, \n" +
                        "                  sum(used) used,\n" +
                        "                  sum(vaccinated) vaccinated \n" +
                        "        from stock_status vd \n" +
                        "        join \n" +
                        "                  ( \n" +
                        "                           select   vd.facilityid, \n" +
                        "                                    sum(coalesce(denominator,0)) denominator, \n" +
                        "                                    sum(coalesce(population,0))  population \n" +
                        "                           from     vw_vaccine_population_denominator vd \n" +
                        "                           join     vw_districts d \n" +
                        "                           on       vd.district_id = d.district_id \n" +
                        "                           where    programid = fn_get_vaccine_program_id() \n" +
                        "                           and      (productid = "+params.getProduct()+" ) \n" +
                        "                           and      year = extract(year from '"+params.getPeriodStart()+"'::date) \n" +
                        "                           and      ( 0 = "+params.getDistrict()+"  or d.district_id = "+params.getDistrict()+"  or " +
                        "                                     d.region_id = "+params.getDistrict()+" or d.parent = "+params.getDistrict()+" ) \n" +
                        "                           group by 1\n" +
                        "                           order by 2, \n" +
                        "                                    1 ) demographics  on   demographics.facilityid = vd.facility_id \n" +
                        "           group by 1,2,3,4,5,6,7,8,9,10,11,12,13\n" +
                        "           order by 1,5\n" +
                ")\n" +

                ", classification_with_progresive_total as (\n" +
                        "  select   *, \n" +
                        "  case when vaccinated_tot != 0 then vaccinated_tot / used_tot * 100 else 0 end as usage_rate,\n" +
                        "  case when coalesce(population_tot, 0) != 0 then vaccinated_tot/ population_tot *10 *0.1 else 0 end coverage_rate,\n" +
                        "  100 - (case when coalesce(population_tot, 0) != 0 then vaccinated_tot/ population_tot *10 *0.1 else 0 end) wastage_rate   \n" +
                        "  from (\n" +
                        "    select *,\n" +
                        "      sum(population) over (partition by facility_id order by facility_id, month_number) population_tot,\n" +
                        "      sum(vaccinated) over (partition by facility_id order by facility_id, month_number) vaccinated_tot,\n" +
                        "      sum(used)       over (partition by facility_id order by facility_id, month_number) used_tot\n" +
                        "    from \n" +
                        "       classification_with_facility_count\n" +
                        "    join ( \n" +
                        "      select coalesce(whoratio, 0)      mincoverage, \n" +
                        "             coalesce(dropout, 0)       mindropout, \n" +
                        "             coalesce(wastagefactor, 0) minwastage \n" +
                        "      from   program_products pp \n" +
                        "             join isa_coefficients c \n" +
                        "         on pp.isacoefficientsid = c.id \n" +
                        "             join vaccine_inventory_product_configurations pc \n" +
                        "         on pc.productid = pp.productid \n" +
                        "      where  pp.productid = "+params.getProduct()+"  \n" +
                        "      limit  1 \n" +
                        "     ) as isa_coffecients on true\n" +
                        "     order by region_name, district_name, startdate\n" +
                        "   ) as running_totals\n" +
                ")\n" +

                "select \n" +
                        "  region_name regionName,\n" +
                        "  region_id regionId,\n" +
                        "  district_name districtName,\n" +
                        "  district_id districtId,\n" +
                        "  facility_name facilityName,\n" +
                        "  facility_id facilityId,\n" +
                        "  period_name periodName,\n" +
                        "  startdate startDate,\n" +
                        "  coalesce(population,0) population,\n" +
                        "  case\n" +
                        "  when coverage_rate >= mincoverage and wastage_rate <= minwastage then 'A'\n" +
                        "  when coverage_rate < mincoverage and wastage_rate <= minwastage then 'C'\n" +
                        "  when coverage_rate >= mincoverage and wastage_rate > minwastage then 'B'\n" +
                        "  else 'D' end classification\n" +
                "from classification_with_progresive_total";
        return sql;
    }

    public String getStockStatus(Map map) {

        ClassificationVaccineUtilizationPerformanceReportParam params = (ClassificationVaccineUtilizationPerformanceReportParam) map.get("filterCriteria");
        String sql =
                "with stock_status as\n" +
                        "  (  select \n" +
                        "    vd.region_name, \n" +
                        "    vd.region_id,\n" +
                        "    vd.district_id, \n" +
                        "    vd.district_name, \n" +
                        "    vc.facility_id, \n" +
                        "    vs.facility_name, \n" +
                        "    vs.period_start_date startdate, \n" +
                        "    vs.period_id,\n" +
                        "    vs.period_name,\n" +
                        "    coalesce(usage_denominator, 0) :: numeric used,\n" +
                        "    coalesce(within_outside_total, 0)  :: numeric vaccinated,\n" +
                        "    extract( month from vs.period_start_date) month_number, \n" +
                        "    extract( year from vs.period_start_date) year_number\n" +
                        "   from   vw_vaccine_stock_status vs\n" +
                        "          inner join vw_districts vd on vd.district_id = geographic_zone_id \n" +
                        "          inner join vw_vaccine_coverage vc on \n" +
                        "         vc.facility_id = vs.facility_id \n" +
                        "         and vc.period_id = vs.period_id \n" +
                        "         and vc.geographic_zone_id = vs.geographic_zone_id \n" +
                        "         and vc.product_id = vs.product_id\n" +
                        "  where  vs.product_id = "+params.getProduct()+" \n" +
                        "  and vs.period_start_date :: date >= '"+params.getPeriodStart()+"' \n" +
                        "  and vs.period_end_date :: date <= '"+params.getPeriodEnd()+"' \n" +
                        "  and ( vd.parent = "+params.getDistrict()+" or vd.district_id = "+params.getDistrict()+" or vd.region_id = "+params.getDistrict()+" or 0 = "+params.getDistrict()+")" +
                        ")\n";
            return sql;
    }

    // the above builder methods are for the v2 implementaion
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
            Long doseId = (Long) params.get("doseId");
        return " select year, region_name, district_name, facility_name, " +
                " denominator, population from vw_vaccine_population_denominator vd " +
                " where programid = fn_get_vaccine_program_id() and " +
                " (productid = " + productId + " or "+ productId +"=0 )"+
                " and (doseid = " + doseId + " or "+doseId +"=0 )"+
                "  and (vd.district_id = " + zone + "       or vd.region_id = " + zone + "      or 0=" + zone + "       ) ";


    }
        public String getDistrictPopulationInformation(Map params) {
                Long productId = (Long) params.get("productId");
                Long doseId = (Long) params.get("doseId");
                Long zone = (Long) params.get("zoneId");
                return " select year, region_name, district_name, " +
                        "coalesce(sum(denominator),0) denominator, " +
                        "coalesce(sum(population),0) population \n" +
                        "from vw_vaccine_population_denominator vd " +
                        " where programid = fn_get_vaccine_program_id() and " +
                        " (productid = " + productId + " or "+ productId +"=0 )"+
                        " and (doseid = " + doseId + " or "+doseId +"=0 )"+
                        "  and (vd.district_id = " + zone + "       or vd.region_id = " + zone + "      or 0=" + zone + "       ) " +
                        " group by 1,2,3";


        }
        public String getRegionPopulationInformation(Map params) {
                Long productId = (Long) params.get("productId");
                Long zone = (Long) params.get("zoneId");
                Long doseId = (Long) params.get("doseId");
                return " select year, region_name,  " +
                        "coalesce(sum(denominator),0) denominator, " +
                        "coalesce(sum(population),0) population \n" +
                        "from vw_vaccine_population_denominator vd " +
                        " where programid = fn_get_vaccine_program_id() and " +
                        " (productid = " + productId + " or "+ productId +"=0 )"+
                        " and (doseid = " + doseId + " or "+doseId +"=0 )"+
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