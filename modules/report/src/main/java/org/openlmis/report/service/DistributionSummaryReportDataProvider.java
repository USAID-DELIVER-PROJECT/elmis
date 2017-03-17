package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.DistributionSummaryReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.CompletenessAndTimelinessReportParam;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.model.report.CompletenessAndTimelinessReport;
import org.openlmis.report.model.report.CompletenessAndTimelinessReportFields;
import org.openlmis.report.model.report.DistributionSummaryReport;
import org.openlmis.report.model.report.DistributionSummaryReportFields;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.openlmis.report.util.VIMSReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hassan on 2/3/17.
 */
@Component
@NoArgsConstructor
public class DistributionSummaryReportDataProvider extends ReportDataProvider {

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

        return reportMapper.getSummaryReport(params, this.getUserId());

    }

    public ResultRow getDistributionSummaryReportData(Map<String, String[]> filterCriteria) {

        Map<String, List<DistributionSummaryReportFields>> result = new HashMap<>();

        DistributionSummaryReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());

        DistributionSummaryReport report = new DistributionSummaryReport();
        report.setSummaryReportFieldsList(reportMapper.getSummaryReport(params,  this.getUserId()));
       // report.setSummaryReport(reportMapper.getSummaryReport(params,  this.getUserId()));
        //report.setSummaryPeriodList(VIMSReportUtil.getSummaryPeriodList(params.getPeriodStart(), params.getPeriodEnd()));
        return report;
    }
    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return  filterHelper.getReportCombinedFilterString(
                filterHelper.getGeoZoneFilterString(params),
                filterHelper.getSelectedPeriodRange(params));
    }

}
