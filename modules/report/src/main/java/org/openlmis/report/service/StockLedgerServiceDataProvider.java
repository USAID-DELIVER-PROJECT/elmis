package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockLedgerReportMapper;
import org.openlmis.report.model.ReportData;
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


    @Override
    protected List<? extends ReportData> getResultSet(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(getParameter(filterCriteria), rowBounds);
    }

    private StockLedgerReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, StockLedgerReportParam.class);
    }

    @Override
    public List<? extends ReportData> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getParameter(filterCriteria), rowBounds);
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramPeriodGeoZone(params);
    }
}
