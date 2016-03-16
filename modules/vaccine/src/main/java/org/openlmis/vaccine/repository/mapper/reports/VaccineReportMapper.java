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

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.ivdform.domain.reports.*;
import org.openlmis.vaccine.domain.reports.VaccineCoverageReport;
import org.openlmis.vaccine.repository.mapper.reports.builder.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface VaccineReportMapper {

    @Select("select COALESCE(cases, 0) as cases, COALESCE(death, 0) as death, COALESCE(cum_cases, 0) as cumulative, disease_name as diseaseName \n" +
            "from vw_vaccine_disease_surveillance \n" +
            "where report_id = #{reportId} order by display_order")
    List<DiseaseLineItem> getDiseaseSurveillance(@Param("reportId") Long reportId);

    @Select("SELECT disease_name as diseaseName,display_order,\n" +
            " sum(COALESCE (cases, 0)) AS calculatedCumulativeCases,\n" +
            " sum(COALESCE (death, 0)) AS calculatedCumulativeDeaths\n" +
            "FROM\n" +
            " vw_vaccine_disease_surveillance d\n" +
            " INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id \n" +
            "               where period_start_date <= (select startdate from processing_periods \n" +
            "                           where id = (select periodid from vaccine_reports where id = #{reportId})) \n" +
            "            and   period_year = (select extract(year from startdate) from processing_periods           \n" +
            "                            where id = (select periodid from vaccine_reports where id =  #{reportId}))\n" +
            " and facility_id=  #{facilityId} \n" +
            "group by diseaseName, display_order order by display_order")
    @MapKey("diseaseName")
    @ResultType(HashMap.class)
    Map<String, DiseaseLineItem> getCumFacilityDiseaseSurveillance(@Param("reportId") Long reportId,@Param("facilityId") Long facilityId );

    @Select("SELECT disease_name as diseaseName, display_order,\n" +
            " sum(COALESCE (cases, 0)) AS calculatedCumulativeCases,\n" +
            " sum(COALESCE (death, 0)) AS calculatedCumulativeDeaths\n" +
            "FROM\n" +
            " vw_vaccine_disease_surveillance d\n" +
            " INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id \n" +
            "where period_start_date::date <= (select startdate::date from processing_periods \n" +
            "                          where id =  #{periodId}) \n" +
            "            and   period_year = (select extract(year from startdate) from processing_periods \n" +
            "                          where id = #{periodId})\n" +
            " and (vd.parent = #{zoneId} or vd.district_id = #{zoneId} or vd.region_id = #{zoneId} or vd.zone_id = #{zoneId} ) " +
            "group by diseaseName, display_order order by display_order")
    @MapKey("diseaseName")
    @ResultType(HashMap.class)
    Map<String, DiseaseLineItem> getCumDiseaseSurveillanceAggregateByGeoZone(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("Select id from vaccine_reports where facilityid = #{facilityId} and periodid = #{periodId}")
    Long getReportIdForFacilityAndPeriod(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

    @Select("select disease_name as diseaseName, display_order,\n" +
            "SUM(COALESCE(cum_cases,0)) cumulative,\n" +
            "SUM(COALESCE(cum_deaths,0)) calculatedCumulativeDeaths,\n" +
            "SUM(COALESCE(cases, 0)) cases,\n" +
            "SUM(COALESCE(death,0)) death\n" +
            "from vw_vaccine_disease_surveillance\n" +
            "INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id\n" +
            "where period_id = #{periodId} and (vd.parent = #{zoneId} or vd.district_id = #{zoneId} or vd.region_id = #{zoneId} or vd.zone_id = #{zoneId} )\n" +
            "group by disease_name, display_order order by display_order \n")
    List<DiseaseLineItem> getDiseaseSurveillanceAggregateByGeoZone(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select equipment_name as equipmentName, model, minTemp, maxTemp, \n" +
            " minEpisodeTemp, maxEpisodeTemp, energy_source as energySource, serialnumber as serial, geographic_zone_name location_value\n" +
            "from vw_vaccine_cold_chain \n" +
            "where report_id = #{reportId} order by 1 ")
    List<ColdChainLineItem> getColdChain(@Param("reportId") Long reportId);

    @Select("select equipment_name as equipmentName,\n" +
            "model,\n" +
            "energy_source as energySource" +
            ", serialnumber as serial, " +
            "geographic_zone_name as location_value, \n" +
            "MIN(COALESCE(minTemp,0)) minTemp,\n" +
            "MAX(COALESCE(maxTemp,0)) maxTemp,\n" +
            "MIN(COALESCE(minEpisodeTemp,0)) minEpisodeTemp,\n" +
            "MAX(COALESCE(maxEpisodeTemp,0)) maxEpisodeTemp \n" +
             "from vw_vaccine_cold_chain \n" +
            "join vw_districts d ON d.district_id = geographic_zone_id\n" +
            "where period_id = #{periodId} and (d.parent = #{zoneId} or d.district_id = #{zoneId} or d.region_id = #{zoneId} or d.zone_id = #{zoneId})\n" +
            "group by equipment_name, model, energy_source, serialnumber,location_value order by 1\n")
    List<ColdChainLineItem> getColdChainAggregateReport(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select product_name as productName, aefi_expiry_date as expiry, aefi_case as cases, aefi_batch as batch, manufacturer, is_investigated as isInvestigated from vw_vaccine_iefi \n" +
            "where report_id = #{reportId}    order by display_order"

    )
    List<AdverseEffectLineItem> getAdverseEffectReport(@Param("reportId") Long reportId);

    @Select("select product_name as productName, display_order,\n" +
            "MIN(aefi_expiry_date) as expiry,\n" +
            "SUM(COALESCE(aefi_case,0)) as cases, \n" +
            "null as batch,\n" +
            "null as manufacturer,\n" +
            "null as isInvestigated \n" +
            "from vw_vaccine_iefi \n" +
            "join vw_districts d ON d.district_id = geographic_zone_id\n" +
            "where period_id = #{periodId} and (d.parent = #{zoneId} or d.district_id = #{zoneId} or d.region_id = #{zoneId} or d.zone_id = #{zoneId} )\n" +
            "group by product_name,display_order" +
            " order by display_order ")
    List<AdverseEffectLineItem> getAdverseEffectAggregateReport(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("" +
            "select \n" +
            " \n" +
            "            product_name ||'_'|| display_name product_name,\n" +
            "           sum(  COALESCE(within_total,0)) within_total,  \n" +
            "           case when sum(COALESCE(denominator,0)) > 0 \n" +
            "           then round(sum(  COALESCE(within_total,0)) / sum(COALESCE(denominator,0))::numeric * 100, 2)\n" +
            "            else 0 end within_coverage,   \n" +
            "           sum(COALESCE(outside_total, 0)) outside_total,  \n" +
            "           sum(   COALESCE(within_outside_total, 0)) within_outside_total,             \n" +
            "           case when sum(COALESCE(denominator,0)) > 0 \n" +
            "           then round(sum(  COALESCE(within_outside_total,0)) / sum(COALESCE(denominator,0))::numeric * 100,2) \n" +
            "           else 0 end within_outside_coverage  \n" +
            "              from vw_vaccine_coverage \n" +
            "              where period_start_date <= (select startdate from processing_periods\n" +
            "               where id = (select periodid from vaccine_reports where id = #{reportId}))" +
            "and   period_year = (select extract(year from startdate) from processing_periods\n" +
            "                where id = (select periodid from vaccine_reports where id = #{reportId})) " +
            " and facility_id=#{facilityId}" +
            "              group by 1 \n" +
            "")

    @MapKey("product_name")
    @ResultType(HashMap.class)
    Map<String, VaccineCoverageReport> calculateVaccineCoverageReport(@Param("reportId") Long reportId,@Param("facilityId") Long facilityId);

    @Select("" +
            "select \n" +
            " \n" +
            "            product_name ||'_'|| display_name product_name,\n" +

            "           sum(  COALESCE(within_total,0)) within_total,  \n" +
            "           case when sum(COALESCE(denominator,0)) > 0 \n" +
            "           then round(sum(  COALESCE(within_total,0)) / sum(COALESCE(denominator,0))::numeric * 100, 2)\n" +
            "            else 0 end within_coverage,   \n" +

            "           sum(COALESCE(outside_total, 0)) outside_total,  \n" +
            "           sum(   COALESCE(within_outside_total, 0)) within_outside_total,             \n" +
            "           case when sum(COALESCE(denominator,0)) > 0 \n" +
            "           then round(sum(  COALESCE(within_outside_total,0)) / sum(COALESCE(denominator,0))::numeric * 100,2) \n" +
            "           else 0 end within_outside_coverage   \n" +

            "              from vw_vaccine_coverage \n" +
            " INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id \n" +
            "              where period_start_date <= (select startdate from processing_periods\n" +
            "               where id =  #{periodId})" +
            "and   period_year = (select extract(year from startdate) from processing_periods\n" +
            "               where id   = #{periodId})" +
            " and (vd.parent = #{zoneId} or vd.district_id = #{zoneId} or vd.region_id = #{zoneId} or vd.zone_id = #{zoneId} )" +
            "              group by 1")

    @MapKey("product_name")
    @ResultType(HashMap.class)
    Map<String, VaccineCoverageReport> calculateAggeregatedVaccineCoverageReport(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select \n" +
            "product_name,\n" +
            "display_name, \n" +
            " COALESCE(denominator, 0) denominator," +
            "COALESCE(within_male, 0) within_male, \n" +
            "COALESCE(within_female,0) within_female, \n" +
            "COALESCE(within_total,0) within_total, \n" +
            "           case when COALESCE(denominator, 0) > 0 then \n" +
            "COALESCE(within_total,0) /  COALESCE(denominator, 0)::numeric * 100" +
            " else 0 end within_coverage,  \n" +
            "COALESCE(outside_male, 0) outside_male, \n" +
            "COALESCE(outside_female,0) outside_female, COALESCE(outside_total, 0) outside_total, \n" +
            "COALESCE(within_outside_total, 0) within_outside_total, \n" +
            "           case when COALESCE(denominator,0) > 0 then \n" +
            "COALESCE(within_outside_total,0) /  COALESCE(denominator, 0)::numeric * 100" +
            " else 0 end  within_outside_coverage, \n" +
            "COALESCE(cum_within_total,0) cum_within_total, \n" +
            "COALESCE(cum_within_coverage,0) cum_within_coverage, \n" +
            "COALESCE(cum_outside_total,0) cum_outside_total, \n" +
            "COALESCE(cum_within_outside_total,0) cum_within_outside_total, " +
            " case when  COALESCE(denominator, 0)::numeric> 0 then  \n" +
            "COALESCE(cum_within_outside_total,0) /  COALESCE(denominator, 0)::numeric * 100 else 0 end cum_within_outside_coverage, \n" +
            "case when dtp_1 > 0 then ((dtp_1 - dtp_3)::double precision / dtp_1::double precision) * 100 else 0 end dtp_dropout, \n" +
            "case when bcg_1 > 0 then ((bcg_1 - mr_1)::double precision / bcg_1::double precision) * 100 else 0 end bcg_mr_dropout \n" +
            "  from vw_vaccine_coverage  \n" +
            "  where report_id = #{reportId}" +
            " order by display_order")
    List<HashMap<String, Object>> getVaccineCoverageReport(@Param("reportId") Long reportId);

    @Select("select \n" +
            "product_name product_name,\n" +
            "display_name display_name,  \n" +
            "display_order display_order,  \n" +
            "sum( COALESCE(denominator, 0)) denominator," +
            "SUM(COALESCE(within_male, 0)) within_male,  \n" +
            "SUM(COALESCE(within_female,0)) within_female,  \n" +
            "SUM(COALESCE(within_total,0)) within_total,  \n" +
            " case when  sum( COALESCE(denominator, 0))::numeric> 0 then  \n" +
            "SUM(COALESCE(within_total,0)) / sum( COALESCE(denominator, 0))::numeric * 100" +
            " else 0 end within_coverage,  \n" +
            "SUM(COALESCE(outside_male, 0)) outside_male,  \n" +
            "SUM(COALESCE(outside_female,0)) outside_female, \n" +
            "SUM(COALESCE(outside_total, 0)) outside_total, \n" +
            "SUM(COALESCE(within_outside_total, 0)) within_outside_total,  \n" +
            " case when  sum( COALESCE(denominator, 0))::numeric> 0 then  \n" +
            "SUM(COALESCE(within_outside_total,0)) / sum( COALESCE(denominator, 0))::numeric * 100" +
            " else 0 end within_outside_coverage, \n" +
            "SUM(COALESCE(cum_within_total,0)) cum_within_total,  \n" +
            "SUM(COALESCE(cum_within_coverage,0)) cum_within_coverage, \n" +
            "SUM(COALESCE(cum_outside_total,0)) cum_outside_total,  \n" +
            "SUM(COALESCE(cum_within_outside_total,0)) cum_within_outside_total, \n" +
            " case when  sum( COALESCE(denominator, 0))::numeric> 0 then  \n" +
            "SUM(COALESCE(cum_within_outside_total,0)) / sum( COALESCE(denominator, 0))::numeric * 100" +
            " else 0 end cum_within_outside_coverage, \n" +
            "SUM(COALESCE(case when dtp_1 > 0 then ((dtp_1 - dtp_3)::double precision / dtp_1::double precision) * 100 else 0 end)) dtp_dropout, \n" +
            "SUM(COALESCE(case when bcg_1 > 0 then ((bcg_1 - mr_1)::double precision / bcg_1::double precision) * 100 else 0 end)) bcg_mr_dropout \n" +
            "from vw_vaccine_coverage  \n" +
            "INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id \n" +
            "where period_id = #{periodId} and (vd.parent = #{zoneId} or vd.district_id = #{zoneId} or vd.region_id = #{zoneId} or vd.zone_id = #{zoneId} )\n" +
            "group by product_name,display_name,display_order \n" +
            "order by display_order")
    List<HashMap<String, Object>> getVaccineCoverageAggregateReportByGeoZone(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("SELECT COALESCE(fixedimmunizationsessions, 0) fixedimmunizationsessions, COALESCE(outreachimmunizationsessions, 0) outreachimmunizationsessions, COALESCE(outreachimmunizationsessionscanceled, 0) outreachimmunizationsessionscanceled FROM vaccine_reports WHERE id = #{reportId} ")
    List<VaccineReport> getImmunizationSession(@Param("reportId") Long reportId);

    @Select("SELECT \n" +
            "sum(COALESCE(fixedimmunizationsessions, 0)) fixedimmunizationsessions, \n" +
            "sum(COALESCE(outreachimmunizationsessions, 0)) outreachimmunizationsessions, \n" +
            "sum(COALESCE(outreachimmunizationsessionscanceled, 0)) outreachimmunizationsessionscanceled \n" +
            "FROM vaccine_reports r\n" +
            "join facilities f on r.facilityid = f.id\n" +
            "join vw_districts d ON d.district_id = f.geographiczoneid\n" +
            "where r.periodid = #{periodId} and (d.parent = #{zoneId} or d.district_id = #{zoneId} or d.region_id = #{zoneId} or d.zone_id = #{zoneId} )\n")
    List<VaccineReport> getImmunizationSessionAggregate(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select * from vw_vaccine_stock_status where product_category_code = " +
            "(select value from configuration_settings" +
            " where key = #{productCategoryCode}) and report_id = #{reportId}" +
            " order by display_order")
    List<HashMap<String, Object>> getVaccinationReport(@Param("productCategoryCode") String categoryCode, @Param("reportId") Long reportId);

    @Select("select * from vw_vaccine_target_population\n" +
            "where facility_id = #{facilityId} and year =  (select date_part('year'::text, processing_periods.startdate) from processing_periods where id = #{periodId})\n" +
            "order by category_id\n")
    List<HashMap<String, Object>> getTargetPopulation(@Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

    @Select("           select tp.category_name,\n" +
            "             sum(COALESCE(tp.target_value_annual,0)) target_value_annual,   \n" +
            "             round(sum(COALESCE(tp.target_value_annual,0))/12) target_value_monthly   \n" +
            "             from vw_vaccine_target_population tp   \n" +
            "              join vw_districts d on d.district_id = tp.geographic_zone_id\n" +
            "              join vaccine_reports vr on vr.facilityid = tp.facility_id and vr.programid = tp.program_id            \n" +
            "             where  tp.year = (select date_part('year'::text, processing_periods.startdate) from processing_periods where id = #{periodId} )  \n" +
            "             and (d.parent = #{zoneId} or d.district_id = #{zoneId} or d.region_id = #{zoneId} or d.zone_id = #{zoneId})\n" +
            "             and vr.periodid = #{periodId}   \n" +
            "             group by tp.category_id, tp.category_name   \n" +
            "             order by tp.category_id  \n")
    List<HashMap<String, Object>> getTargetPopulationAggregateByGeoZone(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("Select age_group AS ageGroup, vitamin_name AS vitaminName, male_value AS maleValue, female_value AS femaleValue from vw_vaccine_vitamin_supplementation where report_id = #{reportId} order by age_group_display_order")
    List<VitaminSupplementationLineItem> getVitaminSupplementationReport(@Param("reportId") Long reportId);

    @Select("Select age_group AS ageGroup,\n" +
            "vitamin_name AS vitaminName,\n" +
            "age_group_display_order AS age_group_display_order,\n" +
            "SUM(COALESCE(male_value, 0)) AS maleValue,\n" +
            "SUM(COALESCE(female_value,0)) AS femaleValue\n" +
            "from vw_vaccine_vitamin_supplementation\n" +
            "join vw_districts d ON d.district_id = geographic_zone_id\n" +
            "where period_id = #{periodId} and (d.parent = #{zoneId} or d.district_id = #{zoneId} or d.region_id = #{zoneId} or d.zone_id = #{zoneId} ) \n" +
            "group by 1,2,3\n" +
            "order by age_group_display_order\n")
    List<VitaminSupplementationLineItem> getVitaminSupplementationAggregateReport(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select COALESCE(fr.quantity_issued, 0) quantity_issued, COALESCE(fr.closing_balance, 0) closing_balance, pp.name period_name \n" +
            "from fn_vaccine_facility_n_rnrs('Vaccine',#{facilityCode}, #{productCode},4) fr \n" +
            "JOIN processing_periods pp ON pp.id = fr.period_id\n" +
            " order by pp.id asc;")
    List<HashMap<String, Object>> vaccineUsageTrend(@Param("facilityCode") String facilityCode, @Param("productCode") String productCode);

    @Select("SELECT product_code,\n" +
            "MAX(display_order) display_order," +
            "MAX(product_name) product_name,\n" +
            "sum(opening_balanace) opening_balanace,\n" +
            "sum(quantity_received) quantity_received,\n" +
            "sum(quantity_issued) quantity_issued,\n" +
            "sum(quantity_vvm_alerted) quantity_vvm_alerted,\n" +
            "sum(quantity_freezed) quantity_freezed,\n" +
            "sum(quantity_expired) quantity_expired,\n" +
            "sum(quantity_discarded_unopened) quantity_discarded_unopened,\n" +
            "sum(quantity_discarded_opened) quantity_discarded_opened,\n" +
            "sum(quantity_wasted_other) quantity_wasted_other,\n" +
            "sum(closing_balance) closing_balance,\n" +
            "sum(expired) expired,\n" +
            "sum(broken) broken,\n" +
            "sum(cold_chain_failure) cold_chain_failure,\n" +
            "sum(other) other,\n" +
            "sum(days_stocked_out) days_stocked_out,\n" +
            "'' AS reason_for_discarding,\n" +
            "sum(children_immunized) children_immunized,\n" +
            "sum(pregnant_women_immunized) pregnant_women_immunized,\n" +
            "sum(COALESCE(vaccinated,0)::numeric) vaccinated,\n" +
            "sum(COALESCE(usage_denominator,0)::numeric) usage_denominator,\n" +
            "case when sum(usage_denominator) > 0 \n" +
            "then sum(vaccinated)::numeric/ sum(usage_denominator)::numeric * 100 else 0 \n" +
            "end usage_rate,\n" +
            "case when (\n" +
            "100 - case \n" +
            "    when sum(usage_denominator) = 0 then 0 \n" +
            "    when sum(usage_denominator) > 0 then \n" +
            "    sum(vaccinated)::numeric/ sum(usage_denominator)::numeric * 100\n" +
            "   else 0 \n" +
            "   end ) < 0 then 0 else\n" +
            "100 - case \n" +
            "    when sum(usage_denominator) = 0 then 0 \n" +
            "    when sum(usage_denominator) > 0 then \n" +
            "    sum(vaccinated)::numeric/ sum(usage_denominator)::numeric * 100\n" +
            "   else 0 \n" +
            "   end \n" +
            "end wastage_rate \n" +
            "from vw_vaccine_stock_status \n" +
            "INNER JOIN vw_districts vd ON vd.district_id = geographic_zone_id\n" +
            "where  product_category_code = (select value from configuration_settings where key = #{productCategoryCode}) and period_id = #{periodId} and (vd.parent = #{zoneId} or vd.district_id = #{zoneId} or vd.region_id = #{zoneId} or vd.zone_id = #{zoneId} )\n" +
            "group by product_code \n" +
            " order by display_order")
    List<HashMap<String, Object>> getVaccinationAggregateByGeoZoneReport(@Param("productCategoryCode") String categoryCode, @Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select COALESCE(fr.quantity_issued, 0) quantity_issued, COALESCE(fr.closing_balance, 0) closing_balance, pp.name period_name \n" +
            "from fn_vaccine_geozone_n_rnrs('Vaccine', #{periodId}::integer ,#{zoneId}::integer, #{productCode},4) fr\n" +
            "JOIN processing_periods pp ON pp.id = fr.period_id\n" +
            " order by pp.id asc")
    List<HashMap<String, Object>> vaccineUsageTrendByGeographicZone(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId, @Param("productCode") String productCode);

    @Select("select\n" +
            "product_code, \n" +
            "case when product_code = (select value from configuration_settings where key = ('VACCINE_DROPOUT_BCG')) then \n" +
            "'BCG - MR1 ' \n" +
            "else \n" +
            "'DTP-HepB-Hib1/DTP-HepB-Hib3' \n" +
            "end indicator,\n" +
            " case when product_code = (select value from configuration_settings where key = ('VACCINE_DROPOUT_BCG')) then  \n" +
            "             case when sum(bcg_1) > 0 then round((sum(bcg_1) - sum(mr_1))::numeric /sum(bcg_1)::numeric,4) * 100 else 0 end    \n" +
            "            else  \n" +
            "             case when sum(dtp_1) > 0 then round((sum(dtp_1) - sum(dtp_3))::numeric /sum(dtp_3)::numeric,4) * 100 else 0 end   \n" +
            "            end dropout \n" +
            "from vw_vaccine_dropout i\n" +
            "join vw_districts d on i.geographic_zone_id = d.district_id\n" +
            "join vaccine_reports vr on i.report_id = vr.id\n" +
            "JOIN program_products pp ON pp.programid = vr.programid AND pp.productid = i.product_id\n" +
            "JOIN product_categories pg ON pp.productcategoryid = pg.id\n" +
            "where \n" +
            " product_code in (select value from configuration_settings \n" +
            " where key in ('VACCINE_DROPOUT_BCG','VACCINE_DROPOUT_MR','VACCINE_DROPOUT_DTP'))\n" +
            "and i.period_id = #{periodId} and (d.parent = #{zoneId} or d.district_id = #{zoneId} or d.region_id = #{zoneId} or d.zone_id = #{zoneId} )\n" +
            "and i.product_code in ('V001','V010')\n" +
            "group by 1\n" +
            "  order by 1,2")
    List<HashMap<String, Object>> getAggregateDropOuts(@Param("periodId") Long periodId, @Param("zoneId") Long zoneId);

    @Select("select product_code, \n" +
            "case when product_code = (select value from configuration_settings where key = ('VACCINE_DROPOUT_BCG')) then \n" +
            "'BCG - MR1 ' \n" +
            "else \n" +
            "'DTP-HepB-Hib1/DTP-HepB-Hib3' \n" +
            "end indicator,\n" +
            " case when product_code = (select value from configuration_settings where key = ('VACCINE_DROPOUT_BCG')) then  \n" +
            "             case when sum(bcg_1) > 0 then round((sum(bcg_1) - sum(mr_1))::numeric /sum(bcg_1)::numeric,4) * 100 else 0 end    \n" +
            "            else  \n" +
            "             case when sum(dtp_1) > 0 then round((sum(dtp_1) - sum(dtp_3))::numeric /sum(dtp_3)::numeric,4) * 100 else 0 end   \n" +
            "            end dropout\n" +
            "from vw_vaccine_dropout i\n" +
            "join vw_districts d on i.geographic_zone_id = d.district_id\n" +
            "join vaccine_reports vr on i.report_id = vr.id\n" +
            "JOIN program_products pp ON pp.programid = vr.programid AND pp.productid = i.product_id\n" +
            "JOIN product_categories pg ON pp.productcategoryid = pg.id\n" +
            "where \n" +
            " product_code in (select value from configuration_settings \n" +
            " where key in ('VACCINE_DROPOUT_BCG','VACCINE_DROPOUT_MR','VACCINE_DROPOUT_DTP'))\n" +
            "and report_id = #{reportId}\n" +
            "and i.product_code in ('V001','V010')\n" +
            "group by 1\n" +
            " order by 1,2")
    List<HashMap<String, Object>> getDropOuts(@Param("reportId") Long reportId);

    @Select("select * from geographic_zones where parentid is null")
    GeographicZone getNationalZone();

    // Performance coverage report
    @SelectProvider(type = PerformanceCoverageQueryBuilder.class, method = "selectPerformanceCoverageMainReportDataByDistrict")
    List<Map<String, Object>> getPerformanceCoverageMainReportDataByDistrict(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("districtId") Long districtId,
                                                                             @Param("productId") Long productId);


    @SelectProvider(type = PerformanceCoverageQueryBuilder.class, method = "selectPerformanceCoverageSummaryReportDataByDistrict")
    List<Map<String, Object>> getPerformanceCoverageSummaryReportDataByDistrict(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("districtId") Long districtId,
                                                                                @Param("productId") Long productId);

    @SelectProvider(type = PerformanceCoverageQueryBuilder.class, method = "selectPerformanceCoverageMainReportDataByRegion")
    List<Map<String, Object>> getPerformanceCoverageMainReportDataByRegion(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("districtId") Long districtId,
                                                                           @Param("productId") Long productId);


    @SelectProvider(type = PerformanceCoverageQueryBuilder.class, method = "selectPerformanceCoverageSummaryReportDataByRegion")
    List<Map<String, Object>> getPerformanceCoverageSummaryReportDataByRegion(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("districtId") Long districtId,
                                                                              @Param("productId") Long productId);

    @SelectProvider(type = PerformanceCoverageQueryBuilder.class, method = "selectPerformanceCoverageMainReportDataByRegionAggregate")
    List<Map<String, Object>> getPerformanceCoverageMainReportDataByRegionAggregate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("districtId") Long districtId,
                                                                                    @Param("productId") Long productId);

    @SelectProvider(type = PerformanceCoverageQueryBuilder.class, method = "selectPerformanceCoverageSummaryReportDataByRegionAggregate")
    List<Map<String, Object>> getPerformanceCoverageSummaryReportDataByRegionAggregate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("districtId") Long districtId,
                                                                                       @Param("productId") Long productId);


    // Completeness and Timeliness report

    @SelectProvider(type = CompletenessAndTimelinessQueryBuilder.class, method = "selectCompletenessAndTimelinessMainReportDataByDistrict")
    List<Map<String, Object>> getCompletenessAndTimelinessMainReportDataByDistrict(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                                                   @Param("districtId") Long districtId,
                                                                                   @Param("productId") Long productId);

    @SelectProvider(type = CompletenessAndTimelinessQueryBuilder.class, method = "selectCompletenessAndTimelinessSummaryReportDataByDistrict")
    List<Map<String, Object>> getCompletenessAndTimelinessSummaryReportDataByDistrict(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                                                      @Param("districtId") Long districtId,
                                                                                      @Param("productId") Long productId);


    @SelectProvider(type = AdequacyLevelReportQueryBuilder.class, method = "selectAdequacyLevelOfSupplyReportDataByDistrict")
    List<Map<String, Object>> getAdequacyLevelOfSupplyReportDataByDistrict(@Param("startDate") Date startDate,
                                                                           @Param("endDate") Date endDate,
                                                                           @Param("districtId") Long districtId,
                                                                           @Param("productId") Long productId);

    @SelectProvider(type = AdequacyLevelReportQueryBuilder.class, method = "selectAdequacyLevelOfSupplyReportDataByRegion")
    List<Map<String, Object>> getAdequacyLevelOfSupplyReportDataByRegion(@Param("startDate") Date startDate,
                                                                         @Param("endDate") Date endDate,
                                                                         @Param("districtId") Long districtId,
                                                                         @Param("productId") Long productId);


    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getVaccineProducts")
   public List<Map<String, Object>> getVaccineProductsList();

    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "selectClassficationUtilizationPerformanceForFacility")
                                                                                                                                                  public  List<Map<String,Object>> getClassificationVaccineUtilizationPerformanceForFacility1(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "selectClassficationUtilizationPerformanceForDistrict")
    public  List<Map<String,Object>> getClassificationVaccineUtilizationPerformanceForDistrict1(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "selectClassficationUtilizationPerformanceForRegion")
    public  List<Map<String,Object>> getClassificationVaccineUtilizationPerformanceForRegion1(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = CategorizationVaccineUtilizationPerformanceQueryBuilder.class, method = "selectCategorizationUtilizationPerformanceForFacility")
    public  List<Map<String,Object>> getCategorizationVaccineUtilizationPerformanceForFacility(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = CategorizationVaccineUtilizationPerformanceQueryBuilder.class, method = "selectCategorizationUtilizationPerformanceForDistrict")
    public  List<Map<String,Object>> getCategorizationVaccineUtilizationPerformanceForDistrict(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = CategorizationVaccineUtilizationPerformanceQueryBuilder.class, method = "selectCategorizationUtilizationPerformanceForRegion")
    public  List<Map<String,Object>> getCategorizationVaccineUtilizationPerformanceForRegion(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getFacilityPopulationInformation")
    public  List<Map<String,Object>> getClassficationVaccinePopulationForFacility(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getDistrictPopulationInformation")
    public  List<Map<String,Object>> getClassficationVaccinePopulationForDistrict(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);

    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getRegionPopulationInformation")
    public  List<Map<String,Object>> getClassficationVaccinePopulationForRegion(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId);
    @SelectProvider(type=ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getYearQuery")
    public List<Map<String,Object>> getDistincitYearList();
}
