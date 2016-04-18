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

import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.ivdform.domain.reports.*;
import org.openlmis.report.model.dto.Product;
import org.openlmis.vaccine.domain.reports.VaccineCoverageReport;
import org.openlmis.vaccine.repository.mapper.reports.VaccineReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VaccineReportRepository {

  @Autowired
  VaccineReportMapper mapper;

    @Autowired
    GeographicZoneService geographicZoneService;


  public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId){
    return mapper.getReportIdForFacilityAndPeriod(facilityId, periodId);
  }
  public List<DiseaseLineItem> getDiseaseSurveillance(Long reportId,Long userId){
    return mapper.getDiseaseSurveillance(reportId,userId);
  }

  public List<DiseaseLineItem> getDiseaseSurveillanceAggregateReport(Long periodId, Long zoneId,Long userId){
    return mapper.getDiseaseSurveillanceAggregateByGeoZone(periodId, zoneId,userId);
  }
  public Map<String, DiseaseLineItem> getCumFacilityDiseaseSurveillance(Long reportId, Long facilityId,Long userId){
    return mapper.getCumFacilityDiseaseSurveillance(reportId, facilityId,userId);
  }
public Map<String, DiseaseLineItem> getCumDiseaseSurveillanceAggregateReport(Long periodId, Long zoneId,Long userId){
  return mapper.getCumDiseaseSurveillanceAggregateByGeoZone(periodId, zoneId,userId);
}
  public List<ColdChainLineItem> getColdChain(Long reportId,Long userId){
    return mapper.getColdChain(reportId,userId);
  }

  public List<ColdChainLineItem> getColdChainAggregateReport(Long periodId, Long zoneId,Long userId){
    return mapper.getColdChainAggregateReport(periodId, zoneId,userId);
  }

  public List<AdverseEffectLineItem> getAdverseEffectReport(Long reportId,Long userId){
    return mapper.getAdverseEffectReport(reportId,userId);
  }

  public List<AdverseEffectLineItem> getAdverseEffectAggregateReport(Long periodId, Long zoneId,Long userId){
    return mapper.getAdverseEffectAggregateReport(periodId, zoneId,userId);
  }

  public List<HashMap<String, Object>> getVaccineCoverageReport(Long reportId,Long userId){
    return mapper.getVaccineCoverageReport(reportId,userId);
  }

  public List<HashMap<String , Object>> getVaccineCoverageAggregateReport(Long periodId, Long zoneId,Long userId){
    return mapper.getVaccineCoverageAggregateReportByGeoZone(periodId, zoneId,userId);
  }
