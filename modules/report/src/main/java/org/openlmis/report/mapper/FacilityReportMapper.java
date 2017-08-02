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
import org.openlmis.core.dto.FacilitySupervisor;
import org.openlmis.report.builder.FacilityReportQueryBuilder;
import org.openlmis.report.model.params.FacilityReportParam;
import org.openlmis.report.model.report.FacilityProgramReport;
import org.openlmis.report.model.report.FacilityReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityReportMapper {

    @SelectProvider(type=FacilityReportQueryBuilder.class, method="getExportQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    @Results(value = {
            @Result(column="id", property="id"),
            @Result(column="code", property="code"),
            @Result(column="name", property="facilityName"),
            @Result(column="active", property="active"),
            @Result(column="facilityType", property="facilityType"),
            @Result(column="region", property="region"),
            @Result(column="owner", property="owner"),
            @Result(column = "gpsCoordinates", property = "gpsCoordinates"),
            @Result(column="phoneNumber", property="phoneNumber"),
            @Result(column="fax", property="fax"),
            @Result(column="id", property="id"),
            @Result(column="programcode", property="programCode"),
            @Result(column="programname", property="name"),
            @Result(column="activeprogram", property="activeProgram"),
            @Result(column="programid", property="programId"),
            @Result(column="startdate", property="startDate"),
            @Result(column="province", property="province")
    })
    List<FacilityReport> SelectFilteredSortedPagedFacilities(
            @Param("filterCriteria") FacilityReportParam filterCriteria,
            @Param("userId") Long userId
    );

}
