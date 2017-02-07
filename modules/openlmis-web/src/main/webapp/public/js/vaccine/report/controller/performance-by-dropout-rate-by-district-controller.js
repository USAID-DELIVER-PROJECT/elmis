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

function ViewPerformanceByDropoutRateByDistrictController($scope, SettingsByKey, PerformanceByDropoutRateByDistrict,messageService, VaccineSupervisedIvdPrograms, $routeParams,  $window) {

    $scope.customPeriod;
    $scope.products;
    $scope.report;
    $scope.error_message;
    var dtpProductId = 2421;

    var maxReportSubmission = 10;
    var maxReportSubmissionKey;

    $scope.minTemp;
    $scope.maxTemp;
    $scope.average;
    $scope.belowAverage;
    $scope.nonReporting;
    SettingsByKey.get({key: 'VCP_GREEN'}, function (data) {
        $scope.minTemp = data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_RED'}, function (data) {
        $scope.maxTemp = data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_ORANGE'}, function (data) {
        $scope.average = data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_BLUE'}, function (data) {
        $scope.belowAverage = data.settings.value;
    });
    SettingsByKey.get({key: 'VCP_NON_REPORTING'}, function (data) {

        $scope.nonReporting=data.settings.value;
    });
    function getParam(){
        return {facilityId :'',
            geographicZoneId : $scope.filter.zone.id,
            zone : $scope.filter.zone.id,
            zoneId:$scope.filter.zone.id,
            productId : $scope.filter.product,
            periodId :0,
            programId : $scope.filter.program,
            reportType : false,
            periodStart:$scope.filter.periodStart,
            periodEnd:$scope.filter.periodEnd};

    }
    $scope.OnFilterChanged = function () {
        //console.log('period start '+ $scope.filter.periodStart);
        $scope.data = $scope.datarows = [];



        $scope.error_message = '';
        if(!utils.isEmpty($scope.filter)&&!utils.isEmpty($scope.filter.periodStart)&&!utils.isEmpty($scope.filter.periodEnd)&& !utils.isEmpty($scope.filter.product)) {

            PerformanceByDropoutRateByDistrict.get(getParam(), function (data) {

                var reportVal;
                 {

                    $scope.data = data.PerformanceByDropoutRateList.performanceByDropOutDistrictsList;
                    $scope.datarows = data.PerformanceByDropoutRateList.performanceByDropOutDistrictsList;
                    $scope.regionrows = data.PerformanceByDropoutRateList.performanceByDropOutRegionsList;
                    $scope.reportType = data.PerformanceByDropoutRateList.facillityReport;
                    $scope.columnVals = data.PerformanceByDropoutRateList.columnNames;
                    $scope.regionColumnVals = data.PerformanceByDropoutRateList.regionColumnsValueList;
                    $scope.report = data.PerformanceByDropoutRateList;
                    $scope.colValueList = data.PerformanceByDropoutRateList.columnsValueList;


                    if (!utils.isEmpty($scope.datarows)) {

                        if ($scope.reportType === true) {
                            $scope.districtSubAggregate = aggregateSubTotal($scope.datarows, 1);

                        } else {
                            $scope.districtSubAggregate = aggregateSubTotal($scope.datarows, 2);

                        }

                    }
                    if (!utils.isEmpty($scope.regionrows)) {

                        $scope.regionSubAggregate = aggregateSubTotal($scope.regionrows, 3);
                    }
                }
            });
        }
    };
    $scope.reporting = function (value) {


        if(value.generated!=='undefined' && value.generated===true){
            return  messageService.get('label.reported.no');
        }
        return messageService.get('label.reported.yes');
    };

    $scope.getBackGroundColor = function (value,cumulative) {
        var bgColor = '';
        if(value.generated!=='undefined' && value.generated===true){

            return  $scope.nonReporting;
        }
        var dropout=cumulative===1?value.cum_bcg_mr_dropout:cumulative===0?value:value.bcg_mr_dropout;
        if (dropout > 10 || dropout<0) {
            bgColor = $scope.maxTemp;
        }
        else {
            bgColor = $scope.minTemp;
        }
        return bgColor;
    };
    $scope.getBackGroundColorSummary = function (value) {
        var bgColor = '';
        if (value == '4_dropoutGreaterThanHigh') {
            bgColor = $scope.maxTemp;
        } else if (value == '3_droOputBetweenMidAndHigh') {
            bgColor = $scope.average;
        } else if (value == '2_dropOutBetweenMidAndMin') {
            bgColor = $scope.belowAverage;
        }
        else if(value == '1_dropoutGreaterThanHigh') {
            bgColor = $scope.minTemp;
        }else{
            bgColor = $scope.nonReporting;
        }

        return bgColor;
    };
    $scope.getColumnNameSummary = function (value) {
        var bgColor = '';
        if (value == '4_dropoutGreaterThanHigh') {
            bgColor = 'DO >10% and DO (-ve)';
        } else if(value == '1_dropoutGreaterThanHigh') {
            bgColor = 'DO <=10 and DO (+ve)';
        }else{
            bgColor = 'Non Reporting';
        }
        return bgColor;
    };
    $scope.calculateTotalPercentage = function (total_bcg_vaccinated, total_mr_vaccinated) {

        return total_bcg_vaccinated === 0 ? 0 : ((total_bcg_vaccinated - total_mr_vaccinated) / total_bcg_vaccinated * 100);
    };
    $scope.calculateSubTotalPercentage = function (value) {
        var dropOut;

            dropOut= utils.isNullOrUndefined(value)||utils.isEmpty(value)|| value.bcg_vaccinated === 0 ? 0 : ((value.bcg_vaccinated - value.mr_vaccinated) / value.bcg_vaccinated * 100);


        return dropOut.toFixed(0);
    };
    $scope.concatPercentage = function (value) {

        return utils.isNullOrUndefined(value)?'0.00%':value + '%';
    };

    function getPopulationKey(dreport, type) {
        var keyValue = '';
        if (type === 1) {
            keyValue = dreport.facility_name;
        } else if (type === 2) {
            keyValue = dreport.district_name;
        }
        else {
            keyValue = dreport.region_name;
        }
        return keyValue;
    }

    function aggregateSubTotal(reportList, type) {
        var subAggregateTotal = {};
        var len = reportList.length;
        var totalObj={target:0,bcg_vaccinated:0,mr_vaccinated:0,cum_bcg_vaccinated:0,cum_mr_vaccinated:0};
        var i = 0;
        for (i; i < len; i++) {
            totalObj.target=totalObj.target+reportList[i].target;
            totalObj.bcg_vaccinated=totalObj.bcg_vaccinated+reportList[i].bcg_vaccinated;
            totalObj.mr_vaccinated=totalObj.mr_vaccinated+reportList[i].mr_vaccinated;
            totalObj.cum_bcg_vaccinated=totalObj.cum_bcg_vaccinated+reportList[i].cum_bcg_vaccinated;
            totalObj.cum_mr_vaccinated=totalObj.cum_mr_vaccinated+reportList[i].cum_mr_vaccinated;
            var keyName = getPopulationKey(reportList[i], type);
            if (keyName in subAggregateTotal) {
                subAggregateTotal[keyName] = {
                    target: subAggregateTotal[keyName].target + reportList[i].target,
                    bcg_vaccinated: subAggregateTotal[keyName].bcg_vaccinated + reportList[i].bcg_vaccinated,
                    mr_vaccinated: subAggregateTotal[keyName].mr_vaccinated + reportList[i].mr_vaccinated,
                    cum_bcg_vaccinated: subAggregateTotal[keyName].cum_bcg_vaccinated + reportList[i].cum_bcg_vaccinated,
                    cum_mr_vaccinated: subAggregateTotal[keyName].cum_mr_vaccinated + reportList[i].cum_mr_vaccinated
                };

            } else {

                var obj = {
                    target: reportList[i].target,
                    bcg_vaccinated: reportList[i].bcg_vaccinated,
                    mr_vaccinated: reportList[i].mr_vaccinated,
                    cum_bcg_vaccinated: reportList[i].cum_bcg_vaccinated,
                    cum_mr_vaccinated: reportList[i].cum_mr_vaccinated
                };
                subAggregateTotal[keyName] = obj;
            }
        }
        subAggregateTotal.total_row = totalObj;

        return subAggregateTotal;
    }
    $scope.exportReport = function(type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param(getParam());

        var url = '/reports/download/performance_dropout/' + type + '?' + params;
        $window.open(url, '_blank');
    };
}