public Map<String, VaccineCoverageReport> calculateVaccineCoverageReportForFacility( Long reportId, Long facilityId,Long userId){
  return mapper.calculateVaccineCoverageReport(reportId, facilityId,userId);
}
  public Map<String, VaccineCoverageReport> calculateVaccineCoverageReport( Long periodId, Long zoneId,Long userId){
    return mapper.calculateAggeregatedVaccineCoverageReport(periodId, zoneId,userId);
  }
  public List<VaccineReport> getImmunizationSession(Long reportId){
    return mapper.getImmunizationSession(reportId);
  }

  public List<VaccineReport> getImmunizationSessionAggregate(Long periodId, Long zoneId,Long userId){
    return mapper.getImmunizationSessionAggregate(periodId, zoneId,userId);
  }

  public List<HashMap<String, Object>> getVaccinationReport(String productCategoryCode, Long reportId,Long userId){
    return mapper.getVaccinationReport(productCategoryCode, reportId,userId);
  }
  public List<HashMap<String, Object>> getVaccinationAggregateByGeoZoneReport(String productCategoryCode, Long periodId, Long zoneId,Long userId){
    return mapper.getVaccinationAggregateByGeoZoneReport(productCategoryCode, periodId, zoneId,userId);
  }

  public List<HashMap<String, Object>> getTargetPopulation(Long facilityId, Long periodId){
    return mapper.getTargetPopulation(facilityId, periodId);
  }

  public List<HashMap<String, Object>> getTargetPopulationAggregateByGeoZone(Long periodId, Long zoneId,Long userId){
    return mapper.getTargetPopulationAggregateByGeoZone(periodId, zoneId,userId);
  }
  public List<VitaminSupplementationLineItem> getVitaminSupplementationReport(Long reportId){
    return mapper.getVitaminSupplementationReport(reportId);
  }

  public List<VitaminSupplementationLineItem> getVitaminSupplementationAggregateReport(Long periodId, Long zoneId,Long userId){
    return mapper.getVitaminSupplementationAggregateReport(periodId, zoneId,userId);
  }

  public List<HashMap<String, Object>> vaccineUsageTrend(String facilityCode, String productCode){
    return mapper.vaccineUsageTrend(facilityCode, productCode);
  }
  public List<HashMap<String, Object>> vaccineUsageTrendByGeographicZone(Long periodId, Long zoneId, String productCode){
    return mapper.vaccineUsageTrendByGeographicZone(periodId, zoneId, productCode);
  }

  public List<HashMap<String, Object>> getAggregateDropOuts(Long periodId, Long zoneId,Long userId){
    return mapper.getAggregateDropOuts(periodId, zoneId,userId);
  }

  public List<HashMap<String, Object>> getDropOuts(Long reportId,Long userId){
    return mapper.getDropOuts(reportId,userId);
  }

  public GeographicZone getNationalZone() {
    return mapper.getNationalZone();
  }


    public List<Map<String, Object>> getPerformanceCoverageMainReportDataByRegionAggregate(Date startDate, Date endDate, Long districtId, Long productId){
        return mapper.getPerformanceCoverageMainReportDataByRegionAggregate(startDate, endDate, districtId, productId);
    }

    public List<Map<String, Object>> getPerformanceCoverageSummaryReportDataByRegionAggregate(Date startDate, Date endDate, Long districtId, Long productId){
        return mapper.getPerformanceCoverageSummaryReportDataByRegionAggregate(startDate, endDate, districtId, productId);
    }

    public List<Map<String, Object>> getPerformanceCoverageMainReportDataByDistrict(Date startDate, Date endDate, Long districtId, Long productId){
        return  mapper.getPerformanceCoverageMainReportDataByDistrict(startDate, endDate, districtId, productId);
    }

    public List<Map<String, Object>> getPerformanceCoverageSummaryReportDataByDistrict(Date startDate, Date endDate, Long districtId, Long productId){
        return mapper.getPerformanceCoverageSummaryReportDataByDistrict(startDate, endDate, districtId, productId);
    }
    public List<Map<String, Object>> getPerformanceCoverageMainReportDataByRegion(Date startDate, Date endDate, Long districtId, Long productId){
        return mapper.getPerformanceCoverageMainReportDataByRegion(startDate, endDate, districtId, productId);
    }
    public List<Map<String, Object>> getPerformanceCoverageSummaryReportDataByRegion(Date startDate, Date endDate, Long districtId, Long productId){
        return  mapper.getPerformanceCoverageSummaryReportDataByRegion(startDate, endDate, districtId, productId);
    }


    public List<Map<String,Object>> getCompletenessAndTimelinessMainReportDataByDistrict(Date startDate, Date endDate, Long districtId) {
        return mapper.getCompletenessAndTimelinessMainReportDataByDistrict(startDate, endDate, districtId);
    }

    public List<Map<String,Object>> getCompletenessAndTimelinessSummaryReportDataByDistrict(Date startDate, Date endDate, Long districtId) {
         return mapper.getCompletenessAndTimelinessSummaryReportDataByDistrict(startDate, endDate, districtId);
    }

    public List<Map<String,Object>> getAdequacyLevelOfSupplyByDistrict(Date startDate, Date endDate, Long districtId, Long productId) {
        return mapper.getAdequacyLevelOfSupplyReportDataByDistrict(startDate, endDate, districtId, productId);
    }

    public List<Map<String,Object>> getAdequacyLevelOfSupplyByRegion(Date startDate, Date endDate, Long districtId, Long productId) {
        return mapper.getAdequacyLevelOfSupplyReportDataByRegion(startDate, endDate, districtId, productId);
    }
 public List<Map<String, Object>> getVaccineProductsList(){
    return this.mapper.getVaccineProductsList();
  }

  public List<Map<String,Object>> getClassificationVaccineUtilizationPerformanceFacility(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getClassificationVaccineUtilizationPerformanceForFacility1(startDate, endDate, zoneId, productId);
  }
  public List<Map<String,Object>> getClassificationVaccineUtilizationPerformanceByZone(Date startDate, Date endDate, Long zoneId, Long productId) {
   
    return mapper.getClassificationVaccineUtilizationPerformanceForDistrict1(startDate, endDate, zoneId, productId);
  }
  public List<Map<String,Object>> getClassificationVaccineUtilizationPerformanceRegion(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getClassificationVaccineUtilizationPerformanceForRegion1(startDate, endDate, zoneId, productId);
  }

  public List<Map<String,Object>> getCategorizationVaccineUtilizationPerformanceFacility(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getCategorizationVaccineUtilizationPerformanceForFacility(startDate, endDate, zoneId, productId);
  }
  public List<Map<String,Object>> getCategorizationVaccineUtilizationPerformanceByZone(Date startDate, Date endDate, Long zoneId, Long productId) {

    return mapper.getCategorizationVaccineUtilizationPerformanceForDistrict(startDate, endDate, zoneId, productId);
  }
  public List<Map<String,Object>> getCategorizationVaccineUtilizationPerformanceRegion(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getCategorizationVaccineUtilizationPerformanceForRegion(startDate, endDate, zoneId, productId);
  }
  public List<Map<String,Object>> getClassficationVaccinePopulationForFacility(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getClassficationVaccinePopulationForFacility(startDate, endDate, zoneId, productId);
  }
  public List<Map<String,Object>> getClassficationVaccinePopulationForDistrict(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getClassficationVaccinePopulationForDistrict(startDate, endDate, zoneId, productId);
  }

  public List<Map<String,Object>> getClassficationVaccinePopulationForRegion(Date startDate, Date endDate, Long zoneId, Long productId) {
    return mapper.getClassficationVaccinePopulationForRegion(startDate, endDate, zoneId, productId);
  }
    public List<Map<String,Object>> loadYearList(){
        return mapper.getDistincitYearList();
    }
}
