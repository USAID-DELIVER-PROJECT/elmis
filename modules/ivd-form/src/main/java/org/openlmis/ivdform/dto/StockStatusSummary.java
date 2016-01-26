package org.openlmis.ivdform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockStatusSummary {

  private String productCode;

  private Long productId;

  private String status;

  private Long periodId;

  private Long stockStatus;

  private Long daysOutOfStock;

  private Long amc;

}
