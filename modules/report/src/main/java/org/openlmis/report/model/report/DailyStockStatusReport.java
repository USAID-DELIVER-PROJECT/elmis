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
    private String status;
    private String dailyStatus;
    private String district;
    private String province;
    private String facility;
    private Long productId;
    private String product;
    private String productCode;
    private Long stockOnHand;
    private Long daysAfterFirstSubmission;
    private Long daysAfterLastSubmission;
    private Long amc;
    private Long mos;
    private Long stockinhand;
    private Date fistSubmissionDate;
    private Date lastSubmissionDate;

}
