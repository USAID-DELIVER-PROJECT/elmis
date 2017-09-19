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

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.report.SupplyStatusReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyStatusReportMapper {

  @Select("SELECT " +
      "  li.rnrid, " +
      "  li.productCode, " +
      "  li.product, " +
      "  li.dispensingUnit as unit, " +
      "  li.beginningbalance          AS beginningbalance, " +
      "  li.quantityreceived          AS quantityReceived, " +
      "  li.quantitydispensed         AS quantitydispensed, " +
      "  li.stockinhand               AS stockinhand, " +
      "  COALESCE(li.stockinhand,0) / (CASE WHEN COALESCE(li.amc,1) = 0 THEN 1 ELSE  COALESCE(li.amc,1) END)  AS mos, " +
      "  li.quantityapproved          AS quantityapproved, " +
      "  li.totallossesandadjustments AS totallossesandadjustments, " +
      "  li.newpatientcount           AS newpatientcount, " +
      "  li.stockoutdays              AS stockoutdays, " +
      "  li.normalizedconsumption     AS normalizedconsumption, " +
      "  li.amc                       AS amc, " +
      "  li.maxmonthsofstock          AS maxmonthsofstock, " +
      "  li.maxstockquantity          AS maxstockquantity, " +
      "  li.packstoship               AS packstoship, " +
      "  li.quantityapproved         AS quantityRequested, " +
      "  prl.stockinhand              AS previousStockInHand, " +
      "  sli.quantityShipped," +
      "  COALESCE(sli.substitutedproductquantityshipped,0) as substituteProductQuantityShipped," +
      "  COALESCE(sli.quantityShipped,0) + COALESCE(sli.substitutedproductquantityshipped,0) as totalQuantityShipped, " +
      "  li.packsize, " +
      "  (CASE WHEN prl.stockInHand IS NOT NULL AND prl.stockInHand != li.beginningBalance  THEN 1 ELSE 0 END) as openingBalanceError, " +
      "  CASE WHEN (li.quantityrequested != li.calculatedorderquantity) THEN 1 ELSE 0 END as quantityRequestedWasChanged, " +
      "  CASE WHEN li.stockinhand <> ( COALESCE(li.beginningbalance, 0) + COALESCE(li.quantityreceived, 0) - " +
      "                      COALESCE(li.quantitydispensed, 0) + " +
      "                      COALESCE(li.totallossesandadjustments, 0)) THEN 1 ELSE 0 END AS stockInHandError, " +
      "  sli.substitutedproductcode, " +
      "  sli.substitutedproductname, " +
      "  sli.substitutedproductquantityshipped, " +
      "  sli.quantityshipped " +
      "FROM " +
      "  requisition_line_items li " +
      "  LEFT JOIN ( " +
      "              SELECT productCode, stockinhand " +
      "              FROM  requisition_line_items rr " +
      "                WHERE rr.rnrid = ( SELECT max(previousrnrid) " +
      "                                   FROM vw_previous_rnr WHERE " +
      "                                     id = #{rnrid}) " +
      "            )  as prl " +
      "       ON li.productcode = prl.productcode " +
      "  LEFT JOIN ( " +
      "              SELECT DISTINCT " +
      "                productcode, " +
      "                quantityshipped, " +
      "                substitutedproductcode, " +
      "                substitutedproductname, " +
      "                substitutedproductquantityshipped " +
      "              FROM " +
      "                ( " +
      "                  SELECT " +
      "                    NULL    orderid, " +
      "                    NULL AS quantityshipped, " +
      "                    productcode, " +
      "                    substitutedproductcode, " +
      "                    substitutedproductname, " +
      "                    substitutedproductquantityshipped " +
      "                  FROM shipment_line_items li " +
      "                  WHERE li.substitutedproductcode IS NOT NULL AND orderid = #{rnrid} " +
      "                  UNION " +
      "                  SELECT " +
      "                    orderid, " +
      "                    sum(quantityshipped) quantityshipped, " +
      "                    productcode, " +
      "                    NULL                 substitutedproductcode, " +
      "                    NULL AS              substitutedproductname, " +
      "                    NULL AS              substitutedproductquantityshipped " +
      "                  FROM shipment_line_items " +
      "                  WHERE orderid = #{rnrid} " +
      "                  GROUP BY orderid, productcode " +
      "                ) AS tr " +
      "              ORDER BY productcode, substitutedproductcode DESC " +
      "        ) sli on sli.productcode = li.productcode" +
      " WHERE " +
      "  li.rnrid = #{rnrid} and skipped = false order by productCode")
  List<SupplyStatusReport> getSupplyStatus(Long rnrId);

}
