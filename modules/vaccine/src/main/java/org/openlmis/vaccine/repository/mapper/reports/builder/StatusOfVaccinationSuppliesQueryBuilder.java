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

package org.openlmis.vaccine.repository.mapper.reports.builder;

import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.builder.helpers.PerformanceByDropOutRateHelper;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class StatusOfVaccinationSuppliesQueryBuilder {
    public String getStatusOfVaccineSupplyForFacility(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        String query = "with temp as (\n" +
                "select " +
                "d.region_name ," +
                "d.district_name," +
                "s.facility_name,\n" +
                "s.period_name,\n" +
                "extract(month from s.period_start_date) period_month, \n" +
                "extract(year from s.period_start_date) period_year,\n" +
                "sum(COALESCE(s.quantity_received,0)) received,\n" +
                "sum(COALESCE(s.quantity_issued,0)) issued,\n" +
                "sum(COALESCE(s.closing_balance,0)) onhand,\n" +
                "sum(COALESCE(s.quantity_discarded_unopened,0) + COALESCE(s.quantity_discarded_opened,0) + COALESCE(s.quantity_wasted_other,0)) wasted,\n" +
                "sum((COALESCE(s.opening_balanace,0) + COALESCE(s.quantity_received,0) - COALESCE(s.quantity_issued,0) - COALESCE(s.quantity_discarded_unopened,0) + COALESCE(s.quantity_discarded_opened,0) + COALESCE(s.quantity_wasted_other,0)) - COALESCE(s.closing_balance,0) ) used" +
                " , \n" +
                "sum(COALESCE(s.vaccinated,0)) vaccinated\n" +

                "from vw_vaccine_stock_status s\n" +
                "join vw_districts d on s.geographic_zone_id = d.district_id\n" +
                writePredicates(filter) +
                "group by 1,2,3,4,5,6)\n" +
                "select \n" +
                "t.region_name," +
                "t.district_name," +
                " t.facility_name," +
                "t.period_name,\n" +
                "t.period_month,\n" +
                "t.period_year,\n" +
                "t.received,\n" +
                "t.issued,\n" +
                "t.onhand,\n" +
                "t.vaccinated administered," +
                "case when t.wasted < 0 then 0 else t.wasted end wasted,\n" +
                "case when t.used < 0 then 0 else t.used end used\n" +
                "from temp t\n" +
                " order by 1,2,3,6,5" ;

        return query;
    }
    public String getStatusOfVaccineSupplyForDistrict(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        String query = "with temp as (\n" +
                "select " +
                "d.region_name ," +
                "d.district_name," +
                "s.period_name,\n" +
                "extract(month from s.period_start_date) period_month, \n" +
                "extract(year from s.period_start_date) period_year,\n" +
                "sum(COALESCE(s.quantity_received,0)) received,\n" +
                "sum(COALESCE(s.quantity_issued,0)) issued,\n" +
                "sum(COALESCE(s.closing_balance,0)) onhand,\n" +
                "sum(COALESCE(s.quantity_discarded_unopened,0) + COALESCE(s.quantity_discarded_opened,0) + COALESCE(s.quantity_wasted_other,0)) wasted,\n" +
                "sum((COALESCE(s.opening_balanace,0) + COALESCE(s.quantity_received,0) - COALESCE(s.quantity_issued,0) - COALESCE(s.quantity_discarded_unopened,0) + COALESCE(s.quantity_discarded_opened,0) + COALESCE(s.quantity_wasted_other,0)) - COALESCE(s.closing_balance,0) ) used\n" +
                ", sum(COALESCE(s.vaccinated,0)) vaccinated " +
                "from vw_vaccine_stock_status s\n" +
                "join vw_districts d on s.geographic_zone_id = d.district_id\n" +
                writePredicates(filter) +
                "group by 1,2,3,4,5)\n" +
                "select \n" +
                "t.region_name," +
                "t.district_name," +
                "t.period_name,\n" +
                "t.period_month,\n" +
                "t.period_year,\n" +
                "t.received,\n" +
                "t.issued,\n" +
                "t.onhand,\n" +
                "t.vaccinated administered," +
                "case when t.wasted < 0 then 0 else t.wasted end wasted,\n" +
                "case when t.used < 0 then 0 else t.used end used\n" +
                "from temp t\n" +
                " order by 1,2,5,4" ;

        return query;
    }
    public String getStatusOfVaccineSupplyForRegion(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        String query = "with temp as (\n" +
                "select " +
                "d.region_name ," +
                "s.period_name," +
                "extract(month from s.period_start_date) period_month, " +
                "extract(year from s.period_start_date) period_year,\n" +
                "sum(COALESCE(s.quantity_received,0)) received,\n" +
                "sum(COALESCE(s.quantity_issued,0)) issued,\n" +
                "sum(COALESCE(s.closing_balance,0)) onhand,\n" +
                "sum(COALESCE(s.quantity_discarded_unopened,0) + COALESCE(s.quantity_discarded_opened,0) + COALESCE(s.quantity_wasted_other,0)) wasted,\n" +
                "sum((COALESCE(s.opening_balanace,0) + COALESCE(s.quantity_received,0) - COALESCE(s.quantity_issued,0) - COALESCE(s.quantity_discarded_unopened,0) + COALESCE(s.quantity_discarded_opened,0) + COALESCE(s.quantity_wasted_other,0)) - COALESCE(s.closing_balance,0) ) used\n" +
                ", sum(COALESCE(s.vaccinated,0)) vaccinated " +
                "from vw_vaccine_stock_status s\n" +
                "join vw_districts d on s.geographic_zone_id = d.district_id\n" +
                writePredicates(filter) +
                "group by 1,2,3,4)\n" +
                "select \n" +
                "t.region_name," +
                "t.period_name,\n" +
                "t.period_month,\n" +
                "t.period_year,\n" +
                "t.received,\n" +
                "t.issued,\n" +
                "t.onhand,\n" +
                "t.vaccinated administered," +
                "case when t.wasted < 0 then 0 else t.wasted end wasted,\n" +
                "case when t.used < 0 then 0 else t.used end used\n" +
                "from temp t\n" +
                "order by 1,4,3" ;

        return query;
    }

    private static String writePredicates(PerformanceByDropoutRateParam param) {
        String predicate ;

        predicate = "where  s.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )";
        predicate+=" and ";
        predicate+=PerformanceByDropOutRateHelper.isFilteredPeriodStartDate("s.period_start_date");
        predicate+=" and ";
        predicate+=PerformanceByDropOutRateHelper.isFilteredPeriodEndDate("s.period_end_date");
        predicate+=" and ";
        predicate+= PerformanceByDropOutRateHelper.isFilteredProductId("s.product_id");
        if (param.getFacility_id() != null && param.getFacility_id() != 0l) {

            predicate+=" and ";
            predicate+=PerformanceByDropOutRateHelper.isFilteredFacilityId("s.facility_id");
        }
        predicate+=" and ";
        predicate+=PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id");
        return predicate;


    }
    ////////
    public String getPopulationForFacility(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        String query = "select \n" +
                "d.region_name,\n" +
                "d.district_name,\n" +
                "s.facility_name||'_'||s.period_name  facility_name ,\n" +
                "s.period_name,\n" +
                "extract(month from s.period_start_date) period_month, \n" +
                "extract(year from s.period_start_date) period_year,\n" +
                "sum(s.within_outside_total) administered,\n" +
                "sum(denominator) targetpopulation\n" +
                "from vw_vaccine_coverage s\n" +
                "join vw_districts d on s.geographic_zone_id = d.district_id\n" +
                writePredicates(filter) +
                "group by 1,2,3,4,5,6\n"  ;

        return query;
    }
    public String getPopulationForDistrict(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        String query = "select \n" +
                "d.region_name,\n" +
                "d.district_name||'_'||s.period_name district_name,\n" +
                "s.period_name,\n" +
                "extract(month from s.period_start_date) period_month, \n" +
                "extract(year from s.period_start_date) period_year,\n" +
                "sum(s.within_outside_total) administered,\n" +
                "sum(denominator) targetpopulation\n" +
                "from vw_vaccine_coverage s\n" +
                "join vw_districts d on s.geographic_zone_id = d.district_id\n" +
                writePredicates(filter) +
                "group by 1,2,3,4,5\n"  ;

        return query;
    }
    public String getPopulationForRegion(Map params) {

        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get("filterCriteria");
        String query = "select \n" +
                "d.region_name||'_'||s.period_name  region_name,\n" +
                "s.period_name,\n" +
                "extract(month from s.period_start_date) period_month, \n" +
                "extract(year from s.period_start_date) period_year,\n" +
                "sum(s.within_outside_total) administered,\n" +
                "sum(denominator) targetpopulation\n" +
                "from vw_vaccine_coverage s\n" +
                "join vw_districts d on s.geographic_zone_id = d.district_id\n" +
                writePredicates(filter) +
                "group by 1,2,3,4\n"  ;

        return query;
    }
}
