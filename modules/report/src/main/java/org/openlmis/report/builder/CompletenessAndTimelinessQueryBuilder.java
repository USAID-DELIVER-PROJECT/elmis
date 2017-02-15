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

import org.openlmis.report.model.params.CompletenessAndTimelinessReportParam;

import java.util.Map;


public class CompletenessAndTimelinessQueryBuilder {


    public static String selectCompletenessAndTimelinessMainReportDataByDistrict(Map param) {
        CompletenessAndTimelinessReportParam params = getParamsValues(param);

        String sql = "with completeness_with_reporting_periods as (select   \n" +
                "                  a.region_name,   \n" +
                "                  a.district_name,  \n" +
                "                  a.priod_id,      \n" +
                "                  a.period_name,   \n" +
                "                  a.period_start_date,  \n" +
                "                  a.geographiczoneid,   \n" +
                "                  a.fixed,   \n" +
                "                  a.outreach,   \n" +
                "                  a.session_total,   \n" +
                "                  a.target,  \n" +
                "                  a.expected,  \n" +
                "                  a.reported,   \n" +
                "                  a.ontime,    \n" +
                "                  a.late,  \n" +
                "                  trunc((a.reported::numeric/a.expected::numeric)*100,2) percent_reported,  \n" +
                "                  trunc((a.late::numeric/a.reported::numeric)*100,2) percent_late  \n" +
                "                    \n" +
                "                from (  \n" +
                "                     with temp as (  select  pp.id priod_id, pp.name period_name,   \n" +
                "                           pp.startdate::date period_start_date,   \n" +
                "                           z.id geographiczoneid,   \n" +
                "                           z.name district,   \n" +
                "                           f.name facility_name,   \n" +
                "                           f.code facility_code,    \n" +
                "                           to_char(COALESCE(vr.submissiondate::date,vr.createddate::date) , 'DD Mon YYYY') reported_date,   \n" +
                "                           COALESCE(vr.fixedimmunizationsessions,0) fixed,   \n" +
                "                           COALESCE(vr.outreachimmunizationsessions,0) outreach,   \n" +
                "                           COALESCE(z.catchmentpopulation,0) target,   \n" +
                "                         CASE   \n" +
                "                            WHEN date_part('day'::text, COALESCE(vr.submissiondate::date,vr.createddate::date)  - pp.enddate::date::timestamp without time zone) <=  \n" +
                "                            COALESCE((( SELECT configuration_settings.value FROM configuration_settings    \n" +
                "                            WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision  \n" +
                "                            THEN 'T'::text    \n" +
                "                 \n" +
                "                            WHEN COALESCE(date_part('day'::text, COALESCE(vr.submissiondate::date,vr.createddate::date)  - pp.enddate::date::timestamp without time zone), 0::double precision) >  \n" +
                "                            COALESCE((( SELECT configuration_settings.value FROM configuration_settings    \n" +
                "                            WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision  \n" +
                "                            THEN 'L'::text    \n" +
                "                            ELSE 'N'::text    \n" +
                "                         END AS reporting_status   \n" +
                "                                from programs_supported ps    \n" +
                "                                left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid  and status in ('SUBMITTED','APPROVED','REJECTED')  \n" +
                "                                left outer join processing_periods pp on pp.id = vr.periodid   \n" +
                "                                join facilities f on f.id = ps.facilityId     \n" +
                "                                join geographic_zones z on z.id = f.geographicZoneId    \n" +
                "                                where ps.programId = (select id from programs where enableivdform = 't' limit 1)   \n" +
                "                                   and pp.startdate::date >= '" + params.getPeriodStart() + "'::date and pp.enddate::date <=  '" + params.getPeriodEnd() + "'::date \n" +
                "                                )     \n" +
                "                                select                                                \n" +
                "                                vd.region_name,   \n" +
                "                                vd.district_name, \n" +
                "                                priod_id,       \n" +
                "                                t.period_name,   \n" +
                "                                t.period_start_date,  \n" +
                "                                t.geographiczoneid,  \n" +
                "                                sum(fixed) fixed,   \n" +
                "                                sum(outreach) outreach,   \n" +
                "                                sum(fixed)+ sum(outreach) session_total,   \n" +
                "                                sum(target) target,     \n" +
                "                                (select count(*) from requisition_group_members rgm\n" +
                "join facilities f on f.id = rgm.facilityid\n" +
                "join programs_supported ps on ps.facilityid=f.id \n" +
                "join requisition_group_program_schedules rgs on rgs.programid=(select id from programs where enableivdform = 't' limit 1)" +
                " and  rgs.requisitiongroupid=rgm.requisitiongroupid and rgs.scheduleid=45\n" +
                "\n" +
                "where f.geographiczoneid = t.geographiczoneid  and f.active=true\n" +
                "and f.sdp = true \n) expected,   \n" +
                "                                sum(case when reporting_status IN ('T','L') then 1 else 0 end) reported,   \n" +
                "                                sum(case when reporting_status = 'T' then 1 else 0 end) ontime,    \n" +
                "                                sum(case when reporting_status = 'L' then 1 else 0 end) late  \n" +
                "                        from temp t   \n" +
                "                            join vw_districts vd on vd.district_id = t.geographiczoneid     \n" +
                writeDistrictPredicate(params.getDistrict()) +
                "                      where vd.district_id in (select district_id from vw_user_facilities where user_id = "+params.getUserId()+"   and program_id = fn_get_vaccine_program_id())  \n" +
                "                        group by 1, 2, 3, 4,5 ,6  \n" +
                "                  \n" +
                "                ) a   \n" +
                "                ),  \n" +
                "                                  \n" +
                "                completness_with_nonreporting_periods as ( \n" +
                "                      select c.geographiczoneid, periods.*, \n" +
                "                            ( select count(*) from requisition_group_members rgm\n" +
                "join facilities f on f.id = rgm.facilityid\n" +
                "join programs_supported ps on ps.facilityid=f.id  \n" +
                "join requisition_group_program_schedules rgs on rgs.programid=(select id from programs where enableivdform = 't' limit 1)" +
                " and  rgs.requisitiongroupid=rgm.requisitiongroupid and rgs.scheduleid=periods.scheduleid\n" +
                "\n" +
                "where f.geographiczoneid = c.geographiczoneid  and f.active=true\n" +
                "and f.sdp = true) expected \n" +
                "                        from  \n" +
                "                         ( \n" +
                "                              select id, scheduleid,name period_name, startdate period_start_date from processing_periods pp  \n" +
                "                                where pp.startdate::date >= '" + params.getPeriodStart() + "'::date and pp.enddate::date <= '" + params.getPeriodEnd() + "'::date\n" +
                "                              AND pp.numberofmonths = 1  \n" +
                "                          ) periods ,  \n" +
                "                          ( \n" +
                "                              select distinct geographiczoneid from  \n" +
                "                              completeness_with_reporting_periods c \n" +
                "                          ) c \n" +
                "                    ) \n" +
                "                 \n" +
                "                 SELECT          \n" +
                "                    vd.region_name as regionName,   \n" +
                "                    vd.district_name as districtName,  \n" +
                "                    nonreporting.period_name as periodName,  \n" +
                "                    nonreporting.period_start_date as periodStartDate,  \n" +
                "                    nonreporting.geographiczoneid as geographicZoneId,   \n" +
                "                    COALESCE(c.fixed,0) as fixed,   \n" +
                "                    COALESCE(c.outreach,0) as outreach,   \n" +
                "                    COALESCE(c.session_total,0) as sessionTotal,   \n" +
                "                    CASE WHEN c.target is null then z.catchmentpopulation ELSE c.target end as target,  \n" +
                "                    nonreporting.expected as expected,  \n" +
                "                    COALESCE(c.reported,0) as reported,   \n" +
                "                    COALESCE(c.ontime,0) as ontime,    \n" +
                "                    COALESCE(c.late,0) late,  \n" +
                "                    COALESCE(c.percent_reported,0) percentReported,  \n" +
                "                    COALESCE(c.percent_late,0) percentLate, \n" +
                "                    CASE WHEN c.geographiczoneid is null then 'NONREPORTING' else 'REPORTING' end as reportingStatus \n" +
                "                FROM completness_with_nonreporting_periods nonreporting  \n" +
                "                    join geographic_zones z on z.id = nonreporting.geographiczoneid   \n" +
                "                    join vw_districts vd on vd.district_id = nonreporting.geographiczoneid \n" +
                "                    left outer join completeness_with_reporting_periods c  On c.geographiczoneid = nonreporting.geographiczoneid      \n" +
                "                                AND nonreporting.id = c.priod_id \n" +
                "                                order by 1,2,4;";


        return sql;
    }

