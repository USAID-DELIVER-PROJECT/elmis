package org.openlmis.ivdform.dto;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.ivdform.domain.reports.ColdChainLineItem;

import java.util.List;

@Getter
@Setter
public class FacilityIvdSummary {


  public FacilityIvdSummary(String facilityCode, String programCode, Long periodId) {
    this.facilityCode = facilityCode;
    this.programCode = programCode;
    this.periodId = periodId;
  }

  private String facilityCode;

  private String programCode;

  private String status;

  private Long periodId;

  List<StockStatusSummary> products;

  List<ColdChainLineItem> equipments;

}
