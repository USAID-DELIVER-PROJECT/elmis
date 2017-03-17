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
package org.openlmis.vaccine.repository.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface VaccineDashboardMapper {

        /*
         * Action Bar
         * TODO: Add userid parameter to summary dashlets
        */
        @Select("with temp as ( \n" +
                "            select vd.district_name district, f.name facility_name, f.code facility_code, \n" +
                "                   to_char(vr.createdDate, 'DD Mon YYYY') reported_date,  \n" +
                "                 CASE \n" +
                "                        WHEN date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone) <= COALESCE((( SELECT configuration_settings.value \n" +
                "                           FROM configuration_settings \n" +
                "                          WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'T'::text \n" +
                "                        WHEN COALESCE(date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value \n" +
                "                           FROM configuration_settings \n" +
                "                          WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'L'::text \n" +
                "                        ELSE 'N'::text \n" +
                "                 END AS reporting_status \n" +
                "                from programs_supported ps \n" +
                "                left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid and vr.periodid = fn_get_vaccine_current_reporting_period() \n" +
                "                left join processing_periods pp on pp.id = vr.periodid      \n" +
                "                join facilities f on f.id = ps.facilityId  \n" +
                "                join vw_districts vd on f.geographiczoneid = vd.district_id \n" +
                "            where ps.programId = (select id from programs where enableivdform = 't' limit 1)\n" +
                "            and (vd.district_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)) or \n" +
                "                 vd.region_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)))  \n" +
                "            )  \n" +
                "            select \n" +
                "            sum(1) expected, \n" +
                "            sum(case when reporting_status = 'T' then 1 else 0 end) ontime, \n" +
                "            sum(case when reporting_status = 'L' then 1 else 0 end) late, \n" +
                "            sum(case when reporting_status = 'N' then 1 else 0 end) not_reported  \n" +
                "             from temp t")
        Map<String, Object> getReportingSummary(@Param("userId") Long userId);

        /* */
        @Select("with temp as ( \n" +
                "            select vd.district_name district, f.name facility_name, f.code facility_code, f.id facility_id," +
                " (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts, \n" +
                "                   to_char(vr.createdDate, 'DD Mon YYYY') reported_date,  \n" +
                "                 CASE \n" +
                "                        WHEN date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone) <= COALESCE((( SELECT configuration_settings.value \n" +
                "                           FROM configuration_settings \n" +
                "                          WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'T'::text \n" +
                "                        WHEN COALESCE(date_part('day'::text, vr.createddate::date - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value \n" +
                "                           FROM configuration_settings \n" +
                "                          WHERE configuration_settings.key::text = 'VACCINE_LATE_REPORTING_DAYS'::text))::integer, 0)::double precision THEN 'L'::text \n" +
                "                        ELSE 'N'::text \n" +
                "                    END AS reporting_status, f.mainphone \n" +
                "                from programs_supported ps \n" +
                "                left join vaccine_reports vr on vr.programid = ps.programid and vr.facilityid = ps.facilityid and vr.periodid = fn_get_vaccine_current_reporting_period() \n" +
                "                left join processing_periods pp on pp.id = vr.periodid      \n" +
                "                join facilities f on f.id = ps.facilityId  \n" +
                "                join vw_districts vd on f.geographiczoneid = vd.district_id \n" +
                "            where ps.programId = (select id from programs where enableivdform = 't' limit 1)\n" +
                "            and (vd.district_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)) or \n" +
                "                 vd.region_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)))              \n" +
                "            ) select district, facility_name, facility_code, reported_date, reporting_status,facility_id, hasContacts, mainphone  from temp t")
        List<HashMap<String, Object>> getReportingDetails(@Param("userId") Long userId);

        /* */
        @Select("select count(1) repairing from ( \n" +
                "               select facility_id, facility_name, geographic_zone_name district, equipment_name, model, yearofinstallation year_installed, period_start_date::date date_reported \n" +
                "                 from vw_vaccine_cold_chain cc\n" +
                "                 join vw_districts vd on cc.geographic_zone_id = vd.district_id \n" +
                "                 where upper(status) = 'NOT FUNCTIONAL' and programid = fn_get_vaccine_program_id()      \n" +
                "                 and period_id = fn_get_vaccine_current_reporting_period()\n" +
                "                 and (vd.district_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)) or  \n" +
                "                      vd.region_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer))) \n" +
                "            ) a")
        Map<String, Object> getRepairingSummary(@Param("userId") Long userId);

        /* */
        @Select("select facility_id, facility_name, geographic_zone_name district, equipment_name, model, \n" +
                "         yearofinstallation year_installed, period_start_date::date date_reported \n" +
                "                     from vw_vaccine_cold_chain cc\n" +
                "                      join vw_districts vd on cc.geographic_zone_id = vd.district_id \n" +
                "                      where upper(status) = 'NOT FUNCTIONAL' and programid = fn_get_vaccine_program_id()   \n" +
                "                      and period_id = fn_get_vaccine_current_reporting_period()\n" +
                "                      and (vd.district_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)) or  \n" +
                "                      vd.region_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer))\n" +
                "                      ) ")
        List<HashMap<String, Object>> getRepairingDetails(@Param("userId") Long userId);

        /* */
        @Select(" select count(1) from ( \n" +
                "                 select facility_code, facility_name, geographic_zone_name district, aefi_case, aefi_batch, aefi_date, aefi_notes \n" +
                "                   from vw_vaccine_iefi i\n" +
                "                    join vw_districts vd on i.geographic_zone_id = vd.district_id \n" +
                "                    where is_investigated = 'f'  \n" +
                "                    and relatedtolineitemid is null \n" +
                "                    and program_id = (select id from programs where enableivdform = 't' limit 1)  \n" +
                "                    and  period_id = fn_get_vaccine_current_reporting_period()\n" +
                "		             and (vd.district_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)) or  \n" +
                "                    vd.region_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer))\n" +
                "                     )  \n" +
                "                ) a ")
        Map<String, Object> getInvestigatingSummary(@Param("userId") Long userId);

        /* */
        @Select(" select facility_code, facility_name, geographic_zone_name district, aefi_case,product_name, aefi_batch, aefi_date, aefi_notes,aefi_expiry_date,manufacturer \n" +
                "                   from vw_vaccine_iefi i\n" +
                "                    join vw_districts vd on i.geographic_zone_id = vd.district_id \n" +
                "                    where is_investigated = 'f'  \n" +
                "                    and program_id = fn_get_vaccine_program_id()  \n" +
                "                    and  period_id = fn_get_vaccine_current_reporting_period()\n" +
                "                    and (vd.district_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer)) or  \n" +
                "                      vd.region_id = (select geographiczoneid from fn_get_user_preferences(#{userId}::integer))\n" +
                "                     )  ")
        List<HashMap<String, Object>> getInvestigatingDetails(@Param("userId") Long userId);

/* End Action Bar */

       /* @Select("SELECT\n" +
                "d.region_name,\n" +
                "d.district_name,\n" +
                "i.period_name, \n" +
                "i.period_start_date,\n" +
                "sum(i.denominator) target, \n" +
                "sum(COALESCE(i.within_outside_total,0)) actual,\n" +
                "(case when sum(denominator) > 0 then (sum(COALESCE(i.within_outside_total,0)) / \n" +
                "sum(denominator)::numeric) else 0 end) * 100 coverage\n" +
                "FROM\n" +
                "vw_vaccine_coverage i\n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
                "JOIN program_products pp ON pp.programid = vr.programid\n" +
                "AND pp.productid = i.product_id\n" +
                "WHERE\n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1) \n" +
                "AND i.period_start_date::date>= #{startDate} and i.period_end_date::date <= #{endDate}\n" +
                "and i.product_id = #{product} \n" +
                "group by 1,2,3,4\n" +
                "ORDER BY\n" +
                "d.region_name,\n" +
                "d.district_name,\n" +
                "i.period_start_date")
        List<HashMap<String, Object>> getMonthlyCoverage(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product);*/

        /*
         * ---------------- Coverage ------------------------------------
        */

        String vaccineDistrictCoverageDenominatorSql = "fn_get_vaccine_coverage_district_denominator(" +
                "       (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int," +
                "       (select extract(year from startdate) from processing_periods where id = #{period} )::int," +
                "       #{product}::int," +
                "       fn_get_vaccine_program_id()::int" +
                "  )";
        String vaccineDistrictCoverageDenominatorWthPeriodStartDateSql = "fn_get_vaccine_coverage_district_denominator(" +
                "       (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int," +
                "       (select extract(year from  #{startDate}::date))::int," +
                "       #{product}::int," +
                "       fn_get_vaccine_program_id()::int" +
                "  )";

        @Select("SELECT\n" +
                "d.region_name,\n" +
                "i.period_name,\n" +
                "i.period_start_date,\n" +
                "round("+vaccineDistrictCoverageDenominatorWthPeriodStartDateSql+") target, \n" +
                "sum(COALESCE(i.within_outside_total,0)) actual, \n" +
                "round((case when sum("+vaccineDistrictCoverageDenominatorWthPeriodStartDateSql+") > 0 then (sum(COALESCE(i.within_outside_total,0)) / round("+vaccineDistrictCoverageDenominatorWthPeriodStartDateSql+")::numeric) else 0 end) * 100) coverage \n" +
                "FROM \n" +
                "vw_vaccine_coverage i \n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID \n" +
                "JOIN program_products pp ON pp.programid = vr.programid \n" +
                "AND pp.productid = i.product_id \n" +
                "WHERE \n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1) \n" +
                "and i.period_start_date >= #{startDate} and i.period_end_date <= #{endDate}\n" +
                "and i.product_id = #{product} \n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or d.region_id = (select value from user_preferences up where up.userid =  #{user}  and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                ")\n" +
                "GROUP BY 1,2,3 \n" +
                "ORDER BY 3;")
        List<HashMap<String, Object>> getMonthlyCoverage(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("user") Long userId, @Param("product") Long product);

        @Select("\n" +
                "SELECT\n" +
                "d.district_name geographic_zone_name,\n" +
                "round("+vaccineDistrictCoverageDenominatorSql+") target, \n" +
                "sum(COALESCE(i.within_outside_total,0)) actual,\n" +
                "round((case when sum("+vaccineDistrictCoverageDenominatorSql+") > 0 then (sum(COALESCE(i.within_outside_total,0)) / round("+vaccineDistrictCoverageDenominatorSql+")::numeric) else 0 end) * 100) coverage \n" +
                "FROM\n" +
                "vw_vaccine_coverage i\n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
                "JOIN program_products pp ON pp.programid = vr.programid\n" +
                "AND pp.productid = i.product_id\n" +
                "  and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "  or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "WHERE\n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1) \n" +
                "AND i.period_id = #{period}\n" +
                "and i.product_id = #{product}\n" +
                "group by 1\n" +
                "ORDER BY\n" +
                "d.district_name\n")
        List<HashMap<String, Object>> getDistrictCoverage(@Param("period") Long period, @Param("product") Long product,@Param("user") Long user);

        @Select("SELECT\n" +
                "d.district_name, \n" +
                "i.facility_name,\n" +
                "round(sum(i.denominator)) target, \n" +
                "sum(COALESCE(i.within_outside_total,0)) actual, \n" +
                "round((case when sum(denominator) > 0 then (sum(COALESCE(i.within_outside_total,0)) / round(sum(denominator))::numeric) else 0 end) * 100) coverage \n" +
                "FROM \n" +
                "vw_vaccine_coverage i \n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID \n" +
                "JOIN program_products pp ON pp.programid = vr.programid \n" +
                "AND pp.productid = i.product_id \n" +
                "WHERE \n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1) \n" +
                "and i.product_id = #{product} \n" +
                "and i.period_id = #{period}\n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)\n" +
                "GROUP BY 1,2\n" +
                "ORDER BY 2;\n")
        List<HashMap<String, Object>> getFacilityCoverage(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        @Select("SELECT\n" +
                "d.district_name,\n" +
                "i.facility_name,\n" +
                "d.district_name || i.facility_name key_val," +
                "i.period_name,\n" +
                "i.period_start_date,\n" +
                "round(sum(i.denominator)) target, \n" +
                "sum(COALESCE(i.within_outside_total,0)) actual, \n" +
                "round((case when sum(denominator) > 0 then (sum(COALESCE(i.within_outside_total,0)) / round(sum(denominator))::numeric) else 0 end) * 100) coverage \n" +
                "FROM \n" +
                "vw_vaccine_coverage i \n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id \n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID \n" +
                "JOIN program_products pp ON pp.programid = vr.programid \n" +
                "AND pp.productid = i.product_id \n" +
                "WHERE \n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1) \n" +
                "and i.period_start_date::date >= #{startDate} and i.period_end_date::date <= #{endDate}\n" +
                "and i.product_id =#{product}\n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                ")\n" +
                "GROUP BY 1,2,3, 4,5\n" +
                "ORDER BY 4,2;")
        List<HashMap<String, Object>> getFacilityCoverageDetails(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product, @Param("user") Long user);

        /*
         * ---------------- Dropout ------------------------------------
        */
        @Select("SELECT\n" +
                "i.period_name,\n" +
                "i.period_start_date, \n" +
                "sum(i.bcg_1) bcg_vaccinated, \n" +
                "sum(i.dtp_1) dtp1_vaccinated,\n" +
                "sum(i.mr_1) mr_vaccinated, \n" +
                "sum(i.dtp_3) dtp3_vaccinated,\n" +
                "case when sum(COALESCE(i.bcg_1,0)) > 0 then ( (sum(COALESCE(i.bcg_1,0)) - sum(COALESCE(i.mr_1,0))) / sum(COALESCE(i.bcg_1,0))::numeric) * 100 else 0 end bcg_mr_dropout, \n" +
                "case when sum(COALESCE(i.dtp_1,0)) > 0 then ( (sum(COALESCE(i.dtp_1,0)) - sum(COALESCE(i.dtp_3,0))) / sum(COALESCE(i.dtp_1,0))::numeric) * 100 else 0 end dtp1_dtp3_dropout\n" +
                "FROM\n" +
                "vw_vaccine_dropout i\n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "JOIN vaccine_reports vr ON i.report_id = vr. ID\n" +
                "JOIN program_products pp ON pp.programid = vr.programid\n" +
                "AND pp.productid = i.product_id\n" +
                "JOIN product_categories pg ON pp.productcategoryid = pg. ID\n" +
                "WHERE\n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE )\n" +
                "AND i.period_start_date >= #{startDate} and i.period_end_date <= #{endDate}\n" +
                "and i.product_id = #{product}\n" +
                "group by 1,2\n" +
                "order by 2")
        List<HashMap<String, Object>> getMonthlyDropout(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long productId);

        /* */
        @Select("SELECT\n" +
                "i.geographic_zone_id,\n" +
                "i.geographic_zone_name,\n" +
                "sum(i.bcg_1) bcg_vaccinated, \n" +
                "sum(i.dtp_1) dtp1_vaccinated,\n" +
                "sum(i.mr_1) mr_vaccinated, \n" +
                "sum(i.dtp_3) dtp3_vaccinated,\n" +
                "case when sum(COALESCE(i.bcg_1,0)) > 0 then ( (sum(COALESCE(i.bcg_1,0)) - sum(COALESCE(i.mr_1,0))) / sum(COALESCE(i.bcg_1,0))::numeric) * 100 else 0 end bcg_mr_dropout, \n" +
                "case when sum(COALESCE(i.dtp_1,0)) > 0 then ( (sum(COALESCE(i.dtp_1,0)) - sum(COALESCE(i.dtp_3,0))) / sum(COALESCE(i.dtp_1,0))::numeric) * 100 else 0 end dtp1_dtp3_dropout\n" +
                "FROM\n" +
                "vw_vaccine_dropout i\n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
                "JOIN vaccine_reports vr ON i.report_id = vr. ID\n" +
                "JOIN program_products pp ON pp.programid = vr.programid\n" +
                "AND pp.productid = i.product_id\n" +
                "JOIN product_categories pg ON pp.productcategoryid = pg. ID\n" +
                "WHERE\n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p.enableivdform = TRUE )\n" +
                "AND i.period_id = #{period}\n" +
                "and i.product_id = #{product} \n" +
                " and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "              or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "group by 1,2\n" +
                "order by 2")
        List<HashMap<String, Object>> getDistrictDropout(@Param("period") Long period, @Param("product") Long productId,@Param("user") Long user);

        /* */
        @Select("\n" +
                "SELECT \n" +
                "d.district_name,  \n" +
                "i.facility_name,\n" +
                "i.bcg_1 bcg_vaccinated, \n" +
                "i.dtp_1 dtp1_vaccinated,\n" +
                "i.mr_1 mr_vaccinated, \n" +
                "i.dtp_3 dtp3_vaccinated,\n" +
                "case when i.bcg_1 > 0 then(i.bcg_1 - i.mr_1) / i.bcg_1::numeric * 100 else 0 end bcg_mr_dropout, \n" +
                "case when i.dtp_1 > 0 then(i.dtp_1 - i.dtp_3) / i.dtp_1::numeric * 100 else 0 end dtp1_dtp3_dropout \n" +
                "FROM  \n" +
                "vw_vaccine_dropout i  \n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id  \n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID  \n" +
                "JOIN program_products pp ON pp.programid = vr.programid  \n" +
                "AND pp.productid = i.product_id  \n" +
                "WHERE  \n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and i.product_id = #{product}  \n" +
                "and i.period_id = #{period} \n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "ORDER BY 1,2;")
        List<HashMap<String, Object>> getFacilityDropout(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        /* */
        @Select("SELECT \n" +
                "d.district_name,  \n" +
                "i.facility_name,\n" +
                "d.district_name || i.facility_name key_val," +
                "i.period_name,\n" +
                "i.period_start_date,\n" +
                "i.bcg_1 bcg_vaccinated, \n" +
                "i.dtp_1 dtp1_vaccinated,\n" +
                "i.mr_1 mr_vaccinated, \n" +
                "i.dtp_3 dtp3_vaccinated,\n" +
                "case when i.bcg_1 > 0 then(i.bcg_1 - i.mr_1) / i.bcg_1::numeric * 100 else 0 end bcg_mr_dropout, \n" +
                "case when i.dtp_1 > 0 then(i.dtp_1 - i.dtp_3) / i.dtp_1::numeric * 100 else 0 end dtp1_dtp3_dropout \n" +
                "FROM  \n" +
                "vw_vaccine_dropout i  \n" +
                "JOIN vw_districts d ON i.geographic_zone_id = d.district_id  \n" +
                "JOIN vaccine_reports vr ON i.report_id = vr.ID  \n" +
                "JOIN program_products pp ON pp.programid = vr.programid  \n" +
                "AND pp.productid = i.product_id  \n" +
                "WHERE  \n" +
                "i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and i.product_id = #{product}  \n" +
                "and i.period_start_date::date >= #{startDate} and i.period_end_date::date <= #{endDate}\n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "ORDER BY 4,1,2;")
        List<HashMap<String, Object>> getFacilityDropoutDetails(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product, @Param("user") Long user);


/* End Drop Out */

        /*
         * ---------------- Wastage ------------------------------------
        */
        @Select("with temp as (\n" +
                "select period_name, period_start_date::date," +
                " sum(COALESCE(vaccinated,0)) vaccinated,\n" +
                " sum(COALESCE(usage_denominator,0)) usage_denominator, \n" +
                "CASE WHEN sum(COALESCE(usage_denominator,0)) > 0 \n" +
                "THEN (100 - round(sum(COALESCE(vaccinated,0)) / (sum(COALESCE(usage_denominator,0))), 4) * 100)\n" +
                "else 0\n" +
                "END wastage_rate\n" +
                "from vw_vaccine_stock_status vss\n" +
                "where vss.period_start_date >= #{startDate} and vss.period_end_date <= #{endDate}\n" +
                "and product_id = #{product}\n" +
                "and vss.product_category_code = 'Vaccine'\n" +
                "group by 1,2 \n" +
                ")select t.period_name, t.period_start_date, wastage_rate, t.vaccinated, t.usage_denominator\n" +
                "from temp t\n" +
                "where wastage_rate > 0\n" +
                "order by 2")
        List<HashMap<String, Object>> getMonthlyWastage(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product);

        /* */
        @Select("with temp as (\n" +
                "select geographic_zone_name,\n" +
                " sum(COALESCE(vaccinated,0)) vaccinated,\n" +
                " sum(COALESCE(usage_denominator,0)) usage_denominator, \n" +
                "CASE WHEN sum(COALESCE(usage_denominator,0)) > 0 \n" +
                "THEN (100 - round(sum(COALESCE(vaccinated,0)) / (sum(COALESCE(usage_denominator,0))), 4) * 100)\n" +
                "else 0\n" +
                "END wastage_rate\n" +
                "from vw_vaccine_stock_status vss\n" +
                "  left join vw_districts vd on  vss.geographic_zone_id=vd.district_id \n" +
                "where vss.period_id = #{period}\n" +
                "and product_id = #{product}\n" +
                "and vss.product_category_code = 'Vaccine'\n" +
                " and (vd.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "              or vd.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "group by 1 )\n" +
                "select t.geographic_zone_name, wastage_rate, t.vaccinated, t.usage_denominator\n" +
                "from temp t\n" +
                "where wastage_rate > 0\n")
        List<HashMap<String, Object>> getWastageByDistrict(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        /* */
        @Select("SELECT \n" +
                "d.district_name,  \n" +
                "ss.facility_name,\n" +
                " COALESCE(vaccinated,0) vaccinated,\n" +
                "COALESCE(usage_denominator,0) usage_denominator, \n" +
                "usage_rate,\n" +
                "wastage_rate \n" +
                "FROM  \n" +
                "vw_vaccine_stock_status ss  \n" +
                "JOIN vw_districts d ON ss.geographic_zone_id = d.district_id  \n" +
                "JOIN vaccine_reports vr ON ss.report_id = vr.ID  \n" +
                "JOIN program_products pp ON pp.programid = vr.programid  \n" +
                "AND pp.productid = ss.product_id  \n" +
                "WHERE  \n" +
                "ss.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and ss.product_id = #{product}  \n" +
                "and ss.period_id = #{period} \n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "ORDER BY 2;")
        List<HashMap<String, Object>> getFacilityWastage(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        /* */
        @Select("\n" +
                "SELECT \n" +
                "d.district_name,  \n" +
                "ss.facility_name,\n" +
                "d.district_name || ss.facility_name key_val," +
                "usage_rate,\n" +
                "wastage_rate ,\n" +
                "ss.period_start_date,\n" +
                "ss.period_name \n" +
                "FROM  \n" +
                "vw_vaccine_stock_status ss  \n" +
                "JOIN vw_districts d ON ss.geographic_zone_id = d.district_id  \n" +
                "JOIN vaccine_reports vr ON ss.report_id = vr.ID  \n" +
                "JOIN program_products pp ON pp.programid = vr.programid  \n" +
                "AND pp.productid = ss.product_id  \n" +
                "WHERE  \n" +
                "ss.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and ss.product_id = #{product}  \n" +
                "and ss.period_start_date::date >= #{startDate} and  ss.period_end_date::date <= #{endDate}\n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "ORDER BY 5,2;")
        List<HashMap<String, Object>> getFacilityWastageDetails(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product, @Param("user") Long user);

/* End Wastage */

        /*
         * ---------------- Sessions ------------------------------------
        */
        @Select("with temp as (\n" +
                "select\n" +
                "period_name,\n" +
                "period_start_date, \n" +
                "COALESCE(fixed_sessions,0) fixed_sessions,\n" +
                "COALESCE(outreach_sessions,0) outreach_sessions\n" +
                "from vw_vaccine_sessions\n" +
                "where period_start_date::date >= #{startDate} and period_end_date::date <= #{endDate})\n" +
                "select \n" +
                "t.period_name,\n" +
                "t.period_start_date,\n" +
                "sum(t.fixed_sessions) fixed_sessions, \n" +
                "sum(t.outreach_sessions) outreach_sessions\n" +
                "from temp t\n" +
                "group by 1,2\n" +
                "order by 2 ")
        List<HashMap<String, Object>> getMonthlySessions(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        /* */
        @Select("with temp as \n" +
                "( select \n" +
                "geographic_zone_name,\n" +
                "COALESCE(fixed_sessions,0) fixed_sessions,\n" +
                "COALESCE(outreach_sessions,0) outreach_sessions,\n" +
                "COALESCE(fixed_sessions,0) + COALESCE(outreach_sessions,0) total_sessions\n" +
                "from vw_vaccine_sessions\n" +
                " left join vw_districts vd on  geographic_zone_id=vd.district_id \n" +
                "where period_id = #{period}\n" +
                "  and (vd.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "              or vd.region_id = (select value from user_preferences up where up.userid =  #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                ")\n" +
                "select \n" +
                "t.geographic_zone_name,\n" +
                "sum(t.fixed_sessions) fixed_sessions, \n" +
                "sum(t.outreach_sessions) outreach_sessions,\n" +
                "sum(t.total_sessions) total_sessions\n" +
                "from temp t\n" +
                "where total_sessions > 0\n" +
                "group by 1\n" +
                "order by total_sessions desc\n" +
                "limit 5\n")
        List<HashMap<String, Object>> getDistrictSessions(@Param("period") Long period,@Param("user") Long user);

        /* */
        @Select("SELECT \n" +
                "d.district_name,  \n" +
                "s.facility_name,\n" +
                "COALESCE(fixed_sessions,0) fixed_sessions, \n" +
                "COALESCE(outreach_sessions,0) outreach_sessions\n" +
                "from vw_vaccine_sessions  s  \n" +
                "JOIN vw_districts d ON s.geographic_zone_id = d.district_id  \n" +
                "WHERE\n" +
                "s.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and s.period_id = #{period} \n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "ORDER BY 2;")
        List<HashMap<String, Object>> getFacilitySessions(@Param("period") Long period, @Param("user") Long user);

        /* */
        @Select("SELECT \n" +
                "d.district_name,  \n" +
                "s.facility_name,\n" +
                "d.district_name || s.facility_name key_val," +
                "s.period_name,\n" +
                "s.period_start_date,\n" +
                "COALESCE(fixed_sessions,0) fixed_sessions, \n" +
                "COALESCE(outreach_sessions,0) outreach_sessions\n" +
                "from vw_vaccine_sessions  s  \n" +
                "JOIN vw_districts d ON s.geographic_zone_id = d.district_id  \n" +
                "WHERE\n" +
                "s.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and s.period_start_date::date >= #{startDate}  and s.period_end_date::date <= #{endDate} \n" +
                "and (d.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int) \n" +
                "ORDER BY 4,2;")
        List<HashMap<String, Object>> getFacilitySessionsDetails(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("user") Long user);

/* End Session */

        /*
         * ---------------- Bundling ------------------------------------
        */
        @Select("select \n" +
                "vvb.programid, \n" +
                "vvb.periodid, \n" +
                "vvb.period_name,\n" +
                "vvb.period_start_date,\n" +
                "vvb.period_end_date,\n" +
                "vvb.facilityid,\n" +
                "vvb.productid, \n" +
                "vvb.sup_received,\n" +
                "vvb.sup_closing,\n" +
                "vvb.vac_received, \n" +
                "vvb.vac_closing,\n" +
                "vvb.bund_received,\n" +
                "vvb.bund_issued,\n" +
                "vb.minlimit,\n" +
                "vb.maxlimit\n" +
                "from vw_vaccine_bundles vvb\n" +
                "join vaccine_bundles vb on vvb.programid = vb.programid and vvb.productid = vb.productid\n" +
                "where vvb.productid = #{product}\n" +
                "and vvb.period_start_date >= #{startDate} and vvb.period_end_date <= #{endDate}")
        List<HashMap<String, Object>> getBundling(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long productId);

/* End Bundling */

/*
 * ---------------- Stock -----------------------------------
*/

        @Select("WITH TEMP AS (\n" +
                "SELECT\n" +
                "vss.period_name,\n" +
                "vss.period_start_date::date period_start,\n" +
                "COALESCE(vss.closing_balance,0)*220 cb,\n" +
                "COALESCE(vss.quantity_issued,0) issued\n" +
                "FROM\n" +
                "vw_vaccine_stock_status vss\n" +
                "where period_start_date >= #{startDate} and period_end_date <= #{endDate} and product_id = #{product} \n" +
                "ORDER BY\n" +
                "period_start_date\n" +
                ") SELECT\n" +
                "T .period_name,\n" +
                "T .period_start,\n" +
                "case when sum(t.issued) > 0 then round((sum(t.cb) / sum(t.issued)::numeric),1) else 0 end mos\n" +
                "FROM\n" +
                "TEMP T\n" +
                "group by 1,2\n" +
                "order by 2")
        List<HashMap<String, Object>> getMonthlyStock(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long productId);

        /* */
        @Select("WITH TEMP AS (\n" +
                "SELECT\n" +
                "vss.period_name,\n" +
                "vss.geographic_zone_name geographic_zone_name,\n" +
                "COALESCE(vss.closing_balance,0)*220 cb,\n" +
                "COALESCE(vss.quantity_issued,0) issued\n" +
                "FROM\n" +
                "vw_vaccine_stock_status vss\n" +
                "where period_id =#{period} and product_id =#{product}\n" +
                ") SELECT\n" +
                "T.geographic_zone_name,\n" +
                "case when sum(t.issued) > 0 then round((sum(t.cb) / sum(t.issued)::numeric),1) end mos\n" +
                "FROM\n" +
                "TEMP T\n" +
                "group by 1\n" +
                "order by 2\n" +
                "limit 5")
        List<HashMap<String, Object>> getDistrictStock(@Param("period") Long period, @Param("product") Long productId);


        @Select("WITH TEMP AS (\n" +
                "SELECT \n" +
                "vss.facility_id,\n" +
                "vss.facility_name,\n" +
                "COALESCE(vss.closing_balance,0)*220 cb, \n" +
                "COALESCE(vss.quantity_issued,0) issued \n" +
                "FROM \n" +
                " vw_vaccine_stock_status vss \n" +
                " JOIN vw_districts d ON vss.geographic_zone_id = d.district_id   \n" +
                "where period_id =#{period} and product_id =#{product}\n" +
                "and vss.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and (d.district_id  = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int  \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)   \n" +
                "ORDER BY period_start_date \n" +
                ") " +
                "SELECT \n" +
                "T .facility_id, \n" +
                "T .facility_name, \n" +
                "case when sum(t.issued) > 0 then round((sum(t.cb) / sum(t.issued)::numeric),1) else 0 end mos \n" +
                "FROM \n" +
                "TEMP T \n" +
                "group by 1,2 \n" +
                "order by 2\n")
        List<HashMap<String, Object>> getFacilityStock(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        @Select("WITH TEMP AS (\n" +
                "SELECT \n" +
                "d.district_name,\n" +
                "vss.facility_name,\n" +
                "vss.period_name,\n" +
                "vss.period_start_date,\n" +
                "COALESCE(vss.closing_balance,0) cb, \n" +
                "COALESCE(vss.quantity_issued,0) issued , product_id\n" +
                "FROM \n" +
                " vw_vaccine_stock_status vss \n" +
                " JOIN vw_districts d ON vss.geographic_zone_id = d.district_id   \n" +
                "where period_start_date >= #{startDate} and period_end_date <= #{endDate} and product_id = #{product}\n" +
                "and vss.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1)  \n" +
                "and (d.district_id  = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int  \n" +
                "or d.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)   \n" +
                "\t\n" +
                ") SELECT \n" +
                "T.district_name, \n" +
                "T.facility_name,\n" +
                "T.period_name,\n" +
                "T.period_start_date,\n" +
                "SUM(T.cb) cb,\n" +
                "SUM(T.issued) issued\n" +
                "FROM \n" +
                "TEMP T \n" +
                "group by 1,2,3,4\n" +
                "order by 1,2,4")
        List<HashMap<String, Object>> getFacilityStockDetail(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product,
                                                             @Param("user") Long user);


        @Select("\n" +
                "select count(*) from geographic_zones  gz  \n" +
                "            join geographic_levels gl on gz.levelid= gl.id  \n" +
                "             where gl.code='dist' and  \n" +
                "            gz.id= (select value::integer from user_preferences \n" +
                "            where userid=2 and userpreferencekey='DEFAULT_GEOGRAPHIC_ZONE' limit 1)")

        public Long isDistrictUser(@Param("userId") Long userId);


        /* End Stock */
 /*
         * ---------------- Stock Status ------------------------------------
        */
        @Select("with temp as (select ss.period_name, ss.period_start_date period_start_date,  coalesce(ss.closing_balance,0) closing_balance, \n" +
                "coalesce((select isavalue from stock_requirements where facilityid = ss.facility_id \n" +
                "and programid = ss.program_id\n" +
                "and productid = ss.product_id \n" +
                "and year = extract(year from ss.period_start_date)),0) need,\n" +
                "coalesce(fp.minmonthsofstock,0) minmonthsofstock, \n" +
                "coalesce(fp.maxmonthsofstock,0) maxmonthsofstock, vd.region_name, vd.district_name\n" +
                ", f.name facility_name\n" +
                "from vw_vaccine_stock_status ss\n" +
                "left join program_products pp on pp.programid = ss.program_id and pp.productid = ss.product_id\n" +
                "left join facility_approved_products fp on fp.programproductid = pp.id and fp.facilitytypeid = ss.facility_type_id\n" +
                "left join vw_districts vd on ss.geographic_zone_id=vd.district_id\n" +
                "left join facilities f on f.id= ss.facility_id\n" +
                "where program_id = fn_get_vaccine_program_id() \n" +
                "and period_start_date::date>= #{startDate}\n" +
                "and period_end_date::date  <= #{endDate}\n" +
                "and product_id = #{product}\n" +
                "and (vd.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or vd.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)\n" +
                ")\n" +
                "select t.period_name,period_start_date, sum(t.closing_balance), sum(t.need), min(t.minmonthsofstock) min, max(t.maxmonthsofstock) max, \n" +
                "case when sum(t.need)> 0 and (sum(t.closing_balance) / sum(t.need)::numeric<=min(minmonthsofstock))then sum(t.closing_balance)  / sum(t.need)::numeric  end mos_g1 ,\n" +
                "case when sum(t.need) > 0 and (sum(t.closing_balance)  / sum(t.need)::numeric>min(minmonthsofstock))and (sum(t.closing_balance)  / sum(t.need)::numeric<=max(maxmonthsofstock))\n" +
                "then sum(t.closing_balance) / sum(t.need)::numeric  end mos_g2 ,\n" +
                "case when sum(t.need) > 0 and (sum(t.closing_balance) / sum(t.need)::numeric>max(maxmonthsofstock))then sum(t.closing_balance)  /sum(t.need)::numeric  end mos_g3\n" +
                "from temp t\n" +
                "group by 1,2\n" +
                "order by 2,1;")
        List<HashMap<String, Object>> getStockStatusByMonthly(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("user") Long userId, @Param("product") Long product);

        @Select("with temp as (select coalesce(ss.closing_balance,0) closing_balance, \n" +
                "coalesce((select isavalue from stock_requirements where facilityid = ss.facility_id \n" +
                "and programid = ss.program_id\n" +
                "and productid = ss.product_id \n" +
                "and year = extract(year from ss.period_start_date)),0) need,\n" +
                "coalesce(fp.minmonthsofstock,0) minmonthsofstock, \n" +
                "coalesce(fp.maxmonthsofstock,0) maxmonthsofstock, vd.region_name, vd.district_name\n" +
                ", f.name facility_name\n" +
                "from vw_vaccine_stock_status ss\n" +
                "left join program_products pp on pp.programid = ss.program_id and pp.productid = ss.product_id\n" +
                "left join facility_approved_products fp on fp.programproductid = pp.id and fp.facilitytypeid = ss.facility_type_id\n" +
                "left join vw_districts vd on ss.geographic_zone_id=vd.district_id\n" +
                "left join facilities f on f.id= ss.facility_id\n" +
                "where program_id = fn_get_vaccine_program_id() \n" +
                "and period_id = #{period}\n" +
                "and product_id = #{product}\n" +
                "and (vd.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or vd.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)\n" +
                ")\n" +
                "select t.region_name, t.district_name, sum(t.closing_balance), sum(t.need), min(t.minmonthsofstock) minmonthsofstock, max(t.maxmonthsofstock) maxmonthsofstock, \n" +
                "case when sum(t.need)> 0 and (sum(t.closing_balance) / sum(t.need)::numeric<=min(minmonthsofstock))then sum(t.closing_balance)  / sum(t.need)::numeric  end mos_g1 ,\n" +
                "case when sum(t.need) > 0 and (sum(t.closing_balance)  / sum(t.need)::numeric>min(minmonthsofstock))and (sum(t.closing_balance)  / sum(t.need)::numeric<=max(maxmonthsofstock))\n" +
                "then sum(t.closing_balance) / sum(t.need)::numeric  end mos_g2 ,\n" +
                "case when sum(t.need) > 0 and (sum(t.closing_balance) / sum(t.need)::numeric>max(maxmonthsofstock))then sum(t.closing_balance)  /sum(t.need)::numeric  end mos_g3\n" +
                "from temp t\n" +
                "group by 1,2\n" +
                "order by 1,2")
        List<HashMap<String, Object>> getDistrictStockStatus(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        @Select("with temp as (select coalesce(ss.closing_balance,0) closing_balance, \n" +
                "coalesce((select isavalue from stock_requirements where facilityid = ss.facility_id and programid = ss.program_id\n" +
                "and productid = ss.product_id and year = extract(year from ss.period_start_date)),0) need,\n" +
                "coalesce(fp.minmonthsofstock,0) minmonthsofstock, coalesce(fp.maxmonthsofstock,0) maxmonthsofstock, vd.region_name, vd.district_name\n" +
                ", f.name facility_name\n" +
                "from vw_vaccine_stock_status ss\n" +
                "left join program_products pp on pp.programid = ss.program_id and pp.productid = ss.product_id\n" +
                "left join facility_approved_products fp on fp.programproductid = pp.id and fp.facilitytypeid = ss.facility_type_id\n" +
                "left join vw_districts vd on ss.geographic_zone_id=vd.district_id\n" +
                "left join facilities f on f.id= ss.facility_id\n" +
                "where program_id = fn_get_vaccine_program_id() \n" +
                "and period_id = #{period}\n" +
                "and product_id = #{product}\n" +
                "and (vd.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or vd.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)\n" +
                ")\n" +
                "select t.region_name, t.district_name,t.facility_name, t.closing_balance, t.need, t.minmonthsofstock, t.maxmonthsofstock, \n" +
                "case when t.need > 0 and (t.closing_balance / t.need::numeric<=minmonthsofstock)then t.closing_balance / t.need::numeric  end mos_g1 ,\n" +
                "case when t.need > 0 and (t.closing_balance / t.need::numeric>minmonthsofstock)and (t.closing_balance / t.need::numeric<=maxmonthsofstock)\n" +
                "then t.closing_balance / t.need::numeric  end mos_g2 ,\n" +
                "case when t.need > 0 and (t.closing_balance / t.need::numeric>maxmonthsofstock)then t.closing_balance / t.need::numeric  end mos_g3\n" +
                "from temp t\n" +
                "order by 1,2,3")
        List<HashMap<String, Object>> getFacilityStockStatus(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

        @Select("with temp as (select ss.period_name,  coalesce(ss.closing_balance,0) closing_balance, \n" +
                "coalesce((select isavalue from stock_requirements where facilityid = ss.facility_id \n" +
                "and programid = ss.program_id\n" +
                "and productid = ss.product_id \n" +
                "and year = extract(year from ss.period_start_date)),0) need," +
                "ss.period_start_date period_start_date,\n" +
                "coalesce(fp.minmonthsofstock,0) minmonthsofstock, \n" +
                "coalesce(fp.maxmonthsofstock,0) maxmonthsofstock, vd.region_name, vd.district_name\n" +
                ", f.name facility_name\n" +
                "from vw_vaccine_stock_status ss\n" +
                "left join program_products pp on pp.programid = ss.program_id and pp.productid = ss.product_id\n" +
                "left join facility_approved_products fp on fp.programproductid = pp.id and fp.facilitytypeid = ss.facility_type_id\n" +
                "left join vw_districts vd on ss.geographic_zone_id=vd.district_id\n" +
                "left join facilities f on f.id= ss.facility_id\n" +
                "where program_id = fn_get_vaccine_program_id() \n" +
                "and period_start_date::date >= #{startDate}\n" +
                "and period_end_date::date <= #{endDate}\n" +
                "and product_id = #{product}\n" +
                "and (vd.district_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int\n" +
                "or vd.region_id = (select value from user_preferences up where up.userid = #{user} and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int)\n" +
                ")\n" +
                "select t.region_name,t.district_name, t.facility_name," +
                "t.district_name || t.facility_name key_val," +
                " t.period_name,t.period_start_date, sum(t.closing_balance), sum(t.need), min(t.minmonthsofstock) min," +
                " max(t.maxmonthsofstock) max,\n" +
                "case when sum(t.need)> 0 then sum(t.closing_balance)  / sum(t.need)::numeric  end mos , \n" +
                "case when sum(t.need)> 0 and (sum(t.closing_balance) / sum(t.need)::numeric<=min(minmonthsofstock))then sum(t.closing_balance)  / sum(t.need)::numeric  end mos_g1 ,\n" +
                "case when sum(t.need) > 0 and (sum(t.closing_balance)  / sum(t.need)::numeric>min(minmonthsofstock))and (sum(t.closing_balance)  / sum(t.need)::numeric<=max(maxmonthsofstock))\n" +
                "then sum(t.closing_balance) / sum(t.need)::numeric  end mos_g2 ,\n" +
                "case when sum(t.need) > 0 and (sum(t.closing_balance) / sum(t.need)::numeric>max(maxmonthsofstock))then sum(t.closing_balance)  /sum(t.need)::numeric  end mos_g3\n" +
                "from temp t\n" +
                "group by 1,2,3,4,5,6\n" +
                "order by 5,1,2;")
        List<HashMap<String, Object>> getFacilityStockStatusDetails(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("product") Long product, @Param("user") Long user);
        @Select("select id current_period, name, startdate from processing_periods p where\n" +
                "p.id=  fn_get_vaccine_current_reporting_period()")
        Map<String,Object> getVaccineCurrentReportingPeriod();
        @Select("select d.name zone_name, l.name level_name from \n" +
                "geographic_zones  d\n" +
                "inner join geographic_levels l on d.levelid=l.id\n" +
                " where d.id = \n" +
                "(select value from user_preferences up where up.userid = #{userId} \n" +
                "and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int limit 1")
        Map<String,Object> getUserZoneInformation(@Param("userId") Long userId);

//        @Select("select * from vw_vaccine_inventory_stock_status st where facility_id=#{facilityId}")
//        List<HashMap<String, Object>> getFacilityVaccineInventoryStockStatus(@Param("facilityId") Long facilityId);

        @Select("SELECT " +
                "  vvisc.facility_name," +
                "  vvisc.product," +
                "  vvisc.maximum_stock," +
                "  vvisc.reorder_level," +
                "  vvisc.buffer_stock," +
                "  vvisc.unity_of_measure," +
                "  vvisc.product_category," +
                "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
                "       vvisc.soh " +
                "  ELSE " +
                "  (select SUM(quantity) from stock_card_entries sce " +
                "      join stock_cards sc on sc.id=sce.stockcardid " +
                "      where sc.facilityid=#{facilityId} and sc.productid=vvisc.product_id and sce.createddate <=#{date}::DATE)::integer " +
                "  END AS soh," +
                "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
                "       vvisc.mos" +
                "  ELSE " +
                "      round((select SUM(quantity) from stock_card_entries sce " +
                "      join stock_cards sc on sc.id=sce.stockcardid " +
                "      where sc.facilityid=#{facilityId} and sc.productid=vvisc.product_id and sce.createddate <=#{date}::DATE)::numeric(10,2) / vvisc.monthly_stock::numeric(10,2), 2) " +
                "  END AS mos," +
                "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
                "       vvisc.color " +
                "  ELSE " +
                "      ( SELECT fn_get_vaccine_stock_color(COALESCE(vvisc.maximum_stock::integer, 0), COALESCE(vvisc.reorder_level::integer, 0)," +
                "        COALESCE(vvisc.buffer_stock::integer, 0), " +
                "        COALESCE((select SUM(quantity) from stock_card_entries sce" +
                "      join stock_cards sc on sc.id=sce.stockcardid " +
                "      where sc.facilityid=#{facilityId} and sc.productid=vvisc.product_id and sce.createddate <=#{date}::DATE)::integer, 0))) " +
                "  END AS color " +
                " FROM vw_vaccine_inventory_stock_status vvisc WHERE vvisc.facility_id=#{facilityId}")
        List<HashMap<String, Object>> getFacilityVaccineInventoryStockStatus(@Param("facilityId") Long facilityId, @Param("date") String date);

        @Select("SELECT " +
                "  vvisc.facility_name," +
                "  vvisc.product," +
                "  vvisc.maximum_stock," +
                "  vvisc.reorder_level," +
                "  vvisc.buffer_stock," +
                "  vvisc.unity_of_measure," +
                "  vvisc.product_category," +
                "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
                "       vvisc.soh " +
                "  ELSE " +
                "  (select SUM(quantity) from stock_card_entries sce" +
                "      join stock_cards sc on sc.id=sce.stockcardid" +
                "      where sc.facilityid=vvisc.facility_id and sc.productid=#{productId} and sce.createddate <=#{date}::DATE)::integer " +
                "  END AS soh," +
                "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
                "       vvisc.mos" +
                "  ELSE " +
                "      round((select SUM(quantity) from stock_card_entries sce" +
                "      join stock_cards sc on sc.id=sce.stockcardid" +
                "      where sc.facilityid=vvisc.facility_id and sc.productid=#{productId} and sce.createddate <=#{date}::DATE)::numeric(10,2) / vvisc.monthly_stock::numeric(10,2), 2) " +
                "  END AS mos," +
                "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
                "       vvisc.color" +
                "  ELSE " +
                "      ( SELECT fn_get_vaccine_stock_color(COALESCE(vvisc.maximum_stock::integer, 0), COALESCE(vvisc.reorder_level::integer, 0)," +
                "        COALESCE(vvisc.buffer_stock::integer, 0), " +
                "        COALESCE((select SUM(quantity) from stock_card_entries sce " +
                "      join stock_cards sc on sc.id=sce.stockcardid " +
                "      where sc.facilityid=vvisc.facility_id and sc.productid=#{productId} and sce.createddate <=#{date}::DATE)::integer, 0))) " +
                "  END AS color " +
                " from vw_vaccine_inventory_stock_status vvisc " +
                " left join facilities f on f.id=vvisc.facility_id " +
                " left join facility_types ft on ft.id=f.typeid " +
                " where vvisc.facility_id = ANY (#{facilityIds}::INT[]) AND vvisc.product_id=#{productId} AND LOWER(ft.code) =LOWER(#{level})")
        List<HashMap<String, Object>> getSupervisedFacilitiesProductStockStatus(@Param("facilityIds") String facilityIds,
                                                                                @Param("productId") Long productId,
                                                                                @Param("date") String date,
                                                                                @Param("level") String level);
}