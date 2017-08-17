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

package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RnRFeedbackReport implements ResultRow {
    private String product;
    private String productCode;
    private String facility;
    private String facilityCode;
    private String unit;
    private Long beginningBalance;
    private Long quantityReceived;
    private Long quantityDispensed;
    private Long totalLossesAndAdjustments;
    private Long stockInHand;
    private Long previousStockInHand;
    private Long amc;
    private Long newEOP;
    private Long calculatedOrderQuantity;
    private Long quantityRequested;
    private Long quantityApproved;
    private Long quantitySupplied;
    private Double maximumStock;
    private Double emergencyOrder;
    private Long openingBalanceError;
    private Long quantityRequestedWasChanged;
    private Long stockInHandError;
    private Long substituteProductQuantityShipped;
    private Long totalQuantityShipped;
    private Long productIndex;
    private String substitutedProductCode;
    private String substitutedProductName;
    private Double substitutedProductQuantityShipped;
    private Double quantityShipped;
}
