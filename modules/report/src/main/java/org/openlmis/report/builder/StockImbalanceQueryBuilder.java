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

import org.openlmis.report.model.params.StockImbalanceReportParam;
import org.openlmis.report.model.report.StockImbalanceReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class StockImbalanceQueryBuilder {
    public static String getQuery(Map params) {


        StockImbalanceReportParam filter = (StockImbalanceReportParam) params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("distinct supplyingFacility, facilityTypeName facilityType,  facility, facilityCode, d.district_name districtName, d.region_name as regionName, d.zone_name zoneName, product, productCode,  stockInHand as physicalCount,  amc,  mos months,  required, ordered as orderQuantity, " +
                "status");
        FROM("  vw_stock_status " +
                        " join facilities f on f.id = facility_id " +
                        " join vw_districts d on d.district_id = f.geographicZoneId " +
                        "join facility_types ft on f.typeid=ft.id "
        );
        WHERE("status in ('" + filter.getStatus().replaceAll(",", "','") + "')");
        WHERE(rnrStatusFilteredBy("req_status", filter.getAcceptedRnrStatuses()));
        WHERE("(amc != 0 or stockInHand != 0 or reported_figures > 0)");
        WHERE(periodIsFilteredBy("periodId"));
        WHERE(programIsFilteredBy("vw_stock_status.programId"));
        WHERE(userHasPermissionOnFacilityBy("facility_id"));
        if (filter.getFacilityType() != 0) {
            WHERE(facilityTypeIsFilteredBy("vw_stock_status.facilityTypeId"));
        }

        if (filter.getFacility() != 0) {
            WHERE(facilityIsFilteredBy("facility_id"));
        }
        if (filter.getProductCategory() != 0) {
            WHERE(productCategoryIsFilteredBy("vw_stock_status.categoryId"));
        }

        if (multiProductFilterBy(filter.getProducts(), "vw_stock_status.productId", "indicator_product") != null) {
            WHERE(multiProductFilterBy(filter.getProducts(), "vw_stock_status.productId", "indicator_product"));
        }

        if (filter.getZone() != 0) {
            WHERE(geoZoneIsFilteredBy("d"));
        }
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, StockImbalanceReport.class, "supplyingFacility asc, facility asc, product asc"));
        return SQL() + " limit 20000";
    }

    public static String getReportQuery(Map params) {
        StockImbalanceReportParam filter = (StockImbalanceReportParam) params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        String reportType = filter.getReportType().replaceAll(",", "','").replaceAll("EM", "t").replaceAll("RE", "f");
        String sql = "";
        String facilityParameterType = filter.getFacilityType() != 0 ? " AND f.typeid=  '" + filter.getFacilityType() + "'::INT\n" : " ";
        sql = "SELECT  \n" +
                "supplyingFacility, \n" +
                "facilityTypeName facilityType,  \n" +
                "facility, \n" +
                "facilityCode, \n" +
                "a.district_name districtName,\n" +
                "a.region_name as regionName, \n" +
                "a.zone_name zoneName, \n" +
                "product, productCode,  \n" +
                "a.stockInHand as physicalCount,\n" +
                "a.amc,  \n" +
                "a.processing_period_name as period, \n"+
                "a.mos months,  \n" +
                "a.required, \n" +
                "a.ordered as orderQuantity, \n" +
                "a.status\n" +
                "\n" +
                " FROM (\n" +
                "SELECT  gz.region_name as supplyingfacility, gz.region_name, gz.district_name, gz.zone_name, " +
                "f.code as facilitycode,\n" +
                "li.productcode,   f.name as facility,   li.product as product,   ft.name facilitytypename,\n" +
                "gz.district_name as location,   pp.name as processing_period_name,  li.stockinhand,\n" +
                "li.stockoutdays stockoutdays,    to_char(pp.startdate, 'Mon') asmonth, \n" +
                "extract(year from pp.startdate) as year,    pg.code as program,\n" +
                "CASE \n" +
                "   -- stocked out when stockinhand\n" +
                "   WHEN li.stockinhand = 0 THEN 'SO'::text\n" +
                "ELSE\n" +
                "    CASE WHEN li.amc > 0 AND li.stockinhand > 0 THEN \n" +
                "     CASE\n" +
                "      WHEN round((li.stockinhand::decimal / li.amc)::numeric, 2) < fap.minmonthsofstock THEN 'US'::text\n" +
                "      WHEN round((li.stockinhand::decimal / li.amc)::numeric, 2) >= fap.minmonthsofstock::numeric " +
                " AND round((li.stockinhand::decimal / li.amc)::numeric, 2) <= fap.maxmonthsofstock::numeric THEN 'SP'::text\n" +
                "      WHEN round((li.stockinhand::decimal / li.amc)::numeric, 2) > fap.maxmonthsofstock THEN 'OS'::text  \n" +
                "     END             \n" +
                "    ELSE 'UK'::text END\n" +
                "END AS status,\n" +
                "CASE\n" +
                "    WHEN COALESCE(li.amc, 0) = 0 THEN 0::numeric\n" +
                "    ELSE trunc(round((li.stockinhand::decimal / li.amc)::numeric, 2),1)::numeric \n" +
                "END AS mos,\n" +
                "li.amc,\n" +
                "COALESCE(\n" +
                "        CASE\n" +
                "            WHEN (COALESCE(li.amc, 0) * ft.nominalmaxmonth - li.stockinhand) < 0 THEN 0\n" +
                "            ELSE COALESCE(li.amc, 0) * ft.nominalmaxmonth - li.stockinhand\n" +
                "        END, 0) AS required,\n" +
                " li.quantityapproved AS ordered\n" +
                "\n" +
                "FROM  processing_periods pp  \n" +
                "  JOIN requisitions r ON pp. ID = r.periodid  \n" +
                "  JOIN requisition_line_items li ON li.rnrid = r. ID  " +
                "  JOIN facilities f on f.id = r.facilityId  \n" +
                "  JOIN facility_types ft on ft.id = f.typeid  JOIN products p on p.code = li.productcode \n" +
                "  JOIN vw_districts gz on gz.district_id = f.geographiczoneid \n" +
                "  JOIN programs pg on pg.id = r.programid\n" +
                "  join program_products pgp on r.programid = pgp.programid and p.id = pgp.productid\n" +
                "  JOIN facility_approved_products fap on ft.id = fap.facilitytypeid and fap.programproductid=pgp.id\n" +
                "\n" +
                "WHERE  li.skipped = false \n" +
                " AND (li.beginningbalance > 0 or li.quantityreceived > 0 or li.quantitydispensed > 0 or abs(li.totallossesandadjustments) > 0 or li.amc > 0) \n" +
                getPredicate(filter) + " )a \n" +
                " WHERE status in ('" + filter.getStatus().replaceAll(",", "','") + "')" +
                " ORDER BY supplyingFacility asc, facility asc, product asc";

        return sql;
    }

    private static String getPredicate(StockImbalanceReportParam filter) {
        String predicate = "";
        String reportType = filter.getReportType().replaceAll(",", "','").replaceAll("EM", "t").replaceAll("RE", "f");

        predicate += " AND " + rnrStatusFilteredBy("status", filter.getAcceptedRnrStatuses());
        predicate += " AND " + periodIsFilteredBy(" r.periodId ");
        predicate += " AND " + programIsFilteredBy("r.programId");
        predicate += " AND " + userHasPermissionOnFacilityBy("f.id");
        if (filter.getFacilityType() != 0) {
            predicate += " AND " + facilityTypeIsFilteredBy("f.typeid");
        }

        if (filter.getFacility() != 0) {
            predicate += " AND " + facilityIsFilteredBy("f.id");
        }
        if (filter.getProductCategory() != 0) {
            predicate += " AND " + productCategoryIsFilteredBy("pgp.productcategoryid ");
        }

        if (multiProductFilterBy(filter.getProducts(), "vw_stock_status.productId", "indicator_product") != null) {
            predicate += " AND " + multiProductFilterBy(filter.getProducts(), "p.id", "indicator_product");
        }

        if (filter.getZone() != 0) {
            predicate += " AND " + geoZoneIsFilteredBy("gz");
        }
        String queryFilter = " AND (li.beginningbalance > 0 or li.quantityreceived > 0 or li.quantitydispensed > 0 or abs(li.totallossesandadjustments) > 0 or li.amc > 0) \n" +
                predicate +

                " and r.emergency in ('" + reportType + "')\n" +
                " AND li.stockinhand IS NOT NULL AND li.skipped = false";
        return queryFilter;

    }
}
