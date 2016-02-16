package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockLedgerReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.StockLedgerReportParam;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class StockLedgerServiceDataProvider extends ReportDataProvider {

    @Autowired
    private StockLedgerReportMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;

    private StockLedgerReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, StockLedgerReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getParameter(filterCriteria), rowBounds);
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramPeriodGeoZone(params);
    }
}
