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

package org.openlmis.ivdform.domain.reports;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsLineItem extends BaseModel {

  private Boolean skipped = false;

  private Long reportId;
  private Long productId;
  private String productCode;
  private String productName;
  private String productCategory;
  private String dosageUnit;

  private Product product;

  private Integer displayOrder;

  private Long openingBalance;
  private Boolean openingBalanceFromPreviousPeriod = false;
  private Long quantityReceived;
  private Long quantityIssued;
  private Long closingBalance;
  private Long quantityVvmAlerted;
  private Long quantityFreezed;
  private Long quantityExpired;
  private Long quantityDiscardedUnopened;
  private Long quantityDiscardedOpened;
  private Long quantityWastedOther;
  private Long daysStockedOut;

  private Long discardingReasonId;
  private String discardingReasonExplanation;

  private String remarks;

  public void copyValuesFrom(LogisticsLineItem source){
    this.openingBalance = source.getOpeningBalance();
    this.quantityReceived = source.getQuantityReceived();
    this.quantityIssued = source.getQuantityIssued();
    this.closingBalance = source.getClosingBalance();
    this.quantityVvmAlerted = source.getQuantityVvmAlerted();
    this.quantityFreezed = source.getQuantityFreezed();
    this.quantityExpired = source.getQuantityExpired();
    this.quantityDiscardedUnopened = source.getQuantityDiscardedUnopened();
    this.quantityDiscardedOpened = source.getQuantityDiscardedOpened();
    this.quantityWastedOther = source.getQuantityWastedOther();
    this.daysStockedOut = source.getDaysStockedOut();
    this.discardingReasonId  = source.getDiscardingReasonId();
    this.remarks = source.getRemarks();
  }
}
