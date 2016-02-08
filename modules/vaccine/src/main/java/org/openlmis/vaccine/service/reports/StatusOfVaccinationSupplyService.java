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

package org.openlmis.vaccine.service.reports;

import org.openlmis.vaccine.domain.reports.StatusOfVaccinationSuppliesReceivedReport;
import org.openlmis.vaccine.domain.reports.StatusOfVaccinationSuppliesRecievedDetail;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;
import org.openlmis.vaccine.repository.reports.PerformanceByDropoutRateByDistrictRepository;
import org.openlmis.vaccine.repository.reports.StatusOfVaccinationSuppliesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class StatusOfVaccinationSupplyService {
    @Autowired
    private StatusOfVaccinationSuppliesRepository repository;
    @Autowired
    private PerformanceByDropoutRateByDistrictRepository dropoutRateByDistrictRepository;


    public StatusOfVaccinationSuppliesReceivedReport loadStatusOfVaccineSupplyReport(Map<String, String[]> filterCriteria) {
        StatusOfVaccinationSuppliesReceivedReport suppliesReceivedReport ;
        boolean isFacilityReport ;
        boolean isRegionReport ;
        List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesFacilityDistrictList;
        List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesRegionList = null;
        Map<String, StatusOfVaccinationSuppliesRecievedDetail> facilityOrDistrictPopulationMap ;
        Map<String, StatusOfVaccinationSuppliesRecievedDetail> regionPopulationMap ;
        PerformanceByDropoutRateParam filterParam ;
        filterParam = ReportsCommonUtilService.prepareParam(filterCriteria);
        isRegionReport = filterParam.getGeographic_zone_id() == 0 ? true : false;
        isFacilityReport = dropoutRateByDistrictRepository.isDistrictLevel(filterParam.getGeographic_zone_id());
        if (isFacilityReport) {
            statusOfVaccinationSuppliesFacilityDistrictList = this.repository.loadStatusOfVaccineSupplyForFacilityReports(filterParam);
            facilityOrDistrictPopulationMap = this.repository.loadPopulationForFacilityReports(filterParam);
            this.extractPopulationInformation(statusOfVaccinationSuppliesFacilityDistrictList,facilityOrDistrictPopulationMap,ReportsCommonUtilService.FACILLITY_REPORT);
        } else {
            statusOfVaccinationSuppliesFacilityDistrictList = this.repository.loadStatusOfVaccineSupplyForDistrict(filterParam);
            facilityOrDistrictPopulationMap = this.repository.loadPopulationForDistrict(filterParam);
            this.extractPopulationInformation(statusOfVaccinationSuppliesFacilityDistrictList,facilityOrDistrictPopulationMap,ReportsCommonUtilService.DISTRICT_REPORT);
            if (isRegionReport) {
                statusOfVaccinationSuppliesRegionList = this.repository.loadStatusOfVaccineSupplyForRegionReports(filterParam);
                regionPopulationMap = this.repository.loadPopulationForRegionReports(filterParam);
                this.extractPopulationInformation(statusOfVaccinationSuppliesRegionList,regionPopulationMap,ReportsCommonUtilService.REGION_REPORT);
            }
        }
        suppliesReceivedReport = this.aggeregaeReportValue(statusOfVaccinationSuppliesFacilityDistrictList);
        suppliesReceivedReport.setFacilityDistrictVaccineStatusList(statusOfVaccinationSuppliesFacilityDistrictList);
        suppliesReceivedReport.setRegionVaccineStatusList(statusOfVaccinationSuppliesRegionList);
        suppliesReceivedReport.setFacilityReport(isFacilityReport);
        suppliesReceivedReport.setRegionReport(isRegionReport);
        return suppliesReceivedReport;

    }

    private void extractPopulationInformation(List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesFacilityDistrictList,
                                              Map<String, StatusOfVaccinationSuppliesRecievedDetail> populationListMap, int geoLevel) {
        for (StatusOfVaccinationSuppliesRecievedDetail vaccinationSuppliesRecievedDetail : statusOfVaccinationSuppliesFacilityDistrictList) {
            Long population;
            String geoLevelName ;
            StatusOfVaccinationSuppliesRecievedDetail populationInfo ;
            if (geoLevel == ReportsCommonUtilService.FACILLITY_REPORT) {
                geoLevelName = vaccinationSuppliesRecievedDetail.getFacility_name();
            } else if (geoLevel == ReportsCommonUtilService.DISTRICT_REPORT) {
                geoLevelName = vaccinationSuppliesRecievedDetail.getDistrict_name();
            } else {
                geoLevelName = vaccinationSuppliesRecievedDetail.getRegion_name();
            }
            geoLevelName+="_"+vaccinationSuppliesRecievedDetail.getPeriod_name();
            populationInfo = populationListMap.get(geoLevelName);
            population = populationInfo != null ? populationInfo.getTargetpopulation() : 0L;
            vaccinationSuppliesRecievedDetail.setTargetpopulation(population);
        }
    }

    public StatusOfVaccinationSuppliesReceivedReport aggeregaeReportValue(List<StatusOfVaccinationSuppliesRecievedDetail> statusOfVaccinationSuppliesFacilityDistrictList) {
        StatusOfVaccinationSuppliesReceivedReport suppliesReceivedReport = new StatusOfVaccinationSuppliesReceivedReport();
        float totalReceived = 0f;
        float totalOnHand = 0f;
        float totalIssued = 0f;
        float totalUsed = 0f;
        float totalWasted = 0f;
        float totalAdminstered = 0f;
        Long totalPopulation = 0L;
        for (StatusOfVaccinationSuppliesRecievedDetail statusOfVaccinationSuppliesRecievedDetail : statusOfVaccinationSuppliesFacilityDistrictList) {

            totalReceived += statusOfVaccinationSuppliesRecievedDetail.getReceived();
            totalOnHand += statusOfVaccinationSuppliesRecievedDetail.getOnhand();
            totalIssued += statusOfVaccinationSuppliesRecievedDetail.getIssued();
            totalUsed += statusOfVaccinationSuppliesRecievedDetail.getUsed();
            totalWasted += statusOfVaccinationSuppliesRecievedDetail.getWasted();
            totalAdminstered += statusOfVaccinationSuppliesRecievedDetail.getAdministered();
            totalPopulation += statusOfVaccinationSuppliesRecievedDetail.getTargetpopulation();
        }
        suppliesReceivedReport.setTotalReceived(totalReceived);
        suppliesReceivedReport.setTotalOnHand(totalOnHand);
        suppliesReceivedReport.setTotalIssued(totalIssued);
        suppliesReceivedReport.setTotalUsed(totalUsed);
        suppliesReceivedReport.setTotalWasted(totalWasted);
        suppliesReceivedReport.setTotalAdminstered(totalAdminstered);
        suppliesReceivedReport.setTotalPopulation(totalPopulation);
        return suppliesReceivedReport;
    }
}
