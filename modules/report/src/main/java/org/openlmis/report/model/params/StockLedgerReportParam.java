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
public class StockLedgerReportParam extends BaseParam implements ReportParameter {

    private String product;
    private String facility;
    private Long program;
    private Date ledgerDate;
    private String startDate;
    private String endDate;
}
