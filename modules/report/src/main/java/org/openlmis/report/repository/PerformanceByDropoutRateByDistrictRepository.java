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

package org.openlmis.report.repository;

import org.openlmis.report.model.params.PerformanceByDropoutRateParam;
import org.openlmis.report.model.report.vaccine.DropoutProduct;
import org.openlmis.report.model.report.vaccine.PerformanceByDropoutRateByDistrict;
import org.openlmis.report.mapper.PerformanceByDropoutRateByDistrictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PerformanceByDropoutRateByDistrictRepository {
    @Autowired
    PerformanceByDropoutRateByDistrictMapper mapper;

    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateDistrictReports(PerformanceByDropoutRateParam filterCriteria) {
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList ;
        performanceByDropoutRateByDistrictList = mapper.loadPerformanceByDropoutRateDistrictReports(filterCriteria);
        return performanceByDropoutRateByDistrictList;
    }
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateFacillityReports(PerformanceByDropoutRateParam filterCriteria) {
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList ;
        performanceByDropoutRateByDistrictList = mapper.loadPerformanceByDropoutRateFacilityReports(filterCriteria);
        return performanceByDropoutRateByDistrictList;
    }
    public List<PerformanceByDropoutRateByDistrict> loadPerformanceByDropoutRateRegionReports(PerformanceByDropoutRateParam filterCriteria) {
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList ;
        performanceByDropoutRateByDistrictList = mapper.loadPerformanceByDropoutRateRegionReports(filterCriteria);
        return performanceByDropoutRateByDistrictList;
    }
    public boolean isDistrictLevel(Long goegraphicZoneId){

        return goegraphicZoneId!=0 &&mapper.isDistrictLevel(goegraphicZoneId)>0;
}
    public List<DropoutProduct> loadDropoutProductList(){

        return this.mapper.loadDropoutProductList();

    }

    public List<Map<String,Object>> getClassficationVaccinePopulationForFacility(Date startDate, Date endDate, Long zoneId, Long productId) {
        return mapper.getClassficationVaccinePopulationForFacility(startDate, endDate, zoneId, productId, 0L);
    }
    public List<Map<String,Object>> getClassficationVaccinePopulationForDistrict(Date startDate, Date endDate, Long zoneId, Long productId) {
        return mapper.getClassficationVaccinePopulationForDistrict(startDate, endDate, zoneId, productId, 0L);
    }

    public List<Map<String,Object>> getClassficationVaccinePopulationForRegion(Date startDate, Date endDate, Long zoneId, Long productId) {
        return mapper.getClassficationVaccinePopulationForRegion(startDate, endDate, zoneId, productId, 0L);
    }
}
