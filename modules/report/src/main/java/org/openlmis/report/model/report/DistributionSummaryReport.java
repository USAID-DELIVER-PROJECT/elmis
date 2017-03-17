package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ResultRow;

import java.util.List;

/**
 * Created by hassan on 2/3/17.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionSummaryReport implements ResultRow {

    List<DistributionSummaryReportFields> summaryReportFieldsList;

}

