/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.PerformanceCoverageReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.PerformanceCoverageReportParam;
import org.openlmis.report.model.report.PerformanceCoverageReport;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class PerformanceCoverageDataProvider extends ReportDataProvider {

    @Autowired
    private PerformanceCoverageReportMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;

    private PerformanceCoverageReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, PerformanceCoverageReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {

        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

        PerformanceCoverageReportParam params = getParameter(filterCriteria);

        PerformanceCoverageReport performanceCoverageReport = new PerformanceCoverageReport();

        performanceCoverageReport.setDistrictReport(reportMapper.getDistrictReport(params, sortCriteria, rowBounds, this.getUserId()));
        performanceCoverageReport.setDistrictReportSummary(reportMapper.getDistrictReportSummary(params, sortCriteria, rowBounds, this.getUserId()));
        if(params.getDistrict() == 0) {
            performanceCoverageReport.setRegionReport(reportMapper.getRegionReport(params, sortCriteria, rowBounds, this.getUserId()));
            performanceCoverageReport.setRegionReportSummary(reportMapper.getRegionReportSummary(params, sortCriteria, rowBounds, this.getUserId()));
        }

        List<PerformanceCoverageReport> list = new ArrayList<PerformanceCoverageReport>();
        list.add(performanceCoverageReport);
        return list;

    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramPeriodGeoZone(params);
    }
}
