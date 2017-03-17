package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;

/**
 * Created by hassan on 2/18/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineReceivedSummaryReportParam extends BaseParam implements ReportParameter {

    Long district;
    String periodEnd;
    String periodStart;

}
