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

public class PerformanceByDropoutRateQueryBuilder {
    public  static final String REGION_NAME="d.region_name";
    public  static final String DISTRICT_NAME=" d.district_name";
    public  static final String FACILITY_CRITERIA="filterCriteria";
    public  static final String PERIOD_NAME="to_date(to_char(i.period_start_date, 'Mon YYYY'), 'Mon YYYY')    period_name";
    public  static final String START_DATE="i.period_start_date";
    public  static final String END_DATE="i.period_end_date";
    public  static final String PRODUCT_ID="i.product_id";
    public  static final String VACCINE_COVERAGE_VIEW="vw_vaccine_dropout i";
    public  static final String DISTRICTS_VIEW=" vw_districts d ON i.geographic_zone_id = d.district_id";
    public  static final String VACCINE_REPORTS_VIEW="vaccine_reports vr ON i.report_id = vr.ID";
    public  static final String PROGRAM_PRODUCTS="program_products pp  ON pp.programid = vr.programid   AND pp.productid = i.product_id";
    public  static final String PRODUCT_CATEGORIES=" product_categories pg  ON pp.productcategoryid = pg.ID";
    public static final  String PROGRAM_FILTER_ENABLED="i.program_id = (SELECT id FROM programs p WHERE p.enableivdform = TRUE )";

    public String getByFacilityQuery(Map params) {
        String query ;
        PerformanceByDropoutRateParam filter = (PerformanceByDropoutRateParam) params.get(FACILITY_CRITERIA);
        BEGIN();
        SELECT(REGION_NAME);
        SELECT(DISTRICT_NAME);
        SELECT("    i.denominator target");
        SELECT("     i.facility_name");
        SELECT("    i.facility_id");
        SELECT(PERIOD_NAME);
        SELECT("   sum(i.bcg_1) bcg_vaccinated");
        SELECT("   sum(i.dtp_1) dtp1_vaccinated");
        SELECT("   sum(i.mr_1) mr_vaccinated");
        SELECT("   sum(i.dtp_3) dtp3_vaccinated");
        SELECT(" case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) - sum(i.mr_1)) / sum(i.bcg_1)::numeric) * 100 else 0 end bcg_mr_dropout");
        SELECT("   case when sum(i.dtp_1) > 0 then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1)::numeric) * 100 else 0 end dtp1_dtp3_dropout");
        FROM(VACCINE_COVERAGE_VIEW);
        JOIN(DISTRICTS_VIEW);
        JOIN(VACCINE_REPORTS_VIEW);
        JOIN(PROGRAM_PRODUCTS);
        JOIN(PRODUCT_CATEGORIES);
        writePredicates(filter);
        GROUP_BY("1,2,3,4,5,6,"+START_DATE);
        ORDER_BY("1,2,4," + START_DATE);
        query = SQL();
        return query;
    }

    private static void writePredicates(PerformanceByDropoutRateParam param) {
        WHERE(PROGRAM_FILTER_ENABLED);
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate(START_DATE));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate(END_DATE));
        if (param.getFacility_id() != null && param.getFacility_id()!=0L) {
            WHERE(PerformanceByDropOutRateHelper.isFilteredFacilityId("i.facility_id"));
        }
        WHERE(PerformanceByDropOutRateHelper.isFilteredProductId(PRODUCT_ID));
        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }

    public String getByDistrictQuery() {
        String query ;
        BEGIN();
        SELECT(REGION_NAME);
        SELECT(DISTRICT_NAME);
        SELECT("   sum( i.denominator) target");
        SELECT(PERIOD_NAME);
        SELECT(" sum(   i.dtp_1) dtp1_vaccinated");
        SELECT(" sum(  i.bcg_1) bcg_vaccinated");
        SELECT("  sum( i.mr_1) mr_vaccinated");
        SELECT(" sum( i.dtp_3) dtp3_vaccinated");
        SELECT("   case when sum(i.dtp_1) > 0 " +
                "then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1::numeric)) * 100 else 0 end dtp1_dtp3_dropout");
        SELECT(" case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) " +
                "- sum(i.mr_1)) / sum(i.bcg_1::numeric)) * 100 else 0 end bcg_mr_dropout");
        FROM(VACCINE_COVERAGE_VIEW);
        JOIN(DISTRICTS_VIEW);
        JOIN(VACCINE_REPORTS_VIEW);
        JOIN(PROGRAM_PRODUCTS);
        JOIN(PRODUCT_CATEGORIES);
        writePredicatesForDistrict();
        GROUP_BY("d.region_name, i.period_name, d.district_name,   i.period_start_date");
        ORDER_BY("d.region_name, d.district_name,  i.period_start_date,   i.period_start_date");


        query = SQL();
        return query;
    }
    public String getDistrict() {
        String query ;
        BEGIN();
        SELECT(REGION_NAME);
        SELECT(DISTRICT_NAME);
        FROM(VACCINE_COVERAGE_VIEW);
        JOIN(DISTRICTS_VIEW);
        JOIN(VACCINE_REPORTS_VIEW);
        JOIN(PROGRAM_PRODUCTS);
        JOIN(PRODUCT_CATEGORIES);
        writePredicatesForDistrict();
        GROUP_BY("d.region_name, d.district_name,  i.period_name,  i.period_start_date");
        ORDER_BY("d.region_name, d.district_name,  i.period_start_date,  i.period_start_date");


        query = SQL();
        return query;
    }

    private static void writePredicatesForDistrict() {
        WHERE(PROGRAM_FILTER_ENABLED);
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate(START_DATE));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate(END_DATE));

        WHERE(PerformanceByDropOutRateHelper.isFilteredProductId(PRODUCT_ID));
        WHERE(PerformanceByDropOutRateHelper.isFilteredGeographicZoneId("d.parent", "d.region_id", "d.district_id"));


    }
    public String getByRegionQuery() {
        String query ;
        BEGIN();
        SELECT(REGION_NAME);

        SELECT("   sum( i.denominator) target");

        SELECT(PERIOD_NAME);
        SELECT(" sum(  i.bcg_1) bcg_vaccinated");
        SELECT(" sum(   i.dtp_1) dtp1_vaccinated");
        SELECT("  sum( i.mr_1) mr_vaccinated");
        SELECT(" sum( i.dtp_3) dtp3_vaccinated");
        SELECT(" case when sum(i.bcg_1) > 0 then((sum(i.bcg_1) " +
                "- sum(i.mr_1)) / sum(i.bcg_1::numeric)) * 100 else 0 end bcg_mr_dropout");
        SELECT("   case when sum(i.dtp_1) > 0 " +
                "then((sum(i.dtp_1) - sum(i.dtp_3)) / sum(i.dtp_1::numeric)) * 100 else 0 end dtp1_dtp3_dropout");
        FROM(VACCINE_COVERAGE_VIEW);
        JOIN(DISTRICTS_VIEW);
        JOIN(VACCINE_REPORTS_VIEW);
        JOIN(PROGRAM_PRODUCTS);
        JOIN(PRODUCT_CATEGORIES);
        writePredicatesForRegion();
        GROUP_BY("d.region_name,  i.period_name,  i.period_start_date");
        ORDER_BY("d.region_name,    i.period_start_date,  i.period_start_date");


        query = SQL();
        return query;
    }
    private static void writePredicatesForRegion() {
        WHERE(PROGRAM_FILTER_ENABLED);
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodStartDate(START_DATE));
        WHERE(PerformanceByDropOutRateHelper.isFilteredPeriodEndDate(END_DATE));

        WHERE(PerformanceByDropOutRateHelper.isFilteredProductId(PRODUCT_ID));



    }
}
