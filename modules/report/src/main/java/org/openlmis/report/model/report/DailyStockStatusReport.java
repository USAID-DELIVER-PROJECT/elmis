package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyStockStatusReport implements ResultRow {
    private Long id;
    private String facilityCode;
    private Long facilityId;
    private Long programId;
    private Date date;
    private String source;
    private String facilityName;
    private Long productId;
    private String productName;
    private String productCode;
    private Long stockOnHand;
    private Long daysAfterFirstSubmission;
    private Long daysAfterLastSubmission;
}
