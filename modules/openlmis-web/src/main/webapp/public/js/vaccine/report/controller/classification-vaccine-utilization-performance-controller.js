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
                $scope.zonereport = data.classificationVaccineUtilizationPerformance.zoneReport;
                $scope.facilityReportList = data.classificationVaccineUtilizationPerformance.facilityReport;
                $scope.periodlist = data.classificationVaccineUtilizationPerformance.summaryPeriodLists;
                $scope.regionReportList = data.classificationVaccineUtilizationPerformance.regionReport;
                $scope.facilityReport = !utils.isEmpty($scope.facilityReportList);
                $scope.regionReport = !utils.isEmpty($scope.regionReportList);

                if ($scope.facilityReport === true) {
                    $scope.zoneMainReport = reformatZoneReportResult($scope.facilityReportList, 1);
                    $scope.zoneSummary = getDistrictSummeryReportData($scope.facilityReportList);
                }
                else {
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
    function reformatZoneReportResult(unformatedReportList, type) {

        var parentReport = [], childReport = [], periodsWithReport = [];
        var repKey;
        _.each(unformatedReportList, function (dreport) {
            repKey = getKey(dreport, type);
            if (hasKey(parentReport, repKey)) {
            } else {
                parentReport.push({report: dreport, period_class: childReport, repKey: repKey});
            }
            periodsWithReport.push(dreport.period_name);
        });
        periodsWithReport = _.uniq(periodsWithReport);
        $scope.periodlist = periodsWithReport;

        for (var ii = 0; ii < parentReport.length; ii++) {
            var _childeren = [];
            for (var jj = 0; jj < unformatedReportList.length; jj++) {
                if (parentReport[ii].repKey === ( getKey(unformatedReportList[jj], type))
                ) {
                    _childeren.push({
                        period_name: unformatedReportList[jj].period_name,
                        classification: unformatedReportList[jj].classification
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
        var districts = _.pluck($scope.zonereport, 'geographic_zone_name'),
            facilities = _.pluck($scope.zonereport, 'facility_count');
        console.log("facility count");
        console.log(facilities);
        totalDistricts = _.uniq(districts).length;
        _.each($scope.zoneMainReport, function (facility) {
            totalFacilities += facility.report.facility_count;
        });
        $scope.totalDistricts = totalDistricts;
        $scope.totalFacilities = totalFacilities;
    }


    function getDistrictSummeryReportData(unformattedReport) {
        var vaccineUtilClasses = [
            {class: 'A', displayName: 'Class A', description: 'Good Access & Good Utilisation', classColour: '#52C552'},
            {class: 'B', displayName: 'Class B', description: 'Good Access & Poor Utilisation', classColour: '#dce6f1'},
            {class: 'C', displayName: 'Class C', description: 'Poor Access & Good Utilisation', classColour: '#E4E44A'},
            {class: 'D', displayName: 'Class D', description: 'Poor Access & Poor Utilisation', classColour: '#ff0000'}

        ], arr = [], tempArr = [], classCount = 0;


        _.each(vaccineUtilClasses, function (vacClass) {
            tempArr = [];
            _.each($scope.periodlist, function (period) {
                classCount = _.where(unformattedReport, {
                    period_name: period,
                    classification: vacClass.class
                }).length;
                tempArr.push(classCount);
            });
            arr.push({
                classification: vacClass.displayName,
                classDescription: vacClass.description,
                classColour: vacClass.classColour,
                classCountArray: tempArr
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

}

