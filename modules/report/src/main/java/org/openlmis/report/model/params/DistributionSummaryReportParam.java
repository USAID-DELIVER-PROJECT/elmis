package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;

/**
 * Created by hassan on 2/3/17.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DistributionSummaryReportParam extends BaseParam implements ReportParameter{

    private Long facility;

    private String startDate;

    private String endDate;


}
