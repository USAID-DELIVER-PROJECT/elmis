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

package org.openlmis.vaccine.repository.reports;

import org.openlmis.vaccine.domain.reports.TrendOfMinMaxColdChainTempratureDetail;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.TrendOfMinMaxColdChainRangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TrendMinMaxColdRangeRepository {
    @Autowired
    private TrendOfMinMaxColdChainRangeMapper mapper;
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureReports(
            PerformanceByDropoutRateParam filterCriteria
    ){
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList;
        coldChainTempratureDetailList=mapper.loadTrendMinMaxColdChainTempratureReports(filterCriteria);

        return  coldChainTempratureDetailList;
    }
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainDistrictTempratureReports(
            PerformanceByDropoutRateParam filterCriteria
    ){
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList;
        coldChainTempratureDetailList=mapper.loadTrendMinMaxColdChainTempratureDisrictReports(filterCriteria);

        return  coldChainTempratureDetailList;
    }
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureRegionReports(
            PerformanceByDropoutRateParam filterCriteria
    ){
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList;
        coldChainTempratureDetailList=mapper.loadTrendMinMaxColdChainTempratureRegionReports(filterCriteria);

        return  coldChainTempratureDetailList;
    }

    //////////////////////////////////////////////////

    private Map<String,TrendOfMinMaxColdChainTempratureDetail> getFacilityVaccineTargetInformation(
            PerformanceByDropoutRateParam filterCriteria
    ){
     return this.mapper.getFacilityVaccineTargetInformation(filterCriteria);
    }


    private Map<String,TrendOfMinMaxColdChainTempratureDetail> getDistrictVaccineTargetInformation(
            PerformanceByDropoutRateParam filterCriteria
    ){
        return this.mapper.getDistrictVaccineTargetInformation(filterCriteria);
    }


    private Map<String,TrendOfMinMaxColdChainTempratureDetail> getRegionVaccineTargetInformation(
             PerformanceByDropoutRateParam filterCriteria
    ){
        return this.mapper.getRegionVaccineTargetInformation(filterCriteria);
    }
}
