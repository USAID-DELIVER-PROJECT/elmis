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
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.mapper.RnRFeedbackReportMapper;
import org.openlmis.report.model.ResultRow;
import org.openlmis.report.model.params.OrderFillRateReportParam;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.model.report.OrderFillRateReport;
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
public class OrderFillRateReportDataProvider extends ReportDataProvider {

  public static final String ORDER_FILL_RATE = "ORDER_FILL_RATE";
  public static final String TOTAL_PRODUCTS_APPROVED = "TOTAL_PRODUCTS_APPROVED";
  public static final String TOTAL_PRODUCT_SHIPPED = "TOTAL_PRODUCT_SHIPPED";
  public static final String REPORT_STATUS = "REPORT_STATUS";
  @Autowired
  private OrderFillRateReportMapper reportMapper;

  @Autowired
  private SelectedFilterHelper selectedFilterHelper;

  @Autowired
  private RnRFeedbackReportMapper feedbackReportMapper;


  @Override
  public List<? extends ResultRow> getResultSet(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

    OrderFillRateReportParam parameter = ParameterAdaptor.parse(filterCriteria, OrderFillRateReportParam.class);
    parameter.setRnrId(feedbackReportMapper.getRnrId(parameter.getProgram(), parameter.getFacility(), parameter.getPeriod()));
    parameter.setUserId(this.getUserId());

    return reportMapper.getReport(parameter, rowBounds, this.getUserId());
  }

  @Override
  public List<? extends ResultRow> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

    OrderFillRateReportParam parameter = ParameterAdaptor.parse(filterCriteria, OrderFillRateReportParam.class);
    parameter.setUserId(this.getUserId());

    List<MasterReport> reportList = new ArrayList<MasterReport>();
    MasterReport report = new MasterReport();

    parameter.setRnrId(feedbackReportMapper.getRnrId(parameter.getProgram(), parameter.getFacility(), parameter.getPeriod()));
    List<OrderFillRateReport> detail = reportMapper.getReport(parameter, rowBounds, this.getUserId());
    report.setDetails(detail);

    // stateless lambdas doesn't create an overhead on memory
    Long approved = detail.stream().filter(row -> row.getApproved()!= null && row.getApproved() > 0).count();
    Long shipped = detail.stream().filter(row -> (row.getReceipts() != null && row.getReceipts() > 0)
            || (row.getSubstitutedProductQuantityShipped() != null && row.getSubstitutedProductQuantityShipped() > 0)).count();
    Float orderFillRate = ((approved == 0L || approved == null) ? 0L : ((float)shipped/approved)*100);

    report.setKeyValueSummary(new HashedMap(){{
      put(ORDER_FILL_RATE, orderFillRate);
      put(TOTAL_PRODUCTS_APPROVED, approved);
      put(TOTAL_PRODUCT_SHIPPED, shipped);
      //for the report to show data, the rnr status needs to be 'SUBMITED'.
      //If the report data is empty and the rnr is already there, lets show the status of the requisition.
      put(REPORT_STATUS, detail.size() == 0 ? reportMapper.getFillRateReportRequisitionStatus(parameter) : null);
    }});

    reportList.add(report);

    return reportList;
  }

  @Override
  public HashMap<String, String> getExtendedHeader(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();
    OrderFillRateReportParam parameter = ParameterAdaptor.parse(params, OrderFillRateReportParam.class);

    /*result.put("TOTAL_PRODUCTS_RECEIVED", totalProductsReceived);
    result.put("TOTAL_PRODUCTS_APPROVED", totalProductsOrdered);
    result.put("PERCENTAGE_ORDER_FILL_RATE", percent.toString());
    */
    result.put("REPORT_FILTER_PARAM_VALUES", selectedFilterHelper.getProgramGeoZoneFacility(params));
    return result;

  }
}
