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
import org.openlmis.report.mapper.ColdChainTemperaturesReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.ColdChainTemperatureReportParam;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class ColdChainTemperatureDataProvider extends ReportDataProvider {

    @Autowired
    private ColdChainTemperaturesReportMapper reportMapper;

    @Override
    @Transactional
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReportData(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }

    public ColdChainTemperatureReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

        ColdChainTemperatureReportParam ccTParams = new ColdChainTemperatureReportParam();

        ccTParams.setZoneId(StringHelper.isBlank(filterCriteria, "zone") ? 0: Long.parseLong(filterCriteria.get("zone")[0]));
        ccTParams.setYear(StringHelper.isBlank(filterCriteria, "year") ? 0: Long.parseLong(filterCriteria.get("year")[0]));
        ccTParams.setRegionSelected(StringHelper.isBlank(filterCriteria, "regionSelected") ? false : Boolean.parseBoolean(filterCriteria.get("regionSelected")[0]));
        return ccTParams;
    }

    @Transactional
    public List<? extends ResultRow> getMainReportMinMaxAggregate(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getMainReportMinMaxAggregate(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }

    @Transactional
    public List<? extends ResultRow> getMainReportAggregateMinMaxTempRecorded(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getMainReportAggregateMinMaxTempRecorded(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }

    @Transactional
    public List<? extends ResultRow> getMainReportAggregateTotal(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getMainReportAggregateTotal(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }


    @Transactional
    public List<? extends ResultRow> getSubReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getSubReportData(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }

    @Transactional
    public List<? extends ResultRow> getSubReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getSubReportData(getReportFilterData(filterCriteria), rowBounds, this.getUserId());
    }

    @Override
    @Transactional
    public String getFilterSummary(Map<String, String[]> params) {
        return  "";//filterHelper.getProgramPeriodGeoZone(params);
    }



}
