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
function DailyConsumptionReportController($scope, DailyConsumption, GeoZoneLevel, $window, $routeParams) {
    $scope.perioderror = "";
    $scope.aggregated = false;
    $scope.filter = {
        aggregated: false
    };
    $scope.all = true;
    $scope.allReportType = false;
    $scope.reportType = {
        so: true,
        us: true,
        as: true,
        os: true,
        uk: true

    };
    $scope.onToggleAll = function () {
        $scope.reportType = {
            so: $scope.all,
            us: $scope.all,
            as: $scope.all,
            os: $scope.all,
            uk: $scope.all

        };
        $scope.aggregateReport();
    };
    $scope.toggle=function () {
        $scope.aggregateReport();
    };
    function filter(data){
        var types= $scope.prepareTypeFilter();
        var filtered=  _.filter( data, function(a){
                return _.some(this,function(b){
                    return b.status === a.dailyStatus;
                });
            }, types);

        console.log("the row data is "+JSON.stringify(types));
        console.log(" the filtered data is " + JSON.stringify($scope.filtered));
        return filtered;
    }
    $scope.prepareTypeFilter = function () {
        var filter = "";
        var types = [];
        if ($scope.reportType.so) {
            types.push({status: 'SO'});
        }
        if ($scope.reportType.us) {
            types.push({status: 'US'});
        }
        if ($scope.reportType.as) {
            types.push({status:'SP'});
        }
        if ($scope.reportType.os) {
            types.push({status: 'OS'});
        }
        if ($scope.reportType.uk) {
            types.push({status: 'UK'});
        }
        return types;
    };
    $scope.filter.date = moment().format("DD/MM/YYYY");

    $scope.searchReport = function () {
        $scope.filter.max = 1000;
        GeoZoneLevel.get({zone: $scope.filter.zone}, function (data) {
            $scope.geoLevel = data.geoLevel;
        });
        DailyConsumption.get($scope.getSanitizedParameter(), function (data) {
            $scope.rowData = data.pages.rows;
            console.log(JSON.stringify(data.pages.rows));
            $scope.aggregateReport();


            console.log("the output value is " + JSON.stringify($scope.data));

        });

    };

    var generateParentChildReport = function (data) {
        var val = _.uniq(data, false, function (item) {
            return item.facilityCode;
        });

        _.each(val, function (row) {
            row.lineItems = _.chain(data).where({facilityCode: row.facilityCode}).map(function (row) {

                var dif = (moment($scope.filter.date) - row.recentDate);
                console.log('the difference ' + dif);
                return {
                    name: row.product,
                    productCode: row.productCode,
                    stockOnHand: row.stockinhand,
                    stockOnDate: row.stockOnHand,
                    recentDate: row.recentDate,
                    dif: dif,
                    amc: row.amc,
                    mos: row.mos,
                    status: row.status,
                    periodName: row.periodName,
                    periodId: row.periodId,
                    dailyStatus: row.dailyStatus
                };
            }).value();
        });
        return val;
    };
    $scope.aggregateReport = function () {
        console.log(JSON.stringify($scope.rowData));
        var data = JSON.parse(JSON.stringify($scope.rowData));
        data=filter(data);
        if ($scope.aggregated) {
            if ($scope.geoLevel.code === 'Country') {
                $scope.data = generateProvinceAggeregatedReport(data);

            } else if ($scope.geoLevel.code === 'Province' || $scope.geoLevel.code === 'District') {
                $scope.data = generateDistrictReport(data);
            }
        } else {
            $scope.data = generateParentChildReport(data);
        }

        $scope.paramsChanged($scope.tableParams);
    };
    var generateProvinceAggeregatedReport = function (data) {
        console.log('row Data ' + JSON.stringify(data));
        var val = _.uniq(data, false, function (item) {
            return item.provinceId;
        });
        console.log('unique value is ' + JSON.stringify(val));
        _.each(val, function (row) {
            row.lineItems = _.chain(data).where({provinceId: row.provinceId}).map(function (row) {
                return {
                    name: row.product,
                    productCode: row.productCode,
                    stockOnHand: row.stockinhand,
                    stockOnDate: row.stockOnHand,
                    amc: row.amc,
                    mos: row.mos,
                    status: row.status,
                    dailyStatus: row.dailyStatus,
                    periodName: row.periodName
                };
            }).value();

            row.aggregateItems = aggregateReport(row.lineItems);


        });

        return val;
    };
    var generateDistrictReport = function (data) {
        var val = _.uniq(data, false, function (item) {
            return item.district;
        });

        _.each(val, function (row) {
            row.lineItems = _.chain(data).where({district: row.district}).map(function (row) {
                return {
                    name: row.product,
                    productCode: row.productCode,
                    stockOnHand: row.stockinhand,
                    stockOnDate: row.stockOnHand,
                    amc: row.amc,
                    mos: row.mos,
                    status: row.status,
                    dailyStatus: row.dailyStatus
                };
            }).value();

            row.aggregateItems = aggregateReport(row.lineItems);


        });

        return val;
    };

    function sum(numbers) {
        return _.reduce(numbers, function (result, current) {
            return result + parseFloat(current === null || current === undefined ? 0 : current);
        }, 0);
    }

    function average(arr) {
        return _.reduce(arr, function (memo, num) {
                return memo + (num === null || num === undefined ? 0 : parseFloat(num));
            }, 0) / (arr.length === 0 ? 1 : arr.length);
    }

    var aggregateReport = function (data) {

        var result = _.chain(data)
            .groupBy("productCode")
            .map(function (value, key) {

                return {
                    name: value[0].name,
                    productCode: key,
                    stockOnHand: sum(_.pluck(value, "stockOnHand")),
                    stockOnDate: sum(_.pluck(value, "stockOnDate")),
                    amc: average(_.pluck(value, "amc")),
                    mos: average(_.pluck(value, "mos")),
                    status: value.status
                };
            })
            .value();
        return result;
    };


    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var url = '/reports/download/daily_consumption' + (($scope.filter.disaggregated === true) ? '_disaggregated' : '') + '/' + type + '?' + jQuery.param($scope.getSanitizedParameter());
        $window.open(url, '_blank');
    };


}
