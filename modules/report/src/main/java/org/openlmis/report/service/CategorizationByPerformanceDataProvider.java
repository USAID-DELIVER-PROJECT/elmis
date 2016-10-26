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
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.report.mapper.CategorizationByPerformanceReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.CategorizationVaccineUtilizationPerformanceReportParam;
import org.openlmis.report.model.report.CategorizationVaccineUtilizationPerformanceReport;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class CategorizationByPerformanceDataProvider extends ReportDataProvider {

    @Autowired
    private CategorizationByPerformanceReportMapper categorizationByPerformanceReportMapper;

    @Autowired
    private GeographicZoneService GeographicZoneService;

    @Autowired
    private SelectedFilterHelper filterHelper;

    private CategorizationVaccineUtilizationPerformanceReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, CategorizationVaccineUtilizationPerformanceReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {

        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);



        CategorizationVaccineUtilizationPerformanceReportParam params = getParameter(filterCriteria);

        GeographicZone zone =  GeographicZoneService.getById(params.getDistrict());

        CategorizationVaccineUtilizationPerformanceReport report = new CategorizationVaccineUtilizationPerformanceReport();

        if (zone != null && zone.getLevel().getCode().equals("dist")) {
            report.setFacilityReport(categorizationByPerformanceReportMapper.getFacilityReport(params, sortCriteria, rowBounds, this.getUserId()));
            report.setFacilityDistrictSummary(report.getFacilityReport());
        }
        else{
                report.setDistrictReport(categorizationByPerformanceReportMapper.getDistrictReport(params, sortCriteria, rowBounds, this.getUserId()));
                report.setFacilityDistrictSummary(report.getDistrictReport());
        }

        if(params.getDistrict() == 0) {
            report.setRegionReport(categorizationByPerformanceReportMapper.getRegionReport(params, sortCriteria, rowBounds, this.getUserId()));
        }

        List<CategorizationVaccineUtilizationPerformanceReport> list = new ArrayList<CategorizationVaccineUtilizationPerformanceReport>();
        list.add(report);
        return list;

    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramPeriodGeoZone(params);
    }
}
