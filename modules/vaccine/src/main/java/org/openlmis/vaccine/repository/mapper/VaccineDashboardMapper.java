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

import net.sf.jasperreports.engine.json.expression.member.ObjectKeyExpression;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface VaccineDashboardMapper {

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

    @Select("SELECT\n" +
            "d.region_name,\n" +
            "i.period_name,\n" +
            "i.period_start_date,\n" +
            "round(" + vaccineDistrictCoverageDenominatorWthPeriodStartDateSql + ") target, \n" +
            "sum(COALESCE(i.within_outside_total,0)) actual, \n" +
            "round((case when sum(" + vaccineDistrictCoverageDenominatorWthPeriodStartDateSql + ") > 0 then (sum(COALESCE(i.within_outside_total,0)) / round(" + vaccineDistrictCoverageDenominatorWthPeriodStartDateSql + ")::numeric) else 0 end) * 100) coverage \n" +
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
            "round(" + vaccineDistrictCoverageDenominatorSql + ") target, \n" +
            "sum(COALESCE(i.within_outside_total,0)) actual,\n" +
            "round((case when sum(" + vaccineDistrictCoverageDenominatorSql + ") > 0 then (sum(COALESCE(i.within_outside_total,0)) / round(" + vaccineDistrictCoverageDenominatorSql + ")::numeric) else 0 end) * 100) coverage \n" +
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
    List<HashMap<String, Object>> getDistrictCoverage(@Param("period") Long period, @Param("product") Long product, @Param("user") Long user);

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
    List<HashMap<String, Object>> getDistrictDropout(@Param("period") Long period, @Param("product") Long productId, @Param("user") Long user);

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
    List<HashMap<String, Object>> getDistrictSessions(@Param("period") Long period, @Param("user") Long user);

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
    Map<String, Object> getVaccineCurrentReportingPeriod();

    @Select("select d.name zone_name, l.name level_name from \n" +
            "geographic_zones  d\n" +
            "inner join geographic_levels l on d.levelid=l.id\n" +
            " where d.id = \n" +
            "(select value from user_preferences up where up.userid = #{userId} \n" +
            "and up.userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' limit 1)::int limit 1")
    Map<String, Object> getUserZoneInformation(@Param("userId") Long userId);

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
            "      where " +
            "   (SELECT date_part('YEAR',sce.createddate::DATE)) = ( SELECT date_part('YEAR', #{date}::DATE )) AND " +

            "sc.facilityid=#{facilityId} and sc.productid=vvisc.product_id and sce.createddate <=#{date}::DATE)::integer  " +
            "  END AS soh," +
            "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
            "       vvisc.mos" +
            "  ELSE " +
            "      round((select SUM(quantity) from stock_card_entries sce " +
            "      join stock_cards sc on sc.id=sce.stockcardid " +
            "      where " +
            "   (SELECT date_part('YEAR',sce.createddate::DATE)) = ( SELECT date_part('YEAR', #{date}::DATE )) AND " +
            " sc.facilityid=#{facilityId} and sc.productid=vvisc.product_id and sce.createddate <=#{date}::DATE)::numeric(10,2) / vvisc.monthly_stock::numeric(10,2), 2) " +
            "  END AS mos," +
            "  CASE WHEN (select NOW()::DATE) =#{date}::DATE THEN " +
            "       vvisc.color " +
            "  ELSE " +
            "      ( SELECT fn_get_vaccine_stock_color(COALESCE(vvisc.maximum_stock::integer, 0), COALESCE(vvisc.reorder_level::integer, 0)," +
            "        COALESCE(vvisc.buffer_stock::integer, 0), " +
            "        COALESCE((select SUM(quantity) from stock_card_entries sce" +
            "      join stock_cards sc on sc.id=sce.stockcardid " +
            "      where " +
            "   (SELECT date_part('YEAR',sce.createddate::DATE)) = ( SELECT date_part('YEAR', #{date}::DATE )) AND " +
            "  sc.facilityid=#{facilityId} and sc.productid=vvisc.product_id and sce.createddate <=#{date}::DATE)::integer, 0))) " +
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

    @Select(" \n" +
            "\n" +
            "                       SELECT CASE WHEN total > 0 THEN  ROUND(overstock * 100 /total,1) else 0 end as overstock,\n" +
            "                       \n" +
            "\t\t\tCASE WHEN total > 0 THEN  ROUND(sufficient * 100 /total,1) else 0 end as sufficient,\n" +
            "\t\t\tCASE WHEN total > 0 THEN  ROUND(minimum * 100 /total,1) else 0 end as minimum,\n" +
            "\t\t\tCASE WHEN total > 0 THEN  ROUND(zero * 100 /total,1) else 0 end as zero\n" +
            "\t\t\t\n" +
            "                           from (\n" +
            "\n" +
            "                           WITH Q AS ( SELECT  x.* , r.isaValue,\n" +
            "                           case when  (x.soh >0 ) and x.soh > r.maximumstock then 1 else 0 end as blue,\n" +
            "                           case when  (x.soh >0 ) and x.soh <= r.maximumstock AND x.soh  >= r.reorderlevel then 1 else 0 end as green,\n" +
            "                           case when (x.soh >0 ) and (x.soh < r.reorderlevel AND x.soh >= r.bufferstock ) or \n" +
            "\t\t\t  ( (x.soh >0 ) and x.soh < r.bufferstock )\n" +
            "                           then 1 else 0 end as yellow,\n" +
            "                          \n" +
            "                           case when  x.soh = 0 then 1 else 0 end as zero,\n" +
            "                           (          \n" +
            "                           select fn_get_vaccine_stock_color(r.maximumstock::int, reorderlevel::int, bufferstock::int, x.soh::int)\n" +
            "                            )  color           \n" +
            "                            FROM (             \n" +
            "                            SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.* , N.*  \n" +
            "                            FROM  (                             \n" +
            "                            SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
            "                            p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,   \n" +
            "                            e.modifiedDate::timestamp lastUpdate \n" +
            "  \n" +
            "                            FROM stock_cards s   \n" +
            "                            JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
            "                            JOIN program_products pp ON s.productId = pp.productId   \n" +
            "                            JOIN programs ON pp.programId = programs.id    \n" +
            "                            JOIN products p ON pp.productId = p.id      \n" +
            "                            JOIN facilities f ON s.facilityId = f.id  \n" +
            "                            JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
            "                            JOIN facility_types  ON f.typeId = facility_types.Id  \n" +
            "                           where facility_types.code = 'dvs' " +
            "                           AND pp.productCategoryId= #{category}     \n" +
            "                            ORDER BY e.modifiedDate ) t,\n" +
            "\t\t\t(select (m.total * Z.total ) totalVaccineAvailableInAllStore from\n" +
            "\t\t\t(\n" +
            "\t\t\tselect  count(*) total  from \n" +
            "\t\t\tproducts P\n" +
            "\t\t\tJoin program_products pp on P.ID =PP.productId\n" +
            "\t\t\tjoin facility_APPROVED_PRODUCTS fap ON pp.id = fap.programproductId\n" +
            "\t\t\tJOIN facility_types ft ON fap.facilityTypeID = ft.id\n" +
            "\t\t\twhere " +
            "  pp.productCategoryId= #{category} AND P.active =true AND programId= (fn_get_vaccine_program_id()) and ft.code = 'dvs'\n" +
            "\t\t\t)M,(\n" +
            "\t\t\tselect count(*) total from facilities f\n" +
            "\t\t\tJOIN facility_types ft ON f.typeid = FT.ID\n" +
            "\t\t\tWHERE FT.CODE = 'dvs' and f.active =true\n" +
            "\t\t\t)Z\n" +
            "\t\t\t) N\n" +
            "                         ) x \n" +
            "                            JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid\n" +
            "                            WHERE  x.r <= 1       \n" +
            "                            ORDER BY facilityId,productId )\n" +
            "                            SELECT \n" +
            "                             sum(bluE) * MAX(totalVaccineAvailableInAllStore) overstock,\n" +
            "                             sum(green) * MAX(totalVaccineAvailableInAllStore)  sufficient,\n" +
            "                             sum(yellow) * MAX(totalVaccineAvailableInAllStore)  minimum,\n" +
            "                             sum(zero)* MAX(totalVaccineAvailableInAllStore)  zero,\n" +
            "                             sum(bluE) * MAX(totalVaccineAvailableInAllStore) + sum(green) * MAX(totalVaccineAvailableInAllStore) +\n" +
            "                             sum(yellow) * MAX(totalVaccineAvailableInAllStore) + sum(zero)* MAX(totalVaccineAvailableInAllStore) total\n" +
            "                             from q\n" +
            "\t\t      )M\n" +
            "\n ")
    List<HashMap<String, Object>> getStockStatusOverViewNotUsed(@Param("userId") Long userId, @Param("category") Long category, @Param("productId") Long productId, @Param("dateString") String dateString, @Param("level") String level);


    @Select("\n" +
            "                                        SELECT\n" +
            "                                   \n" +
            "                                        SUM(y.yellow) minimum, SUM(y.blue) overstock, SUM(y.green) sufficient,SUM(y.red) zero ,SUM(y.blue + y.green +y.yellow \n" +
            "                                         +red) total \n" +
            "                                        \n" +
            "                                         FROM (\n" +
            "                                         \n" +
            "                                         SELECT \n" +
            "                                          SUM( case when  (x.soh >0 ) and x.soh > r.maximumstock then 1 else 0 end ) as  blue,  \n" +
            "                                          SUM( case when  (x.soh >0 ) and x.soh <= r.maximumstock AND x.soh  >= r.reorderlevel then 1 else 0 end )as green,\n" +
            "                                           SUM(case when (x.soh >0 ) and (x.soh < r.reorderlevel AND x.soh >= r.bufferstock ) or\n" +
            "                                           ( (x.soh >0 ) and x.soh < r.bufferstock )\n" +
            "                                           then 1 else 0 end) as yellow,\n" +
            "                                           SUM( case when x.soh = 0 then 1 else 0 end) as red\n" +
            "                                                   \n" +
            "                                            FROM (             \n" +
            "                                            SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.*  \n" +
            "                                            FROM  (                             \n" +
            "                                            SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
            "                                            p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,\n" +
            "                                            e.modifiedDate::timestamp lastUpdate \n" +
            "                                            FROM stock_cards s   \n" +
            "                                            JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
            "                                            JOIN program_products pp ON s.productId = pp.productId\n" +
            "                                            join facility_APPROVED_PRODUCTS fap ON pp.id = fap.programproductId\n" +
            "                                            JOIN programs ON pp.programId = programs.id\n" +
            "                                            JOIN products p ON pp.productId = p.id    \n" +
            "                                            JOIN facilities f ON s.facilityId = f.id  \n" +
            "                                            JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
            "                                            JOIN facility_types  ON f.typeId = facility_types.Id  \n" +
            "                                            where productcategoryId =#{category}  AND facility_types.code =#{level} aND\n" +
            "                                            P.active =true AND programId= (fn_get_vaccine_program_id()) \n" +
            "                                            AND d.district_id in (select district_id from vw_user_facilities where user_id = #{userId}::INT and program_id = fn_get_vaccine_program_id()) \n" +
            "                                            ORDER BY e.modifiedDate ) t\n" +
            "                                             ) x \n" +
            "                                            JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid AND programId = (fn_get_vaccine_program_id()) \n" +
            "                                            WHERE  x.r <= 1 \n" +
            "                                            ) y")
    List<HashMap<String, Object>> getStockStatusOverView(@Param("userId") Long userId, @Param("category") Long category, @Param("dateString") String dateString, @Param("level") String level);


    @Select("           WITH Q AS (\n" +
            "                                     SELECT k.overstock,k.sufficient,k.minimum,k.zero,product\n" +
            "\n" +
            "                                      FROM (\n" +
            "                                      \n" +
            "                                      SELECT \n" +
            "                                      ROUND(100.0 * (CASE WHEN total > 0 THEN blue ELSE 0 END / total), 1) AS overstock,\n" +
            "                                      ROUND(100.0 * (CASE WHEN total > 0 THEN green ELSE 0 END / total), 1) AS sufficient,\n" +
            "                                      ROUND(100.0 * (CASE WHEN total > 0 THEN yellow ELSE 0 END / total), 1) AS minimum,\n" +
            "                                      ROUND(100.0 * (CASE WHEN total > 0 THEN red ELSE 0 END / total), 1) AS zero,product\n" +
            "                                     \n" +
            "                                      from (\n" +
            "\n" +
            "                                      \n" +
            "                                      SELECT SUM(y.yellow) yellow, SUM(y.blue) blue, SUM(y.green) green,SUM(y.red) red ,SUM(y.blue + y.green +y.yellow \n" +
            "                                         +red) total,product from (\n" +
            "                                         \n" +
            "                                         SELECT  x.productId,x.facilityid,product,\n" +
            "                                          \n" +
            "                                          SUM( case when  (x.soh >0 ) and x.soh > r.maximumstock then 1 else 0 end ) as  blue,\n" +
            "                                          SUM( case when  (x.soh >0 ) and x.soh <= r.maximumstock AND x.soh  >= r.reorderlevel then 1 else 0 end )as green,\n" +
            "                                           SUM(case when (x.soh >0 ) and (x.soh < r.reorderlevel AND x.soh >= r.bufferstock ) or\n" +
            "                                           ( (x.soh >0 ) and x.soh < r.bufferstock )\n" +
            "                                           then 1 else 0 end) as yellow,\n" +
            "                                           SUM( case when x.soh = 0 then 1 else 0 end) as red\n" +
            "                                                   \n" +
            "                                            FROM (             \n" +
            "                                            SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.* \n" +
            "                                            FROM  ( \n" +
            "\n" +
            "\t\t\t\t\tSELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId,\n" +
            "\t\t\t\t\t region_name region,  \n" +
            "                                            p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh, \n" +
            "                                            e.modifiedDate::timestamp lastUpdate \n" +
            "               \n" +
            "                                            FROM stock_cards s   \n" +
            "                                            JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
            "                                            JOIN program_products pp ON s.productId = pp.productId  \n" +
            "                                            JOIN programs ON pp.programId = programs.id \n" +
            "                                            JOIN products p ON pp.productId = p.id      \n" +
            "                                            JOIN facilities f ON s.facilityId = f.id  \n" +
            "                                            JOIN vw_districts d ON f.geographiczoneId = d.district_id \n" +
            "                                            JOIN facility_types  ON f.typeId = facility_types.Id  \n" +
            "                                           where facility_types.code = 'dvs'   \n" +
            "                                           AND programId= (fn_get_vaccine_program_id())\n" +
            "                                            ORDER BY e.modifiedDate\n" +
            "\n" +
            "                                            )t\n" +
            "\n" +
            "                                                                        \n" +
            "                                          --   SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
            "--                                             p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,\n" +
            "--                                             e.modifiedDate::timestamp lastUpdate \n" +
            "--                 \n" +
            "--                                             FROM stock_cards s   \n" +
            "--                                             JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
            "--                                             JOIN program_products pp ON s.productId = pp.productId\n" +
            "--                                             join facility_APPROVED_PRODUCTS fap ON pp.id = fap.programproductId\n" +
            "--                                             JOIN facility_types  ON fap.facilityTypeID = facility_types.id\n" +
            "--                                             JOIN programs ON pp.programId = programs.id\n" +
            "--                                             JOIN products p ON pp.productId = p.id    \n" +
            "--                                             JOIN facilities f ON s.facilityId = f.id  \n" +
            "--                                             JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
            "--                                           \n" +
            "--                                             where   pp.productCategoryId = #{category} and facility_types.code = 'dvs' and \n" +
            "--                                             P.active =true AND programId= (fn_get_vaccine_program_id())\n" +
            "--                                                \n" +
            "--                                              \n" +
            "--                                             ORDER BY e.modifiedDate ) t \n" +
            "                                             ) x \n" +
            "                                            JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid AND programId =(fn_get_vaccine_program_id()) \n" +
            "                                            WHERE  x.r <= 1 \n" +
            "                                            \n" +
            "                                            group by 1,2,3 \n" +
            "                                            ORDER BY facilityId,productId,product\n" +
            "                                            ) y\n" +
            "                                            group by productId,product\n" +
            "                                            )l\n" +
            "                                            )K\n" +
            "                                            )\n" +
            "                                            select * from q\n" +
            "                                            where #{status} is not null" +
            "                                             order by #{status} desc   ")
    List<HashMap<String, Object>> getInventoryStockStatusDetail(@Param("category") String category, @Param("userId") Long userId, @Param("status") String status,
                                                                @Param("dateString") String dateString, @Param("level") String level);


    //Get Stock by STatus
    @Select("\n" +
            "\t\t\t\t      select *\n" +
            "\t\t\t\t     \n" +
            "\t\t\t\t       from (\n" +
            "\n" +
            "                                        SELECT\n" +
            "                                   \n" +
            "                                         productId,product,SUM(y.yellow) yellow, SUM(y.blue) blue, SUM(y.green) green,SUM(y.red) red ,SUM(y.blue + y.green +y.yellow \n" +
            "                                         +red) total \n" +
            "                                        \n" +
            "                                         from (\n" +
            "                                         \n" +
            "                                         SELECT \n" +
            "\t\t\t\n" +
            "                                          x.productId,product,count(x.facilityId) prod,\n" +
            "                                         \n" +
            "                                          \n" +
            "                                          SUM( case when  (x.soh >0 ) and x.soh > r.maximumstock then 1 else 0 end ) as  blue,  \n" +
            "                                          SUM( case when  (x.soh >0 ) and x.soh <= r.maximumstock AND x.soh  >= r.reorderlevel then 1 else 0 end )as green,\n" +
            "                                           SUM(case when (x.soh >0 ) and (x.soh < r.reorderlevel AND x.soh >= r.bufferstock ) or\n" +
            "                                           ( (x.soh >0 ) and x.soh < r.bufferstock )\n" +
            "                                           then 1 else 0 end) as yellow,\n" +
            "                                           SUM( case when x.soh = 0 then 1 else 0 end) as red\n" +
            "                                                   \n" +
            "                                            FROM (             \n" +
            "                                            SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.*  \n" +
            "                                            FROM  (                             \n" +
            "                                            SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
            "                                            p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,\n" +
            "                                            e.modifiedDate::timestamp lastUpdate \n" +
            "                                            FROM stock_cards s   \n" +
            "                                            JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
            "                                            JOIN program_products pp ON s.productId = pp.productId\n" +
            "                                            join facility_APPROVED_PRODUCTS fap ON pp.id = fap.programproductId\n" +
            "                                            JOIN programs ON pp.programId = programs.id\n" +
            "                                            JOIN products p ON pp.productId = p.id    \n" +
            "                                            JOIN facilities f ON s.facilityId = f.id  \n" +
            "                                            JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
            "                                            JOIN facility_types  ON f.typeId = facility_types.Id  \n" +
            "                                            where productcategoryId =#{category}  AND facility_types.code =#{level} and\n" +
            "                                            P.active =true AND programId= (fn_get_vaccine_program_id()) \n" +
            "                                            AND d.district_id in (select district_id from vw_user_facilities where user_id = #{userId}::INT and program_id = fn_get_vaccine_program_id()) \n" +
            "                                            ORDER BY e.modifiedDate ) t\n" +
            "                                             ) x \n" +
            "                                            JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid AND programId = (fn_get_vaccine_program_id()) \n" +
            "                                            WHERE  x.r <= 1 \n" +
            "                                            group by 1,2\n" +
            "                                            ORDER BY productId\n" +
            "                                            ) y\n" +
            "                                            group by 1,2\n" +
            "                                            )l \n" +
            "                                             WHERE red > 0\n" +
            "                                             ORDER BY total desc")
    List<HashMap<String, Object>> getVaccineInventoryStockByStatus(@Param("category") Long category, @Param("level") String level, @Param("userId") Long userId);


    @Select(
            "\t\t\t\t       SELECT *     \n" +
                    "\t\t\t\t       FROM (\n" +
                    "                                        SELECT\n" +
                    "                                          facilityName,facilityId,SUM(y.yellow) yellow, SUM(y.blue) blue, SUM(y.green) green,SUM(y.red) red ,SUM(y.blue + y.green +y.yellow \n" +
                    "                                         +red) total ,MAX(SOH) SOH\n" +
                    "                                        \n" +
                    "                                         from (\n" +
                    "                                         \n" +
                    "                                         SELECT \n" +
                    "\t\t\t\n" +
                    "                                          x.facilityName,X.facilityId,x.soh SOH ,\n" +
                    "                                 \n" +
                    "                                          SUM( case when  (x.soh >0 ) and x.soh > r.maximumstock then 1 else 0 end ) as  blue,  \n" +
                    "                                          SUM( case when  (x.soh >0 ) and x.soh <= r.maximumstock AND x.soh  >= r.reorderlevel then 1 else 0 end )as green,\n" +
                    "                                           SUM(case when (x.soh >0 ) and (x.soh < r.reorderlevel AND x.soh >= r.bufferstock ) or\n" +
                    "                                           ( (x.soh >0 ) and x.soh < r.bufferstock )\n" +
                    "                                           then 1 else 0 end) as yellow,\n" +
                    "                                           SUM( case when x.soh = 0 then 1 else 0 end) as red\n" +
                    "                                                   \n" +
                    "                                            FROM (             \n" +
                    "                                            SELECT ROW_NUMBER() OVER (PARTITION BY facilityId,productId ORDER BY LastUpdate desc) AS r, t.*  \n" +
                    "                                            FROM  (                             \n" +
                    "                                            SELECT  facilityId, s.productId, f.name facilityName,district_id districtId, district_name district,region_id regionId, region_name region,  \n" +
                    "                                            p.primaryName product,sum(e.quantity) OVER (PARTITION BY s.facilityId, s.productId) soh,\n" +
                    "                                            e.modifiedDate::timestamp lastUpdate \n" +
                    "                                            FROM stock_cards s   \n" +
                    "                                            JOIN stock_card_entries e ON e.stockCardId = s.id     \n" +
                    "                                            JOIN program_products pp ON s.productId = pp.productId\n" +
                    "                                            join facility_APPROVED_PRODUCTS fap ON pp.id = fap.programproductId\n" +
                    "                                            JOIN programs ON pp.programId = programs.id\n" +
                    "                                            JOIN products p ON pp.productId = p.id    \n" +
                    "                                            JOIN facilities f ON s.facilityId = f.id  \n" +
                    "                                            JOIN vw_districts d ON f.geographiczoneId = d.district_id  \n" +
                    "                                            JOIN facility_types  ON f.typeId = facility_types.Id  \n" +
                    "                                            WHERE productCategoryId =#{category}  AND facility_types.code =#{level} and s.productId = #{productId} AND\n" +
                    "                                            P.active =true AND programId= (fn_get_vaccine_program_id()) \n" +
                    "                                            AND d.district_id in (select district_id from vw_user_facilities where user_id = #{userId}::INT and program_id = fn_get_vaccine_program_id()) \n" +
                    "                                            ORDER BY e.modifiedDate ) t\n" +
                    "                                             ) x \n" +
                    "                                            JOIN stock_requirements r on r.facilityid=x.facilityid and r.productid=x.productid AND programId = (fn_get_vaccine_program_id()) \n" +
                    "                                            WHERE  x.r <= 1 \n" +
                    "                                            group by 1,2,3\n" +
                    "                                            ORDER BY facilityName\n" +
                    "                                            ) y\n" +
                    "                                            group by 1,2\n" +
                    "                                            )l \n" +
                    "                                             LEFT JOIN facilities f ON facilityId = f.id  \n" +
                    "                                             LEFT JOIN vw_districts d ON f.geographiczoneId = d.district_id \n" +
                    "                                             ORDER BY facilityName\n")
    List<HashMap<String, Object>> getVaccineInventoryFacilitiesByProduct(@Param("category") Long category, @Param("level") String level, @Param("userId") Long userId, @Param("productId") Long productId, @Param("color") String color);

    @Select(" select Max(MaximumStock)Maximum,MAX(isaValue) minimum,\n" +
            "to_char(e.createddate, 'yyyy-mm-dd') \"days\",to_char(e.createddate, 'dd') \"day\", count(e.*) \"Events Recorded\",sum(e.quantity) quantity \n" +
            "from stock_card_entries e\n" +
            "join stock_cards sc on e.stockcardid = sc.id\n" +
            "LEFT JOIN stock_requirements sr on sc.facilityId =SR.FACILITYID AND SC.PRODUCTiD = sr.productId AND YEAR =2017\n" +
            "where  extract('year' from e.createddate) = 2017 and extract('month' from e.createddate) =1\n" +
            "and sc.productId =2412 and sc.facilityId =19181\n" +
            "group by to_char(e.createddate, 'yyyy-mm-dd'),to_char(e.createddate, 'dd'),sc.FACILITYiD\n" +
            "order by to_char(e.createddate, 'yyyy-mm-dd') asc ")
    List<HashMap<String, Object>> getStockEventByMonth();

    @Select(" select Max(MaximumStock)Maximum,MAX(isaValue) minimum,\n" +
            "to_char(e.createddate, 'yyyy-mm-dd') \"days\",to_char(e.createddate, 'dd') \"day\", count(e.*) \"Events Recorded\",sum(e.quantity) quantity \n" +
            "from stock_card_entries e\n" +
            "join stock_cards sc on e.stockcardid = sc.id\n" +
            "LEFT JOIN stock_requirements sr on sc.facilityId =SR.FACILITYID AND SC.PRODUCTiD = sr.productId AND YEAR =#{year}\n" +
            "where  extract('year' from e.createddate) = #{year} and extract('month' from e.createddate) =#{period}\n" +
            "and sc.productId = #{product} and sc.facilityId =#{district}\n" +
            "group by to_char(e.createddate, 'yyyy-mm-dd'),to_char(e.createddate, 'dd'),sc.FACILITYiD\n" +
            "order by to_char(e.createddate, 'yyyy-mm-dd') asc ")
    List<HashMap<String, Object>> geStockEventByMonth(@Param("product") Long product,
                                                      @Param("period") Long period,
                                                      @Param("year") Long year,
                                                      @Param("district") Long district);


    @Select("SELECT p.name period, round(coverage.percentage::int,0) percentageCoverage, \n" +
            "round(equipmentpercentage::int,0) equipmentpercentage,\n" +
            "stock.percentage stockPercentage\n" +
            "\n" +
            "FROM processing_periods p\n" +
            "LEFT JOIN \n" +
            "(\n" +
            "select periodId,productId,  \n" +
            " case when estimate > 0 then  cumulativeMonthlyRegular * 100 / (estimate) else 0 end as percentage\n" +
            "from \n" +
            " (\n" +
            "       SELECT         \n" +
            "         d.periodId,\n" +
            "         d.productid,\n" +
            "         d.year,\n" +
            "         \n" +
            "(SELECT SUM(target_value_monthly) target_value_monthly\n" +
            "FROM vw_vaccine_district_target_population \n" +
            "WHERE YEAR = 2017 \n" +
            "AND CATEGORY_ID = case when d.productid = 2412 then 12 when d.productid = 2413 then 3 when d.productid = 2418 then 2 else 4 \n" +
            "end \n" +
            " \n" +
            ") estimate,\n" +
            "         sum(monthlyregular) AS cumulativeMonthlyRegular\n" +
            "         \n" +
            "       FROM\n" +
            "         vw_vaccine_cumulative_coverage_by_dose d\n" +
            "         JOIN facilities f ON f.id = d.facilityid\n" +
            "\twhere d.year = 2017::INT AND productId = #{product} \n" +
            "       GROUP BY d.year, d.productid,d.periodId\n" +
            "       order by periodid\n" +
            ")a\n" +
            "\n" +
            ")coverage \n" +
            "\n" +
            "ON p.id = coverage.periodId\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select period_id, \n" +
            "ROUND(case when notworking +  functional > 0 then functional::numeric/ ( notworking +  functional) * 100 else 0 end,0) as\n" +
            " equipmentpercentage  from\n" +
            " (select period_id, period_name, SUM(case when  lower(status) = 'functional' THEN 1 else 0 end ) functional,\n" +
            "SUM(case when  lower(status) != 'functional' THEN 1 else 0 end) as notworking\n" +
            "from vw_vaccine_cold_chain\n" +
            "where extract (year from period_start_date ) = 2017\n" +
            "group by  period_id,period_name) b\n" +
            "\n" +
            "\n" +
            " ) equipment ON p.id = equipment.period_id\n" +
            " LEFT JOIN\n" +
            " (\n" +
            "\n" +
            "\n" +
            "\n" +
            "                SELECT  periodID,CASE WHEN total > 0 then round(available::numeric /(total) * 100,0) else 0 end as percentage\n" +
            "                from (\n" +
            "                select  periodId,\n" +
            "                sum(available)+sum(notavailable) total,\n" +
            "                   sum(available) available,sum(notavailable) notavailable\n" +
            "                FROM (\n" +
            "                WITH t as \n" +
            "                (\n" +
            "                SELECT CASE WHEN l.closingbalance > 0 then 1 else 0 end as available, \n" +
            "                CASE WHEN  l.closingbalance = 0 then 1 else 0 end as notAvailable,\n" +
            "                pr.startdate::date, pr.name period_name, r.periodid, r.programid, l.closingbalance soh, l.quantityissued consumption \n" +
            "                from vaccine_reports r \n" +
            "                join vaccine_report_logistics_line_items l on r.id = l.reportid\n" +
            "                join processing_periods pr on pr.id = r.periodid and pr.numberofmonths = 1\n" +
            "                where \n" +
            "                productid = #{product}::INT\n" +
            "                and status <> 'DRAFT' \n" +
            "                ) \n" +
            "                SELECT  periodid, period_name, startdate,  soh, available,notavailable      \n" +
            "                FROM t\n" +
            "                WHERE extract(month from startdate) between 1 and 12 \n" +
            "                WINDOW w AS (ORDER BY  startdate desc ROWS BETWEEN 0 FOLLOWING AND 2 FOLLOWING) \n" +
            "                ) a\n" +
            "                 group by 1\n" +
            "                 ) x\n" +
            "               \n" +
            "\n" +
            "\n" +
            " ) stock ON p.id = stock.periodId\n" +
            "\n" +
            "where numberofMonths = 1 and extract(year from p.startdate) = 2017\n" +
            "order by p.id \n"
    )
    List<HashMap<String, Object>> getAvailableStockForDashboard(@Param("product") Long product,
                                                                @Param("period")Long period,
                                                                @Param("year")Long year,
                                                                @Param("userId")Long userId);

    @Select("select product_code productCode,product_name product,display_name DisplayName,\n" +
            " within_male male,within_female female, within_total, *\n" +
            "\n" +
            "  from vw_vaccine_coverage WHERE  facility_id=19324 AND PERIOD_ID = 121\n" +
            "  order by product_id,display_name")
    List<HashMap<String, Object>>getAllVaccineImmunization();


   //Dashboard Queries
   @Select("select * from vw_full_stock_availability_vw ")
   List<HashMap<String,Object>>getFullStockAvailability();


  @Select("\n" +
          "\n" +
          "WITH Q as (\n" +
          "\n" +
          "select b.*, \n" +
          "CASE when soh = 0 THEN 'bad' WHEN mos between 1 and 1.5 THEN 'good' WHEN (mos > 1.5 or mos IS NULL ) THEN 'normal'" +
          " WHEN mos < 1 THEN 'warn' ELSE NULL END AS Classification from \n" +
          "\n" +
          "( select region_name, district_name, district_id, EXTRACT (YEAR FROM A .startdate) \"year\", EXTRACT (MONTH FROM A .startdate) \"month\", a.periodid, a.period_name, sum(soh) soh,\n" +
          " sum(consumption) consumption, case when sum(COALESCE(consumption,0)) > 0 then round(sum(COALESCE(soh,0))/(sum(COALESCE(consumption,0))/3::numeric)::numeric,2) else null end mos \n" +
          " from ( \n" +
          " \n" +
          " with t as (\n" +
          "  SELECT * FROM vaccine_facility_report_view \n" +
          "  where startdate::date >= (#{year}::text||'-01-01')::date - interval '3 months' and \n" +
          "  enddate::date <= (#{year}::text||'-12-31')::date and productid = #{productId}::INT and status <> 'DRAFT'\n" +
          "\n" +
          "   ) \n" +
          "  \n" +
          "SELECT facilityid, periodid, period_name, startdate, consumption, soh, extract(month from startdate) AS dow,\n" +
          " CASE WHEN count(consumption) OVER w = 3 THEN avg(consumption) OVER w END AS amc FROM t \n" +
          "\n" +
          " WHERE extract(month from startdate) \n" +
          " \n" +
          " between 1 and 12 WINDOW w AS (ORDER BY facilityid, startdate desc ROWS BETWEEN 0 FOLLOWING AND 2 FOLLOWING) ) a \n" +
          " join facilities f on f.id = a.facilityid\n" +
          " join vw_districts d on f.geographiczoneid = d.district_id \n" +
          " join processing_periods pr on pr.id = a.periodid \n" +
          " group by 1,2,3,4,5,6,7\n" +
          "  ) b\n" +
          " ) select period_name,Reported.count total,\n" +
          " -- sum(case when classification = 'normal' then 1 else 0 end) as normal,\n" +
          " --SUM(case when classification = 'bad' then 1 else 0 end) as bad,\n" +
          "-- SUM(case when classification = 'warn' then 1 else 0 end)as warn,\n" +
          " --SUM(case when classification = 'good' then 1 else 0 end) as good,\n" +
          "  --SUM(case when classification is null then 1 else 0 end) as noMonthOfStock,\n" +
          "CASE WHEN Reported.count > 0 then \n" +
          "ROUND((sum(case when classification = 'normal' then 1 else 0 end))::numeric/(Reported.count) * 100,0) end as accordingPlan,\n" +
          "\n" +
          "CASE WHEN Reported.count > 0 then \n" +
          "ROUND((sum(case when classification = 'bad' then 1 else 0 end))::numeric/(Reported.count) * 100,0) end as stockedOut,\n" +
          "\n" +
          "\n" +
          "CASE WHEN Reported.count > 0 then \n" +
          "ROUND((sum(case when classification = 'warn' then 1 else 0 end))::numeric/(Reported.count) * 100,0) end as underStocked,\n" +
          "\n" +
          "\n" +
          "\n" +
          "CASE WHEN Reported.count > 0 then \n" +
          "ROUND((sum(case when classification = 'good' then 1 else 0 end))::numeric/(Reported.count) * 100,0) end as overStocked\n" +
          "  \n" +
          "  \n" +
          "    from q\n" +
          "    LEFT JOIN \n" +
          "(\n" +
          "\n" +
          "\n" +
          "                                 select geographicZoneId, count(*) REPORTED from facilities  \n" +
          "                                 join programs_supported ps on ps.facilityId = facilities.id \n" +
          "                                    join geographic_zones gz on gz.id = facilities.geographicZoneId \n" +
          "                                    where  ps.programId = 82 and facilities.id in \n" +
          "                                      (select facilityId from vaccine_reports where periodId = #{periodId}::INT and programId = 82 and\n" +
          "                                      status not in ('DRAFT') )\n" +
          "                                     group by geographicZoneId\n" +
          "                                     ) Reported ON q.district_id = reported.geographicZoneId\n" +
          "   \n" +
          "    \n" +
          "    where periodid = #{periodId}\n" +
          "group by period_name\n" +
          "\n ")
        List<HashMap<String,Object>>getNationalPerformance(@Param("userId") Long userId, @Param("productId") Long productId, @Param("periodId") Long periodId,@Param("year")Long year);

    @Select("with completeness_with_reporting_periods as (\n" +
            "                  select    \n" +
            "                  a.region_name,   \n" +
            "                  a.district_name,  \n" +
            "                  a.priod_id,      \n" +
            "                  a.period_name,   \n" +
            "                  a.period_start_date,\n" +
            "                  a.geographiczoneid,    \n" +
            "                  a.expected,  \n" +
            "                  a.reported,   \n" +
            "                  a.ontime,    \n" +
            "                  a.late ,\n" +
            "                  a.approved,\n" +
            "                  a.distributed          \n" +
            "                  from (  \n" +
            "                       with temp as ( \n" +
            "                       select * from dashboard_reporting_view v\n" +
            "                       where  v.startdate::date >='2017-01-01'::DATE and v.enddate::date <='2017-12-01'::DATE \n" +
            "                      )    \n" +
            "\n" +
            "                      select    \n" +
            "                      vd.region_name,   \n" +
            "                      vd.district_name, \n" +
            "                      priod_id,       \n" +
            "                      t.period_name,   \n" +
            "                      t.period_start_date,  \n" +
            "                      t.geographiczoneid,  \n" +
            "                      sum(fixed) fixed,   \n" +
            "                      sum(outreach) outreach,   \n" +
            "                      sum(fixed) + sum(outreach) session_total,   \n" +
            "                      sum(target) target,     \n" +
            "                     (select count(*) from requisition_group_members rgm\n" +
            "                \n" +
            "                                join facilities f on f.id = rgm.facilityid \n" +
            "                                join programs_supported ps on ps.facilityid=f.id \n" +
            "                                join requisition_group_program_schedules rgs on rgs.programid=(select id from programs where enableivdform = 't' limit 1) \n" +
            "                                 and  rgs.requisitiongroupid=rgm.requisitiongroupid and rgs.scheduleid=45 \n" +
            "                                where f.geographiczoneid = t.geographiczoneid  and f.active=true \n" +
            "                                and f.sdp = true \n" +
            "                ) expected,   \n" +
            "                      sum(case when reporting_status IN ('T','L') then 1 else 0 end) reported,   \n" +
            "                      sum(case when reporting_status = 'T' then 1 else 0 end) ontime,    \n" +
            "                      sum(case when reporting_status = 'L' then 1 else 0 end) late ,\n" +
            "                      SUM(approved) approved ,\n" +
            "                      sum(distributed) distributed          \n" +
            "\n" +
            "                    from temp t  \n" +
            "                        join vw_districts vd on vd.district_id = t.geographiczoneid \n" +
            "                        --\" + writeDistrictPredicate(params.getDistrict())\n" +
            "                      where vd.district_id in (select district_id from vw_user_facilities where user_id = 307 and program_id = fn_get_vaccine_program_id())  \n" +
            "                    group by 1, 2, 3, 4,5 ,6  \n" +
            "                  ) a   \n" +
            "                     ),  \n" +
            "\n" +
            "                                completness_with_nonreporting_periods as ( \n" +
            "                                      select c.geographiczoneid, periods.*, \n" +
            "                                            (select count(*) from requisition_group_members rgm\n" +
            "                \n" +
            "                                join facilities f on f.id = rgm.facilityid \n" +
            "                                join programs_supported ps on ps.facilityid=f.id  \n" +
            "                                join requisition_group_program_schedules rgs on rgs.programid=(select id from programs where enableivdform = 't' limit 1) \n" +
            "                                 and  rgs.requisitiongroupid=rgm.requisitiongroupid and rgs.scheduleid=45 \n" +
            "                                 \n" +
            "                                where f.geographiczoneid = c.geographiczoneid  and f.active=true \n" +
            "                                and f.sdp = true \n" +
            "                ) expected\n" +
            "                                        from  \n" +
            "                                         ( \n" +
            "                                              select id, name period_name, startdate period_start_date from processing_periods pp  \n" +
            "                                                where pp.startdate::date >='2017-01-01'::date  and pp.enddate::date <= '2017-12-01'::date\n" +
            "                                              AND pp.numberofmonths = 1 \n" +
            "                                          ) periods ,  \n" +
            "                                          ( \n" +
            "                                              select distinct geographiczoneid from  \n" +
            "                                             completeness_with_reporting_periods c \n" +
            "                                          ) c \n" +
            "                                    ) \n" +
            "\n" +
            "                      SELECT         \n" +
            "                      nonreporting.period_name,  \n" +
            "                      nonreporting.period_start_date,\n" +
            "                      Extract(month FROM nonreporting.period_start_date) as month,\n" +
            "                      Extract(year FROM nonreporting.period_start_date) as year,\n" +
            "                      SUM(nonreporting.expected)  expected,  \n" +
            "                      SUM(COALESCE(c.reported,0)) reported,   \n" +
            "                      SUM(COALESCE(c.ontime,0))   ontime,    \n" +
            "                      SUM(COALESCE(c.late,0))     late ,\n" +
            "                       SUM(COALESCE(c.approved,0))approved,\n" +
            "                       SUM(COALESCE(C.distributed,0)) distributed ," +
            "                       \n" +
            "                       ROUND ( CASE when SUM(nonreporting.expected) > 0 THEN \n" +
            "                       100 *( SUM(COALESCE(C.distributed,0))::numeric/SUM(nonreporting.expected) ) else 0 end,0)\n" +
            "                        AS distributed_rate  ,\n" +
            "\n" +
            "                         ROUND ( CASE when SUM(c.approved) > 0 THEN \n" +
            "                       100 *( SUM(COALESCE(C.approved,0))::numeric/SUM(nonreporting.expected) ) else 0 end,0)\n" +
            "                        AS approved_rate  ,\n" +
            "\n" +
            "                         ROUND ( CASE when SUM(c.ontime) > 0 THEN \n" +
            "                       100 *( SUM(COALESCE(C.ontime,0))::numeric/SUM(c.reported) ) else 0 end,0)\n" +
            "                        AS ontime_rate ,\n" +
            "\n" +
            "                         ROUND ( CASE when SUM(c.reported) > 0 THEN \n" +
            "                       100 *( SUM(COALESCE(C.reported,0))::numeric/SUM(nonreporting.expected) ) else 0 end,0)\n" +
            "                        AS reported_rate           \n" +
            "                      FROM completness_with_nonreporting_periods nonreporting  \n" +
            "                      join geographic_zones z on z.id = nonreporting.geographiczoneid   \n" +
            "                      join vw_districts vd on vd.district_id = nonreporting.geographiczoneid \n" +
            "                      left outer join completeness_with_reporting_periods c  On c.geographiczoneid = nonreporting.geographiczoneid AND nonreporting.id = c.priod_id \n" +
            "                    group by 1,2 order by 2")
    List<HashMap<String, Object>>reportingTarget(@Param("userId")Long userId, @Param("periodId")Long periodId, @Param("year")Long year);

    @Select("with q as (\n" +
            "SELECT\n" +
            "  CASE WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood\n" +
            "    THEN 'good'\n" +
            "  WHEN pd.dropout > pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood\n" +
            "    THEN 'normal'\n" +
            "  WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage <= pt.targetcoveragegood\n" +
            "    THEN 'warn'\n" +
            "  ELSE 'bad'\n" +
            "  END AS classificationClass,\n" +
            "  CASE WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood\n" +
            "    THEN 'Cat_1'\n" +
            "  WHEN pd.dropout > pt.targetdropoutgood AND cc.coveragepercentage >= pt.targetcoveragegood\n" +
            "    THEN 'Cat_2'\n" +
            "  WHEN pd.dropout <= pt.targetdropoutgood AND cc.coveragepercentage <= pt.targetcoveragegood\n" +
            "    THEN 'Cat_3'\n" +
            "  ELSE 'Cat_4'\n" +
            "  END AS catagorization,\n" +
            "  cc.month,\n" +
            "  cc.year,\n" +
            "  pd.district_id,\n" +
            "  d.region_id,\n" +
            "  cc.district_name,\n" +
            "  cc.region_name,\n" +
            "  cc.periodid,\n" +
            "  pd.period_name,\n" +
            "  pd.district_name\n" +
            "FROM vw_vaccine_coverage_by_dose_and_district cc\n" +
            "  JOIN vw_penta_dropout_district_summary pd\n" +
            "    ON cc.doseid = 1\n" +
            "       AND pd.productid = cc.productid\n" +
            "       AND pd.district_id = cc.district_id\n" +
            "       AND pd.year = cc.year\n" +
            "       AND pd.month = cc.month\n" +
            "  JOIN vw_districts d on d.district_id = cc.geographiczoneid\n" +
            "  JOIN vaccine_product_targets pt ON pt.productid = cc.productid\n" +
            "WHERE pd.year = 2017 :: INT \n" +
            ")\n" +
            "select catagorization, count(*) total from q\n" +
            "where periodId = #{periodId}\n" +
            "group by catagorization\n" +
            "order by catagorization")
    List<HashMap<String,Object>>getDistrictCategorization(@Param("periodId")Long periodId);
@Select(
        "SELECT region_name region,\n" +
        "ROUND (\n" +
        " (case when sum(target) > 0 then (sum(COALESCE(actual,0)) / \n" +
        "                sum(target)::numeric) else 0 end) * 100,2) coverage\n" +
        "from (\n" +
        "                SELECT\n" +
        "                d.region_name,\n" +
        "                d.district_name,\n" +
        "                i.period_name, \n" +
        "                i.period_start_date,\n" +
        "                sum(i.denominator) target, \n" +
        "                sum(COALESCE(i.within_outside_total,0)) actual,\n" +
        "                (case when sum(denominator) > 0 then (sum(COALESCE(i.within_outside_total,0)) / \n" +
        "                sum(denominator)::numeric) else 0 end) * 100 coverage\n" +
        "                FROM\n" +
        "                vw_vaccine_coverage i\n" +
        "                JOIN vw_districts d ON i.geographic_zone_id = d.district_id\n" +
        "                JOIN vaccine_reports vr ON i.report_id = vr.ID\n" +
        "                JOIN program_products pp ON pp.programid = vr.programid\n" +
        "                AND pp.productid = i.product_id\n" +
        "                WHERE\n" +
        "                i.program_id = ( SELECT id FROM programs p WHERE p .enableivdform = TRUE limit 1) \n" +
        "                AND VR.periodId = #{periodId}\n" +
        "                and i.product_id = #{productId}\n" +
        "                group by 1,2,3,4\n" +
        "                ORDER BY\n" +
        "                d.region_name,\n" +
        "                d.district_name,\n" +
        "                i.period_start_date\n" +
        ")X\n" +
        "group by region_name" +
                " order by coverage desc  \n")
     List<HashMap<String,Object>>getVaccineCoverageByRegionAndProduct(@Param("userId")Long userId, @Param("productId")Long productId,@Param("periodId")Long periodId,@Param("year")Long year);


}