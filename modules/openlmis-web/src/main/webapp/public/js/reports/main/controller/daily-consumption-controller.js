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
function DailyConsumptionReportController($scope, DailyConsumption,$window, $routeParams) {
    $scope.perioderror = "";

    $scope.allReportType = false;
    $scope.searchReport = function () {
			$scope.filter.max=1000;
        DailyConsumption.get($scope.getSanitizedParameter(), function (data) {
            console.log(JSON.stringify(data.pages.rows));
            $scope.data = generateParentChildReport(data.pages.rows);
            console.log(JSON.stringify( $scope.data));
            $scope.paramsChanged($scope.tableParams);
        });
    };

    var generateParentChildReport = function(data) {
       var val = _.uniq(data,false, function (item) { return item.facilityCode; });
        console.log('unique value is '+JSON.stringify( val));
        _.each(val, function(row){
            row.lineItems = _.chain(data).where({facilityCode: row.facilityCode}).map(function(row){
                return {name: row.product,
                    productCode:row.productCode,
                    stockOnHand:row.stockinhand,
                    stockOnDate:row.stockOnHand,
                amc:row.amc,
                mos:row.mos,
                status:row.status,
                dailyStatus:row.dailyStatus};
            }).value();
        });
        return val;
    };

    $scope.statuses = [
        {'name': 'Active', 'value': "true"},
        {'name': 'Inactive', 'value': "false"}
    ];
    if (!utils.isEmpty($routeParams.reportType)) {
        var reportTypes = $routeParams.reportType.split(',');
        $scope.reportTypes = {};
        reportTypes.forEach(function (reportType) {
            $scope.reportTypes[reportType] = true;
        });

    } else {
        $scope.reportTypes = {};
        $scope.reportTypes.AC = true;
    }
    (function init() {


        $routeParams.statusList = "AC";


    })();
    $scope.onToggleReportTypeAll = function () {
        if ($scope.reportTypes === undefined) {
            $scope.reportTypes = {};
        }

        $scope.reportTypes.AC = $scope.reportTypes.IN = $scope.allReportType;
        $scope.onReportTypeCheckboxChanged();
    };
    $scope.onReportTypeCheckboxChanged = function () {

        var reportType = getReportType();

        $scope.applyUrl();
        $scope.OnFilterChanged();
    };
    function getReportType() {
        var reportType = null;
        _.keys($scope.reportTypes).forEach(function (key) {
            var value = $scope.reportTypes[key];
            if (value === true && (key === 'AC' || key === 'IN')) {
                utils.isNullOrUndefined(reportType) ? reportType = key : reportType += "," + key;
            } else if (value === false) {
                $scope.allReportType = false;
            }
        });
        if ($scope.filter === undefined) {
            $scope.filter = {statusList: reportType};
        } else {
            $scope.filter.statusList = reportType;
        }
        return reportType !== null ? reportType : "";
    }

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var url = '/reports/download/daily_consumption' + (($scope.filter.disaggregated === true) ? '_disaggregated' : '') + '/' + type + '?' + jQuery.param($scope.getSanitizedParameter());
        $window.open(url, '_blank');
    };


}
