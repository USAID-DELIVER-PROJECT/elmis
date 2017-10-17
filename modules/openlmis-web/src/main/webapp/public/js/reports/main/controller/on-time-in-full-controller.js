/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function OnTimeInFullReportController($scope, $filter, $window, OnTimeInFullReport) {

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.onTimeInFullReportParams);
        var url = '/reports/download/vaccine_on_time_in_full_report/' + type + '?' + params;
        $window.open(url, "_BLANK");
    };

    $scope.onTimeInFullReportParams ={};

    $scope.OnFilterChanged = function () {

        if (utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;

        $scope.onTimeInFullReportParams =  {

            periodStart: $scope.periodStartDate,
            periodEnd:   $scope.periodEnddate,
            facilityLevel:$scope.getSanitizedParameter().facilityLevel,
            productCategory:  $scope.getSanitizedParameter().productCategory,
            max:100000

        };

        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        //$scope.filter.max = 1000000;

        OnTimeInFullReport.get($scope.onTimeInFullReportParams, function (data) {
            console.log(data);
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

    };

    $scope.formatNumber = function (value) {
        return utils.formatNumber(value, '0,0.00');
    };


    $scope.showPopover=false;

    $scope.popover = {
        title: 'Manufacturer Name',
        message: 'Message'
    };

}
