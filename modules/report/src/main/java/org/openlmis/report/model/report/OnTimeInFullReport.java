package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

import java.util.Date;

/**
 * Created by hassan on 1/29/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OnTimeInFullReport implements ResultRow{

    private String region;

    private String district;

    private String storeName;

    private Integer quantityRequested;

    private Integer quantityReceived;

    private Double inFullGreatRange;

    private Double inFullLessRange;

    private String onFull;

    private Date requestedDate;

    private Date receivedDate;

    private String product;

}
