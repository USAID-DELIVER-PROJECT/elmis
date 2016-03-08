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

import org.openlmis.vaccine.domain.reports.*;
import org.openlmis.vaccine.domain.reports.params.PerformanceByDropoutRateParam;

import org.openlmis.vaccine.repository.reports.PerformanceByDropoutRateByDistrictRepository;
import org.openlmis.vaccine.repository.reports.TrendMinMaxColdRangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TrendOfMinMasColdRangeService {
    @Autowired
    private TrendMinMaxColdRangeRepository repository;
    @Autowired
    private PerformanceByDropoutRateByDistrictRepository dropoutRateByDistrictRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrendOfMinMasColdRangeService.class);

    public TrendOfMinMaxColdChainTempratureReport loadTrendMinMaxColdChainTempratureReports(
            Map<String, String[]> filterCriteria
    ) {
        TrendOfMinMaxColdChainTempratureReport coldChainTempratureReport;
        boolean isFacilityReport;
        boolean isRegionReport;
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList;
        List<TrendMinMaxColdChainColumnRangeValues> districtFacilitySummaryColumnList;
        List<TrendMinMaxColdChainColumnRangeValues> regionSummaryColumnList = null;
        PerformanceByDropoutRateParam filterParam;
        filterParam = ReportsCommonUtilService.prepareParam(filterCriteria);
        isRegionReport = filterParam.getGeographic_zone_id() == 0 ? true : false;
        isFacilityReport = dropoutRateByDistrictRepository.isDistrictLevel(filterParam.getGeographic_zone_id());

        List<Date> columnNameList = ReportsCommonUtilService.extractColumnValues(filterParam);
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailListAggeregateForDistrict;
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailListAggeregateForRegion;
        List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailListRegion = null;
        if (isFacilityReport) {
            coldChainTempratureDetailList = repository.loadTrendMinMaxColdChainTempratureReports(filterParam);

            coldChainTempratureDetailListAggeregateForRegion = coldChainTempratureDetailList;
            districtFacilitySummaryColumnList = this.prepareColumnRangesForSummary(columnNameList, coldChainTempratureDetailList);
            coldChainTempratureReport = this.aggregateReport(coldChainTempratureDetailList);
        } else {
            if (isRegionReport) {
                List<TrendOfMinMaxColdChainTempratureDetail> valueList;
                valueList = this.repository.loadTrendMinMaxColdChainTempratureRegionReports(filterParam);
                coldChainTempratureDetailListRegion =
                        this.buildReportTree(valueList, ReportsCommonUtilService.REGION_REPORT);
                regionSummaryColumnList = this.prepareColumnRangesForSummary(columnNameList, valueList);

            }
            coldChainTempratureDetailListAggeregateForDistrict = this.repository.loadTrendMinMaxColdChainDistrictTempratureReports(filterParam);
            coldChainTempratureDetailListAggeregateForRegion = coldChainTempratureDetailListAggeregateForDistrict;
            districtFacilitySummaryColumnList = this.prepareColumnRangesForSummary(columnNameList, coldChainTempratureDetailListAggeregateForDistrict);
            coldChainTempratureReport = this.aggregateReport(coldChainTempratureDetailListAggeregateForDistrict);
        }

        if (coldChainTempratureReport != null) {
            coldChainTempratureReport.setColumnNames(columnNameList);
            coldChainTempratureReport.setChainTempratureDetailReportTree(coldChainTempratureDetailListAggeregateForRegion);
            coldChainTempratureReport.setChainTempratureDetailRegionReportTree(coldChainTempratureDetailListRegion);
            coldChainTempratureReport.setDistrictFacilitySummaryColumnList(districtFacilitySummaryColumnList);
            coldChainTempratureReport.setRegionSummaryColumnList(regionSummaryColumnList);
            coldChainTempratureReport.setRegionReport(isRegionReport);
            coldChainTempratureReport.setFacilityReport(isFacilityReport);
        }
        return coldChainTempratureReport;
    }

    public TrendOfMinMaxColdChainTempratureReport aggregateReport(List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList) {
        TrendOfMinMaxColdChainTempratureReport coldChainTempratureReport = null;
        if (coldChainTempratureDetailList != null && !coldChainTempratureDetailList.isEmpty()) {
            float mintemp = coldChainTempratureDetailList.get(0).getMintemp();
            float maxtemp = coldChainTempratureDetailList.get(0).getMaxtemp();
            float minepisodetemp = coldChainTempratureDetailList.get(0).getMinepisodetemp();
            float maxepisodetemp = coldChainTempratureDetailList.get(0).getMaxepisodetemp();
            Long targetpopulation = 0L;
            coldChainTempratureReport = new TrendOfMinMaxColdChainTempratureReport();
            for (TrendOfMinMaxColdChainTempratureDetail coldChainTempratureDetail : coldChainTempratureDetailList) {
                mintemp = mintemp > coldChainTempratureDetail.getMintemp() ? coldChainTempratureDetail.getMintemp() : mintemp;
                maxtemp = maxtemp < coldChainTempratureDetail.getMaxtemp() ? coldChainTempratureDetail.getMaxtemp() : maxtemp;
                minepisodetemp = minepisodetemp > coldChainTempratureDetail.getMinepisodetemp() ? coldChainTempratureDetail.getMinepisodetemp() : minepisodetemp;
                maxepisodetemp = maxepisodetemp < coldChainTempratureDetail.getMaxepisodetemp() ? coldChainTempratureDetail.getMaxepisodetemp() : maxepisodetemp;
                targetpopulation += coldChainTempratureDetail.getTargetpopulation() == null ? 0 : coldChainTempratureDetail.getTargetpopulation();
            }
            coldChainTempratureReport.setMaxepisodetemp(maxepisodetemp);
            coldChainTempratureReport.setMinepisodetemp(minepisodetemp);
            coldChainTempratureReport.setMaxtemp(maxtemp);
            coldChainTempratureReport.setMintemp(mintemp);
            coldChainTempratureReport.setTargetpopulation(targetpopulation);
        }
        return coldChainTempratureReport;
    }

    public List<TrendOfMinMaxColdChainTempratureDetail> buildReportTree(List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailList, int reportLevel) {
        List<TrendOfMinMaxColdChainTempratureDetail> levelAggeregateColdChainTempratureDetailList = new ArrayList<>();
        List<String> levelNameList = new ArrayList<>();
        Map<String, List<TrendOfMinMaxColdChainTempratureDetail>> geoLevelAggeregatedReportMap = new HashMap<>();
        if (coldChainTempratureDetailList != null && !coldChainTempratureDetailList.isEmpty()) {
            int size = coldChainTempratureDetailList.size();
            for (int i = 0; i < size; i++) {
                String parentName = coldChainTempratureDetailList.get(i).getRegion_name();
                if (reportLevel == ReportsCommonUtilService.DISTRICT_REPORT) {
                    parentName = parentName + "_" + coldChainTempratureDetailList.get(i).getDistrict_name();
                } else if (reportLevel == ReportsCommonUtilService.FACILLITY_REPORT) {
                    parentName = parentName + "_" + coldChainTempratureDetailList.get(i).getDistrict_name() + "_" + coldChainTempratureDetailList.get(i).getFacility_name();
                }
                if (!geoLevelAggeregatedReportMap.containsKey(parentName)) {
                    levelNameList.add(parentName);
                    geoLevelAggeregatedReportMap.put(parentName, new ArrayList<TrendOfMinMaxColdChainTempratureDetail>());
                }
                geoLevelAggeregatedReportMap.get(parentName).add(coldChainTempratureDetailList.get(i));
            }
            for (String lebelName : levelNameList) {
                levelAggeregateColdChainTempratureDetailList.add(this.aggregateChildValues(geoLevelAggeregatedReportMap.get(lebelName)));
            }

        }
        return levelAggeregateColdChainTempratureDetailList;
    }

    public TrendOfMinMaxColdChainTempratureDetail aggregateChildValues(List<TrendOfMinMaxColdChainTempratureDetail> coldChainTempratureDetailChildList) {
        float mintemp = 0f;
        float maxtemp = 0f;
        float minepisodetemp = 0f;
        float maxepisodetemp = 0f;
        String regionName;
        String districtName;
        TrendOfMinMaxColdChainTempratureDetail trendOfMinMaxColdChainTempratureDetail = new TrendOfMinMaxColdChainTempratureDetail();
        if (coldChainTempratureDetailChildList != null && !coldChainTempratureDetailChildList.isEmpty()) {
            regionName = coldChainTempratureDetailChildList.get(0).getRegion_name();
            districtName = coldChainTempratureDetailChildList.get(0).getDistrict_name();

            for (TrendOfMinMaxColdChainTempratureDetail chainTempratureDetail : coldChainTempratureDetailChildList) {
                mintemp += chainTempratureDetail.getMintemp();
                maxtemp += chainTempratureDetail.getMaxtemp();
                minepisodetemp += chainTempratureDetail.getMinepisodetemp();
                maxepisodetemp += chainTempratureDetail.getMaxepisodetemp();
            }
            trendOfMinMaxColdChainTempratureDetail.setRegion_name(regionName);
            trendOfMinMaxColdChainTempratureDetail.setDistrict_name(districtName);
            trendOfMinMaxColdChainTempratureDetail.setMintemp(mintemp);
            trendOfMinMaxColdChainTempratureDetail.setMaxtemp(maxtemp);
            trendOfMinMaxColdChainTempratureDetail.setMinepisodetemp(minepisodetemp);
            trendOfMinMaxColdChainTempratureDetail.setMaxepisodetemp(maxepisodetemp);
            trendOfMinMaxColdChainTempratureDetail.setChildren(coldChainTempratureDetailChildList);
        }
        return trendOfMinMaxColdChainTempratureDetail;
    }


    public List<TrendMinMaxColdChainColumnRangeValues> prepareColumnRangesForSummary(List<Date> columnNames, List<TrendOfMinMaxColdChainTempratureDetail> trendOfMinMaxColdChainTempratureDetailList) {
        List<TrendMinMaxColdChainColumnRangeValues> trendMinMaxColdChainColumnRangeValuesList;
        Map<String, Map<Date, Float>> columnRangeValues;
        columnRangeValues = this.intializeColRangeValues(columnNames);
        for (TrendOfMinMaxColdChainTempratureDetail coldChainTempratureDetail : trendOfMinMaxColdChainTempratureDetailList) {
            Date dateString = coldChainTempratureDetail.getPeriod_name();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            Date columngName = null;
            try {
                columngName = dateFormat.parse(dateFormat.format(dateString));
            } catch (ParseException e) {
                LOGGER.warn(e.getMessage(), e);
            }
            float tempMinValue = coldChainTempratureDetail.getMintemp();
            float tempMaxValue = coldChainTempratureDetail.getMaxtemp();
            float minAlarmEpisodeValue = coldChainTempratureDetail.getMinepisodetemp();
            float maxAlarmEpisodeValue = coldChainTempratureDetail.getMaxepisodetemp();
            if (!columnRangeValues.get(ReportsCommonUtilService.MIN_TEMP_RECORDED).containsKey(columngName)) {
                columnRangeValues.get(ReportsCommonUtilService.MIN_TEMP_RECORDED).put(columngName, tempMinValue);
            } else {
                Float minTempRecorded = columnRangeValues.get(ReportsCommonUtilService.MIN_TEMP_RECORDED).get(columngName);
                if (minTempRecorded > tempMinValue) {
                    columnRangeValues.get(ReportsCommonUtilService.MIN_TEMP_RECORDED).put(columngName, tempMinValue);
                }
            }
            if (!columnRangeValues.get(ReportsCommonUtilService.MAX_TEMP_RECORDED).containsKey(columngName)) {
                columnRangeValues.get(ReportsCommonUtilService.MAX_TEMP_RECORDED).put(columngName, tempMaxValue);
            } else {
                Float maxTempRecorded = columnRangeValues.get(ReportsCommonUtilService.MAX_TEMP_RECORDED).get(columngName);
                if (maxTempRecorded < tempMaxValue) {
                    columnRangeValues.get(ReportsCommonUtilService.MAX_TEMP_RECORDED).put(columngName, tempMaxValue);
                }
            }
            if (tempMinValue < 2) {

                Float tempLessMinValue = columnRangeValues.get(ReportsCommonUtilService.TEMP_MIN).get(columngName) + 1L;
                columnRangeValues.get(ReportsCommonUtilService.TEMP_MIN).put(columngName, tempLessMinValue);
            } else if (tempMaxValue > 8) {

                Float tempGreaterMinValue = columnRangeValues.get(ReportsCommonUtilService.TEMP_GREATER_MIN).get(columngName) + 1L;
                columnRangeValues.get(ReportsCommonUtilService.TEMP_GREATER_MIN).put(columngName, tempGreaterMinValue);

            }
            if (minAlarmEpisodeValue > 2) {

                Float minAlarmLessMinValue = columnRangeValues.get(ReportsCommonUtilService.ALARM_EPISODES_LESS_MIN).get(columngName) + 1L;
                columnRangeValues.get(ReportsCommonUtilService.ALARM_EPISODES_LESS_MIN).put(columngName, minAlarmLessMinValue);
            }
            if (maxAlarmEpisodeValue > 8) {

                Float minAlarmGreaterMinValue = columnRangeValues.get(ReportsCommonUtilService.ALARM_EPISODES_GREATER_MIN).get(columngName) + 1L;
                columnRangeValues.get(ReportsCommonUtilService.ALARM_EPISODES_GREATER_MIN).put(columngName, minAlarmGreaterMinValue);
            }

        }
        for (Date dateString : columnNames) {
            if (!columnRangeValues.get(ReportsCommonUtilService.MIN_TEMP_RECORDED).containsKey(dateString)) {
                columnRangeValues.get(ReportsCommonUtilService.MIN_TEMP_RECORDED).put(dateString, 0f);
            }
            if (!columnRangeValues.get(ReportsCommonUtilService.MAX_TEMP_RECORDED).containsKey(dateString)) {
                columnRangeValues.get(ReportsCommonUtilService.MAX_TEMP_RECORDED).put(dateString, 0f);
            }
        }
        trendMinMaxColdChainColumnRangeValuesList = this.prepareColumn(columnRangeValues);
        return trendMinMaxColdChainColumnRangeValuesList;

    }

    public Map<String, Map<Date, Float>> intializeColRangeValues(List<Date> columnNameList) {
        Map<String, Map<Date, Float>> columnRangeValues = new HashMap<>();
        columnRangeValues.put(ReportsCommonUtilService.TEMP_MIN, new HashMap<Date, Float>());
        columnRangeValues.put(ReportsCommonUtilService.TEMP_GREATER_MIN, new HashMap<Date, Float>());
        columnRangeValues.put(ReportsCommonUtilService.ALARM_EPISODES_LESS_MIN, new HashMap<Date, Float>());
        columnRangeValues.put(ReportsCommonUtilService.ALARM_EPISODES_GREATER_MIN, new HashMap<Date, Float>());
        columnRangeValues.put(ReportsCommonUtilService.MIN_TEMP_RECORDED, new HashMap<Date, Float>());
        columnRangeValues.put(ReportsCommonUtilService.MAX_TEMP_RECORDED, new HashMap<Date, Float>());
        for (int i = 0; i < columnNameList.size(); i++) {
            columnRangeValues.get(ReportsCommonUtilService.TEMP_MIN).put(columnNameList.get(i), 0f);
            columnRangeValues.get(ReportsCommonUtilService.TEMP_GREATER_MIN).put(columnNameList.get(i), 0f);
            columnRangeValues.get(ReportsCommonUtilService.ALARM_EPISODES_LESS_MIN).put(columnNameList.get(i), 0f);
            columnRangeValues.get(ReportsCommonUtilService.ALARM_EPISODES_GREATER_MIN).put(columnNameList.get(i), 0f);

        }
        return columnRangeValues;
    }

    public List<TrendMinMaxColdChainColumnRangeValues> prepareColumn(Map<String, Map<Date, Float>> columnRangeValues) {
        List<TrendMinMaxColdChainColumnRangeValues> trendMinMaxColdChainColumnRangeValuesList = new ArrayList<>();
        List<String> columnKeySet = new ArrayList<>(columnRangeValues.keySet());
        Collections.sort(columnKeySet);
        Iterator<String> iterator = columnKeySet.iterator();

        while (iterator.hasNext()) {
            String keyVal = iterator.next();
            Map<Date, Float> colRangeVal = columnRangeValues.get(keyVal);
            TrendMinMaxColdChainColumnRangeValues trendMinMaxColdChainColumnRange = new TrendMinMaxColdChainColumnRangeValues();
            trendMinMaxColdChainColumnRange.setRangeName(keyVal);
            List<Date> columnsKey = new ArrayList<>(colRangeVal.keySet());
            Collections.sort(columnsKey);
            Iterator<Date> columnIteratorVal = columnsKey.iterator();
            List<TrendOfMinMaxColdRangeColumn> trendOfMinMaxColdRangeColumnList = new ArrayList<>();

            while (columnIteratorVal.hasNext()) {
                Date colName = columnIteratorVal.next();
                Float value = colRangeVal.get(colName);
                TrendOfMinMaxColdRangeColumn trendOfMinMaxColdRangeColumn = new TrendOfMinMaxColdRangeColumn();
                trendOfMinMaxColdRangeColumn.setColumnName(colName);
                trendOfMinMaxColdRangeColumn.setValue(value);
                trendOfMinMaxColdRangeColumnList.add(trendOfMinMaxColdRangeColumn);
            }
            trendMinMaxColdChainColumnRange.setColdRangeColumnValues(trendOfMinMaxColdRangeColumnList);
            trendMinMaxColdChainColumnRangeValuesList.add(trendMinMaxColdChainColumnRange);
        }
        order(trendMinMaxColdChainColumnRangeValuesList);
        return trendMinMaxColdChainColumnRangeValuesList;

    }

    private static void order(List<TrendMinMaxColdChainColumnRangeValues> trendMinMaxColdChainColumnRangeValues) {

        Collections.sort(trendMinMaxColdChainColumnRangeValues, new Comparator<TrendMinMaxColdChainColumnRangeValues>() {
            @Override
            public int compare(TrendMinMaxColdChainColumnRangeValues o1, TrendMinMaxColdChainColumnRangeValues o2) {


                String x1 = ((TrendMinMaxColdChainColumnRangeValues) o1).getRangeName();
                String x2 = ((TrendMinMaxColdChainColumnRangeValues) o2).getRangeName();

                return x1.compareTo(x2);
            }
        });
    }
}
