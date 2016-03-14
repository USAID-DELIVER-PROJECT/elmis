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

function PerformanceCoverageReportController($scope, $routeParams, PerformanceCoverage, Settings, ReportProductsByProgram, TreeGeographicZoneList, messageService, GetUserUnassignedSupervisoryNode) {

    ReportProductsByProgram.get({programId: 82}, function (data) {
        $scope.product = data.productList;
    });

    $scope.perioderror = "";

    $scope.OnFilterChanged = function () {

        // prevent first time loading
        if (utils.isEmpty($scope.filter.product) || utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;

        PerformanceCoverage.get(
            {
                periodStart: $scope.periodStartDate,
                periodEnd: $scope.periodEnddate,
                range: $scope.range,
                district: utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
                product: $scope.filter.product
            },

            function (data) {

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
                            extractPopulationInfo($scope.datarows, $scope.districtPopulation ,2);
                            extractPopulationInfo($scope.dataRowsRegionAggregate, $scope.regionPopulation ,3);
                        }
                        else {
                            $scope.regionSelected = false;

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

                    // Get a unique periods for the header
                    var uniquePeriods = _.chain($scope.summary).indexBy("period").values().value();
                    $scope.sortedPeriods = _.sortBy(uniquePeriods, function (up) {
                        return up.row;
                    });


                    if ($scope.summary !== null) {
                        $scope.g1 = pivotResultSet($scope.summary, "G1");
                        $scope.g2 = pivotResultSet($scope.summary, "G2");
                        $scope.g3 = pivotResultSet($scope.summary, "G3");
                        $scope.g4 = pivotResultSet($scope.summary, "G4");
                    }

                    if ($scope.summaryRegionAggregate !== null) {
                        $scope.gAll1 = pivotResultSet($scope.summaryRegionAggregate, "G1");
                        $scope.gAll2 = pivotResultSet($scope.summaryRegionAggregate, "G2");
                        $scope.gAll3 = pivotResultSet($scope.summaryRegionAggregate, "G3");
                        $scope.gAll4 = pivotResultSet($scope.summaryRegionAggregate, "G4");

                    }
                }

            });
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

    Settings.get({}, function (data) {

        _.each(data.settings.list, function (item) {

            if (item.key === "VCP_GREEN")
                $scope.colors.color_ninty_percent = item.value;
            else if (item.key === "VCP_BLUE")
                $scope.colors.color_80_percent = item.value;
            else if (item.key === "VCP_ORANGE")
                $scope.colors.color_50_percent_above = item.value;
            else if (item.key === "VCP_RED")
                $scope.colors.color_50_percent_below = item.value;
        });
    });


    $scope.bgColorCode = function (percentageCoverage) {
        if (percentageCoverage > 90)
            return $scope.colors.color_ninty_percent;
        else if (percentageCoverage >= 80)
            return $scope.colors.color_80_percent;
        else if (percentageCoverage >= 50)
            return $scope.colors.color_50_percent_above;

        return $scope.colors.color_50_percent_below;
    };

    function populateCalculatedAggregateValues() {
        var targetTotal = 0, vaccinationTotal = 0, coverage = 0, ctoatlCoverage = 0, ctotalVaccination = 0;
var last = $scope.datarows.length-1;
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
                if (pdistric_id === item.district_id)
                    runningTotal += item.vaccinated;
                else
                    runningTotal = item.vaccinated;

                pdistric_id = item.district_id;
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

    /*function pivotResultSet(summary, sortedUniquePeriods, group){

     var temp = [];

     _.each(sortedUniquePeriods, function(item) {

     for(i=0; i<summary.length; i++)
     {
     if(summary[i].group === group && item.period == summary[i].period) {
     temp.push({row:item.row, period:summary[i].period, group:group, total:summary[i].total});
     break;
     }
     // if no match is found add a dummy object as a place holder
     else if(i+1 == $scope.summary.length) {
     temp.push({row:item.row, period: item.period, group:group, total:'-'});
     }
     }
     });
     return temp;
     }*/


    function pivotResultSet(summary, group) {

        var temp = [];

        _.each($scope.summaryPeriodLists, function (item, index) {

            for (i = 0; i < summary.length; i++) {

                if (summary[i].group === group && item.year == summary[i].year && item.month == summary[i].month) {
                    temp.push({row: item.row, period: summary[i].period, group: group, total: summary[i].total});
                    break;
                }
                // if no match is found add a dummy object as a place holder
                else if (i + 1 == summary.length) {
                    temp.push({row: item.row, period: item.monthString + " " + item.year, group: group, total: '-'});
                }
            }
        });

        return temp;
    }

    // ================need to find to reuse the report filter =====/
    TreeGeographicZoneList.get(function (data) {
        $scope.zones = data.zone;
    });

    GetUserUnassignedSupervisoryNode.get({
        program: 82
    }, function (data) {
        $scope.user_geo_level = "-- All Regions/Districts---";//messageService.get('report.filter.all.geographic.zones');
        if (!angular.isUndefined(data.supervisory_nodes)) {
            if (data.supervisory_nodes === 0)
                $scope.user_geo_level = messageService.get('report.filter.national');
        }
    });

    //+====================================================

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
            keyValue = dreport.facility_name + "_" +parseInt( dreport.year,10);
        } else if (type === 2) {
            keyValue = dreport.district_name + "_" +parseInt( dreport.year,10);
        }
        else {
            keyValue = dreport.region_name + "_" +parseInt( dreport.year,10);
        }

        return keyValue;
    }

    function extractPopulationInfo(reportList, popuplationList, type) {
        var population = 0;
        var denominator=0;
        var i = 0;

        var repLen = reportList.length;
        var popuLen = popuplationList.length;

        for (i; i < repLen; i++) {
            var j = 0;
            var repKey = getPopulationKey(reportList[i], type);
            for (j; j < popuLen; j++) {
                population=0;
                denominator=0;
                var currentKey=getPopulationKey(popuplationList[j], type);

                if (angular.equals(repKey, currentKey)) {
                    population = popuplationList[j].population;
                    denominator=popuplationList[j].denominator;

                    break;
                }

            }
            reportList[i].population=population;
            reportList[i].target=denominator;
            reportList[i].denominator=denominator;
        }
        return population;
    }

}
