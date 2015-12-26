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

package org.openlmis.vaccine.repository.reports;

import org.apache.ibatis.annotations.Param;
import org.openlmis.vaccine.domain.reports.StatusOfVaccinationSuppliesRecievedDetail;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.StatusOfVaccinationSupplyStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class StatusOfVaccinationSuppliesRepository {
    @Autowired
    private StatusOfVaccinationSupplyStatusMapper mapper;

    public List<StatusOfVaccinationSuppliesRecievedDetail> loadStatusOfVaccineSupplyForFacilityReports(
            PerformanceByDropoutRateParam filterCriteria
    ) {
        List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesRecievedDetailList = null;
        statusOfVaccinationSuppliesRecievedDetailList = this.mapper.loadStatusOfVaccineSupplyForFacilityReports(filterCriteria);
        return statusOfVaccinationSuppliesRecievedDetailList;
    }

    public List<StatusOfVaccinationSuppliesRecievedDetail> loadStatusOfVaccineSupplyForDistrict(
            PerformanceByDropoutRateParam filterCriteria
    ) {
        List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesRecievedDetailList = null;
        statusOfVaccinationSuppliesRecievedDetailList = this.mapper.loadStatusOfVaccineSupplyForDistrict(filterCriteria);
        return statusOfVaccinationSuppliesRecievedDetailList;
    }

    public List<StatusOfVaccinationSuppliesRecievedDetail> loadStatusOfVaccineSupplyForRegionReports(
            PerformanceByDropoutRateParam filterCriteria
    ) {
        List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesRecievedDetailList = null;
        statusOfVaccinationSuppliesRecievedDetailList = this.mapper.loadStatusOfVaccineSupplyForRegionReports(filterCriteria);
        return statusOfVaccinationSuppliesRecievedDetailList;
    }
    //////////////////////////////////
    public HashMap<String,StatusOfVaccinationSuppliesRecievedDetail> loadPopulationForFacilityReports(
           PerformanceByDropoutRateParam filterCriteria
    ){
        HashMap<String,StatusOfVaccinationSuppliesRecievedDetail> regionPopulationMap= null;
        regionPopulationMap=this.mapper.loadPopulationForFacilityReports(filterCriteria);
        return regionPopulationMap;
    }
    public HashMap<String,StatusOfVaccinationSuppliesRecievedDetail> loadPopulationForDistrict(
             PerformanceByDropoutRateParam filterCriteria
    ){
        HashMap<String,StatusOfVaccinationSuppliesRecievedDetail> regionPopulationMap= null;
        regionPopulationMap=this.mapper.loadPopulationForDistrict(filterCriteria);
        return regionPopulationMap;
    }
    public HashMap<String,StatusOfVaccinationSuppliesRecievedDetail> loadPopulationForRegionReports(
             PerformanceByDropoutRateParam filterCriteria
    ){
        HashMap<String,StatusOfVaccinationSuppliesRecievedDetail> regionPopulationMap= null;
        regionPopulationMap=this.mapper.loadPopulationForRegionReports(filterCriteria);
        return regionPopulationMap;
    }
}
