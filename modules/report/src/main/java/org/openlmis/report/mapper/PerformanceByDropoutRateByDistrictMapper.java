/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.openlmis.report.builder.ClassificationVaccineUtilizationPerformanceQueryBuilder;
import org.openlmis.report.builder.PerformanceByDropoutRateQueryBuilder;
import org.openlmis.report.model.params.PerformanceByDropoutRateParam;
import org.openlmis.report.model.report.vaccine.DropoutProduct;
import org.openlmis.report.model.report.vaccine.PerformanceByDropoutRateByDistrict;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface PerformanceByDropoutRateByDistrictMapper {
    @SelectProvider(type=PerformanceByDropoutRateQueryBuilder.class, method="getByDistrictQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateDistrictReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=PerformanceByDropoutRateQueryBuilder.class, method="getByFacilityQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateFacilityReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=PerformanceByDropoutRateQueryBuilder.class, method="getByRegionQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateRegionReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @Select("select count(*) from geographic_zones  gz " +
            "join geographic_levels gl on gz.levelid= gl.id " +
            " where gl.code='dist' and  gz.id= #{zoneId}")
    public int isDistrictLevel(@Param("zoneId")Long goegraphicZoneId);
    @Select("select " +
            "   case when code = 'V001' then 'BCG - MR1' else 'DTP-HepB-Hib1/DTP-HepB-Hib3' end dropout , id " +
            "    from products where code in ('V001','V010')")
    public List<DropoutProduct> loadDropoutProductList();

    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getFacilityPopulationInformation")
    public  List<Map<String,Object>> getClassficationVaccinePopulationForFacility(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId,
            @Param("doseId") Long doseId);
    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getDistrictPopulationInformation")
    public  List<Map<String,Object>> getClassficationVaccinePopulationForDistrict(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId,
            @Param("doseId") Long doseId);

    @SelectProvider(type = ClassificationVaccineUtilizationPerformanceQueryBuilder.class, method = "getRegionPopulationInformation")
    public  List<Map<String,Object>> getClassficationVaccinePopulationForRegion(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("zoneId") Long zoneId,
            @Param("productId") Long productId,
            @Param("doseId") Long doseId);
}
