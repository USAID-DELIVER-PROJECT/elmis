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
function AggregateConsumptionReportController($scope, $filter, $window, AggregateConsumptionReport) {

    $scope.reportTypes = [{name: 'EM', value: 'EM', label: 'Emergency'}, {name: 'RE', value: 'RE', label: 'Regular'}];
    $scope.isAll = false;
    $scope.selectAll = function () {

        if ($scope.isAll === false) {
            angular.forEach($scope.reportTypes, function (type) {
                type.checked = true;
            });
            $scope.isAll = true;
        } else {
            angular.forEach($scope.reportTypes, function (type) {
                type.checked = false;
            });
            $scope.isAll = false;
        }
        $scope.filter = {};
        $scope.filter.allReportType = true;
        $scope.OnFilterChanged();

    };


    $scope.toggleSingle = function () {
        var param = [];
        param = _.where($scope.reportTypes, {checked: true});
        if (parseInt(param.length, 10) === 2 || parseInt(param.length, 10) === 0) {
            $scope.allReportType = true;
            $scope.filter = {};
            $scope.filter.allReportType = true;
            console.log($scope.filter);
            $scope.OnFilterChanged();
        }
        else {
            $scope.filter = {};
            var param2 = _.findWhere($scope.reportTypes, {checked: true});
            if (param2.name === 'RE') {
                $scope.filter.isEmergency = false;
                $scope.allReportType = false;
                $scope.filter.allReportType = false;

            } else {
                $scope.filter.isEmergency = true;
                $scope.allReportType = false;
                $scope.filter.allReportType = false;

                // $scope.filter.allReportType = true;
            }
            $scope.OnFilterChanged();
        }
    };


    $scope.OnFilterChanged = function () {

    };

    $scope.searchReport = function () {

        var allParams = angular.extend($scope.filter, $scope.getSanitizedParameter());

        $scope.data = $scope.datarows = [];

        $scope.filter.max = 10000;
        if (allParams.period !== '' &&
            allParams.schedule !== '' &&
            allParams.products !== null &&
            allParams.program !== null
        ) {
            AggregateConsumptionReport.get(allParams, function (data) {
                if (data.pages !== undefined) {
                    $scope.data = data.pages.rows;
                    $scope.paramsChanged($scope.tableParams);
                }
            });

        }
    };
    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var url = '/reports/download/aggregate_consumption' + (($scope.filter.disaggregated === true) ? '_disaggregated' : '') + '/' + type + '?' + jQuery.param($scope.getSanitizedParameter());
        $window.open(url, '_blank');
    };
    $scope.showMoreFilters = false;

    $scope.toggleMoreFilters = function () {
        $scope.showMoreFilters = true;
    };

}
