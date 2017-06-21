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
function StockedOutController($scope, $window, StockedOutReport, $routeParams) {


    $scope.OnFilterChanged = function () {
        // clear old data if there was any

        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;
        $scope.filter.showStockeout = 'N';
        StockedOutReport.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });
    };
    if (!utils.isEmpty($routeParams.reportType)) {
        var reportTypes = $routeParams.reportType.split(',');
        $scope.reportTypes = {};
        reportTypes.forEach(function (reportType) {
            $scope.reportTypes[reportType] = true;
        });

    }else{
        $scope.reportTypes = {};
        $scope.reportTypes.RE = true;
    }
    ( function init(){


        $routeParams.reportType="RE";




    })();
    $scope.onToggleReportTypeAll = function () {
        if ($scope.reportTypes === undefined) {
            $scope.reportTypes = {};
        }

        $scope.reportTypes.EM = $scope.reportTypes.RE = $scope.allReportType;
        $scope.onReportTypeCheckboxChanged();
    };
    $scope.onReportTypeCheckboxChanged = function () {

        var reportType = getReportType();

        $scope.applyUrl();
        $scope.OnFilterChanged();
    };
    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/stocked_out/' + type + '?' + params;
        $window.open(url, '_BLANK');
    };


    function getReportType() {
        var reportType = null;
        _.keys($scope.reportTypes).forEach(function (key) {
            var value = $scope.reportTypes[key];
            if (value === true && (key === 'EM' || key === 'RE')) {
                utils.isNullOrUndefined(reportType) ? reportType = key : reportType += "," + key;
            } else if (value === false) {
                $scope.allReportType = false;
            }
        });
        if ($scope.filter === undefined) {
            $scope.filter = {reportType: reportType};
        } else {
            $scope.filter.reportType = reportType;
        }
        return reportType;
    }
}
