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

function PerformanceCoverageReportController($scope, $routeParams, PerformanceCoverage, SettingsByKey, ReportProductsByProgram,  messageService,DenominatorName ) {

    $scope.perioderror = "";
    $scope.grayCount = {};
    $scope.regionGrayCount = {};
    $scope.coverageReportParams ={};
    $scope.OnFilterChanged = function () {

        $scope.coverageReportParams =  {
            periodStart: $scope.periodStartDate,
            periodEnd: $scope.periodEnddate,
            range: $scope.range,
            district: utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
            product: $scope.filter.product,
            doseId : utils.isEmpty($scope.filter.dose) ? 0 : $scope.filter.dose
        };

         // prevent first time loading
        if (utils.isEmpty($scope.filter.product) || $scope.filter.product === "0" || utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;
        DenominatorName.get(  $scope.coverageReportParams,
        function(data){
            $scope.denominatorName=data.denominatorName;
        });
        PerformanceCoverage.get(

            $scope.coverageReportParams,

            function (data) {
                var formattedDistrictJson = [];
                var formattedRegionJson = [];
                if (data.performanceCoverage.status) {
                    $scope.error = data.performanceCoverage.status[0].error;
                    $scope.datarows = $scope.datarows = null;
                }
                else {

                    $scope.error = "";
                    $scope.datarows = data.performanceCoverage.mainreport;
                    $scope.summary = data.performanceCoverage.summary;
                    $scope.summaryRegionAggregate = data.performanceCoverage.summaryRegionAggregate;
                    $scope.dataRowsRegionAggregate = data.performanceCoverage.mainreportRegionAggregate;
                    $scope.summaryPeriodLists = data.performanceCoverage.summaryPeriodLists;
                    $scope.districtPopulation = data.performanceCoverage.population;
                    $scope.regionPopulation = data.performanceCoverage.regionPopulation;

                    if ($scope.datarows.length > 0) {
                        if (angular.isUndefined($scope.datarows[0].facility_name)) {
                            $scope.regionSelected = true;

                            extractPopulationInfo($scope.datarows, $scope.districtPopulation, 2);
                            formattedDistrictJson = findMonthValue($scope.datarows, 2);
                            $scope.datarows = formattedDistrictJson.reportList;
                            $scope.grayCount = formattedDistrictJson.grayCount;
                            if (!utils.isEmpty($scope.dataRowsRegionAggregate)) {
                                extractPopulationInfo($scope.dataRowsRegionAggregate, $scope.regionPopulation, 3);
                                formattedRegionJson = findMonthValue($scope.dataRowsRegionAggregate, 3);
                                $scope.dataRowsRegionAggregate = formattedRegionJson.reportList;
                                $scope.regionGrayCount = formattedRegionJson.grayCount;
                            }
                        }
                        else {
                            $scope.regionSelected = false;
                            extractPopulationInfo($scope.datarows, $scope.districtPopulation, 1);
                            formattedDistrictJson = findMonthValue($scope.datarows, 1);
                            $scope.datarows = formattedDistrictJson.reportList;
                            $scope.grayCount = formattedDistrictJson.grayCount;
                        }

                        populateCumulativeColumns();

                        populateCalculatedAggregateValues();
                        if ($scope.regionSelected) {
                            $scope.districtSubAggregate = aggregateSubTotal($scope.datarows, 2);
                        } else {
                            $scope.districtSubAggregate = aggregateSubTotal($scope.datarows, 1);
                        }

                    }

                    if (!utils.isEmpty($scope.dataRowsRegionAggregate)) {
                        $scope.regionSubAggregate = aggregateSubTotal($scope.dataRowsRegionAggregate, 3);
                    }

                }

                $scope.coverageSummary = getPeriodicSummaryData($scope.datarows);
                $scope.coverageRegionSummary = getPeriodicSummaryData($scope.dataRowsRegionAggregate);
            });
    };

    $scope.exportReport   = function (type){
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.coverageReportParams);
        var url = '/reports/download/performance_coverage/' + type +'?' + params;
        window.open(url);
    };

    $scope.calculateVaccinated = function (targetPopulation, vaccinated) {
        return targetPopulation !== null && targetPopulation > 0 ? parseFloat(vaccinated / targetPopulation * 100).toFixed(2) : 0;
    };

    $scope.getSubTotalRow = function (dtReport) {
        var obj;
        var type = 1;
        if ($scope.regionSelected === true) {
            type = 2;
        }
        obj = $scope.districtSubAggregate[getKeyForReport(dtReport, type)];

        return obj;
    };

    $scope.getRegionSubTotalRow = function (dtReport) {

        return $scope.regionSubAggregate[getKeyForReport(dtReport, 3)];
    };

    $scope.colors = {
        color_ninty_percent: '',
        color_80_percent: '',
        color_50_percent_above: '',
        color_50_percent_below: ''
    };




    SettingsByKey.get({key: 'VCP_GREEN'}, function (data) {

        $scope.colors.color_ninty_percent =data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_ORANGE'}, function (data) {

        $scope.colors.color_80_percent =data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_RED'}, function (data) {

        $scope.colors.color_50_percent_below = data.settings.value;
        $scope.colors.color_50_percent_above = data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_NON_REPORTING'}, function (data) {

        $scope.colors.color_non_reporting = data.settings.value;
    });
    $scope.bgColorCode = function (value) {
        var percentageCoverage = $scope.calculateVaccinated(value.target, value.vaccinated);// value.coverage;

        if (value.generated !== 'undefined' && value.generated === true) {
            return $scope.colors.color_non_reporting;
        }
        if (percentageCoverage > 90)
            return $scope.colors.color_ninty_percent;
        else if (percentageCoverage >= 80)
            return $scope.colors.color_80_percent;
        else if (percentageCoverage >= 50)
            return $scope.colors.color_50_percent_above;

        return $scope.colors.color_50_percent_below;
    };

    $scope.reporting = function (value) {

        if (value.generated !== 'undefined' && value.generated === true) {
            return messageService.get('label.reported.no');
        }
        return messageService.get('label.reported.yes');
    };

    function populateCalculatedAggregateValues() {
        var targetTotal = 0, vaccinationTotal = 0, coverage = 0, ctoatlCoverage = 0, ctotalVaccination = 0;
        var last = $scope.datarows.length - 1;
        _.each($scope.datarows, function (item) {
            targetTotal += item.target;
            vaccinationTotal += item.vaccinated;
            ctotalVaccination += item.cumulativeVacinated;

        });

        $scope.targetTotal = targetTotal;
        $scope.vaccinationTotal = vaccinationTotal;
        $scope.coverage = Math.round((targetTotal === 0 ? 0 : (vaccinationTotal / targetTotal) * 100) * 100) / 100;
        $scope.ctotalVaccination = vaccinationTotal;
        $scope.ctoatlCoverage = Math.round((targetTotal === 0 ? 0 : (vaccinationTotal / targetTotal) * 100) * 100) / 100;
    }

    function populateCumulativeColumns() {

        var pdistric_id = 0, facilityName = '', regionName = '', runningTotal = 0;
        if ($scope.dataRowsRegionAggregate !== null) {

            _.each($scope.dataRowsRegionAggregate, function (item) {
                if (angular.equals(regionName, item.region_name))
                    runningTotal += item.vaccinated;
                else
                    runningTotal = item.vaccinated;

                regionName = item.region_name;
                item.cumulativeVacinated = runningTotal;
                item.cumulativeCoverage = Math.round((item.target === 0 ? 0 : (runningTotal / (item.target * item.month) ) * 100) * 100) / 100;
            });

        }

        _.each($scope.datarows, function (item) {

            if ($scope.regionSelected) {
                if (pdistric_id === item.district_name)
                    runningTotal += item.vaccinated;
                else
                    runningTotal = item.vaccinated;

                pdistric_id = item.district_name;
            }
            else {

                if (angular.equals(facilityName, item.facility_name))
                    runningTotal += item.vaccinated;
                else
                    runningTotal = item.vaccinated;

                facilityName = item.facility_name;
            }
            item.cumulativeCoverage = Math.round((item.target === 0 ? 0 : (runningTotal / (item.target * item.month)) * 100) * 100) / 100;
            item.cumulativeVacinated = runningTotal;
        });
    }



    var coverageGroup = {groupOne: "G1", groupTwo: "G2", groupThree: "G3", groupFour: "G4"};

    function getPeriodicSummaryData(reportData){

        var coverageSummary = {groupOne:[], groupTwo:[], groupThree:[], groupFour: []};

        _.each($scope.summaryPeriodLists, function (period, index) {

            coverageSummary.groupOne.push( {total : {month: period.monthString, value: getReportCountByCoverage(reportData, period, coverageGroup.groupOne)}});
            coverageSummary.groupTwo.push({total : {month: period.monthString, value: getReportCountByCoverage(reportData, period, coverageGroup.groupTwo)}});
            coverageSummary.groupThree.push({total : {month: period.monthString, value: getReportCountByCoverage(reportData, period, coverageGroup.groupThree)}});
            coverageSummary.groupFour.push({total : {month: period.monthString, value: getReportCountByCoverage(reportData, period, coverageGroup.groupFour)}});

        });

        return coverageSummary;
    }

    function getReportCountByCoverage(reportData, period, group){

       return _.filter(reportData, function(data) {

           var coverage = $scope.calculateVaccinated(data.target, data.vaccinated);

           if(angular.equals(group, coverageGroup.groupOne))
            return (data.month == period.month && data.year == period.year && !data.generated && coverage > 90);

           else if(angular.equals(group, coverageGroup.groupTwo))
               return (data.month == period.month && data.year == period.year && !data.generated && coverage < 90 && coverage >=80);

           else if(angular.equals(group, coverageGroup.groupThree))
               return (data.month == period.month && data.year == period.year && !data.generated && coverage < 80);

           else if(angular.equals(group, coverageGroup.groupFour)) // non reported periods
               return (data.month == period.month && data.year == period.year && data.generated);

       }).length;
    }

    function aggregateSubTotal(reportList, type) {
        var subAggregateTotal = [];
        var len = reportList.length;
        var i = 0;
        for (i; i < len; i++) {
            var keyName = getKeyForReport(reportList[i], type);
            if (keyName in subAggregateTotal) {
                subAggregateTotal[keyName] = {
                    target: subAggregateTotal[keyName].target + reportList[i].target,
                    vaccinated: subAggregateTotal[keyName].vaccinated + reportList[i].vaccinated,
                    cumulativeVacinated: subAggregateTotal[keyName].cumulativeVacinated + reportList[i].cumulativeVacinated
                };

            } else {

                var obj = {
                    target: reportList[i].target,
                    vaccinated: reportList[i].vaccinated,
                    cumulativeVacinated: reportList[i].cumulativeVacinated
                };
                subAggregateTotal[keyName] = obj;
            }
        }

        return subAggregateTotal;
    }

    function getKeyForReport(dreport, type) {

        var keyValue = '';
        if (type === 1) {
            keyValue = dreport.region_name + "_" + dreport.district_name + "_" + dreport.facility_name;
        } else if (type === 2) {
            keyValue = dreport.region_name + "_" + dreport.district_name;
        }
        else {
            keyValue = dreport.region_name;
        }
        return keyValue;
    }

    function getPopulationKey(dreport, type) {
        var keyValue = '';
        if (type === 1) {
            keyValue = dreport.facility_name + "_" + parseInt(dreport.year, 10);
        } else if (type === 2) {
            keyValue = dreport.district_name + "_" + parseInt(dreport.year, 10);
        }
        else {
            keyValue = dreport.region_name + "_" + parseInt(dreport.year, 10);
        }

        return keyValue;
    }

    function extractPopulationInfo(reportList, popuplationList, type) {
        var population = 0;
        var denominator = 0;
        var i = 0;
        var repLen = reportList.length;
        var popuLen = popuplationList.length;

        for (i; i < repLen; i++) {
            var j = 0;
            var repKey = getPopulationKey(reportList[i], type);
            for (j; j < popuLen; j++) {
                population = 0;
                denominator = 0;
                var currentKey = getPopulationKey(popuplationList[j], type);

                if (angular.equals(repKey, currentKey)) {
                    population = popuplationList[j].population;
                    denominator = popuplationList[j].denominator;

                    break;
                }

            }
            reportList[i].population = population;
            reportList[i].target = denominator;
            reportList[i].denominator = denominator;
        }
        return population;
    }

    function findMonthValue(reportList, type) {
        var formattedData = [];
        var date= new Date($scope.periodEnddate);
        var indexValue=date.getMonth()+1;
        var grayCount = {};
        if (utils.isEmpty(reportList)) {
            return reportList;
        }
        if ($scope.staticYear !== '0') {
            var len = reportList.length;

            var distrctList = {};
            var periodList = utils.generatePeriodNamesForVaccineYear($scope.staticYear);
            grayCount = intializeGrayCount(12);
            reportList.forEach(function (value) {
                var district = getPopulationKey(value, type);
                if (!(district in distrctList)) {
                    distrctList[district] = value;
                }
            });
            for (var key in distrctList) {
                for (var i = 0; i < indexValue; i++) {
                    var hasValue = false;
                    for (var j = 0; j < len; j++) {
                        if (angular.equals(getPopulationKey(reportList[j], type), key) && reportList[j].month === i + 1) {
                            formattedData.push(reportList[j]);
                            hasValue = true;
                            break;
                        }
                    }
                    if (!hasValue) {
                        var object = {
                            target: distrctList[key].target,
                            denominator: distrctList[key].denominator,
                            month: i + 1,
                            year: $scope.staticYear,
                            period_name: periodList[i],
                            region_name: distrctList[key].region_name,
                            district_name: distrctList[key].district_name,
                            facility_name: distrctList[key].facility_name,
                            vaccinated: 0,
                            generated: true

                        };
                        grayCount[$scope.staticYear + "_" + i].count++;
                        formattedData.push(object);
                    }
                }
            }
        } else {
            formattedData = reportList;
            grayCount = intializeGrayCount(1);
        }

        return {reportList: formattedData, grayCount: grayCount};

    }

    function intializeGrayCount(periods) {
        var grayCount = {};
        for (var i = 0; i < periods; i++) {
            grayCount[$scope.staticYear + "_" + i] = {count: 0};
        }
        return grayCount;
    }
}
