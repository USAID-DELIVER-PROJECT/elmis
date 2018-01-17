package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.stockmanagement.domain.LotOnHand;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectedRnRReportParam  extends BaseParam implements ReportParameter {

private Long program;
private String status;
private Long zone;
public Long period;
}
