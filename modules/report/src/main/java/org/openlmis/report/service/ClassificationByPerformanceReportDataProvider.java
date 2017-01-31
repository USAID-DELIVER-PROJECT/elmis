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
import org.openlmis.report.mapper.ClassificationVaccineUtilizationByPerformanceReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.CategorizationVaccineUtilizationPerformanceReportParam;
import org.openlmis.report.model.params.ClassificationVaccineUtilizationPerformanceReportParam;
import org.openlmis.report.model.report.CategorizationVaccineUtilizationPerformanceReport;
import org.openlmis.report.model.report.ClassificationVaccineUtilizationPerformanceReport;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class ClassificationByPerformanceReportDataProvider extends ReportDataProvider {

    @Autowired
    private ClassificationVaccineUtilizationByPerformanceReportMapper mapper;

    @Autowired
    private GeographicZoneService GeographicZoneService;

    @Autowired
    private SelectedFilterHelper filterHelper;

    private ClassificationVaccineUtilizationPerformanceReportParam getParameter(Map params) {
        return ParameterAdaptor.parse(params, ClassificationVaccineUtilizationPerformanceReportParam.class);
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria,
                                                   Map<String, String[]> sortCriteria,
                                                   int page, int pageSize) {

        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

        ClassificationVaccineUtilizationPerformanceReportParam params = getParameter(filterCriteria);

        GeographicZone zone =  GeographicZoneService.getById(params.getDistrict());

        ClassificationVaccineUtilizationPerformanceReport report =
                new ClassificationVaccineUtilizationPerformanceReport();

        if (zone != null && zone.getLevel().getCode().equals("dist")) {
            report.setFacilityReport(mapper.getFacilityReport(params, sortCriteria, rowBounds, this.getUserId()));
            report.setFacilityDistrictSummary(report.getFacilityReport());
        }
        else{
                report.setDistrictReport(mapper.getDistrictReport(params, sortCriteria, rowBounds, this.getUserId()));
                report.setFacilityDistrictSummary(report.getDistrictReport());
        }

        if(params.getDistrict() == 0) {
            report.setRegionReport(mapper.getRegionReport(params, sortCriteria, rowBounds, this.getUserId()));
        }

        List<ClassificationVaccineUtilizationPerformanceReport> list =
                new ArrayList<ClassificationVaccineUtilizationPerformanceReport>();
        list.add(report);
        return list;

    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramPeriodGeoZone(params);
    }
}
