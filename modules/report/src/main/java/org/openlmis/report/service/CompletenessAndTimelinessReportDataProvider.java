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
import org.openlmis.report.mapper.CompletenessAndTimelinessMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.CompletenessAndTimelinessReportParam;
import org.openlmis.report.model.report.CompletenessAndTimelinessReport;
import org.openlmis.report.model.report.CompletenessAndTimelinessReportFields;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.VIMSReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@NoArgsConstructor
public class CompletenessAndTimelinessReportDataProvider extends ReportDataProvider {

    @Autowired
    private CompletenessAndTimelinessMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;

    private CompletenessAndTimelinessReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, CompletenessAndTimelinessReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {

        CompletenessAndTimelinessReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());

        return reportMapper.getDistrictReport(params, this.getUserId());

    }

    public ResultRow getCompletenessAndTimelinessReportData(Map<String, String[]> filterCriteria) {

        Map<String, List<CompletenessAndTimelinessReportFields>> result = new HashMap<>();

        CompletenessAndTimelinessReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());

        CompletenessAndTimelinessReport report = new CompletenessAndTimelinessReport();
        report.setMainReport(reportMapper.getDistrictReport(params,  this.getUserId()));
        report.setSummaryReport(reportMapper.getSummaryReport(params,  this.getUserId()));
        report.setSummaryPeriodList(VIMSReportUtil.getSummaryPeriodList(params.getPeriodStart(), params.getPeriodEnd()));

         return report;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return  filterHelper.getReportCombinedFilterString(
                filterHelper.getGeoZoneFilterString(params),
                filterHelper.getSelectedPeriodRange(params));
    }
}
