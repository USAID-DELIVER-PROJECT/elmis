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

package org.openlmis.vaccine.repository.mapper.reports.builder;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.builder.helpers.PerformanceByDropOutRateHelper;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class TrendOfMinMaxColdRangeBuilder {
    public String getTredofMinMaxColdRangeFacilityQuery(Map params) {
        String query = "";
        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        BEGIN();
        SELECT(" d.region_name");
        SELECT("   d.district_name");
        SELECT(" gz.catchmentpopulation targetpopulation");
        SELECT(" tr.period_id");
        SELECT("    to_date(tr.period_name, 'Mon YYYY') period_name");
        SELECT("    tr.period_start_date");
        SELECT("    tr.period_end_date");
        SELECT("    tr.geographic_zone_id");
        SELECT("    tr.geographic_zone_name");
        SELECT("    tr.level_id");
        SELECT("    tr.parent_id");
        SELECT("    tr.facility_id");
        SELECT("    tr.facility_code");
        SELECT("    tr.facility_name");
        SELECT("    tr.report_id");
        SELECT("    tr.programid");

        SELECT("    tr.equipment_name");
        SELECT("    tr.model");

        SELECT("    tr.equipment_type_name");
        SELECT("    tr.mintemp");
        SELECT("    tr.maxtemp");
        SELECT("    tr.minepisodetemp");
        SELECT("    tr.maxepisodetemp");
        SELECT("    tr.energy_source");
//        SELECT("    vt.targetpopulation");
        SELECT("    tr.status");
        FROM(" vw_vaccine_cold_chain tr");
        JOIN(" vw_districts d ON tr.geographic_zone_id = d.district_id");
        JOIN(" geographic_zones gz ON gz.id = d.district_id");
//        JOIN(" vaccine_facility_targets vt ON tr.facility_id =  vt.facilityid");
        writePredicates(filter);

        ORDER_BY("        tr.geographic_zone_name, tr.facility_name,  to_date(tr.period_name, 'Mon YYYY')");
        query = SQL();
        return query;
    }

    private static void writePredicates(PerformanceByDropoutRateParam param) {
        WHERE(" tr.programid = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )");
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate("tr.period_start_date"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate("tr.period_end_date"));
        if (param.getFacility_id() != null && param.getFacility_id() != 0l) {
            WHERE(PerformanceByDropOutRateHelper.isFilteredFacilityId("tr.facility_id"));
        }
//    discuss why product id is not part of the view
//    WHERE(PerformanceByDropOutRateHelper.isFilteredProductId("tr.product_id"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }

    public String getTredofMinMaxColdRangeDistrictQuery(Map params) {
        String query = "";
        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        BEGIN();
        SELECT(" d.region_name");
        SELECT("   d.district_name");
        SELECT(" sum(gz.catchmentpopulation ) targetpopulation");
        SELECT("    to_date(tr.period_name, 'Mon YYYY') period_name");
        SELECT("  sum(  tr.mintemp) mintemp");
        SELECT("  sum(  tr.maxtemp) maxtemp");
        SELECT(" sum(   tr.minepisodetemp) minepisodetemp");
        SELECT("   sum( tr.maxepisodetemp)  maxepisodetemp");
        FROM(" vw_vaccine_cold_chain tr");
        JOIN(" vw_districts d ON tr.geographic_zone_id = d.district_id");
        JOIN(" geographic_zones gz ON gz.id = d.district_id");
//        JOIN(" vaccine_facility_targets vt ON tr.facility_id =  vt.facilityid");
        writeRegionPredicates(filter);
        GROUP_BY(" d.region_name, d.district_name , to_date(tr.period_name, 'Mon YYYY')");
        ORDER_BY("        d.region_name, d.district_name ,  to_date(tr.period_name, 'Mon YYYY')");
        query = SQL();
        return query;
    }

    private static void writeDistrictPredicates(PerformanceByDropoutRateParam param) {
        WHERE(" tr.programid = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )");
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate("tr.period_start_date"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate("tr.period_end_date"));

//    discuss why product id is not part of the view
//    WHERE(PerformanceByDropOutRateHelper.isFilteredProductId("tr.product_id"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }

    public String getTredofMinMaxColdRangeRegionQuery(Map params) {
        String query = "";
        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        BEGIN();
        SELECT(" d.region_name");
        SELECT("    to_date(tr.period_name, 'Mon YYYY') period_name");
        SELECT(" sum(gz.catchmentpopulation ) targetpopulation");
        SELECT("  sum(  tr.mintemp) mintemp");
        SELECT("  sum(  tr.maxtemp) maxtemp");
        SELECT(" sum(   tr.minepisodetemp) minepisodetemp");
        SELECT("   sum( tr.maxepisodetemp)  maxepisodetemp");
        FROM(" vw_vaccine_cold_chain tr");
        JOIN(" vw_districts d ON tr.geographic_zone_id = d.district_id");
        JOIN(" geographic_zones gz ON gz.id = d.district_id");
//        JOIN(" vaccine_facility_targets vt ON tr.facility_id =  vt.facilityid");
        writeRegionPredicates(filter);
        GROUP_BY(" d.region_name, to_date(tr.period_name, 'Mon YYYY')");
        ORDER_BY("         d.region_name,  to_date(tr.period_name, 'Mon YYYY')");
        query = SQL();
        return query;
    }

    private static void writeRegionPredicates(PerformanceByDropoutRateParam param) {
        WHERE(" tr.programid = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )");
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate("tr.period_start_date"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate("tr.period_end_date"));

//    discuss why product id is not part of the view
//    WHERE(PerformanceByDropOutRateHelper.isFilteredProductId("tr.product_id"));
        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }

}
