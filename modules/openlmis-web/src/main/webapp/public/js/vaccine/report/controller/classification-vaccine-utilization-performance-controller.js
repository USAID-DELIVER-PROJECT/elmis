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

function ClassificationVaccineUtilizationPerformanceController($scope, ClassificationVaccineUtilizationPerformance, ReportProductsByProgram) {


    ReportProductsByProgram.get({programId: 82}, function (data) {
        $scope.product = data.productList;
    });

    $scope.perioderror = "";

    $scope.OnFilterChanged = function () {
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
        ClassificationVaccineUtilizationPerformance.get(
            {
                periodStart: $scope.periodStartDate,
                periodEnd: $scope.periodEnddate,
                range: $scope.range,
                zone: utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
                product: $scope.filter.product
            },

            function (data) {
                console.log(data);
                $scope.error = "";
                $scope.zonereport = cumulateMonthProgressive(data.classificationVaccineUtilizationPerformance.zoneReport, 2);
                $scope.facilityReportList = cumulateMonthProgressive(data.classificationVaccineUtilizationPerformance.facilityReport, 1);
                $scope.periodlist = data.classificationVaccineUtilizationPerformance.summaryPeriodLists;
                $scope.regionReportList = cumulateMonthProgressive(data.classificationVaccineUtilizationPerformance.regionReport, 3);
                $scope.facilityReport = !utils.isEmpty($scope.facilityReportList);
                $scope.regionReport = !utils.isEmpty($scope.regionReportList);

                if ($scope.facilityReport === true) {
                    extractPeriod($scope.facilityReportList);
                    $scope.zoneMainReport = reformatZoneReportResult($scope.facilityReportList, 1);
                    $scope.zoneSummary = getDistrictSummeryReportData($scope.facilityReportList);

                }
                else {
                    extractPeriod($scope.zonereport);
                    $scope.zoneMainReport = reformatZoneReportResult($scope.zonereport, 2);
                    $scope.zoneSummary = getDistrictSummeryReportData($scope.zonereport);
                }
                if ($scope.regionReport === true) {
                    $scope.regionMainReport = reformatZoneReportResult($scope.regionReportList, 3);
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
                periodsWithReport.push({period_name: dreport.period_name, hide: dreport.hide});
            }
        });

        $scope.periodlist = periodsWithReport;

    }

    function hasKeyPeriod(periods, period_name) {
        var len =periods.length;
        for(var i=0;i <len;i ++){

            if (periods[i].period_name === period_name) {

                return true;
            }
        }
        return false;
    }

    function reformatZoneReportResult(unformatedReportList, type) {

        var parentReport = [], childReport = [];
        var repKey;
        _.each(unformatedReportList, function (dreport) {
            repKey = getKey(dreport, type);
            if (hasKey(parentReport, repKey)) {
            } else {
                parentReport.push({report: dreport, period_class: childReport, repKey: repKey});
            }
        });

        for (var ii = 0; ii < parentReport.length; ii++) {

            var _childeren = [];
            for (var jj = 0; jj < unformatedReportList.length; jj++) {

                if (parentReport[ii].repKey === ( getKey(unformatedReportList[jj], type))
                ) {
                    _childeren.push({
                        period_name: unformatedReportList[jj].period_name,
                        classification: unformatedReportList[jj].classification,
                        total_population:unformatedReportList[jj].total_population,
                        total_vaccinated:unformatedReportList[jj].total_vaccinated,
                        vaccinated: unformatedReportList[jj].vaccinated,
                        total_used:unformatedReportList[jj].total_used,
                        used:unformatedReportList[jj].used,
                        hide: unformatedReportList[jj].hide
                    });
                    parentReport[ii].period_class = _childeren;
                }
            }
        }
        return parentReport;
    }

    function getKey(dreport, type) {
        var repKey = dreport.region_name;
        if (type == 1) {
            repKey = repKey + "_" + dreport.facility_count + "_" + dreport.geographic_zone_name + "_" + dreport.facility_name;
        } else if (type == 2) {
            repKey = dreport.region_name + "_" + dreport.facility_count + "_" + dreport.geographic_zone_name;
        }
        return repKey;
    }

    function calculateTotals() {

        var totalDistricts = 0;
        var totalFacilities = 0;
        var totalPopulation=0;
        var districts = _.pluck($scope.zonereport, 'geographic_zone_name'),
            facilities = _.pluck($scope.zonereport, 'facility_count');
        console.log("facility count");
        console.log(facilities);
        totalDistricts = _.uniq(districts).length;
        _.each($scope.zoneMainReport, function (facility) {
            totalFacilities += facility.report.facility_count;
            totalPopulation+=facility.report.population;
        });
        $scope.totalDistricts = totalDistricts;
        $scope.totalFacilities = totalFacilities;
        $scope.totalPopulation=totalPopulation;
    }


    function getDistrictSummeryReportData(unformattedReport) {
        var vaccineUtilClasses = [
            {class: 'A', displayName: 'Class_A', description: 'Good Access & Good Utilisation', classColour: '#52C552'},
            {class: 'B', displayName: 'Class_B', description: 'Good Access & Poor Utilisation', classColour: '#dce6f1'},
            {class: 'C', displayName: 'Class_C', description: 'Poor Access & Good Utilisation', classColour: '#E4E44A'},
            {class: 'D', displayName: 'Class_D', description: 'Poor Access & Poor Utilisation', classColour: '#ff0000'}

        ], arr = [], tempArr = [], classCount = 0,hideInfo=[];


        _.each(vaccineUtilClasses, function (vacClass) {
            tempArr = [];
            _.each($scope.periodlist, function (period) {
                classCount = _.where(unformattedReport, {
                    period_name: period.period_name,
                    classification: vacClass.class
                }).length;
                tempArr.push({classCount:classCount, hide:period.hide});
                hideInfo.push(period.hide);
            });
            arr.push({
                classification: vacClass.displayName,
                classDescription: vacClass.description,
                classColour: vacClass.classColour,
                classCountArray: tempArr,
                hideInfo:hideInfo
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
            return '#52C552';
        } else if (classification == 'B') {
            return '#dce6f1';
        } else if (classification == 'C') {
            return '#E4E44A';
        } else {
            return '#ff0000';
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
                coverage_rate = total_vaccinated / (total_population * target * 0.1);
                wastage_rate = 100 - usage_rate;
                unformattedReport[i].total_population = total_population;
                unformattedReport[i].total_vaccinated = total_vaccinated;
                unformattedReport[i].total_used = total_used;
                unformattedReport[i].usage_rate = usage_rate;
                unformattedReport[i].coverage_rate = coverage_rate;
                unformattedReport[i].wastage_rate = wastage_rate;
                unformattedReport[i].classification = determineClass(coverage_rate, wastage_rate);
                unformattedReport[i].hide = unformattedReport[i].year_number == $scope.year && unformattedReport[i].month_number < $scope.month ? true : false;
                console.log("district  " + getKey(unformattedReport[i], type) + "  year " + unformattedReport[i].year_number  +
                    " month " + unformattedReport[i].month_number+
                     " population " + unformattedReport[i].population+
                     " tot_population " + unformattedReport[i].total_population+
                     " usage_rate " + unformattedReport[i].usage_rate+
                     " coverage_rate " + unformattedReport[i].coverage_rate+
                     " wastage_rate " + unformattedReport[i].wastage_rate+
                     " hidden " + unformattedReport[i].hide +
                    " year " + $scope.year+
                    " month number is " + $scope.month);

            }
        }
        return unformattedReport;

    }

    function determineClass(coverage, wastage) {
        var minWastage = 70;
        var minCoverage = 90;
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


}

