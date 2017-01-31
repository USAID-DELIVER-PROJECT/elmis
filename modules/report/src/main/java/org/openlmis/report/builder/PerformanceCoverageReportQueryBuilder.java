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

import org.openlmis.report.model.params.PerformanceCoverageReportParam;

import java.util.Map;

public class  PerformanceCoverageReportQueryBuilder {

    public static String getFacilityReport(Map map){

        PerformanceCoverageReportParam params  = (PerformanceCoverageReportParam)map.get("filterCriteria");

        String sql =
                "-- Get population target\n" +
                        "with ppl_demographics as (\n" +
                        "  select \"year\", vd.region_name, vd.region_id, vd.district_name, vd.district_id, productid, " +
                        "        vd.facility_name, vd.facilityid,\n" +
                        "  coalesce(sum(denominator),0) target, coalesce(sum(population),0) population \n" +
                        "  from vw_vaccine_population_denominator vd \n" +
                        "  join vw_districts d ON vd.district_id = d.district_id \n" +
                        "  where programid = fn_get_vaccine_program_id() \n" +
                        "      and (productid = "+ params.getProduct()+") \n" +
                        "      and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                        "      and doseid = " + params.getDoseId() +
                        "      and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "
                        +params.getDistrict()+" or d.parent = "+params.getDistrict()+") "+
                        "  group by 1,2,3,4,5,6,7,8\n" +
                        ")\n" +

                "-- Generate for each target a 12 month row for later use\n" +
                        ", region_period AS (\n" +
                        "  select pp.id period_id, pp.startdate, pp.name period_name, dr.*\n" +
                        "  from processing_periods pp, (select * from ppl_demographics) dr\n" +
                        "  where \n" +
                        "  pp.startdate::date >= '"+ params.getPeriodStart()+"'::date  \n" +
                        "  and pp.enddate::date <= '"+ params.getPeriodEnd()+"'::date   \n" +
                        "  and pp.numberofmonths = 1 \n" +
                        ") \n" +

                "-- Get coverage\n" +
                        ", coverage as (\n" +
                        "    SELECT        d.region_name, \n" +
                        "                  d.region_id,\n" +
                        "                  d.district_id ,\n" +
                        "                  d.district_name,\n" +
                        "                  i.period_name,\n" +
                        "                  i.period_id,\n" +
                        "                  i.facility_name, \n" +
                        "                  i.facility_id,\n" +
                        "                  sum(i.within_outside_total) vaccinated, \n" +
                        "                  period_start_date,\n" +
                        "          extract(month from i.period_start_date) \"month\", \n" +
                        "                  extract(year from i.period_start_date) \"year\"\n" +
                        "                FROM \n" +
                        "                 vw_vaccine_coverage i\n" +
                        "          JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                        "          WHERE   i.program_id = fn_get_vaccine_program_id() \n" +
                        "                   AND i.period_start_date::date >= '"+ params.getPeriodStart()+"'::date \n" +
                        "                   AND i.period_end_date::date <= '"+ params.getPeriodEnd()+"'::date \n" +
                        "                   AND i.product_id = "+ params.getProduct()+"\n" +
                        "                   AND d.district_id in (select district_id from vw_user_facilities where user_id = "+params.getUserId()   +" and program_id = fn_get_vaccine_program_id())  "+
                        "                 group by d.region_name,d.region_id, d.district_name, d.district_id, i.period_name, \n" +
                        "                 i.period_id, i.period_start_date, i.facility_name, i.facility_id\n" +
                        " ) \n" +

                "--  get cumulative coverages\n" +
                        ", coverage_with_cumulatives as (\n" +
                        "    select r.*, c.vaccinated, \n" +
                        "      round((case when r.target > 0 then (c.vaccinated /r.target::numeric) else 0 end) * 100,2) coverage,\n" +
                        "      ( select sum(coalesce(c.vaccinated,0)) from coverage c \n" +
                        "        where c.period_start_date <= startdate and c.region_id = r.region_id \n" +
                        "        and r.district_id = c.district_id AND c.facility_id = r.facilityid) cum_vaccinated \n" +
                        "    from region_period r           \n" +
                        "        left outer JOIN coverage c on r.region_id = c.region_id AND r.district_id = c.district_id AND r.period_id = c.period_id \n" +
                        "        AND c.facility_id = r.facilityid\n" +
                        "        where r.facilityid in (select distinct facility_id from coverage)\n" +
                        " )\n" ;
        return sql;
    }

