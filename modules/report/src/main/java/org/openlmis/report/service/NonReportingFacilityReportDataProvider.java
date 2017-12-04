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
import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.openlmis.report.model.params.NonReportingFacilityParam;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.model.report.NonReportingFacilityDetail;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class NonReportingFacilityReportDataProvider extends ReportDataProvider {

    private static final String REPORT_FILTER_PARAM_VALUES = "REPORT_FILTER_PARAM_VALUES";
    private static final String TOTAL_NON_REPORTING = "TOTAL_NON_REPORTING";
    private static final String TOTAL_FACILITIES = "TOTAL_FACILITIES";
    private static final String REPORTING_FACILITIES = "REPORTING_FACILITIES";
    public static final String REPORTING_STATUS = "reportingStatus";
    public static final String PERIODS_FOR_CHART = "periodsForChart";

    @Autowired
    private NonReportingFacilityReportMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;

    List<NameCount> summary = new ArrayList<>();

    @Override
    public List<? extends ResultRow> getResultSet(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

        NonReportingFacilityParam nonReportingFacilityParam = getFilterParameters(filterCriteria);
        String periodListString = this.getPeriodParameter(nonReportingFacilityParam, rowBounds);
        nonReportingFacilityParam.setPeriodString(periodListString);

        if (filterCriteria.get(REPORTING_STATUS) != null) {
            List<NonReportingFacilityDetail> detail = reportMapper.getNonReportingFacilities(nonReportingFacilityParam, rowBounds, this.getUserId());
            detail.addAll(reportMapper.getReportingFacilities(nonReportingFacilityParam, rowBounds, this.getUserId()));
            return detail;
        }

        return reportMapper.getNonReportingFacilities(nonReportingFacilityParam, rowBounds, this.getUserId());
    }

    private String getPeriodParameter(NonReportingFacilityParam nonReportingFacilityParam, RowBounds rowBounds) {
        String periodListString = "";
        List<ProcessingPeriod> periodList = null;
        if (nonReportingFacilityParam.getPeriodEnd() != null && ! nonReportingFacilityParam.getPeriodEnd().isEmpty()){
            periodList = reportMapper.getReportingPeriodList(nonReportingFacilityParam);
        } else {
            return "{" + nonReportingFacilityParam.getPeriod().toString() + "}";
        }
        if (periodList != null && !periodList.isEmpty()) {
            for (ProcessingPeriod periodId : periodList) {
                if (periodListString.trim().isEmpty()) {
                    periodListString = periodId.getId().toString();
                }
                periodListString += "," + periodId.getId().toString();
            }
        }
        periodListString = "{" + periodListString + "}";
        return periodListString;
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

        NonReportingFacilityParam nonReportingFacilityParam = getFilterParameters(filterCriteria);
        String periodListString = this.getPeriodParameter(nonReportingFacilityParam, rowBounds);
        nonReportingFacilityParam.setPeriodString(periodListString);

        List<MasterReport> reportList = new ArrayList<>();
        MasterReport report = new MasterReport();

        List<NonReportingFacilityDetail> nonReportingFacilityDetails = reportMapper.getNonReportingFacilities(nonReportingFacilityParam, rowBounds, this.getUserId());
        Double nonReporting = Double.parseDouble(String.valueOf(nonReportingFacilityDetails.size()));

        List<NonReportingFacilityDetail> reportingFacilities = reportMapper.getReportingFacilities(nonReportingFacilityParam, rowBounds, this.getUserId());
        nonReportingFacilityDetails.addAll(reportingFacilities);

        report.setDetails(nonReportingFacilityDetails);

        Double totalFacilities = Double.parseDouble(String.valueOf(nonReportingFacilityDetails.size()));
        summary = new ArrayList<>();

        summary.add(new NameCount(TOTAL_FACILITIES, totalFacilities.toString()));
        summary.add(new NameCount(TOTAL_NON_REPORTING, nonReporting.toString()));
        summary.add(new NameCount(REPORTING_FACILITIES, Double.toString(totalFacilities - nonReporting)));

        report.setKeyValueSummary(new HashedMap(){{
            put(PERIODS_FOR_CHART, reportMapper.getPeriodsTicksForChart(nonReportingFacilityParam));
            put("chartData", reportMapper.getReportingStatusChartData(nonReportingFacilityParam, rowBounds, getUserId()));
        }});


        report.setSummary(summary);
        reportList.add(report);

        List<? extends ResultRow> list;
        list = reportList;
        return list;
    }

    private NonReportingFacilityParam getFilterParameters(Map params) {
        return ParameterAdaptor.parse(params, NonReportingFacilityParam.class);
    }

    @Override
    public HashMap<String, String> getExtendedHeader(Map params) {
        NonReportingFacilityParam nonReportingFacilityParam = getFilterParameters(params);

        HashMap<String, String> result = new HashMap<String, String>();

        for (NameCount nc : summary) {
            result.put(nc.getName(), nc.getCount());
        }

        result.put(REPORT_FILTER_PARAM_VALUES, filterHelper.getProgramPeriodGeoZone(params));
        return result;
    }


}
