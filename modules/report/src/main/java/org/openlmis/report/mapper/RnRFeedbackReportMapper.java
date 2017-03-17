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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.report.RnRFeedbackReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnRFeedbackReportMapper {

  @Select("SELECT " +
      "  li.rnrid, " +
      "  li.productCode, " +
      "  li.product, " +
      "  li.dispensingUnit as unit, " +
      "  li.beginningbalance          AS beginningbalance, " +
      "  li.quantityreceived          AS quantityreceived, " +
      "  li.quantitydispensed         AS quantitydispensed, " +
      "  li.stockinhand               AS stockinhand, " +
      "  li.quantityapproved          AS quantityapproved, " +
      "  li.totallossesandadjustments AS totallossesandadjustments, " +
      "  li.newpatientcount           AS newpatientcount, " +
      "  li.stockoutdays              AS stockoutdays, " +
      "  li.normalizedconsumption     AS normalizedconsumption, " +
      "  li.amc                       AS amc, " +
      "  li.maxmonthsofstock          AS maxmonthsofstock, " +
      "  li.maxstockquantity          AS maxstockquantity, " +
      "  li.packstoship               AS packstoship, " +
      "  li.quantityrequested         AS quantityRequested, " +
      "  prl.stockinhand              AS previousStockInHand, " +
      "  sli.quantityShipped," +
      "  COALESCE(sli.substitutedproductquantityshipped,0) as substituteProductQuantityShipped," +
      "  COALESCE(sli.quantityShipped,0) + COALESCE(sli.substitutedproductquantityshipped,0) as totalQuantityShipped, " +
      "  li.packsize, " +
      "  (CASE WHEN prl.stockInHand IS NOT NULL AND prl.stockInHand != li.beginningBalance  THEN 1 ELSE 0 END) as openingBalanceError, " +
      "  CASE WHEN (li.quantityrequested != li.calculatedorderquantity) THEN 1 ELSE 0 END as quantityRequestedWasChanged, " +
      "  CASE WHEN li.stockinhand <> ( COALESCE(li.beginningbalance, 0) + COALESCE(li.quantityreceived, 0) - " +
      "                      COALESCE(li.quantitydispensed, 0) + " +
      "                      COALESCE(li.totallossesandadjustments, 0)) THEN 1 ELSE 0 END AS stockInHandError " +
      " " +
      " " +
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
      "  LEFT JOIN (SELECT sum(quantityshipped) quantityShipped, " +
      "               sum(substitutedproductquantityshipped) substitutedproductquantityshipped, " +
      "               productcode " +
      "             FROM " +
      "               shipment_line_items WHERE orderid = #{rnrid} " +
      "             GROUP BY productcode " +
      "            ) sli " +
      "      ON sli.productcode = li.productcode " +
      "WHERE " +
      "  li.rnrid = #{rnrid} and skipped = false")
  List<RnRFeedbackReport> getRnRFeedbackReport( @Param("rnrid") Long rnrId );

  @Select("SELECT id from requisitions where facilityId = #{facilityId} and programId = #{programId} and periodId = #{periodId} and emergency = false limit 1")
  Long getRnrId(@Param("programId") Long programId, @Param("facilityId") Long facilityId, @Param("periodId") Long periodId);

}