    public static String getFacilitytMainReport(Map map){

        return getFacilityReport(map) +
                "select c.region_name,\n" +
                "      c.district_name, \n" +
                "      c.period_name,\n" +
                "      c.facility_name,\n" +
                "      case when c.vaccinated is null then 'No' else 'Yes' end reported,\n" +
                "      coalesce(c.target,0) target, \n" +
                "      coalesce(c.vaccinated,0) vaccinated, \n" +
                "      coalesce(c.coverage,0) coverage, \n" +
                "      coalesce(c.cum_vaccinated,0) cum_vaccinated,\n" +
                "      coalesce(round((case when c.target > 0 then (c.cum_vaccinated / c.target::numeric) else 0 end) * 100,2),0) cum_coverage,\n" +
                "      extract(month from startdate) \"month\",\n" +
                "      extract(year from startdate) \"year\"\n" +
                "   from coverage_with_cumulatives c\n" +
                "   order by c.region_name, c.district_name, facility_name, startdate asc";
    }

    public static String getFacilityReportSummary(Map map){

        return getFacilityReport(map) +
                "select     \n" +
                    "period_name,     \n" +
                    "'Non Reporting' status_name,     \n" +
                    "case when vaccinated is null then 1 else 0 end status,     \n" +
                    "startdate     \n" +
                "from  coverage_with_cumulatives     \n" +
            " union all " +
                "select     \n" +
                    "period_name,     \n" +
                    "'Coverage < 80%' status_name,     \n" +
                    "case when vaccinated is not null and coverage < 80 then 1 else 0 end status,     \n" +
                    "startdate     \n" +
                "from  coverage_with_cumulatives     \n" +
            " union all " +
                "select     \n" +
                    "period_name,     \n" +
                    "'Coverage >= 90' status_name,     \n" +
                    "case when coverage >=90 then 1 else 0 end status,     \n" +
                    "startdate     \n" +
                "from  coverage_with_cumulatives     \n" +
            " union all " +
                "select     \n" +
                    "period_name,     \n" +
                    "'80% <= coverage < 90%' status_name,     \n" +
                    "case when coverage < 90 and coverage >=80 then 1 else 0 end status,     \n" +
                    "startdate     \n" +
                "from  coverage_with_cumulatives\n";
    }

