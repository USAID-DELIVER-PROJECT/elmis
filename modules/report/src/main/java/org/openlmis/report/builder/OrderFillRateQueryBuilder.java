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

import org.openlmis.report.model.params.OrderFillRateReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class OrderFillRateQueryBuilder {

  public static String getQuery(Map params) {
    OrderFillRateReportParam queryParam = (OrderFillRateReportParam) params.get("filterCriteria");
    return getQueryStringV2(queryParam, queryParam.getUserId());
  }

  private static void writePredicates(OrderFillRateReportParam param) {

    WHERE(programIsFilteredBy("programid"));
    WHERE(periodIsFilteredBy("periodid"));

    if (param.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("gz"));
    }

    if(param.getFacilityType() != 0){
      WHERE(facilityTypeIsFilteredBy("facilityTypeId"));
    }

    if(param.getFacility() != 0){
      WHERE(facilityIsFilteredBy("facilityId"));
    }

    if(param.getProductCategory() != 0){
      WHERE(productCategoryIsFilteredBy("productCategoryId"));
    }

    if (multiProductFilterBy(param.getProducts(), "productId", "tracer") != null) {
      WHERE(multiProductFilterBy(param.getProducts(), "productId", "tracer"));
    }
  }

  private static String getQueryStringV2(OrderFillRateReportParam param, Long userId) {
    BEGIN();
    SELECT("f.name as facility,\n" +
            "  li.productCode,\n" +
            "  li.product,\n" +
            "  li.quantityrequested                                                                  AS order,\n" +
            "  li.quantityapproved                                                                   AS approved,\n" +
            "  sli.quantityshipped                                                                   AS receipts,\n" +
            "  sli.substitutedproductcode,\n" +
            "  sli.substitutedproductname,\n" +
            "  sli.substitutedproductquantityshipped,\n" +
            "  CASE WHEN COALESCE(li.quantityapproved, 0 :: NUMERIC) = 0 :: NUMERIC  THEN 0 :: NUMERIC\n" +
            "   ELSE round((COALESCE(sli.quantityshipped, 0)::numeric / COALESCE(li.quantityapproved, 0)) * 100, 2)  END  AS item_fill_rate ");
    FROM("requisitions r JOIN\n" +
            "  requisition_line_items li ON r.id = li.rnrid\n" +
            "  JOIN products p on li.productcode = p.code\n" +
            "  JOIN facilities f on f.id = r.facilityid ");

    if(param.getProductCategory() != 0)// gives a overhead on a query performance. Unless category is selected don't do the join
    JOIN(" (SELECT * FROM program_products where programid =  #{filterCriteria.program}) pp ON p.id = pp.productid");

    LEFT_OUTER_JOIN(  " ( SELECT DISTINCT  productcode, quantityshipped, substitutedproductcode,  substitutedproductname, substitutedproductquantityshipped\n" +
            "              FROM\n" +
            "                (\n" +
            "                  SELECT NULL as orderid, NULL AS quantityshipped, productcode, substitutedproductcode, substitutedproductname, \n" +
            "                    substitutedproductquantityshipped\n" +
            "                  FROM shipment_line_items li\n" +
            "                  WHERE li.substitutedproductcode IS NOT NULL AND orderid = #{filterCriteria.rnrId}\n" +
            "                    UNION\n" +
            "                  SELECT orderid, sum(quantityshipped) quantityshipped, productcode, NULL substitutedproductcode, NULL AS substitutedproductname, \n" +
            "                    NULL AS substitutedproductquantityshipped\n" +
            "                  FROM shipment_line_items\n" +
            "                  WHERE orderid = #{filterCriteria.rnrId}\n" +
            "                  GROUP BY orderid, productcode\n" +
            "                ) AS substitutes\n" +
            "              ORDER BY productcode, substitutedproductcode DESC\n" +
            "            ) sli on sli.productcode = li.productcode");
        WHERE(" li.rnrid = #{filterCriteria.rnrId}");
        WHERE(" status = 'RELEASED'");
        WHERE(" li.quantityapproved > 0");

        //facility type is not necessary at this point since the report is facility based report
        //if(param.getFacilityType() != 0)
         // WHERE(facilityTypeIsFilteredBy("f.type"));

        //We alredy have the rnrid at this point so no need to filter by facility
       // if(param.getFacility() != 0)
       //   WHERE(facilityIsFilteredBy("f.id"));

        if(param.getProductCategory() != 0)
          WHERE(productCategoryIsFilteredBy("pp.productcategoryid"));
        if (multiProductFilterBy(param.getProducts(), "p.id", "tracer") != null)
          WHERE(multiProductFilterBy(param.getProducts(), "p.id", "tracer"));
      ORDER_BY(" productCode ");
    String query=SQL();
    return query;
  }

  public static String getFillRateReportRequisitionStatus(Map params){
    BEGIN();
    SELECT("distinct status\n");
       FROM(     "requisitions r");
      WHERE("  r.id = #{filterCriteria.rnrId}");


    String query=SQL();
    return query.concat(" LIMIT 1");
  }

  @Deprecated
  private static String getQueryString(OrderFillRateReportParam param, Long userId) {
    BEGIN();
    SELECT_DISTINCT("facilityname facility,quantityapproved as Approved,quantityreceived receipts ,productcode, product, " +
        " CASE WHEN COALESCE(quantityapproved, 0::numeric) = 0::numeric THEN 0::numeric\n" +
        "    ELSE COALESCE(quantityreceived,0 )/ COALESCE(quantityapproved,0) * 100::numeric\n" +
        "                                     END AS item_fill_rate ");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = vw_order_fill_rate.zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program} )");
    WHERE(" status in ('RELEASED') and totalproductsapproved > 0 ");
    writePredicates(param);
    GROUP_BY("product, approved, " +
        "  quantityreceived,  productcode, " +
        "  facilityname ");
    ORDER_BY("facilityname");
    String query=SQL();
    return query;
  }

  public static String getTotalProductsReceived(Map param) {
    OrderFillRateReportParam queryParam = (OrderFillRateReportParam) param.get("filterCriteria");
    BEGIN();
    SELECT("count(totalproductsreceived) quantityreceived");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("totalproductsreceived>0 and totalproductsapproved >0  and status in ('RELEASED') and periodId = #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId = #{filterCriteria.facility}");
    writePredicates(queryParam);
    GROUP_BY("totalproductsreceived");
    return SQL();
  }


  public static String getTotalProductsOrdered(Map params) {
    OrderFillRateReportParam queryParam = (OrderFillRateReportParam) params.get("filterCriteria");

    BEGIN();
    SELECT("count(totalproductsapproved) quantityapproved");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("totalproductsapproved > 0  and status in ('RELEASED') and periodId= #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId= #{filterCriteria.facility} ");
    writePredicates(queryParam);
    return SQL();

  }


  public static String getSummaryQuery(Map params) {
    OrderFillRateReportParam queryParam = (OrderFillRateReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT("count(totalproductsreceived) quantityreceived");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("totalproductsreceived>0 and totalproductsapproved > 0 and  status in ('RELEASED') and periodId= #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId= #{filterCriteria.facility}");
    writePredicates(queryParam);
    GROUP_BY("totalproductsreceived");
    String query = SQL();
    RESET();
    BEGIN();
    SELECT("count(totalproductsapproved) quantityapproved");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("status in ('RELEASED') and totalproductsapproved > 0 and periodId= #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId= #{filterCriteria.facility}");
    writePredicates(queryParam);
    query += " UNION " + SQL();
    return query;
  }
}
