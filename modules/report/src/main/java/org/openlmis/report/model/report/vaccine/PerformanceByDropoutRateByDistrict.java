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

package org.openlmis.report.model.report.vaccine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceByDropoutRateByDistrict {


    private String region_name;
    private String district_name;
    private Long target;
    private String facility_name;
    private Long facility_id;
    private Date period_name;
    private Long cum_bcg_vaccinated;
    private Long cum_mr_vaccinated;
    private Long cum_dtp3_vaccinated;
    private float cum_bcg_mr_dropout;
    private float cum_dtp1_dtp3_dropout;
    private Long cum_dtp1_vaccinated;
    private Long bcg_vaccinated;
    private Long dtp1_vaccinated;
    private Long mr_vaccinated;
    private Long dtp3_vaccinated;
    private float bcg_mr_dropout;
    private float dtp1_dtp3_dropout;
    List<PerformanceByDropoutRateByDistrict> reportedPeriods;
    boolean generated;

    @Override
    public boolean equals(Object obj) {
        String objKey = "";
        String thisKey = "";
        PerformanceByDropoutRateByDistrict report = (PerformanceByDropoutRateByDistrict) obj;
        StringBuilder uniqueZoneKey = new StringBuilder();
        StringBuilder thisZoneKey = new StringBuilder();
        uniqueZoneKey.append(report.getRegion_name()).append(" _").append(report.getDistrict_name()).append("_").append(report.getFacility_name());
        thisZoneKey.append(this.getRegion_name()).append(" _").append(this.getDistrict_name()).append("_").append(this.getFacility_name());
        objKey = uniqueZoneKey.toString();
        thisKey = thisZoneKey.toString();
        return objKey.equals(thisKey);

    }

    public PerformanceByDropoutRateByDistrict getPeriodValue(Date period_name) {
        if (reportedPeriods != null && !reportedPeriods.isEmpty()) {
            for (PerformanceByDropoutRateByDistrict rateByDistrict : this.getReportedPeriods()) {
                if (rateByDistrict.getPeriod_name().equals(period_name)) {
                    return rateByDistrict;
                }
            }

        }
        return null;
    }
    @Override
    public PerformanceByDropoutRateByDistrict clone(){
        PerformanceByDropoutRateByDistrict dropoutRateByDistrict = new PerformanceByDropoutRateByDistrict();
        dropoutRateByDistrict.setPeriod_name(this.period_name);

        dropoutRateByDistrict.setCum_bcg_vaccinated(this.getCum_bcg_vaccinated());
        dropoutRateByDistrict.setCum_mr_vaccinated(this.getCum_mr_vaccinated());
        dropoutRateByDistrict.setCum_bcg_mr_dropout(this.cum_bcg_mr_dropout);
        dropoutRateByDistrict.setCum_dtp1_vaccinated(this.getCum_dtp1_vaccinated());
        dropoutRateByDistrict.setCum_dtp3_vaccinated(this.cum_dtp3_vaccinated);
        dropoutRateByDistrict.setCum_dtp1_dtp3_dropout(this.getCum_dtp1_dtp3_dropout());

        return  dropoutRateByDistrict;
    }
}
