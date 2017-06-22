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
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Product;
import org.openlmis.report.mapper.PipelineExportReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.PipelineExportParams;
import org.openlmis.report.util.ParameterAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class PipelineExportReportProvider extends ReportDataProvider{
    private PipelineExportReportMapper reportMapper;

    @Autowired
    public PipelineExportReportProvider(PipelineExportReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria),sortCriteria, rowBounds);
    }

    public List<Product> getProducts(Map<String, String[]> filterCriteria){
        return reportMapper.getProductsForProgram(getReportFilterData(filterCriteria));
    }

    public ProcessingPeriod getPeriod(Map<String, String[]> filterCriteria){
        return reportMapper.getPeriod(getReportFilterData(filterCriteria));
    }


    public PipelineExportParams getReportFilterData(Map<String, String[]> filterCriteria) {
        return ParameterAdaptor.parse(filterCriteria, PipelineExportParams.class);
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return getReportFilterData(params).toString();

    }


}