    public static String getDistrictReport(Map map){

        PerformanceCoverageReportParam params  = (PerformanceCoverageReportParam)map.get("filterCriteria");

        String sql =
                "-- Get population target\n" +
                        "with ppl_demographics as (\n" +
                        "    select \"year\", vd.region_name, vd.region_id, vd.district_name, vd.district_id, productid, coalesce(sum(denominator),0) target, coalesce(sum(population),0) population \n" +
                        "    from vw_vaccine_population_denominator vd \n" +
                        "   join vw_districts d ON vd.district_id = d.district_id \n" +
                        "    where programid = fn_get_vaccine_program_id() \n" +
                        "      and (productid = "+ params.getProduct()+") \n" +
                        "      and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                        "      and doseid = " + params.getDoseId() +
                        "      and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "
                        +params.getDistrict()+" or d.parent = "+params.getDistrict()+") " +
                        "    group by 1,2,3,4,5,6\n" +
                        ")\n" +

                        "-- Generate for each target a 12 month row for later use\n" +
                        ", region_period AS (\n" +
                        "    select pp.id period_id, pp.startdate, pp.name period_name, dr.*\n" +
                        "       from processing_periods pp, (select * from ppl_demographics) dr\n" +
                        "          where \n" +
                        "        pp.startdate::date >= '"+ params.getPeriodStart()+"'::date  \n" +
                        "        and pp.enddate::date <= '"+ params.getPeriodEnd()+"'::date   \n" +
                        "        and pp.numberofmonths = 1 \n" +
                        ") \n" +

                        "-- Get coverage\n" +
                        ", coverage as (\n" +
                        "    SELECT    d.region_name, \n" +
                        "                  d.region_id,\n" +
                        "                  d.district_id ,\n" +
                        "                  d.district_name,\n" +
                        "                  i.period_name,\n" +
                        "                  i.period_id, \n" +
                        "                  sum(i.within_outside_total) vaccinated, \n" +
                        "                  period_start_date,\n" +
                        "          extract(month from i.period_start_date) \"month\", \n" +
                        "                  extract(year from i.period_start_date) \"year\"\n" +
                        "                FROM \n" +
                        "                  vw_vaccine_coverage i\n" +
                        "          JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                        "          WHERE   i.program_id = fn_get_vaccine_program_id() \n" +
                        "                   AND i.period_start_date::date >= '"+ params.getPeriodStart()+"'::date \n" +
                        "                   AND i.period_end_date::date <= '"+ params.getPeriodEnd()+"'::date \n" +
                        "                   AND i.product_id = "+ params.getProduct()+"\n" +
                        "                   AND d.district_id in (select district_id from vw_user_facilities where user_id = "+params.getUserId()   +" and program_id = fn_get_vaccine_program_id())  "+
                        "                 group by d.region_name,d.region_id, d.district_name, d.district_id, i.period_name, i.period_id, i.period_start_date\n" +
                        ") \n" +

                        "--  get cumulative coverages\n" +
                        ", coverage_with_cumulatives as (\n" +
                        "    select r.*, c.vaccinated, round((case when r.target > 0 then (c.vaccinated /r.target::numeric) else 0 end) * 100,2) coverage,\n" +
                        "    (select sum(coalesce(c.vaccinated,0)) from coverage c where c.period_start_date <= startdate and c.region_id = r.region_id and r.district_id = c.district_id) cum_vaccinated \n" +
                        "    from region_period r           \n" +
                        "        left outer JOIN coverage c on r.region_id = c.region_id AND r.district_id = c.district_id AND r.period_id = c.period_id\n" +
                        "        where r.district_id in (select distinct district_id from coverage)\n" +
                        ")\n" +

                        "   select c.region_name,\n" +
                        "      c.district_name, \n" +
                        "      c.period_name,\n" +
                        "      case when c.vaccinated is null then 'No' else 'Yes' end reported,\n" +
                        "      coalesce(c.target,0) target, \n" +
                        "      coalesce(c.vaccinated,0) vaccinated, \n" +
                        "      coalesce(c.coverage,0) coverage, \n" +
                        "      coalesce(c.cum_vaccinated,0) cum_vaccinated,\n" +
                        "      coalesce(round((case when c.target > 0 then (c.cum_vaccinated / c.target::numeric) else 0 end) * 100,2),0) cum_coverage,\n" +
                        "      extract(month from startdate) \"month\",\n" +
                        "      extract(year from startdate) \"year\"\n" +
                        "   from coverage_with_cumulatives c\n" +
                        "   order by c.region_name, c.district_name, startdate asc\n";


        return sql;
    }

