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

function ClassificationVaccineUtilizationPerformanceController($scope,
                                                               ClassificationVaccineUtilizationPerformance,
                                                               ReportProductsByProgram,
                                                               colors,
                                                               CoefficientValues) {


    $scope.perioderror = "";

        $scope.exportReport   = function (type){
            $scope.filter.pdformat = 1;
            var params = jQuery.param($scope.reportParam);
            var url = '/reports/download/classification_of_vacccine_utilization/' + type +'?' + params;
            window.open(url);
        };

    $scope.OnFilterChanged = function () {

     $scope.reportParam =  {
                    periodStart: $scope.periodStartDate,
                    periodEnd: $scope.periodEnddate,
                    district: utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
                    product: $scope.filter.product,
                };

        if (utils.isEmpty($scope.filter.product) || utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;

        $scope.districtreport = null;
        $scope.regionalreport = null;
        $scope.periodlist = null;
        var startDate = new Date($scope.periodStartDate);
        var year = startDate.getFullYear();
        var month = startDate.getMonth();
        $scope.year = year;
        $scope.month = month + 1;
        $scope.colors = colors;
        CoefficientValues.get({
                product: $scope.filter.product
            },
            function (data) {

                $scope.coefficients = data.coefficients;

            });
        ClassificationVaccineUtilizationPerformance.get(
            {
                periodStart: $scope.periodStartDate,
                periodEnd: $scope.periodEnddate,
                range: $scope.range,
                zone: utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
                product: $scope.filter.product
            },

            function (data) {

                $scope.error = "";
                $scope.zonereport = data.classificationVaccineUtilizationPerformance.zoneReport;
                $scope.facilityReportList = data.classificationVaccineUtilizationPerformance.facilityReport;
                $scope.regionReportList = data.classificationVaccineUtilizationPerformance.regionReport;
                $scope.population = data.classificationVaccineUtilizationPerformance.population;
                $scope.regionPopulation = data.classificationVaccineUtilizationPerformance.regionPopulation;

                extractPopulationInfo($scope.zonereport, $scope.population, 2);
                extractPopulationInfo($scope.facilityReportList, $scope.population, 1);
                extractPopulationInfo($scope.regionReportList, $scope.regionPopulation, 3);

                $scope.zonereport = cumulateMonthProgressive($scope.zonereport, 2);
                $scope.facilityReportList = cumulateMonthProgressive($scope.facilityReportList, 1);
                $scope.periodlist = data.classificationVaccineUtilizationPerformance.summaryPeriodLists;
                $scope.regionReportList = cumulateMonthProgressive($scope.regionReportList, 3);
                $scope.facilityReport = !utils.isEmpty($scope.facilityReportList);
                $scope.regionReport = !utils.isEmpty($scope.regionReportList);


                if ($scope.facilityReport === true) {
                    extractPeriod($scope.facilityReportList);
                    $scope.zoneMainReport = reformatZoneReportResult($scope.facilityReportList, $scope.periodlist, 1);
                    $scope.zoneSummary = getDistrictSummeryReportData($scope.facilityReportList);

                }
                else {
                    extractPeriod($scope.zonereport);
                    $scope.zoneMainReport = reformatZoneReportResult($scope.zonereport, $scope.periodlist, 2);
                    $scope.zoneSummary = getDistrictSummeryReportData($scope.zonereport);
                }
                if ($scope.regionReport === true) {
                    $scope.regionMainReport = reformatZoneReportResult($scope.regionReportList, $scope.periodlist, 3);
                    $scope.regionSummary = getDistrictSummeryReportData($scope.regionReportList);
                } else {
                    $scope.regionMainReport = [];
                    $scope.regionSummary = [];
                }
                calculateTotals();
            });
    };
    function extractPeriod(unformatedReportList) {
        var periodsWithReport = [];

        _.each(unformatedReportList, function (dreport) {
            if (!hasKeyPeriod(periodsWithReport, dreport.period_name)) {
                periodsWithReport.push({
                    year_number: dreport.year_number,
                    month_number: dreport.month_number,
                    period_name: dreport.period_name,
                    hide: dreport.hide
                });
            }
        });

        var sortedList = periodsWithReport.sort(function (obj1, obj2) {

            var val = ((parseInt(obj1.year_number, 10) < parseInt(obj2.year_number, 10) ) || ((parseInt(obj1.year_number, 10) === parseInt(obj2.year_number, 10)) && ( parseInt(obj1.month_number, 10) < parseInt(obj2.month_number, 10)))) === true ? -11 : 11;

            return val;
        });

        $scope.periodlist = sortedList;

    }

    function hasKeyPeriod(periods, period_name) {
        var len = periods.length;
        for (var i = 0; i < len; i++) {

            if (periods[i].period_name === period_name) {

                return true;
            }
        }
        return false;
    }

    function reformatZoneReportResult(unformatedReportList, columns, type) {

        var parentReport = [], childReport = [];
        var repKey;
        _.each(unformatedReportList, function (dreport) {
            repKey = getKey(dreport, type);
            if (hasKey(parentReport, repKey) === false) {

                parentReport.push({report: dreport, period_class: childReport, repKey: repKey});
            }
        });

        for (var ii = 0; ii < parentReport.length; ii++) {

            var _childeren = [];
            for (var jj = 0; jj < columns.length; jj++) {

                _childeren.push(getChild(parentReport[ii].repKey, columns[jj].period_name, unformatedReportList, type, columns[jj].hide));
            }
            parentReport[ii].period_class = _childeren;


        }
        return parentReport;
    }

    function getChild(_parent, _periodName, _rawData, _dataType, _hidden) {
        var len = _rawData.length;
        var child;
        var found;

        var _index;
        for (_index = 0; _index < _rawData.length; _index++) {

            if ((_parent === getKey(_rawData[_index], _dataType)) && (_rawData[_index].period_name === _periodName)) {
                child = {
                    period_name: _rawData[_index].period_name,
                    classification: _rawData[_index].classification,
                    total_population: _rawData[_index].total_population,
                    total_vaccinated: _rawData[_index].total_vaccinated,
                    vaccinated: _rawData[_index].vaccinated,
                    total_used: _rawData[_index].total_used,
                    used: _rawData[_index].used,
                    hide: _hidden
                };

                return child;
            }

        }
        child = {
            period_name: _periodName,
            classification: '',
            total_population: 0,
            total_vaccinated: 0,
            vaccinated: 0,
            total_used: 0,
            used: 0,
            hide: _hidden
        };

        return child;
    }

    function getKey(dreport, type) {
        var repKey = dreport.region_name;
        if (type === 1) {
            repKey = repKey + "_" + dreport.geographic_zone_name + "_" + dreport.facility_name;
        } else if (type === 2) {
            repKey = dreport.region_name + "_" + dreport.geographic_zone_name;
        }
        return repKey;
    }

    function calculateTotals() {

        var totalDistricts = 0;
        var totalFacilities = 0;
        var totalPopulation = 0;
        var totalRegionPopulation = 0;
        var districts = _.pluck($scope.facilityReport ? $scope.facilityReportList : $scope.zonereport, 'geographic_zone_name');
        //facilities = _.pluck($scope.facilityReport ? $scope.facilityReportList : $scope.zonereport, 'facility_count');

        totalDistricts = _.uniq(districts).length;
        _.each($scope.zoneMainReport, function (facility) {
            totalFacilities += facility.report.facility_count;
            totalPopulation += facility.report.population;
        });
        _.each($scope.regionMainReport, function (facility) {
            totalRegionPopulation += facility.report.population;
        });
        $scope.totalDistricts = totalDistricts;
        $scope.totalFacilities = $scope.facilityReport && !utils.isEmpty($scope.zoneMainReport) ? $scope.zoneMainReport.length : totalFacilities;
        $scope.totalPopulation = totalPopulation;
        $scope.totalRegionPopulation = totalRegionPopulation;
    }


    function getDistrictSummeryReportData(unformattedReport) {
        var vaccineUtilClasses = [
            {class: 'A', displayName: 'Class_A', description: 'Good Access & Good Utilisation', classColour: $scope.colors.green_color},
            {class: 'B', displayName: 'Class_B', description: 'Good Access & Poor Utilisation', classColour: $scope.colors.blue_color},
            {class: 'C', displayName: 'Class_C', description: 'Poor Access & Good Utilisation', classColour: $scope.colors.yellow_color},
            {class: 'D', displayName: 'Class_D', description: 'Poor Access & Poor Utilisation', classColour: $scope.colors.red_color}

        ], arr = [], tempArr = [], classCount = 0, hideInfo = [];


        _.each(vaccineUtilClasses, function (vacClass) {
            tempArr = [];
            _.each($scope.periodlist, function (period) {
                classCount = _.where(unformattedReport, {
                    period_name: period.period_name,
                    classification: vacClass.class
                }).length;
                tempArr.push({classCount: classCount, hide: period.hide});
                hideInfo.push(period.hide);
            });
            arr.push({
                classification: vacClass.displayName,
                classDescription: vacClass.description,
                classColour: vacClass.classColour,
                classCountArray: tempArr,
                hideInfo: hideInfo
            });
        });
        $scope.vaccineUtilClasses = vaccineUtilClasses;
        return arr;
    }

    function hasKey(parentReport, keyVal) {
        var len = parentReport.length;
        for (var i = 0; i < len; i++) {
            if (parentReport[i].repKey === keyVal) {
                return true;
            }
        }
        return false;
    }

    $scope.getCellColor = function (classification) {
        if (classification == 'A') {
            return $scope.colors.green_color;
        } else if (classification == 'B') {
            return $scope.colors.blue_color;
        } else if (classification == 'C') {
            return $scope.colors.yellow_color;
        } else if (classification == 'D') {
            return $scope.colors.red_color;
        } else {
            return '#ffffff';
        }

    };

    function cumulateMonthProgressive(unformattedReport, type) {
        if (!utils.isEmpty(unformattedReport)) {

            var len = unformattedReport.length;
            var prevRep = unformattedReport[0];
            var formattedReportList = [];
            var total_population;
            var total_vaccinated;
            var total_used;
            var usage_rate;
            var coverage_rate;
            var wastage_rate;
            var target = 10;
            for (var i = 0; i < len; i++) {
                if (i > 0 && unformattedReport[i - 1].year_number === unformattedReport[i].year_number && getKey(unformattedReport[i - 1], type) === getKey(unformattedReport[i], type)) {
                    total_population = unformattedReport[i].population + unformattedReport[i - 1].total_population;
                    total_vaccinated = unformattedReport[i].vaccinated + unformattedReport[i - 1].total_vaccinated;
                    total_used = unformattedReport[i].used + unformattedReport[i - 1].total_used;
                } else {
                    total_population = unformattedReport[i].population;
                    total_vaccinated = unformattedReport[i].vaccinated;
                    total_used = unformattedReport[i].used;
                }
                usage_rate = total_vaccinated / total_used * 100;
                coverage_rate = total_population !== null && total_population !== 0 ? total_vaccinated / (total_population * target * 0.1) : 0;
                wastage_rate = 100 - usage_rate;
                unformattedReport[i].total_population = total_population;
                unformattedReport[i].total_vaccinated = total_vaccinated;
                unformattedReport[i].total_used = total_used;
                unformattedReport[i].usage_rate = usage_rate;
                unformattedReport[i].coverage_rate = coverage_rate;
                unformattedReport[i].wastage_rate = wastage_rate;
                unformattedReport[i].classification = determineClass(coverage_rate, wastage_rate);
                unformattedReport[i].hide = unformattedReport[i].year_number == $scope.year && unformattedReport[i].month_number < $scope.month ? true : false;

            }
        }

        return unformattedReport;

    }

    function determineClass(coverage, wastage) {
        var minWastage =  $scope.coefficients.wastage;
        var minCoverage =  $scope.coefficients.coverage;
        var classfication;
        if (coverage >= minCoverage && wastage <= minWastage) {
            classfication = "A";
        } else if (coverage < minCoverage && wastage <= minWastage) {
            classfication = "C";
        } else if (coverage >= minCoverage && wastage > minWastage) {
            classfication = "B";
        } else {
            classfication = "D";
        }
        return classfication;
    }

    function extractPopulationInfo(reportList, popuplationList, type) {

        if (utils.isNullOrUndefined(reportList) || utils.isNullOrUndefined(popuplationList)) {
            return;
        }
        var population = 0;
        var denominator = 0;
        var i = 0;


        var repLen = reportList.length;
        var popuLen = popuplationList.length;

        for (i; i < repLen; i++) {
            var j = 0;
            var repKey = type === 1 ? reportList[i].facility_name : type == 2 ? reportList[i].geographic_zone_name : reportList[i].region_name;
            repKey = repKey + "_" + parseInt(reportList[i].year_number, 10);
            for (j; j < popuLen; j++) {
                population = 0;
                denominator = 0;
                var currentKey = getPopulationKey(popuplationList[j], type) + "_" + parseInt(popuplationList[j].year, 10);


                if (repKey === currentKey) {
                    population = popuplationList[j].population;
                    denominator = popuplationList[j].denominator;

                    break;
                }

            }
            reportList[i].population = population;

        }

        return population;
    }

    function getPopulationKey(dreport, type) {
        var keyValue = '';
        if (type === 1) {
            keyValue = dreport.facility_name;
        } else if (type === 2) {
            keyValue = dreport.district_name;

        }
        else {
            keyValue = dreport.region_name;
        }

        return keyValue;
    }
}
ClassificationVaccineUtilizationPerformanceController.resolve = {
    colors: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        var color_values = {};
        $timeout(function () {
            SettingsByKey.get({key: 'VCP_GREEN'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.green_color = data.settings.value;
                } else {
                    color_values.green_color = 'green';
                }

            });
            SettingsByKey.get({key: 'VCP_BLUE'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.blue_color = data.settings.value;
                } else {
                    color_values.blue_color = 'blue';
                }

            });
            SettingsByKey.get({key: 'VCP_RED'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.red_color = data.settings.value;
                } else {
                    color_values.blue_color = 'red';
                }

            });
            SettingsByKey.get({key: 'STOCK_GREATER_THAN_BUFFER_COLOR'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.yellow_color = data.settings.value;
                } else {
                    color_values.blue_color = 'yellow';
                }

            });
            deferred.resolve(color_values);
        }, 100);

        return deferred.promise;
    }
};

