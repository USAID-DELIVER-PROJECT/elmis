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

package org.openlmis.report.service;

import org.apache.commons.lang.StringUtils;
import org.openlmis.report.model.params.PerformanceByDropoutRateParam;
import org.openlmis.report.model.report.vaccine.*;
import org.openlmis.report.repository.PerformanceByDropoutRateByDistrictRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class PerformanceByDropoutRateByDistrictService {
    @Autowired
    private PerformanceByDropoutRateByDistrictRepository repository;
    public static final String BELOW_MIN = "1_dropoutGreaterThanHigh";
    public static final String MIN = "2_dropOutBetweenMidAndMin";
    public static final String AVERAGE = "3_droOputBetweenMidAndHigh";
    public static final String HIGHER = "4_dropoutGreaterThanHigh";
    public static final String NONREPORTING = "5_nonReporting";
    public static final String DISTRICT_LEVEL = "";
    public static final int REGION_REPORT = 1;
    public static final int DISTRICT_REPORT = 2;
    public static final int FACILLITY_REPORT = 3;
    public static final Long DTP_PRODUCT_ID = 2421L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceByDropoutRateByDistrictService.class);


    public PerformanceByDisrictReport loadPerformanceByDropoutRateDistrictReports(Map<String, String[]> filterCriteria, Long userId) {
        boolean isFailityReport;
        boolean isRegionReport;
        PerformanceByDisrictReport performanceByDisrictReport;
        Map<String, Map<Date, Long>> columnRangeValues;
        Map<String, Map<Date, Long>> regionColumnRangeValues;
        List<Date> columnNames;
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList;
        List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByRegionList = null;
        Date startDate = null;
        Date endDate = null;
        PerformanceByDropoutRateParam filterParam;
        filterParam = prepareParam(filterCriteria);
        columnNames = ReportsCommonUtilService.extractColumnValues(filterParam);
        filterParam.setUserId(userId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            startDate = filterParam.getPeriod_start_date() != null ? dateFormat.parse(filterParam.getPeriod_start_date()) : null;
            startDate = filterParam.getPeriod_end_date() != null ? dateFormat.parse(filterParam.getPeriod_end_date()) : null;
        } catch (Exception ex) {
            LOGGER.warn("error while formatting date ", ex);
        }

        isRegionReport = filterParam.getGeographic_zone_id() == 437 ? true : false;

        isFailityReport = repository.isDistrictLevel(filterParam.getGeographic_zone_id());
        if (!isFailityReport) {
            performanceByDropoutRateByDistrictList = repository.loadPerformanceByDropoutRateDistrictReports(filterParam);

            if (filterParam.getProduct_id().equals(DTP_PRODUCT_ID)) {
                performanceByDropoutRateByDistrictList = this.transposeDptVAlueToBg(performanceByDropoutRateByDistrictList);
            }
            generateRowForNonReportingPeriods(columnNames, performanceByDropoutRateByDistrictList, DISTRICT_REPORT);
            if (isRegionReport) {
                performanceByDropoutRateByRegionList = repository.loadPerformanceByDropoutRateRegionReports(filterParam);

                if (filterParam.getProduct_id().equals(DTP_PRODUCT_ID)) {
                    performanceByDropoutRateByRegionList = this.transposeDptVAlueToBg(performanceByDropoutRateByRegionList);
                }

                generateRowForNonReportingPeriods(columnNames, performanceByDropoutRateByRegionList, REGION_REPORT);
            }

        } else {

            performanceByDropoutRateByDistrictList = repository.loadPerformanceByDropoutRateFacillityReports(filterParam);
            if (filterParam.getProduct_id().equals(DTP_PRODUCT_ID)) {
                performanceByDropoutRateByDistrictList = this.transposeDptVAlueToBg(performanceByDropoutRateByDistrictList);

            }
            generateRowForNonReportingPeriods(columnNames, performanceByDropoutRateByDistrictList, FACILLITY_REPORT);
        }


        performanceByDisrictReport = this.aggregateReport(performanceByDropoutRateByDistrictList);
        if (filterParam.getProduct_id().equals(DTP_PRODUCT_ID)) {
            performanceByDisrictReport.setDtpProduct(true);
        }

        performanceByDisrictReport.setRegionReport(isRegionReport);
        performanceByDisrictReport.setFacillityReport(isFailityReport);

        columnRangeValues = this.prepareColumnRangesForSummary(columnNames, performanceByDropoutRateByDistrictList);
        if (isRegionReport) {
            regionColumnRangeValues = this.prepareColumnRangesForSummary(columnNames, performanceByDropoutRateByRegionList);

            performanceByDisrictReport.setRegionColumnsValueList(this.prepareColumn(regionColumnRangeValues));
        }
        performanceByDisrictReport.setColumnsValueList(this.prepareColumn(columnRangeValues));
        performanceByDisrictReport.setColumnNames(columnNames);
        performanceByDisrictReport.setDetailPerformanceByDropoutRateByDistrictList(performanceByDropoutRateByDistrictList);
        performanceByDisrictReport.setPerformanceByDropOutDistrictsList(performanceByDropoutRateByDistrictList);
        performanceByDisrictReport.setPerformanceByDropOutRegionsList(performanceByDropoutRateByRegionList);
        performanceByDisrictReport.setFacillityReport(isFailityReport);
        return performanceByDisrictReport;
    }

    public Map<String, Map<Date, Long>> prepareColumnRangesForSummary(List<Date> columnNames, List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList) {

        Map<String, Map<Date, Long>> columnRangeValues;
        columnRangeValues = this.intializeColRangeValues(columnNames);
        for (PerformanceByDropoutRateByDistrict performanceByDropoutRateByDistrict : performanceByDropoutRateByDistrictList) {
            Date dateString = performanceByDropoutRateByDistrict.getPeriod_name();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            Date columngName = null;
            try {
                columngName = dateFormat.parse(dateFormat.format(dateString));
            } catch (ParseException e) {
                LOGGER.warn(" error while preparing:", e);
            }
            float value = performanceByDropoutRateByDistrict.getBcg_mr_dropout();
            if (performanceByDropoutRateByDistrict.isGenerated()) {
                Long highVal = columnRangeValues.get(NONREPORTING).get(columngName) + 1L;
                columnRangeValues.get(NONREPORTING).put(columngName, highVal);
            } else if (value > 10 || value < 0) {

                Long highVal = columnRangeValues.get(HIGHER).get(columngName) + 1L;
                columnRangeValues.get(HIGHER).put(columngName, highVal);
            }  else {

                Long highVal = columnRangeValues.get(BELOW_MIN).get(columngName) + 1L;
                columnRangeValues.get(BELOW_MIN).put(columngName, highVal);
            }

        }
        return columnRangeValues;

    }

    private List<PerformanceByDropoutRateByDistrict> transposeDptVAlueToBg(List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList) {
        for (PerformanceByDropoutRateByDistrict rateByDistrict : performanceByDropoutRateByDistrictList) {
            rateByDistrict.setBcg_mr_dropout(rateByDistrict.getDtp1_dtp3_dropout());
            rateByDistrict.setBcg_vaccinated(rateByDistrict.getDtp1_vaccinated());
            rateByDistrict.setMr_vaccinated(rateByDistrict.getDtp3_vaccinated());
            rateByDistrict.setBcg_mr_dropout(rateByDistrict.getDtp1_dtp3_dropout());
            /////////////////
            rateByDistrict.setCum_bcg_mr_dropout(rateByDistrict.getCum_dtp1_dtp3_dropout());
            rateByDistrict.setCum_bcg_vaccinated(rateByDistrict.getCum_dtp1_vaccinated());
            rateByDistrict.setCum_mr_vaccinated(rateByDistrict.getCum_dtp3_vaccinated());
            rateByDistrict.setCum_bcg_mr_dropout(rateByDistrict.getCum_dtp1_dtp3_dropout());
        }
        return performanceByDropoutRateByDistrictList;
    }

    public PerformanceByDisrictReport aggregateReport(List<PerformanceByDropoutRateByDistrict> performanceByDropoutRateByDistrictList) {
        PerformanceByDisrictReport performanceByDisrictReport = new PerformanceByDisrictReport();
        Long total_target = 0L;
        Long total_bcg_vaccinated = 0L;
        Long total_dtp1_vaccinated = 0L;
        Long total_mr_vaccinated = 0L;
        Long total_dtp3_vaccinated = 0L;
        Long total_bcg_mr_dropout = 0L;
        for (PerformanceByDropoutRateByDistrict performanceByDropoutRateByDistrict : performanceByDropoutRateByDistrictList) {

            total_target = performanceByDropoutRateByDistrict.getTarget() != null ? total_target + performanceByDropoutRateByDistrict.getTarget() : total_target;
            total_bcg_vaccinated = total_bcg_vaccinated + performanceByDropoutRateByDistrict.getBcg_vaccinated();
            total_dtp1_vaccinated = total_dtp1_vaccinated + performanceByDropoutRateByDistrict.getDtp1_vaccinated();
            total_mr_vaccinated = total_mr_vaccinated + performanceByDropoutRateByDistrict.getMr_vaccinated();
            total_dtp3_vaccinated = total_dtp3_vaccinated + performanceByDropoutRateByDistrict.getDtp3_vaccinated();
            total_bcg_mr_dropout = total_bcg_mr_dropout + performanceByDropoutRateByDistrict.getBcg_vaccinated();
        }
        performanceByDisrictReport.setTotal_target(total_target);
        performanceByDisrictReport.setTotal_bcg_vaccinated(total_bcg_vaccinated);
        performanceByDisrictReport.setTotal_dtp1_vaccinated(total_dtp1_vaccinated);
        performanceByDisrictReport.setTotal_mr_vaccinated(total_mr_vaccinated);
        performanceByDisrictReport.setTotal_dtp3_vaccinated(total_dtp3_vaccinated);
        performanceByDisrictReport.setTotal_bcg_mr_dropout(total_bcg_mr_dropout);
        return performanceByDisrictReport;
    }


    public Map<String, Map<Date, Long>> intializeColRangeValues(List<Date> columnNameList) {
        Map<String, Map<Date, Long>> columnRangeValues = new HashMap<>();
        columnRangeValues.put(HIGHER, new HashMap<Date, Long>());

        columnRangeValues.put(BELOW_MIN, new HashMap<Date, Long>());
        columnRangeValues.put(NONREPORTING, new HashMap<Date, Long>());
        for (int i = 0; i < columnNameList.size(); i++) {
            columnRangeValues.get(HIGHER).put(columnNameList.get(i), 0L);

            columnRangeValues.get(BELOW_MIN).put(columnNameList.get(i), 0L);
            columnRangeValues.get(NONREPORTING).put(columnNameList.get(i), 0L);
        }
        return columnRangeValues;
    }


    public List<PerformanceByDropoutRange> prepareColumn(Map<String, Map<Date, Long>> columnRangeValues) {
        List<PerformanceByDropoutRange> performanceByDropoutColumnList = new ArrayList<>();
        List<String> columnKeySet = new ArrayList<>(columnRangeValues.keySet());
        Collections.sort(columnKeySet);
        Iterator<String> iterator = columnKeySet.iterator();

        while (iterator.hasNext()) {
            String keyVal = iterator.next();
            Map<Date, Long> colRangeVal = columnRangeValues.get(keyVal);
            PerformanceByDropoutRange performanceByDropoutRange = new PerformanceByDropoutRange();
            performanceByDropoutRange.setRangeName(keyVal);
            List<Date> columnsKey = new ArrayList<>(colRangeVal.keySet());
            Collections.sort(columnsKey);
            Iterator<Date> columnIteratorVal = columnsKey.iterator();
            List<PerformanceByDropoutColumn> performanceByDropoutColumn = new ArrayList<>();
            while (columnIteratorVal.hasNext()) {
                Date colName = columnIteratorVal.next();
                Long value = colRangeVal.get(colName);
                PerformanceByDropoutColumn performanceByDropoutColumn1 = new PerformanceByDropoutColumn();
                performanceByDropoutColumn1.setColumnName(colName.toString());
                performanceByDropoutColumn1.setPeriodDate(colName);
                performanceByDropoutColumn1.setValue(value);
                performanceByDropoutColumn1.setRangeName(keyVal);
                performanceByDropoutColumn.add(performanceByDropoutColumn1);
            }
            performanceByDropoutRange.setColumns(performanceByDropoutColumn);
            performanceByDropoutColumnList.add(performanceByDropoutRange);
        }
        order(performanceByDropoutColumnList);
        return performanceByDropoutColumnList;

    }


    public PerformanceByDropoutRateParam prepareParam(Map<String, String[]> filterCriteria) {
        PerformanceByDropoutRateParam filterParam = null;
        if (filterCriteria != null) {
            filterParam = new PerformanceByDropoutRateParam();
            filterParam.setFacility_id(filterCriteria.get("facility") == null || filterCriteria.get("facility").length <= 0 || StringUtils.isBlank(filterCriteria.get("facility")[0]) ? 0 : Long.parseLong(filterCriteria.get("facility")[0])); //defaults to 0
            filterParam.setGeographic_zone_id(filterCriteria.get("geographicZoneId") == null || StringUtils.isBlank(filterCriteria.get("geographicZoneId")[0]) ? 0 : Long.parseLong(filterCriteria.get("geographicZoneId")[0]));
            filterParam.setPeriod_end_date(filterCriteria.get("periodEnd") == null || StringUtils.isBlank(filterCriteria.get("periodEnd")[0]) ? null : filterCriteria.get("periodEnd")[0]);
            filterParam.setPeriod_start_date(filterCriteria.get("periodStart") == null || StringUtils.isBlank(filterCriteria.get("periodStart")[0]) ? null : filterCriteria.get("periodStart")[0]);
            filterParam.setProduct_id(filterCriteria.get("productId") == null || StringUtils.isBlank(filterCriteria.get("productId")[0]) ? 0 : Long.parseLong(filterCriteria.get("productId")[0]));
        }
        return filterParam;

    }

    public List<DropoutProduct> loadDropoutProductList() {

        return this.repository.loadDropoutProductList();

    }

    private static void order(List<PerformanceByDropoutRange> performanceByDropoutRangeList) {

        Collections.sort(performanceByDropoutRangeList, new Comparator<PerformanceByDropoutRange>() {
            @Override
            public int compare(PerformanceByDropoutRange o1, PerformanceByDropoutRange o2) {


                String x1 = ((PerformanceByDropoutRange) o1).getRangeName();
                String x2 = ((PerformanceByDropoutRange) o2).getRangeName();

                return x1.compareTo(x2);
            }
        });

    }

    private static void orderDistrictReportList(List<PerformanceByDropoutRateByDistrict> rateByDistrictList) {

        Collections.sort(rateByDistrictList, new Comparator<PerformanceByDropoutRateByDistrict>() {
            @Override
            public int compare(PerformanceByDropoutRateByDistrict o1, PerformanceByDropoutRateByDistrict o2) {


                String x1 = extractUniqueZoneKey(((PerformanceByDropoutRateByDistrict) o1), FACILLITY_REPORT);
                String x2 = extractUniqueZoneKey(((PerformanceByDropoutRateByDistrict) o2), FACILLITY_REPORT);

                if (x1.equals(x2)) {
                    Date x1Date = ((PerformanceByDropoutRateByDistrict) o1).getPeriod_name();
                    Date x2Date = ((PerformanceByDropoutRateByDistrict) o2).getPeriod_name();
                    return x1Date.compareTo(x2Date);
                }
                return x1.compareTo(x2);
            }
        });

    }

    private void generateRowForNonReportingPeriods(List<Date> columnNames, List<PerformanceByDropoutRateByDistrict> reportList, int zoneType) {
        List<PerformanceByDropoutRateByDistrict> formattedList = new ArrayList<>();
        List<PerformanceByDropoutRateByDistrict> zoneUniqueKeyList = extractUniqueZoneKeyList(reportList, zoneType);
        orderDistrictReportList(zoneUniqueKeyList);
        Collections.sort(columnNames);
        PerformanceByDropoutRateByDistrict generatedDropoutRateByDistrict = null;
        for (PerformanceByDropoutRateByDistrict dropoutRateByDistrict : zoneUniqueKeyList) {


            PerformanceByDropoutRateByDistrict recentDropoutRateByDistrict = null;

            for (Date colValue : columnNames) {

                String zoneKey = extractUniqueZoneKey(dropoutRateByDistrict, zoneType);
                if (!isReportingForPeriod(zoneKey, colValue, dropoutRateByDistrict, zoneType)) {
                    generatedDropoutRateByDistrict = new PerformanceByDropoutRateByDistrict();
                    generatedDropoutRateByDistrict.setPeriod_name(colValue);
                    generatedDropoutRateByDistrict.setRegion_name(dropoutRateByDistrict.getRegion_name());
                    generatedDropoutRateByDistrict.setDistrict_name(dropoutRateByDistrict.getDistrict_name());
                    generatedDropoutRateByDistrict.setFacility_name(dropoutRateByDistrict.getFacility_name());
                    generatedDropoutRateByDistrict.setBcg_vaccinated(0L);
                    generatedDropoutRateByDistrict.setMr_vaccinated(0L);
                    generatedDropoutRateByDistrict.setBcg_mr_dropout(0L);
                    generatedDropoutRateByDistrict.setDtp1_vaccinated(0L);

                    generatedDropoutRateByDistrict.setDtp3_vaccinated(0L);
                    generatedDropoutRateByDistrict.setDtp1_dtp3_dropout(0L);
                    generatedDropoutRateByDistrict.setCum_bcg_vaccinated(recentDropoutRateByDistrict != null ? recentDropoutRateByDistrict.getCum_bcg_vaccinated() : 0);
                    generatedDropoutRateByDistrict.setCum_mr_vaccinated(recentDropoutRateByDistrict != null ? recentDropoutRateByDistrict.getCum_mr_vaccinated() : 0);
                    generatedDropoutRateByDistrict.setCum_bcg_mr_dropout(recentDropoutRateByDistrict != null ? recentDropoutRateByDistrict.getCum_bcg_mr_dropout() : 0);
                    generatedDropoutRateByDistrict.setGenerated(true);
                    formattedList.add(generatedDropoutRateByDistrict);
                } else {
                    recentDropoutRateByDistrict = dropoutRateByDistrict.getPeriodValue(colValue);
                }

            }
        }
        reportList.addAll(formattedList);
        orderDistrictReportList(reportList);
    }

    private boolean isReportingForPeriod(String uniqueDistrictKey, Date columnName, PerformanceByDropoutRateByDistrict dropoutRateByDistrict, int zoneType) {
        boolean reporting = false;
        Calendar columnCalendar = Calendar.getInstance();
        Calendar reportCalendar = Calendar.getInstance();
        columnCalendar.setTime(columnName);
        for (PerformanceByDropoutRateByDistrict periodName : dropoutRateByDistrict.getReportedPeriods()) {
            reportCalendar.setTime(periodName.getPeriod_name());

            String reportZoneKey = extractUniqueZoneKey(dropoutRateByDistrict, zoneType);
            if (reportZoneKey.equals(uniqueDistrictKey) && columnCalendar.get(Calendar.YEAR) == reportCalendar.get(Calendar.YEAR) && columnCalendar.get(Calendar.MONTH) == reportCalendar.get(Calendar.MONTH)) {
                return true;
            }
        }
        return reporting;
    }

    private List<PerformanceByDropoutRateByDistrict> extractUniqueZoneKeyList(List<PerformanceByDropoutRateByDistrict> reportList, int zoneType) {
        List<PerformanceByDropoutRateByDistrict> zoneKeyList = new ArrayList<>();
        for (PerformanceByDropoutRateByDistrict disrictReport : reportList) {

            if (!zoneKeyList.contains(disrictReport)) {
                disrictReport.setReportedPeriods(new ArrayList<>());
                disrictReport.getReportedPeriods().add(disrictReport.clone());
                zoneKeyList.add(disrictReport);
            } else {
                zoneKeyList.get(zoneKeyList.indexOf(disrictReport)).getReportedPeriods().add(disrictReport.clone());
            }

        }

        return zoneKeyList;
    }

    private static String extractUniqueZoneKey(PerformanceByDropoutRateByDistrict report, int zoneType) {
        StringBuilder uniqueZoneKey = new StringBuilder();
        switch (zoneType) {
            case REGION_REPORT:
                uniqueZoneKey.append(report.getRegion_name());
                break;
            case DISTRICT_REPORT:
                uniqueZoneKey.append(report.getRegion_name()).append(" _").append(report.getDistrict_name());
                break;
            case FACILLITY_REPORT:
                uniqueZoneKey.append(report.getRegion_name()).append(" _").append(report.getDistrict_name()).append("_").append(report.getFacility_name());
                break;
        }
        return uniqueZoneKey.toString();
    }


}