    public static String getRegionReport(Map map){

        PerformanceCoverageReportParam params  = (PerformanceCoverageReportParam)map.get("filterCriteria");

        String sql =
                "-- GET POPULATION TARGET\n" +
                        "with ppl_demographics as (\n" +
                        "    select \"year\", vd.region_name, vd.region_id,  productid, coalesce(sum(denominator),0) target, coalesce(sum(population),0) population \n" +
                        "    from vw_vaccine_population_denominator vd \n" +
                        "   join vw_districts d ON vd.district_id = d.district_id \n" +
                        "    where programid = fn_get_vaccine_program_id() \n" +
                        "      and (productid = "+ params.getProduct()+") \n" +
                        "      and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                        "      and doseid = " + params.getDoseId() +
                        "      and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "
                        + params.getDistrict()+" or d.parent = "+params.getDistrict()+") "+
                        "      group by 1,2,3,4\n" +
                        ")\n" +
                        "\n" +
                "-- Generate for each target a 12 month row for later use\n" +
                        ", region_period AS (\n" +
                        "    select pp.id period_id, pp.startdate, pp.name period_name, dr.*\n" +
                        "       from processing_periods pp, (select * from ppl_demographics) dr\n" +
                        "          where \n" +
                        "        pp.startdate::date >= '"+ params.getPeriodStart()+"'::date  \n" +
                        "        and pp.enddate::date <= '"+ params.getPeriodEnd()+"'::date   \n" +
                        "        and pp.numberofmonths = 1 \n" +
                        ") \n" +
                        "\n" +
                "-- Get coverage\n" +
                        ", coverage as (\n" +
                        "    SELECT    d.region_name, \n" +
                        "                  d.region_id,\n" +
                        "                  i.period_name,\n" +
                        "                  i.period_id, \n" +
                        "                  sum(i.within_outside_total) vaccinated, \n" +
                        "                  period_start_date,\n" +
                        "          extract(month from i.period_start_date) \"month\", \n" +
                        "                  extract(year from i.period_start_date) \"year\"\n" +
                        "                FROM \n" +
                        "                  vw_vaccine_coverage i\n" +
                        "          JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                        "                WHERE    \n" +
                        "             i.program_id = fn_get_vaccine_program_id() \n" +
                        "                     AND i.period_start_date::date >= '"+ params.getPeriodStart()+"'::date \n" +
                        "             AND i.period_end_date::date <= '"+ params.getPeriodEnd()+"'::date \n" +
                        "             AND i.product_id = "+ params.getProduct()+"\n" +
                        "             AND d.district_id in (select district_id from vw_user_facilities where user_id = "+params.getUserId()   +" and program_id = fn_get_vaccine_program_id())  "+
                        "                 group by d.region_name,d.region_id, i.period_name, i.period_id, i.period_start_date\n" +
                        "                 order by d.region_name, i.period_start_date\n" +
                        ") \n" +
                        "\n" +
                "--  get cumulative coverages\n" +
                        ", coverage_with_cumulatives as (\n" +
                        "    select r.*, c.vaccinated, round((case when r.target > 0 then (c.vaccinated /r.target::numeric) else 0 end) * 100,2) coverage,\n" +
                        "    (select sum(coalesce(c.vaccinated,0)) from coverage c where c.period_start_date <= startdate and c.region_id = r.region_id) cum_vaccinated \n" +
                        "    from region_period r           \n" +
                        "        left outer JOIN coverage c on r.region_id = c.region_id AND r.period_id = c.period_id\n" +
                        "       where r.region_id in (select distinct region_id from coverage) " +
                        " )\n" +
                        "\n" +
                "   select c.region_name,\n" +
                        "      \n" +
                        "      c.period_name,\n" +
                        "      case when c.vaccinated is null then 'No' else 'Yes' end reported,\n" +
                        "      coalesce(c.target,0) target, \n" +
                        "      coalesce(c.vaccinated,0) vaccinated, \n" +
                        "      coalesce(c.coverage,0) coverage, \n" +
                        "      coalesce(c.cum_vaccinated,0) cum_vaccinated,\n" +
                        "      coalesce(round((case when c.target > 0 then (c.cum_vaccinated / c.target::numeric) else 0 end) * 100,2),0) cum_coverage,\n" +
                        "      extract(month from startdate) \"month\",\n" +
                        "      extract(year from startdate) \"year\"\n" +
                        "   from coverage_with_cumulatives c\n" +
                        "   order by c.region_name,  startdate asc\n";


        return sql;
    }

