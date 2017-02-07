package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.DistributionSummaryReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.DistributionSummaryReportParam;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReportData(getReportFilterData(filterCriteria), rowBounds);
    }

    private DistributionSummaryReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

        DistributionSummaryReportParam param = new DistributionSummaryReportParam();

        Long facility = StringHelper.isBlank(filterCriteria, "facility") ? 0L : Long.parseLong(filterCriteria.get("facility")[0]);
        param.setFacility(facility);


        String startDate = StringHelper.isBlank(filterCriteria, "startDate") ? null : ((String[]) filterCriteria.get("startDate"))[0];
        param.setStartDate(startDate);

        String endDate = StringHelper.isBlank(filterCriteria, "endDate") ? null : ((String[]) filterCriteria.get("endDate"))[0];
        param.setEndDate(endDate);

        return param;


    }

}
