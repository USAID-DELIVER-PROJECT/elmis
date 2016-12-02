package org.openlmis.report.service;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.report.vaccine.PerformanceByDisrictReport;
import org.openlmis.report.model.report.vaccine.PerformanceByDropoutColumn;
import org.openlmis.report.model.report.vaccine.PerformanceByDropoutRange;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PerformanceDropoutDataProvider extends ReportDataProvider {
    @Autowired
    private PerformanceByDropoutRateByDistrictService dropoutRateByDistrictService;
    @Autowired
    private SelectedFilterHelper filterHelper;

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filter, Map<String, String[]> sorter, int page, int pageSize) {
        List<PerformanceByDisrictReport> performanceByDisrictReports = new ArrayList<>();
        Long userId = this.getUserId();
        PerformanceByDisrictReport performanceByDisrictReport = this.dropoutRateByDistrictService.loadPerformanceByDropoutRateDistrictReports(filter, userId);
        performanceByDisrictReport.setDistrictFlatList(this.convertToFlatList(performanceByDisrictReport.getColumnsValueList()));
        performanceByDisrictReport.setRegionFlatList(this.convertToFlatList(performanceByDisrictReport.getRegionColumnsValueList()));
        performanceByDisrictReports.add(performanceByDisrictReport);
        return performanceByDisrictReports;
    }

    private List<PerformanceByDropoutColumn> convertToFlatList(List<PerformanceByDropoutRange> columnsValueList) {
        List<PerformanceByDropoutColumn> flatList = new ArrayList<>();
        if (columnsValueList != null) {
            for (PerformanceByDropoutRange dropoutRange : columnsValueList) {
                flatList.addAll(dropoutRange.getColumns());
            }
        } else return null;
        return flatList;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        String district = StringHelper.getValue(params, "geographicZoneId");
        String distrctName = district == null ? "" : filterHelper.getGeoZoneFilterString(Long.parseLong(district));

        return filterHelper.getReportCombinedFilterString(
                distrctName,
                filterHelper.getSelectedProductSummary(params.get("productId")[0]),

                filterHelper.getSelectedPeriodRange(params));
    }
}