    public static String getDistrictReportSummary(Map map) {

        PerformanceCoverageReportParam params = (PerformanceCoverageReportParam) map.get("filterCriteria");

        String sql = "\n-- Get population target   \n " +
                "with ppl_demographics as (    " +
                "    select \"year\", vd.region_name, vd.region_id, vd.district_name, vd.district_id, productid, coalesce(sum(denominator),0) target, coalesce(sum(population),0) population    " +
                "    from vw_vaccine_population_denominator vd    " +
                "   join vw_districts d ON vd.district_id = d.district_id    " +
                "    where programid = fn_get_vaccine_program_id() \n" +
                "      and (productid = "+ params.getProduct()+") \n" +
                "      and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                "      and doseid = " + params.getDoseId() +
                "      and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "
                +params.getDistrict()+" or d.parent = "+params.getDistrict()+") "+
                "      group by 1,2,3,4,5,6 " +
                "    order by 2,1    " +
                ")    " +
                "\n-- Generate for each target a 12 month row for later use   \n " +
                ", region_period AS (    " +
                "    select pp.id period_id, pp.startdate, pp.name period_name, dr.*    " +
                "       from processing_periods pp, (select * from ppl_demographics) dr    " +
                "          where    " +
                "        pp.startdate::date >= '"+ params.getPeriodStart()+"'::date " +
                "        and pp.enddate::date <= '"+ params.getPeriodEnd()+"'::date " +
                "        and pp.numberofmonths = 1    " +
                "        order by region_name, district_name, startdate    " +
                ")    " +
                "\n-- Get coverage   \n " +
                ", coverage as (    " +
                "    SELECT    d.region_name,    " +
                "                  d.region_id,    " +
                "                  d.district_id ,    " +
                "                  d.district_name,    " +
                "                  i.period_name,    " +
                "                  i.period_id,    " +
                "                  sum(i.within_outside_total) vaccinated,    " +
                "                  period_start_date,    " +
                "                extract(month from i.period_start_date) \"month\",    " +
                "                  extract(year from i.period_start_date) \"year\"    " +
                "                FROM    " +
                "                  vw_vaccine_coverage i    " +
                "          JOIN vw_districts d ON i.geographic_zone_id = d.district_id    " +
                "          WHERE   i.program_id = fn_get_vaccine_program_id() \n" +
                "                   AND i.period_start_date::date >= '"+ params.getPeriodStart()+"'::date \n" +
                "                   AND i.period_end_date::date <= '"+ params.getPeriodEnd()+"'::date \n" +
                "                   AND i.product_id = "+ params.getProduct()+"\n" +
                "                   AND d.district_id in (select district_id from vw_user_facilities where user_id = "+params.getUserId()   +" and program_id = fn_get_vaccine_program_id())  "+
                "                 group by d.region_name,d.region_id, d.district_name, d.district_id, i.period_name, i.period_id, i.period_start_date    " +
                "                 order by d.region_name, i.period_start_date    " +
                ")    " +
                "\n--  get cumulative coverages   \n " +
                ", periodic_coverage as (    " +
                "    select r.*, c.vaccinated, round((case when r.target > 0 then (c.vaccinated /r.target::numeric) else 0 end) * 100,2) coverage    " +
                "    " +
                "    from region_period r    " +
                "        left outer JOIN coverage c on r.region_id = c.region_id AND r.district_id = c.district_id AND r.period_id = c.period_id    " +
                "        where r.district_id in (select distinct district_id from coverage)    " +
                ")    " +

                "    " +
                "   select    " +
                "        period_name,    " +
                "        'Non Reporting' status_name,    " +
                "       case when vaccinated is null then 1 else 0 end status,    " +
                "       startdate    " +
                "   from  periodic_coverage    " +
                "    " +
                "union all    " +
                "    " +
                "   select    " +
                "        period_name,    " +
                "        'Coverage < 80%' status_name,    " +
                "       case when vaccinated is not null and coverage < 80 then 1 else 0 end status,    " +
                "       startdate    " +
                "   from  periodic_coverage    " +
                "    " +
                "union all    " +
                "    " +
                "   select    " +
                "        period_name,    " +
                "        'Coverage >= 90' status_name,    " +
                "       case when coverage >=90 then 1 else 0 end status,    " +
                "       startdate    " +
                "   from  periodic_coverage    " +
                "    " +
                "union all    " +
                "   select    " +
                "        period_name,    " +
                "        '80% <= coverage < 90%' status_name,    " +
                "       case when coverage < 90 and coverage >=80 then 1 else 0 end status,    " +
                "       startdate    " +
                "   from  periodic_coverage";
        return sql;
    }

