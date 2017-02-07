package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ResultRow;

/**
 * Created by hassan on 2/3/17.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionSummaryReport implements ResultRow {

    private String region;

    private String storeName;

    private Integer quantityIssued;

    private String product;

    private Integer facilityId;

    private String BCG;

    private String OPV;

    private String PCV_13;

    private String Rota;

    private String DTP;

    private String MR;

}

