package org.openlmis.report.service;


import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.VaccineStockStatusMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.VaccineStockStatusParam;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class VaccineStockStatusReportDataProvider extends ReportDataProvider{
    @Autowired
    private VaccineStockStatusMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;


    private VaccineStockStatusParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, VaccineStockStatusParam.class);
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



