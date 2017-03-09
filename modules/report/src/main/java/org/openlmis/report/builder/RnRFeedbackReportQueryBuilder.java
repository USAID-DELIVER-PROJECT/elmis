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

import org.openlmis.report.model.params.RnRFeedbackReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class RnRFeedbackReportQueryBuilder {
  public static String SelectFilteredSortedPagedRecords(Map params) {


    RnRFeedbackReportParam filter = (RnRFeedbackReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT("facility_code AS facilityCode, " +
        "facility_name AS facility, " +
        "productcode as productCode, " +
        "product, " +
        "productcode as productCodeMain, " +
        "product as productMain, " +
        "dispensingunit AS unit, " +
        "beginningBalance as beginningBalance, " +
        "quantityReceived, " +
        "quantityDispensed, " +
        "totalLossesAndAdjustments AS adjustments, " +
        "stockInHand, " +
        "previousStockInHand, " +
        "amc, " +
        "amc * nominalEop AS newEOP, " +
        "maxStockQuantity AS maximumStock, " +
        "quantityRequested," +
        "calculatedOrderQuantity," +
        "quantityApproved , " +
        "quantityShipped, " +
        "totalQuantityShipped, " +
        "0 AS emergencyOrder, " +
        "0 AS productIndex, " +
        "openingBalanceError, " +
        "quantityRequestedWasChanged, " +
        "stockInHandError");
    FROM("vw_rnr_feedback ");
    WHERE("emergency = false ");
    WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    writePredicates(filter);
    return SQL();

  }

  private static void writePredicates(RnRFeedbackReportParam filter) {
    WHERE("req_status = 'RELEASED'");
    WHERE(programIsFilteredBy("program_id"));
    WHERE(periodIsFilteredBy("processing_periods_id"));

    if (filter.getFacility() != 0) {
      WHERE(facilityIsFilteredBy("facility_id"));
    }

    if (filter.getProduct() > 0) {
      WHERE(productFilteredBy("product_id"));
    }


  }
}
