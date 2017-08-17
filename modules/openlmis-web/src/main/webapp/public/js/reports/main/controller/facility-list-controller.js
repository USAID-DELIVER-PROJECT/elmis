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
function ListFacilitiesController($scope, FacilityList, $routeParams) {
    $scope.perioderror = "";
    $scope.allReportType = false;
    $scope.OnFilterChanged = function () {
        FacilityList.get($scope.getSanitizedParameter(), function (data) {
            $scope.data = generateParentChildReport(data.pages.rows);
            console.log($scope.data);
            $scope.paramsChanged($scope.tableParams);
        });
    };

    var generateParentChildReport = function(data) {
        val = _.uniq(data, function (item, key, a) { return item.id; });

        _.each(val, function(row){
            row.facilityProgramReportList = _.chain(data).where({id: row.id}).map(function(row){ return {'name': row.name, 'startDate': row.startDate};}).value();
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
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/facility-list/' + type + '?' + params;
        if (type === "mailing-list") {
            url = '/reports/download/facility_mailing_list/pdf?' + params;
        }

        window.open(url, '_BLANK');
    };


}
