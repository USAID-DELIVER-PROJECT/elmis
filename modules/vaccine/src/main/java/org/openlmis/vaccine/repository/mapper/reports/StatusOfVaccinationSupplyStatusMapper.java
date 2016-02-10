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

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.reports.StatusOfVaccinationSuppliesRecievedDetail;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.builder.StatusOfVaccinationSuppliesQueryBuilder;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StatusOfVaccinationSupplyStatusMapper {
    @SelectProvider(type=StatusOfVaccinationSuppliesQueryBuilder.class, method="getStatusOfVaccineSupplyForFacility")
    List<StatusOfVaccinationSuppliesRecievedDetail> loadStatusOfVaccineSupplyForFacilityReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=StatusOfVaccinationSuppliesQueryBuilder.class, method="getStatusOfVaccineSupplyForDistrict")
    List<StatusOfVaccinationSuppliesRecievedDetail> loadStatusOfVaccineSupplyForDistrict(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=StatusOfVaccinationSuppliesQueryBuilder.class, method="getStatusOfVaccineSupplyForRegion")
    List<StatusOfVaccinationSuppliesRecievedDetail> loadStatusOfVaccineSupplyForRegionReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    /////////////////////////////////////////////////////
    @SelectProvider(type=StatusOfVaccinationSuppliesQueryBuilder.class, method="getPopulationForFacility")
    @MapKey("facility_name")
    @ResultType(HashMap.class)
    Map<String,StatusOfVaccinationSuppliesRecievedDetail> loadPopulationForFacilityReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=StatusOfVaccinationSuppliesQueryBuilder.class, method="getPopulationForDistrict")
    @MapKey("district_name")
    @ResultType(HashMap.class)
    Map<String,StatusOfVaccinationSuppliesRecievedDetail> loadPopulationForDistrict(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
    @SelectProvider(type=StatusOfVaccinationSuppliesQueryBuilder.class, method="getPopulationForRegion")
    @MapKey("region_name")
    @ResultType(HashMap.class)
    Map<String,StatusOfVaccinationSuppliesRecievedDetail> loadPopulationForRegionReports(
            @Param("filterCriteria") PerformanceByDropoutRateParam filterCriteria
    );
}