    public static CompletenessAndTimelinessReportParam getParamsValues(Map param) {
        return (CompletenessAndTimelinessReportParam) param.get("filterCriteria");
    }

    public static String selectCompletenessAndTimelinessSummaryReportDataByDistrict(Map param) {


        CompletenessAndTimelinessReportParam params = getParamsValues(param);

        String sql = "with completeness_with_reporting_periods as (\n" +
                "  select    \n" +
                "  a.region_name,   \n" +
                "  a.district_name,  \n" +
                "  a.priod_id,      \n" +
                "  a.period_name,   \n" +
                "  a.period_start_date,  \n" +
                "  a.geographiczoneid,    \n" +
                "  a.expected,  \n" +
                "  a.reported,   \n" +
                "  a.ontime,    \n" +
                "  a.late           \n" +
                "  from (  \n" +
                "       with temp as (  select  pp.id priod_id, pp.name period_name,   \n" +
                "       pp.startdate::date period_start_date,   \n" +
                "       z.id geographiczoneid,   \n" +
                "       z.name district,   \n" +
                "       f.name facility_name,   \n" +
                "       f.code facility_code,    \n" +
                "       to_char(vr.createdDate, 'DD Mon YYYY') reported_date,   \n" +
                "       COALESCE(vr.fixedimmunizationsessions,0) fixed,   \n" +
                "       COALESCE(vr.outreachimmunizationsessions,0) outreach,   \n" +
                "       COALESCE(z.catchmentpopulation,0) target,   \n" +
                "     CASE   \n" +
                "        WHEN date_part('day'::text,  COALESCE(vr.submissiondate::date,vr.createddate::date) - pp.enddate::date::timestamp without time zone) <=  \n" +
                "        COALESCE((( SELECT configuration_settings.value FROM configuration_settings    \n" +
                "        WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision  \n" +
                "        THEN 'T'::text    \n" +
                "        WHEN COALESCE(date_part('day'::text,  COALESCE(vr.submissiondate::date,vr.createddate::date) - pp.enddate::date::timestamp without time zone), 0::double precision) >  \n" +
                "        COALESCE((( SELECT configuration_settings.value FROM configuration_settings    \n" +
                "        WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision  \n" +
                "        THEN 'L'::text    \n" +
                "        ELSE 'N'::text    \n" +
                "     END AS reporting_status   \n" +
                "      from programs_supported ps    \n" +
                "      left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid and status in ('SUBMITTED','APPROVED','REJECTED')   \n" +
                "      left outer join processing_periods pp on pp.id = vr.periodid   \n" +
                "      join facilities f on f.id = ps.facilityId     \n" +
                "      join geographic_zones z on z.id = f.geographicZoneId    \n" +
                "      where ps.programId = (select id from programs where enableivdform = 't' limit 1)   \n" +
                "         and pp.startdate::date >= '" + params.getPeriodStart() + "' and pp.enddate::date <= '" + params.getPeriodEnd() + "'  " +
                "      )     \n" +

                "      select    " +
                "      vd.region_name,   \n" +
                "      vd.district_name, \n" +
                "      priod_id,       \n" +
                "      t.period_name,   \n" +
                "      t.period_start_date,  \n" +
                "      t.geographiczoneid,  \n" +
                "      sum(fixed) fixed,   \n" +
                "      sum(outreach) outreach,   \n" +
                "      sum(fixed) + sum(outreach) session_total,   \n" +
                "      sum(target) target,     \n" +
                "     (select count(*) from requisition_group_members rgm\n" +
                " \n" +
                "                join facilities f on f.id = rgm.facilityid \n" +
                "                join programs_supported ps on ps.facilityid=f.id  \n" +
                "                join requisition_group_program_schedules rgs on rgs.programid=(select id from programs where enableivdform = 't' limit 1) \n" +
                "                 and  rgs.requisitiongroupid=rgm.requisitiongroupid and rgs.scheduleid=45 \n" +
                "                 \n" +
                "                where f.geographiczoneid = t.geographiczoneid  and f.active=true \n" +
                "                and f.sdp = true \n" +
                ") expected,   \n" +
                "      sum(case when reporting_status IN ('T','L') then 1 else 0 end) reported,   \n" +
                "      sum(case when reporting_status = 'T' then 1 else 0 end) ontime,    \n" +
                "      sum(case when reporting_status = 'L' then 1 else 0 end) late  \n" +
                "    from temp t   \n" +
                "        join vw_districts vd on vd.district_id = t.geographiczoneid " + writeDistrictPredicate(params.getDistrict())
                + "      where vd.district_id in (select district_id from vw_user_facilities where user_id = " + params.getUserId() + " and program_id = fn_get_vaccine_program_id())  "
                + "    group by 1, 2, 3, 4,5 ,6  \n" +
                "  ) a   \n" +
                "     ),  \n" +

                "                completness_with_nonreporting_periods as ( \n" +
                "                      select c.geographiczoneid, periods.*, \n" +
                "                            (select count(*) from requisition_group_members rgm\n" +
                " \n" +
                "                join facilities f on f.id = rgm.facilityid \n" +
                "                join programs_supported ps on ps.facilityid=f.id  \n" +
                "                join requisition_group_program_schedules rgs on rgs.programid=(select id from programs where enableivdform = 't' limit 1) \n" +
                "                 and  rgs.requisitiongroupid=rgm.requisitiongroupid and rgs.scheduleid=45 \n" +
                "                 \n" +
                "                where f.geographiczoneid = c.geographiczoneid  and f.active=true \n" +
                "                and f.sdp = true \n" +
                ") expected\n" +
                "                        from  \n" +
                "                         ( \n" +
                "                              select id, name period_name, startdate period_start_date from processing_periods pp  \n" +
                "                                where pp.startdate::date >= '" + params.getPeriodStart() + "' and pp.enddate::date <= '" + params.getPeriodEnd() + "' \n" +
                "                              AND pp.numberofmonths = 1 \n" +
                "                          ) periods ,  \n" +
                "                          ( \n" +
                "                              select distinct geographiczoneid from  \n" +
                "                              completeness_with_reporting_periods c \n" +
                "                          ) c \n" +
                "                    ) \n" +

                "      SELECT         " +
                "      nonreporting.period_name,  \n" +
                "      nonreporting.period_start_date,\n" +
                "      Extract(month FROM nonreporting.period_start_date) as month," +
                "      Extract(year FROM nonreporting.period_start_date) as year,\n" +
                "      SUM(nonreporting.expected)  expected,  \n" +
                "      SUM(COALESCE(c.reported,0)) reported,   \n" +
                "      SUM(COALESCE(c.ontime,0))   ontime,    \n" +
                "      SUM(COALESCE(c.late,0))     late    \n" +
                "      FROM completness_with_nonreporting_periods nonreporting  \n" +
                "      join geographic_zones z on z.id = nonreporting.geographiczoneid   \n" +
                "      join vw_districts vd on vd.district_id = nonreporting.geographiczoneid \n" +
                "      left outer join completeness_with_reporting_periods c  On c.geographiczoneid = nonreporting.geographiczoneid AND nonreporting.id = c.priod_id \n" +
                "      group by 1,2 order by 2";
                
        
                /*"SELECT " +
                "    a.year,  \n" +
                "    a.month,  \n" +
                "    sum(a.expected) expected,\n" +
                "    sum(a.reported) reported,  \n" +
                "    sum(a.ontime) ontime,  \n" +
                "    sum(a.late) late \n"+
                "FROM   ( WITH temp AS \n" +
                "       ( \n" +
                "                 SELECT    pp.NAME                                period_name, \n" +
                "                           Extract(month FROM pp.startdate)       period_month, \n" +
                "                           Extract(year FROM pp.startdate)        period_year, \n" +
                "                           pp.startdate::date                     period_start_date, \n" +
                "                           z.id                                   geographiczoneid, \n" +
                "                           z.NAME                                 district, \n" +
                "                           f.NAME                                 facility_name, \n" +
                "                           f.code                                 facility_code, \n" +
                "                           to_char(vr.createddate, 'DD Mon YYYY') reported_date,   \n" +
                "                                 CASE \n" +
                "                                        WHEN date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone) <= COALESCE((( SELECT configuration_settings.value  \n" +
                "                                           FROM configuration_settings  \n" +
                "                                          WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'T'::text  \n" +
                "                                        WHEN COALESCE(date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value  \n" +
                "                                           FROM configuration_settings  \n" +
                "                                          WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'L'::text  \n" +
                "                                        ELSE 'N'::text  \n" +
                "                                 END AS reporting_status  \n" +
                "                                from programs_supported ps  \n" +
                "                                left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid  \n" +
                "                                left join processing_periods pp on pp.id = vr.periodid       \n" +
                "                                join facilities f on f.id = ps.facilityId   \n" +
                "                                join geographic_zones z on z.id = f.geographicZoneId  \n" +
                "                            where ps.programId = (select id from programs where enableivdform = 't' limit 1) \n" +
                "                                 and pp.startdate::date >= '"+startDate+"' and pp.enddate::date <= '"+endDate+"'  \n" +
                "                            )   \n" +
                "                         select  \n" +
                "                            t.period_name, \n" +
                "                            t.period_month  as month, \n" +
                "                            t.period_year  as year,  \n" +
                "                            t.geographiczoneid, \n" +
                "                            ( select count(*) from programs_supported ps  join facilities f on ps.facilityid = f.id  \n" +
                "                              join vw_districts vd on f.geographiczoneid = vd.district_id  \n" +
                "                             where ps.programid = (select id from programs where enableivdform = 't' limit 1)  \n" +
                "                             and district_id = t.geographiczoneid  \n" +
                "                            ) expected,  \n" +
                "                            sum(case when reporting_status IN ('T','L') then 1 else 0 end) reported, \n" +
                "                            sum(case when reporting_status = 'T' then 1 else 0 end) ontime,  \n" +
                "                            sum(case when reporting_status = 'L' then 1 else 0 end) late                             \n" +
                "                from temp t \n" +
                "                join vw_districts vd on vd.district_id = t.geographiczoneid  \n" +  writeDistrictPredicate(zone)  +
                "                group by 1, 2, 3, 4\n" +
                "                \n" +
                ") as a\n" +
                " group by 1,2 " +
                " order by 1, 2, 3";
                */
        return sql;
    }


    private static String writeDistrictPredicate(Long zone) {

        String predicate = " ";
        if (zone != 0 && zone != null) {
            predicate = " AND (district_id = " + zone + " or zone_id = " + zone + " or region_id = " + zone + " or parent = " + zone + ")";
        }
        return predicate;
    }
}
