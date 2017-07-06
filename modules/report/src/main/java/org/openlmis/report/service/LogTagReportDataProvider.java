package org.openlmis.report.service;

import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.LogTagReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.LogTagParam;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
;
import java.util.List;
import java.util.Map;

/**
 * Created by hassan on 7/5/17.
 */

@Service
public class LogTagReportDataProvider extends ReportDataProvider {
@Autowired
private LogTagReportMapper reportMapper;
    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria),this.getUserId(),rowBounds);

    }

    private LogTagParam getReportFilterData(Map<String, String[]> filterCriteria) {

        LogTagParam param = new LogTagParam();
        String startDate = StringHelper.isBlank(filterCriteria, "startDate") ? null : ((String[]) filterCriteria.get("startDate"))[0];
        param.setStartDate(startDate);

        String endDate = StringHelper.isBlank(filterCriteria, "endDate") ? null : ((String[]) filterCriteria.get("endDate"))[0];
        param.setEndDate(endDate);

        return param;


    }

}
