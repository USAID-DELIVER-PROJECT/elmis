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


        String sql = "with temp as ( \n" +
                "            select 	pp.name period_name,\n" +
                "			pp.startdate::date period_start_date,\n" +
                "			z.id geographiczoneid,\n" +
                "			z.name district,\n" +
                "			f.name facility_name,\n" +
                "			f.code facility_code, \n" +
                "			to_char(vr.createdDate, 'DD Mon YYYY') reported_date,\n" +
                "			COALESCE(vr.fixedimmunizationsessions,0) fixed,\n" +
                "			COALESCE(vr.outreachimmunizationsessions,0) outreach,\n" +
                "			COALESCE(z.catchmentpopulation,0) target,\n" +
                "                 CASE\n" +
                "                    WHEN date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone) <= COALESCE((( SELECT configuration_settings.value \n" +
                "                      FROM configuration_settings \n" +
                "                      WHERE configuration_settings.key::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'T'::text \n" +
                "                    WHEN COALESCE(date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value \n" +
                "                      FROM configuration_settings \n" +
                "                      WHERE configuration_settings.key::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'L'::text \n" +
                "                    ELSE 'N'::text \n" +
                "                 END AS reporting_status \n" +
                "                from programs_supported ps \n" +
                "                left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid \n" +
                "                left join processing_periods pp on pp.id = vr.periodid\n" +
                "                join facilities f on f.id = ps.facilityId  \n" +
                "                join geographic_zones z on z.id = f.geographicZoneId \n" +
                "            where ps.programId = (select id from programs where enableivdform = 't' limit 1)\n" +
                "            and pp.startdate::date >= '"+startDate+"' and pp.enddate::date <= '"+endDate+"'  \n" +
                "            )  \n" +
                "            select \n" +
                "            vd.region_name,\n" +
                "            vd.district_name,    \n" +
                "            t.period_name,\n" +
                "            t.period_start_date,\n" +
                "            sum(fixed) fixed,\n" +
                "            sum(outreach) outreach,\n" +
                "            sum(fixed) + sum(outreach) session_total,\n" +
                "            sum(target) target,  \n" +
                "            sum(1) expected,\n" +
                "            sum(case when reporting_status IN ('T','L') then 1 else 0 end) reported,\n" +
                "            sum(case when reporting_status = 'T' then 1 else 0 end) ontime, \n" +
                "            sum(case when reporting_status = 'L' then 1 else 0 end) late,\n" +
                "            trunc((sum(case when reporting_status IN ('T','L') then 1 else 0 end)::numeric/sum(1)::numeric)*100,2) percent_reported,\n" +
                "            trunc(((sum(1)::numeric - sum(case when reporting_status IN ('T','L') then 1 else 0 end)::numeric)/sum(1)::numeric)*100,2) percent_late \n" +
                "from temp t\n" +
                "join vw_districts vd on vd.district_id = t.geographiczoneid\n" + writeDistrictPredicate(zone)  + " \n" +
                "group by 1, 2, 3, 4\n" +
                "order by 1,2, 4\n";

        return sql;
    }
    public static String  selectCompletenessAndTimelinessSummaryReportDataByDistrict(Map params){
        Long zone = (Long) params.get("districtId");
        Date startDate   = (Date) params.get("startDate");
        Date endDate     = (Date) params.get("endDate");
        Long productId   = (Long) params.get("productId");

        String sql = "with temp as ( \n" +
                "               select pp.name period_name, \n" +
                "               Extract(month FROM pp.startdate) period_month, \n" +
                "               Extract(year FROM pp.startdate)  period_year,\n" +
                "pp.startdate::date period_start_date, z.id geographiczoneid, z.name district, f.name facility_name, f.code facility_code, \n" +
                "                   to_char(vr.createdDate, 'DD Mon YYYY') reported_date,  \n" +
                "                 CASE\n" +
                "                        WHEN date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone) <= COALESCE((( SELECT configuration_settings.value \n" +
                "                           FROM configuration_settings \n" +
                "                          WHERE configuration_settings.key::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'T'::text \n" +
                "                        WHEN COALESCE(date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value \n" +
                "                           FROM configuration_settings \n" +
                "                          WHERE configuration_settings.key::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'L'::text \n" +
                "                        ELSE 'N'::text \n" +
                "                 END AS reporting_status \n" +
                "                from programs_supported ps \n" +
                "                left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid \n" +
                "                left join processing_periods pp on pp.id = vr.periodid      \n" +
                "                join facilities f on f.id = ps.facilityId  \n" +
                "                join geographic_zones z on z.id = f.geographicZoneId \n" +
                "            where ps.programId = (select id from programs where enableivdform = 't' limit 1)\n" +
                "            and pp.startdate::date >= '"+startDate+"' and pp.enddate::date <= '"+endDate+"'  \n" +
                "            )  \n" +
                "            select \n" +
                "            t.period_name,\n" +
                "            t.period_month  as month,\n" +
                "            t.period_year  as year, \n" +
                "            sum(1) expected,\n" +
                "            sum(case when reporting_status IN ('T','L') then 1 else 0 end) reported,\n" +
                "            sum(case when reporting_status = 'T' then 1 else 0 end) ontime, \n" +
                "            sum(case when reporting_status = 'L' then 1 else 0 end) late,\n" +
                "            trunc((sum(case when reporting_status IN ('T','L') then 1 else 0 end)::numeric/sum(1)::numeric)*100,2) percent_reported,\n" +
                "            trunc(((sum(1)::numeric - sum(case when reporting_status IN ('T','L') then 1 else 0 end)::numeric)/sum(1)::numeric)*100,2) percent_late \n" +
                "from temp t\n" +
                "join vw_districts vd on vd.district_id = t.geographiczoneid\n" + writeDistrictPredicate(zone)  + " \n" +
                "group by 1, 2, 3\n" +
                "order by 1,3, 2\n";
        return sql;
    }

    public static String selectCompletenessAndTimelinessAggregateSummaryReportDataByDistrict(Map params){

        Long zone = (Long) params.get("districtId");
        Date startDate   = (Date) params.get("startDate");
        Date endDate     = (Date) params.get("endDate");
        Long productId   = (Long) params.get("productId");

        String sql = "SELECT district_id, district_name, period_name, \n" +
                "                       \"month\",  \n" +
                "                       \"year\",  \n" +
                "                       SUM(ontime)                                      ontime,  \n" +
                "                       Count(*)                                         reported,  \n" +
                "                       (SELECT Count(*) AS expected   FROM   facilities f  join vw_districts d  \n" +
                "                          ON f.geographiczoneid = d.district_id \n" +
                "                            AND f.typeid = (SELECT id FROM facility_types WHERE code = 'dvs') ) AS expected  \n" +
                "           FROM  (SELECT d.district_id,\n" +
                "                               d.district_name,  \n" +
                "                               i.period_name,  \n" +
                "                               i.period_start_date,  \n" +
                "                               Extract(month FROM i.period_start_date) \"month\",  \n" +
                "                               Extract(year FROM i.period_start_date)  \"year\",  \n" +
                "                               vr.id,  \n" +
                "                               i.facility_id,  \n" +
                "                               vr.submissiondate,  \n" +
                "                               CASE  \n" +
                "                                 WHEN ( Date_part('day', vr.submissiondate :: timestamp -  \n" +
                "                               i.period_start_date :: timestamp) ) :: NUMERIC <=  conf.cutoff :: NUMERIC THEN 1  \n" +
                "                                 ELSE 0  \n" +
                "                               END  ontime  \n" +
                "                  FROM   vw_vaccine_coverage i  \n" +
                "                               join vw_districts d ON i.geographic_zone_id = d.district_id  \n" +
                "                               join vaccine_reports vr  ON i.report_id = vr.id  \n" +
                "                               join facilities f ON f.id = i.facility_id AND f.typeid = (SELECT id FROM facility_types WHERE code = 'dvs')\n" +
                "                               join program_products pp ON pp.programid = vr.programid AND pp.productid = i.product_id  \n" +
                "                               join product_categories pg ON pp.productcategoryid = pg.id  \n" +
                "                               join (SELECT value AS cutoff, KEY FROM configuration_settings) conf  \n" +
                "                                 ON conf.KEY = 'VACCINE_LATE_REPORTING_DAYS'  \n" +
                "                 WHERE  i.program_id = (SELECT id FROM   programs p WHERE  p.enableivdform = TRUE)  \n" +
                "                 AND i.period_start_date >= '"+startDate+"' and i.period_end_date <= '"+endDate+"'\n" +
                "                 AND i.product_id =  " +productId+""+
                                        writeDistrictPredicate(zone)  +
                "                ORDER  BY i.period_start_date) AS timeliness  \n" +
                "                GROUP  BY district_id,\n" +
                "                          district_name,\n" +
                "                          period_name,  \n" +
                "                          \"month\",  \n" +
                "                          \"year\"  \n" +
                "                ORDER  BY district_name, \n" +
                "                          \"year\",  \n" +
                "                          \"month\"";

        return sql;
    }

    private static String writeDistrictPredicate(Long zone) {

        String predicate = " ";
        if (zone != 0 && zone != null) {
            predicate = " AND (district_id = "+zone+" or zone_id = "+zone+" or region_id = "+zone+" or parent = "+zone+")";
        }
        return predicate;
    }
}
