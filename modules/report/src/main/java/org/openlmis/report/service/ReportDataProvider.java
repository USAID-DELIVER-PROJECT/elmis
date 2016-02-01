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

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.ResultRow;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ReportDataProvider {

  @Getter
  @Setter
  private Long userId;

  public List<? extends ResultRow> getResultSet(Map<String, String[]> params){
    return getReportBody(params, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  public abstract List<? extends ResultRow> getReportBody(Map<String, String[]> filter, Map<String, String[]> sorter, int page, int pageSize);

  public Map<String,String> getExtendedHeader(Map params){
      return Collections.emptyMap();
  }

  public String getFilterSummary(Map<String, String[]> params){
    return "";
  }
}
