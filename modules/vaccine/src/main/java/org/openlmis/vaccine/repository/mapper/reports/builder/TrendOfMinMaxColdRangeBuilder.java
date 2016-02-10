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
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.builder.helpers.PerformanceByDropOutRateHelper;
import java.util.Map;
import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class TrendOfMinMaxColdRangeBuilder {
    public String getTredofMinMaxColdRangeFacilityQuery(Map params) {
        String query ;
        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        BEGIN();
        SELECT(" d.region_name");
        SELECT("   d.district_name");
        SELECT("    tr.facility_name");
        SELECT("    to_date(tr.period_name, 'Mon YYYY') period_name");
        SELECT(" vt.target_value_monthly targetpopulation");

        SELECT("    tr.period_start_date");
        SELECT("   min( tr.mintemp) mintemp");
        SELECT("   max( tr.maxtemp) maxtemp");
        SELECT("   min( tr.minepisodetemp) minepisodetemp");
        SELECT("   max( tr.maxepisodetemp ) maxepisodetemp");
        FROM(" vw_vaccine_cold_chain tr");
        JOIN(" vw_districts d ON tr.geographic_zone_id = d.district_id");
        JOIN(" geographic_zones gz ON gz.id = d.district_id");
        JOIN(" vw_vaccine_target_population vt ON tr.facility_id =  vt.facility_id and vt.year = extract(year from tr.period_start_date)  and vt.geographic_zone_id = gz.id and vt.category_id = 1");
        WHERE("tr.status= 'Functional'");
        writePredicates(filter);
        GROUP_BY("1,2,3,4,5,6");
        ORDER_BY(" 1,2,3,4,5");
        query = SQL();
        return query;
    }

    private static void writePredicates(PerformanceByDropoutRateParam param) {
        WHERE(" tr.programid = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )");
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate("tr.period_start_date"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate("tr.period_end_date"));
        if (param.getFacility_id() != null && param.getFacility_id() != 0L) {
            WHERE(PerformanceByDropOutRateHelper.isFilteredFacilityId("tr.facility_id"));
        }

        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }

    public String getTredofMinMaxColdRangeDistrictQuery(Map params) {
        String query ;

        BEGIN();
        SELECT(" d.region_name");
        SELECT("   d.district_name");
        SELECT("    to_date(tr.period_name, 'Mon YYYY') period_name");
        SELECT("    tr.period_start_date");
        SELECT(" sum(vt.target_value_monthly) targetpopulation");


        SELECT("   min( tr.mintemp) mintemp");
        SELECT("   max( tr.maxtemp) maxtemp");
        SELECT("   min( tr.minepisodetemp) minepisodetemp");
        SELECT("   max( tr.maxepisodetemp ) maxepisodetemp");
        FROM(" vw_vaccine_cold_chain tr");
        JOIN(" vw_districts d ON tr.geographic_zone_id = d.district_id");
        JOIN(" geographic_zones gz ON gz.id = d.district_id");
        JOIN(" vw_vaccine_target_population vt ON tr.facility_id =  vt.facility_id and vt.year = extract(year from tr.period_start_date)  and vt.geographic_zone_id = gz.id and vt.category_id = 1");
        WHERE("tr.status= 'Functional'");
        writeRegionPredicates();
        GROUP_BY("1,2,3,4");
        ORDER_BY(" 1,2,3");
        query = SQL();
        return query;
    }



    public String getTredofMinMaxColdRangeRegionQuery(Map params) {
        String query ;

        BEGIN();
        SELECT(" d.region_name");
        SELECT("    to_date(tr.period_name, 'Mon YYYY') period_name");


        SELECT("    tr.period_start_date");
        SELECT(" sum(vt.target_value_monthly) targetpopulation");
        SELECT("   min( tr.mintemp) mintemp");
        SELECT("   max( tr.maxtemp) maxtemp");
        SELECT("   min( tr.minepisodetemp) minepisodetemp");
        SELECT("   max( tr.maxepisodetemp ) maxepisodetemp");
        FROM(" vw_vaccine_cold_chain tr");
        JOIN(" vw_districts d ON tr.geographic_zone_id = d.district_id");
        JOIN(" geographic_zones gz ON gz.id = d.district_id");
        JOIN(" vw_vaccine_target_population vt ON tr.facility_id =  vt.facility_id and vt.year = extract(year from tr.period_start_date)  and vt.geographic_zone_id = gz.id and vt.category_id = 1");
        WHERE("tr.status= 'Functional'");
        writeRegionPredicates();
        GROUP_BY("1,2,3");
        ORDER_BY(" 1,2");

        query = SQL();
        return query;
    }

    private static void writeRegionPredicates() {
        WHERE(" tr.programid = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )");
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate("tr.period_start_date"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate("tr.period_end_date"));


        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }
/*

 */
    public static String getFacilityVaccineTargetInformation() {
        String query ;

        BEGIN();
        SELECT(" d.region_name");
        SELECT("   d.district_name");
        SELECT("    f.name facility_name");
        SELECT(" vt.target_value_monthly targetpopulation");
        FROM(" vw_vaccine_target_population vt ");
        JOIN(" geographic_zones gz ON gz.id = vt.geographic_zone_id");
        JOIN(" vw_districts d ON vt.geographic_zone_id = d.district_id ");
        JOIN(" facilities f on f.id=vt.facility_id");
        writePopulationPredicts();
        GROUP_BY(" 1,2,3,4 ");
        query = SQL();
        return query;
    }
    public static String getDistrictVaccineTargetInformation() {
        String query ;

        BEGIN();
        SELECT(" d.region_name");
        SELECT("   d.district_name");

        SELECT(" sum( vt.target_value_monthly) targetpopulation");
        FROM(" vw_vaccine_target_population vt ");
        JOIN(" geographic_zones gz ON gz.id = vt.geographic_zone_id");
        JOIN(" vw_districts d ON vt.geographic_zone_id = d.district_id ");
        writePopulationPredicts();
        GROUP_BY(" 1,2 ");
        query = SQL();
        return query;
    }
    public static String getRegionVaccineTargetInformation() {
        String query ;

        BEGIN();
        SELECT(" d.region_name");

        SELECT(" sum( vt.target_value_monthly) targetpopulation");
        FROM(" vw_vaccine_target_population vt ");
        JOIN(" geographic_zones gz ON gz.id = vt.geographic_zone_id");
        JOIN(" vw_districts d ON vt.geographic_zone_id = d.district_id ");
        writePopulationPredicts();
        GROUP_BY(" 1 ");
        query = SQL();
        return query;
    }
    private static void writePopulationPredicts() {
        WHERE(" vt.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )");
        WHERE(" vt.category_id=(select id from demographic_estimate_categories c where c.name='Population')");
        WHERE(PerformanceByDropOutRateHelper.isFilteredByYearFromPeriodStart("vt.year"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }

}
