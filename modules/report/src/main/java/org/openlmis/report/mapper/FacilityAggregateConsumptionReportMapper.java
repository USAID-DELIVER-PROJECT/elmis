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

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.FacilityAggregateConsumptionQueryBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.report.DistrictConsumptionReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FacilityAggregateConsumptionReportMapper {

    @SelectProvider(type = FacilityAggregateConsumptionQueryBuilder.class, method = "getQuery")
    @Results(value = {

            @Result(property = "product", column = "product"),
            @Result(property = "code", column = "code"),
            @Result(property = "consumption", column = "consumption"),
            @Result(property = "consumptionInPacks", column = "consumptionInPacks"),
            @Result(property = "adjustedConsumptionInPacks", column = "adjustedConsumptionInPacks"),
            @Result(property = "dispensed", column = "dispensed"),
            @Result(property = "facilityCode", column = "facilityCode"),
            @Result(property = "facility", column = "facility"),
            @Result(property = "facilityType", column = "facilityType"),
            @Result(property = "facilityId", column = "facilityId"),
            @Result(property = "periodName", column = "periodName"),
            @Result(property = "periodStart", column = "periodStart")
    })
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
    List<DistrictConsumptionReport> getAggregateConsumptionReport(
            @Param("filterCriteria") ReportParameter filterCriteria,
            @Param("SortCriteria") Map<String, String[]> sortCriteria,
            @Param("RowBounds") RowBounds rowBounds,
            @Param("userId") Long userId
    );

}
