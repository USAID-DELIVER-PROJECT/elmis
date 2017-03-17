package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.DistributionSummaryReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.model.report.DistributionSummaryReport;
import org.openlmis.report.model.report.DistributionSummaryReportFields;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassan on 2/19/17.
 */
@Component
@NoArgsConstructor
public class VaccineReceivedReportDataProvider extends ReportDataProvider {

    @Autowired
    private DistributionSummaryReportMapper reportMapper;


    @Autowired
    private SelectedFilterHelper filterHelper;


    private DistributionSummaryReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, DistributionSummaryReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {

        DistributionSummaryReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());

        return reportMapper.getReceivedSummaryReport(params, this.getUserId());

    }

    public ResultRow getVaccineReceivedSummaryReportData(Map<String, String[]> filterCriteria) {

        Map<String, List<DistributionSummaryReportFields>> result = new HashMap<>();

        DistributionSummaryReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());

        DistributionSummaryReport report = new DistributionSummaryReport();
        report.setSummaryReportFieldsList(reportMapper.getReceivedSummaryReport(params,  this.getUserId()));
         return report;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return  filterHelper.getReportCombinedFilterString(
                filterHelper.getGeoZoneFilterString(params),
                filterHelper.getSelectedPeriodRange(params));
    }

}
