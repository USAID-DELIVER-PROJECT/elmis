package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ResultRow;

import java.util.Date;

/**
 * Created by hassan on 2/17/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionSummaryReportFields implements ResultRow{

    private String region;

    private String facilityName;

    private String district;

    private Integer quantityIssued;

    private String product;

    private Long period;

    private Integer facilityId;

    private Long productId;

    private Long districtId;

    private Date receivedDate;

    private Integer quantityReceived;
}
