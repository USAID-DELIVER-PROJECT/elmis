package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceCoverageReportParam extends BaseParam
        implements ReportParameter {

    Long district;
    Long doseId;
    String periodEnd;
    String periodStart;
    Long product;
}