    public static String getRegionReportSummary(Map map) {

        PerformanceCoverageReportParam params = (PerformanceCoverageReportParam) map.get("filterCriteria");

        String sql = "\n-- Get population target  \n" +
                "with ppl_demographics as (  " +
                "    select \"year\", vd.region_name, vd.region_id, productid, coalesce(sum(denominator),0) target, coalesce(sum(population),0) population  " +
                "    from vw_vaccine_population_denominator vd  " +
                "   join vw_districts d ON vd.district_id = d.district_id  " +
                "      and (productid = "+ params.getProduct()+") \n" +
                "      and year = extract(year from '"+ params.getPeriodStart()+"'::date)\n" +
                "      and doseid = " + params.getDoseId() +
                "      and (0 = "+params.getDistrict()+" or d.district_id = "+params.getDistrict()+" or d.region_id = "
                + params.getDistrict()+" or d.parent = "+params.getDistrict()+") "+
                "      group by 1,2,3,4  " +
                "    order by 2,1  " +
                ")  " +
                "\n-- Generate for each target a 12 month row for later use \n " +
                ", region_period AS (  " +
                "    select pp.id period_id, pp.startdate, pp.name period_name, dr.*  " +
                "       from processing_periods pp, (select * from ppl_demographics) dr  " +
                "          where  " +
                "        pp.startdate::date >= '"+ params.getPeriodStart()+"'::date  \n" +
                "        and pp.enddate::date <= '"+ params.getPeriodEnd()+"'::date   \n" +
                "        and pp.numberofmonths = 1  " +
                "        order by region_name, startdate  " +
                ")  " +
                "\n-- Get coverage  \n" +
                ", coverage as (  " +
                "    SELECT    d.region_name,  " +
                "                  d.region_id,  " +
                "                  i.period_name,  " +
                "                  i.period_id,  " +
                "                  sum(i.within_outside_total) vaccinated,  " +
                "                  period_start_date,  " +
                "                extract(month from i.period_start_date) \"month\",  " +
                "                  extract(year from i.period_start_date) \"year\"  " +
                "                FROM  " +
                "                  vw_vaccine_coverage i  " +
                "          JOIN vw_districts d ON i.geographic_zone_id = d.district_id  " +
                "                WHERE  " +
                "             i.program_id = fn_get_vaccine_program_id()  " +
                "             AND i.period_start_date::date >= '"+ params.getPeriodStart()+"'::date \n" +
                "             AND i.period_end_date::date <= '"+ params.getPeriodEnd()+"'::date \n" +
                "             AND i.product_id = "+ params.getProduct() +
                "             AND d.district_id in (select district_id from vw_user_facilities where user_id = "+params.getUserId()   +" and program_id = fn_get_vaccine_program_id())  "+
                "                 group by d.region_name,d.region_id, i.period_name, i.period_id, i.period_start_date  " +
                "                 order by d.region_name, i.period_start_date  " +
                ")  " +
                "\n--  get cumulative coverages  \n" +
                ", periodic_coverage as (  " +
                "    select r.*, c.vaccinated, round((case when r.target > 0 then (c.vaccinated /r.target::numeric) else 0 end) * 100,2) coverage  " +
                "  " +
                "    from region_period r  " +
                "        left outer JOIN coverage c on r.region_id = c.region_id AND r.period_id = c.period_id  " +
                "       where r.region_id in (select distinct region_id from coverage)  " +
                ")  " +
                "select  " +
                "        period_name,  " +
                "        'Non Reporting' status_name,  " +
                "       case when vaccinated is null then 1 else 0 end status,  " +
                "       startdate  " +
                "   from  periodic_coverage  " +
                "  " +
                "union all  " +
                "  " +
                "   select  " +
                "        period_name,  " +
                "        'Coverage < 80%' status_name,  " +
                "       case when vaccinated is not null and coverage < 80 then 1 else 0 end status,  " +
                "       startdate  " +
                "   from  periodic_coverage  " +
                "  " +
                "union all  " +
                "  " +
                "   select  " +
                "        period_name,  " +
                "        'Coverage >= 90' status_name,  " +
                "       case when coverage >=90 then 1 else 0 end status,  " +
                "       startdate  " +
                "   from  periodic_coverage  " +
                "  " +
                "union all  " +
                "   select  " +
                "        period_name,  " +
                "        '80% <= coverage < 90%' status_name,  " +
                "       case when coverage < 90 and coverage >=80 then 1 else 0 end status,  " +
                "       startdate  " +
                "   from  periodic_coverage";
        return sql;
    }
        public static String getDenominatorName(Map map) {

                PerformanceCoverageReportParam params = (PerformanceCoverageReportParam) map.get("filterCriteria");
                String sql= " select * from  get_vaccine_coverage_denominator_name(fn_get_vaccine_program_id(), 0,  extract(year from '"+ params.getPeriodStart()+"'::date)::integer," + params.getProduct()+" ,"+params.getDoseId()+")" ;


                return sql;

        }

}
