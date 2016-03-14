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

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.CCEStorageCapacityQueryBuilder;
import org.openlmis.report.model.params.CCEStorageCapacityReportParam;
import org.openlmis.report.model.report.CCEStorageCapacityReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CCEStorageCapacityReportMapper {

    @SelectProvider(type = CCEStorageCapacityQueryBuilder.class, method = "getData")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    List<CCEStorageCapacityReport> getFilteredSortedCCEStorageCapacityReport(
        @Param("filterCriteria") CCEStorageCapacityReportParam param,
        @Param("rowBounds") RowBounds rowBounds,
        @Param("userId") Long userId
    );

    @Select("select value from facility_demographic_estimates fd " +
            " join demographic_estimate_categories dc on fd.demographicestimateid=dc.id " +
            " where dc.name = #{demographicCategory} and fd.facilityid=#{facilityId} and fd.year=#{year} limit 1")
    Integer getFacilityCategoryPopulation(
            @Param("demographicCategory") String demographicCategory,
            @Param("facilityId") Long facilityId,
            @Param("year") int year
    );

    @Select("select value from district_demographic_estimates dd " +
            " join demographic_estimate_categories dc on dd.demographicestimateid=dc.id " +
            " join facilities f on f.geographiczoneid=dd.districtid " +
            " where dc.name =#{demographicCategory} and f.id=#{facilityId} and dd.year=#{year} limit 1")
    Integer getDistrictCategoryPoulation(
            @Param("demographicCategory") String demographicCategory,
            @Param("facilityId") Long facilityId,
            @Param("year") int year
    );

    @Select("select SUM(value) from district_demographic_estimates dd " +
            " join demographic_estimate_categories dc on dd.demographicestimateid=dc.id " +
            " where dc.name = #{demographicCategory} and dd.districtid " +
            " IN (select id from geographic_zones where parentid= " +
            " (select gz.parentid from facilities f join geographic_zones gz on gz.id=f.geographiczoneid where f.id=#{facilityId})) " +
            " and dd.year=#{year}")
    Integer getRegionalCategoryPoulation(
            @Param("demographicCategory") String demographicCategory,
            @Param("facilityId") Long facilityId,
            @Param("year") int year
    );

}
