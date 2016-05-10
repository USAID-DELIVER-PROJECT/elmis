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

import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.service.*;
import org.openlmis.ivdform.domain.reports.*;
import org.openlmis.report.model.dto.Product;
import org.openlmis.vaccine.domain.reports.VaccineCoverageReport;
import org.openlmis.vaccine.repository.mapper.reports.PerformanceByDropoutRateByDistrictMapper;
import org.openlmis.vaccine.repository.reports.VaccineReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@NoArgsConstructor
public class VaccineReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VaccineReportService.class);
    public static final String VACCINE_REPORT_VACCINE_CATEGORY_CODE = "VACCINE_REPORT_VACCINE_CATEGORY_CODE";
    public static final String VACCINE_REPORT_VITAMINS_CATEGORY_CODE = "VACCINE_REPORT_VITAMINS_CATEGORY_CODE";
    public static final String VACCINE_REPORT_SYRINGES_CATEGORY_CODE = "VACCINE_REPORT_SYRINGES_CATEGORY_CODE";
    @Autowired
    private PerformanceByDropoutRateByDistrictMapper dropoutRateByDistrictMapper;

    @Autowired
    VaccineReportRepository repository;

    @Autowired
    ProgramProductService programProductService;

    @Autowired
    ProcessingPeriodRepository periodService;

    @Autowired
    ProgramService programService;


    @Autowired
    MessageService messageService;

    @Autowired
    ConfigurationSettingService configurationSettingService;

    @Autowired
    GeographicZoneService geographicZoneService;

    private static final String DATE_FORMAT = "yyyy-MM-dd";


    public Long getReportIdForFacilityAndPeriod(Long facilityId, Long periodId) {
        return repository.getReportIdForFacilityAndPeriod(facilityId, periodId);
    }

    private List<DiseaseLineItem> getDiseaseSurveillance(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {

            return repository.getDiseaseSurveillance(reportId,userId);
        }

        return repository.getDiseaseSurveillanceAggregateReport(periodId, zoneId, userId);
    }

    private Map<String, DiseaseLineItem> getCumulativeDiseaseSurveillance(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {

            return repository.getCumFacilityDiseaseSurveillance(reportId, facilityId, userId);
        }

        return repository.getCumDiseaseSurveillanceAggregateReport(periodId, zoneId, userId);
    }

    private List<ColdChainLineItem> getColdChain(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getColdChain(reportId, userId);
        }
        return repository.getColdChainAggregateReport(periodId, zoneId, userId);
    }

    private List<AdverseEffectLineItem> getAdverseEffectReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getAdverseEffectReport(reportId, userId);
        }
        return repository.getAdverseEffectAggregateReport(periodId, zoneId, userId);
    }

    private List<HashMap<String, Object>> getVaccineCoverageReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {

            return repository.getVaccineCoverageReport(reportId, userId);
        }
        return repository.getVaccineCoverageAggregateReport(periodId, zoneId, userId);
    }

    private Map<String, VaccineCoverageReport> calculateVaccineCoverageReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.calculateVaccineCoverageReportForFacility(reportId, facilityId, userId);
        }
        return repository.calculateVaccineCoverageReport(periodId, zoneId, userId);
    }

    private List<VaccineReport> getImmunizationSession(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getImmunizationSession(reportId);
        }
        return repository.getImmunizationSessionAggregate(periodId, zoneId, userId);
    }

    private List<HashMap<String, Object>> getVaccineReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getVaccinationReport(VACCINE_REPORT_VACCINE_CATEGORY_CODE, reportId,userId);
        } else {

            return repository.getVaccinationAggregateByGeoZoneReport(VACCINE_REPORT_VACCINE_CATEGORY_CODE, periodId, zoneId, userId);
        }
    }

    private List<HashMap<String, Object>> getSyringeAndSafetyBoxReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getVaccinationReport(VACCINE_REPORT_SYRINGES_CATEGORY_CODE, reportId, userId);
        }
        return repository.getVaccinationAggregateByGeoZoneReport(VACCINE_REPORT_SYRINGES_CATEGORY_CODE, periodId, zoneId,userId);

    }

    private List<HashMap<String, Object>> getVitaminsReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getVaccinationReport(VACCINE_REPORT_VITAMINS_CATEGORY_CODE, reportId,userId);
        }
        return repository.getVaccinationAggregateByGeoZoneReport(VACCINE_REPORT_VITAMINS_CATEGORY_CODE, periodId, zoneId,userId);
    }

    private List<HashMap<String, Object>> getTargetPopulation(Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getTargetPopulation(facilityId, periodId);
        }
        return repository.getTargetPopulationAggregateByGeoZone(periodId, zoneId, userId);
    }

    private List<VitaminSupplementationLineItem> getVitaminSupplementationReport(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {

            return repository.getVitaminSupplementationReport(reportId);
        }
        return repository.getVitaminSupplementationAggregateReport(periodId, zoneId, userId);
    }

    private List<HashMap<String, Object>> getDropOuts(Long reportId, Long facilityId, Long periodId, Long zoneId,Long userId) {
        if (facilityId != null && facilityId != 0) {
            return repository.getDropOuts(reportId, userId);
        }
        return repository.getAggregateDropOuts(periodId, zoneId, userId);
    }

    public List<HashMap<String, Object>> vaccineUsageTrend(String facilityCode, String productCode, Long periodId, Long zoneId, Long userId) {
        List<HashMap<String, Object>> vaccineUsageTrend = null;
        Long districtId;
        try {


            if (zoneId == -1 || zoneId == 0) {
                districtId = getNationalZoneId();
            } else {
                districtId = zoneId;
            }

            if ((facilityCode == null || facilityCode.isEmpty()) && periodId != 0) {
                vaccineUsageTrend = repository.vaccineUsageTrendByGeographicZone(periodId, districtId, productCode,userId);
            } else {
                vaccineUsageTrend = repository.vaccineUsageTrend(facilityCode, productCode,userId);

            }
        } catch (Exception ex) {
            LOGGER.warn("Error Message: ", ex);
        }
        return vaccineUsageTrend;
    }

    public Map<String, Object> getMonthlyVaccineReport(Long facilityId, Long periodId, Long zoneId,Long userId) {

        Map<String, Object> data = new HashMap();
        Long reportId = null;
        Long districtId;
        if (facilityId != null && facilityId != 0) {
            reportId = getReportIdForFacilityAndPeriod(facilityId, periodId);

        }

        if (zoneId == -1 || zoneId == 0) {
            districtId = getNationalZoneId();
        } else {
            districtId = zoneId;
        }
        try {


            data.put("vaccination", getVaccineReport(reportId, facilityId, periodId, districtId,userId));
            data.put("diseaseSurveillance", getDiseaseSurveillance(reportId, facilityId, periodId, districtId,userId));
            data.put("cumDiseaseSurveillance", this.getCumulativeDiseaseSurveillance(reportId, facilityId, periodId, districtId,userId));
            data.put("vaccineCoverage", getVaccineCoverageReport(reportId, facilityId, periodId, districtId,userId));
            data.put("vaccineCoverageCalculation", calculateVaccineCoverageReport(reportId, facilityId, periodId, districtId,userId));
            data.put("immunizationSession", getImmunizationSession(reportId, facilityId, periodId, districtId,userId));
            data.put("vitaminSupplementation", getVitaminSupplementationReport(reportId, facilityId, periodId, districtId,userId));
            data.put("adverseEffect", getAdverseEffectReport(reportId, facilityId, periodId, districtId,userId));
            data.put("coldChain", getColdChain(reportId, facilityId, periodId, districtId,userId));
            data.put("targetPopulation", getTargetPopulation(facilityId, periodId, districtId,userId));
            data.put("syringes", getSyringeAndSafetyBoxReport(reportId, facilityId, periodId, districtId,userId));
            data.put("vitamins", getVitaminsReport(reportId, facilityId, periodId, districtId,userId));
            data.put("dropOuts", getDropOuts(reportId, facilityId, periodId, districtId,userId));
        } catch (Exception ex) {

            LOGGER.warn("error while loading Reporting summary:... ", ex);

        }

        return data;
    }


    private Long getNationalZoneId() {
        return repository.getNationalZone().getId();
    }

    public Map<String, List<Map<String, Object>>> getPerformanceCoverageReportData(String periodStart, String periodEnd,
                                                                                   Long districtId, Long productId, Long doseId) {

        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();

        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        GeographicZone zone = geographicZoneService.getById(districtId);

        if (districtId == 0) {
            result.put("mainreportRegionAggregate", repository.getPerformanceCoverageMainReportDataByRegionAggregate(startDate, endDate, districtId, productId, doseId));
            result.put("summaryRegionAggregate", repository.getPerformanceCoverageSummaryReportDataByRegionAggregate(startDate, endDate, districtId, productId, doseId));
            result.put("regionPopulation", repository.getClassficationVaccinePopulationForRegion(startDate,endDate,districtId,productId,doseId));

        }

        if (zone != null && zone.getLevel().getCode().equals("dist")) {
            result.put("mainreport", repository.getPerformanceCoverageMainReportDataByDistrict(startDate, endDate, districtId, productId, doseId));
            result.put("summary", repository.getPerformanceCoverageSummaryReportDataByDistrict(startDate, endDate, districtId, productId, doseId));
            result.put("population", repository.getClassficationVaccinePopulationForFacility(startDate, endDate, districtId, productId,doseId));
        } else {
            result.put("mainreport", repository.getPerformanceCoverageMainReportDataByRegion(startDate, endDate, districtId, productId, doseId));
            result.put("summary", repository.getPerformanceCoverageSummaryReportDataByRegion(startDate, endDate, districtId, productId, doseId));
            result.put("population", repository.getClassficationVaccinePopulationForDistrict(startDate,endDate,districtId,productId,doseId));
        }

        result.put("summaryPeriodLists", getSummaryPeriodList(startDate, endDate));

        return result;

    }

    public Map<String, List<Map<String, Object>>> getCompletenessAndTimelinessReportData(String periodStart, String periodEnd,
                                                                                         Long districtId) {

        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();

        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        result.put("mainreport", repository.getCompletenessAndTimelinessMainReportDataByDistrict(startDate, endDate, districtId));
        result.put("summary", repository.getCompletenessAndTimelinessSummaryReportDataByDistrict(startDate, endDate, districtId));
        result.put("summaryPeriodLists", getSummaryPeriodList(startDate, endDate));

        return result;
    }

    public Map<String, List<Map<String, Object>>> getAdequacyLevelOfSupply(String periodStart, String periodEnd, Long districtId,
                                                                           Long productId) {


        Date startDate, endDate;

        startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
        endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();

        Map<String, List<Map<String, Object>>> result = new HashMap<>();


        result.put("bydistrict", repository.getAdequacyLevelOfSupplyByDistrict(startDate, endDate, districtId, productId));
        result.put("byregion", repository.getAdequacyLevelOfSupplyByRegion(startDate, endDate, districtId, productId));
        result.put("summaryPeriodLists", getSummaryPeriodList(startDate, endDate));

        return result;
    }

    private static List<Map<String, Object>> getSummaryPeriodList(Date startDate, Date endDate) {

        DateTime periodStart = new DateTime(startDate);
        DateTime periodEnd = new DateTime(endDate);


        int monthDiff = Months.monthsBetween(periodStart.withDayOfMonth(1), periodEnd.withDayOfMonth(1)).getMonths();

        DateTime temp = periodStart.withDayOfMonth(1);

        List<Map<String, Object>> list = new ArrayList<>();


        while (monthDiff >= 0) {

            Map<String, Object> period = new HashMap<>();
            period.put("year", temp.getYear());
            period.put("month", temp.getMonthOfYear());
            period.put("monthString", temp.toString("MMM"));

            monthDiff--;

            list.add(period);
            temp = temp.plusMonths(1);

        }

        return list;
    }

    public List<Map<String, Object>> getVaccineProductsList() {
        return this.repository.getVaccineProductsList();
    }

    public Map<String, List<Map<String, Object>>> getClassificationVaccineUtilizationPerformance(String periodStart, String periodEnd, Long zoneId, Long productId) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        boolean regionReport;
        boolean facilityReport;
        regionReport = zoneId == 0 ? true : false;
        facilityReport = dropoutRateByDistrictMapper.isDistrictLevel(zoneId) > 0 ? true : false;
        try {


            Date startDate;
            Date endDate;
            Date yearStartDate;
            startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
            endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();
            yearStartDate = getStartOfTheYearDate(startDate);
            if (regionReport) {
                result.put("regionReport", repository.getClassificationVaccineUtilizationPerformanceRegion(yearStartDate, endDate, zoneId, productId));
                result.put("regionPopulation", repository.getClassficationVaccinePopulationForRegion(yearStartDate, endDate, zoneId, productId));
            }
            if (facilityReport) {
                result.put("facilityReport", repository.getClassificationVaccineUtilizationPerformanceFacility(yearStartDate, endDate, zoneId, productId));
                result.put("population", repository.getClassficationVaccinePopulationForFacility(yearStartDate, endDate, zoneId, productId));
            } else {
                result.put("zoneReport", repository.getClassificationVaccineUtilizationPerformanceByZone(yearStartDate, endDate, zoneId, productId));
                result.put("population", repository.getClassficationVaccinePopulationForDistrict(yearStartDate, endDate, zoneId, productId));
            }


            result.put("summaryPeriodLists", getPaddingSummaryPeriodList(startDate, endDate));

        } catch (Exception ex) {
            LOGGER.warn("Error Message: ", ex);
        }
        return result;
    }

    public Map<String, List<Map<String, Object>>> getCategorizationVaccineUtilizationPerformance(String periodStart, String periodEnd, Long zoneId, Long productId) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        boolean regionReport;
        boolean facilityReport;
        regionReport = zoneId == 0 ? true : false;
        facilityReport = dropoutRateByDistrictMapper.isDistrictLevel(zoneId) > 0 ? true : false;
        try {


            Date startDate;
            Date endDate;
            Date yearStartDate;
            startDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodStart).toDate();
            endDate = DateTimeFormat.forPattern(DATE_FORMAT).parseDateTime(periodEnd).toDate();
            yearStartDate = getStartOfTheYearDate(startDate);
            if (regionReport) {
                result.put("regionReport", repository.getCategorizationVaccineUtilizationPerformanceRegion(yearStartDate, endDate, zoneId, productId));
                result.put("regionPopulation", repository.getClassficationVaccinePopulationForRegion(yearStartDate, endDate, zoneId, productId));
            }
            if (facilityReport) {
                result.put("facilityReport", repository.getCategorizationVaccineUtilizationPerformanceFacility(yearStartDate, endDate, zoneId, productId));
                result.put("population", repository.getClassficationVaccinePopulationForFacility(yearStartDate, endDate, zoneId, productId));
            } else {
                result.put("zoneReport", repository.getCategorizationVaccineUtilizationPerformanceByZone(yearStartDate, endDate, zoneId, productId));
                result.put("population", repository.getClassficationVaccinePopulationForDistrict(yearStartDate, endDate, zoneId, productId));
            }


            result.put("summaryPeriodLists", getPaddingSummaryPeriodList(startDate, endDate));

        } catch (Exception ex) {
            LOGGER.warn("Error Message: ", ex);
        }
        return result;
    }

    private static Date getStartOfTheYearDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    private static List<Map<String, Object>> getPaddingSummaryPeriodList(Date startDate, Date endDate) {
        Date yearStartDate = getStartOfTheYearDate(startDate);
        List<Map<String, Object>> paddingDateList = getSummaryPeriodList(yearStartDate, new DateTime(startDate).minusMonths(1).toDate());
        for (Map paddingMapDate : paddingDateList) {
            paddingMapDate.put("hide", "true");
        }
        List<Map<String, Object>> summaryateList = getSummaryPeriodList(startDate, endDate);
        paddingDateList.addAll(summaryateList);
        return paddingDateList;
    }
    public List<Map<String,Object>> loadYearList(){
        return repository.loadYearList();
    }
    public Map<String,String> getCoverageAndDropoutCoeffients(   Long productId){
        return repository.getCoverageAndDropoutCoeffients(productId);
    }
}
