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
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.report.mapper.PerformanceCoverageReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.PerformanceCoverageReportParam;
import org.openlmis.report.model.report.PerformanceCoverageReport;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
public class PerformanceCoverageDataProvider extends ReportDataProvider {

    @Autowired
    private PerformanceCoverageReportMapper reportMapper;

    @Autowired
    private org.openlmis.core.service.GeographicZoneService GeographicZoneService;

    @Autowired
    private SelectedFilterHelper filterHelper;

    private PerformanceCoverageReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, PerformanceCoverageReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {

        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

        PerformanceCoverageReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());

        PerformanceCoverageReport performanceCoverageReport = new PerformanceCoverageReport();

        GeographicZone zone =  GeographicZoneService.getById(params.getDistrict());

        if (zone != null && zone.getLevel().getCode().equals("dist")) {
            performanceCoverageReport.setFacilityReport(reportMapper.getFacilityReport(params, sortCriteria, rowBounds, this.getUserId()));
            performanceCoverageReport.setFacilityReportSummary(reportMapper.getFacilityReportSummary(params, sortCriteria, rowBounds, this.getUserId()));
        }
        else {
            performanceCoverageReport.setDistrictReport(reportMapper.getDistrictReport(params, sortCriteria, rowBounds, this.getUserId()));
            performanceCoverageReport.setDistrictReportSummary(reportMapper.getDistrictReportSummary(params, sortCriteria, rowBounds, this.getUserId()));
        }

        if(params.getDistrict() == 0) {
            performanceCoverageReport.setRegionReport(reportMapper.getRegionReport(params, sortCriteria, rowBounds, this.getUserId()));
            performanceCoverageReport.setRegionReportSummary(reportMapper.getRegionReportSummary(params, sortCriteria, rowBounds, this.getUserId()));
        }
        performanceCoverageReport.setDenominatorName(reportMapper.getDenominatorName(params, sortCriteria, rowBounds, this.getUserId()));
        List<PerformanceCoverageReport> list = new ArrayList<PerformanceCoverageReport>();
        list.add(performanceCoverageReport);
        return list;

    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return  filterHelper.getReportCombinedFilterString(
                filterHelper.getGeoZoneFilterString(params),
                filterHelper.getSelectedProductSummary(params.get("product")[0])+"-"+  params.get("doseId")[0],

                filterHelper.getSelectedPeriodRange(params));
    }

    public String getDenominatorName(String periodStart, String periodEnd, Long districtId, Long product, Long doseId, Long userId){
        PerformanceCoverageReportParam params= new PerformanceCoverageReportParam();
        params.setPeriodStart(periodStart);
        params.setPeriodEnd(periodEnd);
        params.setProduct(product);
        params.setDoseId(doseId);
        String denominatorName=reportMapper.getDenominatorName(params, null, null, this.getUserId());
        return  denominatorName;
    }
    public Map<String,String> getExtendedHeader(Map filterCriteria) {
        Map<String, String> parameterMap = new HashMap<>();
        PerformanceCoverageReportParam params = getParameter(filterCriteria);
        params.setUserId(this.getUserId());
        String denominatorName = reportMapper.getDenominatorName(params, null, null, this.getUserId());
        parameterMap.put("DENOMINATOR_NAME", denominatorName);
        return parameterMap;
    }

}
