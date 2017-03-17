package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ResultRow;

import java.util.List;

/**
 * Created by hassan on 2/18/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineReceivedSummaryReport implements ResultRow {

    List<DistributionSummaryReportFields> summaryReportFieldsList;

}
