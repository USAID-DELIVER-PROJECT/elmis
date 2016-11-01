package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassificationVaccineUtilizationPerformanceReportParam extends BaseParam
        implements ReportParameter {

    Long district;
    String periodEnd;
    String periodStart;
    Long product;
}
