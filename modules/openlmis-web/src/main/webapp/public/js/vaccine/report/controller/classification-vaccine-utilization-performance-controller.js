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

function ClassificationVaccineUtilizationPerformanceController($scope, $routeParams, Settings, ClassificationVaccineUtilizationPerformance, ReportProductsByProgram, messageService) {


    ReportProductsByProgram.get({programId: 82}, function (data) {
        $scope.product = data.productList;
    });

    $scope.perioderror = "";

    $scope.OnFilterChanged = function () {

        // prevent first time loading
        if (utils.isEmpty($scope.filter.product) || utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;

        // clean some of the variables

        $scope.districtreport, $scope.regionalreport, $scope.periodlist = null;

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
                $scope.periodlist = data.classificationVaccineUtilizationPerformance.summaryPeriodLists;

                // reformat the result

                reformatZoneReportResult();

                // get the summery report data

                getDistrictSummeryReportData();

                // calculate the totals

                calculateTotals();


            });


    };


    function reformatZoneReportResult() {

        var parentReport = [], childReport = [], periodsWithReport = [];


        // make sure the period list that the report has and the period list in $scope.periodList are the same
        // so get their intersection and use it for the report

        // first get the list of periods with data from the zonal report
        // at the same time
        // add the child report structure

        _.each($scope.zonereport, function (dreport) {

            parentReport.push({report: dreport, period_class: childReport});
            periodsWithReport.push(dreport.period_name);
        });


        periodsWithReport = _.uniq(periodsWithReport);

        $scope.periodlist = periodsWithReport;


        // check against the original district report and add the classification to the childReport

        _.each(parentReport, function (preport) {

            _.each($scope.zonereport, function (dreport) {


                if ((preport.report.region_name == dreport.region_name) &&
                    (preport.report.facility_count == dreport.facility_count) &&
                    (preport.report.classification == dreport.classification) &&
                    (preport.report.geographic_zone_name == dreport.geographic_zone_name) &&
                    (preport.report.period_name == dreport.period_name)


                ) {
                    preport.period_class.push({
                        period_name: preport.report.period_name,
                        classification: preport.report.classification
                    });


                }

            });


        });


        // there seem to be duplicates on the reports, maybe get unique ones here

        $scope.zoneMainReport = parentReport;


    }


    function calculateTotals() {
        // var totalPopulation = 0, totalFacilities = 0;

        var totalDistricts = 0, totalFacilities = 0;

        var districts = _.pluck($scope.zonereport, 'geographic_zone_name'),
            facilities = _.pluck($scope.zonereport, 'facility_count');

        console.log("facility count");
        console.log(facilities);


        totalDistricts = _.uniq(districts).length;
        totalFacilities = _.uniq(facilities).length;

        $scope.totalDistricts = totalDistricts;
        $scope.totalFacilities = totalFacilities;


    }


    function getDistrictSummeryReportData() {


        var vaccineUtilClasses = [
            {class: 'A', displayName: 'Class A', description: 'Good Access & Good Utilisation', classColour: '#52C552'},
            {class: 'B', displayName: 'Class B', description: 'Good Access & Poor Utilisation', classColour: '#dce6f1'},
            {class: 'C', displayName: 'Class C', description: 'Poor Access & Good Utilisation', classColour: '#E4E44A'},
            {class: 'D', displayName: 'Class D', description: 'Poor Access & Poor Utilisation', classColour: '#ff0000'}

        ], arr = [], tempArr = [], classCount = 0;


        _.each(vaccineUtilClasses, function (vacClass) {

            tempArr = [];

            _.each($scope.periodlist, function (period) {

                classCount = _.where($scope.zonereport, {
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


        $scope.zoneSummary = arr;
        $scope.vaccineUtilClasses = vaccineUtilClasses;


    }


}

