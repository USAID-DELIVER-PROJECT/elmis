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

package org.openlmis.vaccine.repository.reports;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import org.openlmis.vaccine.domain.reports.TrendOfMinMaxColdChainTempratureDetail;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.mapper.reports.TrendOfMinMaxColdChainRangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Component
public class TrendMinMaxColdRangeRepository {
    @Autowired
    private TrendOfMinMaxColdChainRangeMapper mapper;
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureReports(
            PerformanceByDropoutRateParam filterCriteria
    ){
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList=null;
        coldChainTempratureDetailList=mapper.loadTrendMinMaxColdChainTempratureReports(filterCriteria);
        HashMap<String,TrendOfMinMaxColdChainTempratureDetail> facilityPopulationListMap= this.getFacilityVaccineTargetInformation(filterCriteria);
        return  coldChainTempratureDetailList;
    }
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainDistrictTempratureReports(
            PerformanceByDropoutRateParam filterCriteria
    ){
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList=null;
        coldChainTempratureDetailList=mapper.loadTrendMinMaxColdChainTempratureDisrictReports(filterCriteria);
        HashMap<String,TrendOfMinMaxColdChainTempratureDetail> districtPopulationListMap= this.getDistrictVaccineTargetInformation(filterCriteria);
        return  coldChainTempratureDetailList;
    }
    public List<TrendOfMinMaxColdChainTempratureDetail> loadTrendMinMaxColdChainTempratureRegionReports(
            PerformanceByDropoutRateParam filterCriteria
    ){
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList=null;
        coldChainTempratureDetailList=mapper.loadTrendMinMaxColdChainTempratureRegionReports(filterCriteria);
        HashMap<String,TrendOfMinMaxColdChainTempratureDetail> regionPoulationListMap=this.getRegionVaccineTargetInformation(filterCriteria);
        return  coldChainTempratureDetailList;
    }

    //////////////////////////////////////////////////

    private HashMap<String,TrendOfMinMaxColdChainTempratureDetail> getFacilityVaccineTargetInformation(
            PerformanceByDropoutRateParam filterCriteria
    ){
     return this.mapper.getFacilityVaccineTargetInformation(filterCriteria);
    }


    private HashMap<String,TrendOfMinMaxColdChainTempratureDetail> getDistrictVaccineTargetInformation(
            PerformanceByDropoutRateParam filterCriteria
    ){
        return this.mapper.getDistrictVaccineTargetInformation(filterCriteria);
    }


    private HashMap<String,TrendOfMinMaxColdChainTempratureDetail> getRegionVaccineTargetInformation(
             PerformanceByDropoutRateParam filterCriteria
    ){
        return this.mapper.getRegionVaccineTargetInformation(filterCriteria);
    }
}
