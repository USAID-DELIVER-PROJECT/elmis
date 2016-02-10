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

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.openlmis.vaccine.domain.reports.TrendOfMinMaxColdChainTempratureDetail;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.builder.PerformanceByDropoutRateQueryBuilder;
import org.openlmis.vaccine.repository.mapper.reports.builder.TrendOfMinMaxColdRangeBuilder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface TrendOfMinMaxColdChainRangeMapper {
    @SelectProvider(type = TrendOfMinMaxColdRangeBuilder.class, method = "getTredofMinMaxColdRangeFacilityQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );

    @SelectProvider(type = TrendOfMinMaxColdRangeBuilder.class, method = "getTredofMinMaxColdRangeDistrictQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureDisrictReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );

    @SelectProvider(type = TrendOfMinMaxColdRangeBuilder.class, method = "getTredofMinMaxColdRangeRegionQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureRegionReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );


}
