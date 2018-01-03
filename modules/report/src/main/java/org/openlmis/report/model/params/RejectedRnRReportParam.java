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
public class RejectedRnRReportParam  extends BaseParam implements ReportParameter {

private Long program;
private String status;
private Long zone;

}
