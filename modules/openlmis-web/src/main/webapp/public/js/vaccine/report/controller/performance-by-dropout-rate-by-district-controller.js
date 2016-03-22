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

function ViewPerformanceByDropoutRateByDistrictController($scope, SettingsByKey, PerformanceByDropoutRateByDistrict,messageService, VaccineSupervisedIvdPrograms, $routeParams) {

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
    $scope.OnFilterChanged = function () {
        //console.log('period start '+ $scope.filter.periodStart);
        $scope.data = $scope.datarows = [];
        $scope.filter.facilityId = '';
        $scope.filter.geographicZoneId = $scope.filter.zone;
        $scope.filter.productId = $scope.filter.product;
        $scope.filter.periodId = 0;
        $scope.filter.programId = $scope.filter.program;
        $scope.reportType = false;

        var param = $scope.filter;

        $scope.error_message = '';
        PerformanceByDropoutRateByDistrict.get(param, function (data) {
            var reportVal;
            if (data !== undefined &&  data.PerformanceByDropoutRateList!==null && !utils.isEmpty(data.PerformanceByDropoutRateList.performanceByDropOutDistrictsList)) {

                $scope.data = data.PerformanceByDropoutRateList.performanceByDropOutDistrictsList;
                $scope.datarows = data.PerformanceByDropoutRateList.performanceByDropOutDistrictsList;
                $scope.regionrows = data.PerformanceByDropoutRateList.performanceByDropOutRegionsList;
                $scope.reportType = data.PerformanceByDropoutRateList.facillityReport;
                $scope.columnVals = data.PerformanceByDropoutRateList.columnNames;
                $scope.regionColumnVals = data.PerformanceByDropoutRateList.regionColumnsValueList;
                $scope.report = data.PerformanceByDropoutRateList;
                $scope.colValueList = data.PerformanceByDropoutRateList.columnsValueList;
                $scope.population = data.PerformanceByDropoutRateList.population;
                $scope.regionPopulation = data.PerformanceByDropoutRateList.regionPopulation;

                if (!utils.isEmpty($scope.datarows)) {

                    if ($scope.reportType === true) {
                        reportVal=findMonthValue($scope.datarows,1);
                        $scope.datarows=reportVal.reportList;
                        $scope.nonReportingDistrict=reportVal.grayCount;
                        extractPopulationInfo($scope.datarows, $scope.population, 1);
                        $scope.districtSubAggregate = aggregateSubTotal($scope.datarows, 1);

                    } else {
                        reportVal=findMonthValue($scope.datarows,2);
                        $scope.datarows=reportVal.reportList;
                        $scope.nonReportingDistrict=reportVal.grayCount;
                        extractPopulationInfo($scope.datarows, $scope.population, 2);
                        $scope.districtSubAggregate = aggregateSubTotal($scope.datarows, 2);

                    }

                }
                if (!utils.isEmpty($scope.regionrows)) {
                   var  regportVal=findMonthValue($scope.regionrows,3);
                    $scope.regionrows=regportVal.reportList;
                    $scope.nonReportingRegion=regportVal.grayCount;
                    extractPopulationInfo($scope.regionrows, $scope.regionPopulation, 3);
                    $scope.regionSubAggregate = aggregateSubTotal($scope.regionrows, 3);
                }
            }
        });
    };
    $scope.reporting = function (value) {


        if(value.generated!=='undefined' && value.generated===true){
            return  messageService.get('label.reported.no');
        }
        return messageService.get('label.reported.yes');
    };
    function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months -= d1.getMonth() + 1;
        months += d2.getMonth();
        return months <= 0 ? 0 : months;
    }

    $scope.getBackGroundColor = function (value) {
        var bgColor = '';
        if(value.generated!=='undefined' && value.generated===true){

            return  $scope.nonReporting;
        }
        if (value.bcg_mr_dropout > 20) {
            bgColor = $scope.maxTemp;
        } else if (value.bcg_mr_dropout > 10) {
            bgColor = $scope.average;
        } else if (value.bcg_mr_dropout > 5) {
            bgColor = $scope.belowAverage;
        } else {
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
        } else {
            bgColor = $scope.minTemp;
        }
        return bgColor;
    };
    $scope.getColumnNameSummary = function (value) {
        var bgColor = '';
        if (value == '4_dropoutGreaterThanHigh') {
            bgColor = 'DO >20%';
        } else if (value == '3_droOputBetweenMidAndHigh') {
            bgColor = '10% < DO <=20%';
        } else if (value == '2_dropOutBetweenMidAndMin') {
            bgColor = ' 5% < DO <=10%';
        } else {
            bgColor = 'DO <=5';
        }
        return bgColor;
    };
    $scope.calculateTotalPercentage = function (total_bcg_vaccinated, total_mr_vaccinated) {

        return total_bcg_vaccinated === 0 ? 0 : ((total_bcg_vaccinated - total_mr_vaccinated) / total_bcg_vaccinated * 100);
    };
    $scope.calculateSubTotalPercentage = function (value) {
        var dropOut;

            dropOut= utils.isEmpty(value)|| value.bcg_vaccinated === 0 ? 0 : ((value.bcg_vaccinated - value.mr_vaccinated) / value.bcg_vaccinated * 100);


        return dropOut;
    };
    $scope.concatPercentage = function (value) {

        return value + '%';
    };
    $scope.showCategory = function (index) {
        var absIndex = ($scope.pageSize * ($scope.currentPage - 1)) + index;
        return !((index > 0 ) && ($scope.colValueList.length > absIndex) && ($scope.rnr.equipmentLineItems[absIndex].equipmentCategory == $scope.rnr.equipmentLineItems[absIndex - 1].equipmentCategory));
    };
    $scope.getStartDate = function () {
        if ($scope.filter.periodType != 5) {
            var currentDate = new Date();
            var endDate;
            var startDate;
            var months = 0;
            var monthBack = 0;
            var currentDays = currentDate.getDate();
            if (currentDays <= maxReportSubmission) {
                monthBack = 1;
            }
            endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);
            startDate = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 1);

            switch ($scope.filter.periodType) {
                case '1':
                    months = startDate.getMonth() - 1;
                    break;
                case '2':
                    months = startDate.getMonth() - 3;

                    break;
                case '3':
                    months = startDate.getMonth() - 6;
                    break;
                case '4':
                    months = startDate.getMonth() - 12;
                    break;
                default :
                    months = 0;
            }
            startDate.setMonth(months);
            $scope.filter.periodStart = $.datepicker.formatDate("yy-mm-dd", startDate);
            $scope.filter.periodEnd = $.datepicker.formatDate("yy-mm-dd", endDate);

        }
    };
    $scope.getCurrentPeriodDateRange = function () {
        var d = new Date();
        var quarter = Math.floor((d.getMonth() / 3));
        var firstDate1 = new Date(d.getFullYear(), quarter * 3, 1);
        var endDate1 = new Date(firstDate1.getFullYear(), firstDate1.getMonth() + 3, 0);
        $scope.filter.periodStart = $.datepicker.formatDate("yy-mm-dd", firstDate1);
        $scope.filter.periodEnd = $.datepicker.formatDate("yy-mm-dd", endDate1);

    };
    function getStyle(className_) {

        var styleSheets = window.document.styleSheets;
        var styleSheetsLength = styleSheets.length;
        for (var i = 0; i < styleSheetsLength; i++) {
            var classes = styleSheets[i].rules || styleSheets[i].cssRules;
            var classesLength = classes.length;
            for (var x = 0; x < classesLength; x++) {

                if (classes[x].selectorText == className_) {
                    var ret;
                    if (classes[x].cssText) {
                        ret = classes[x].cssText;
                    } else {
                        ret = classes[x].style.cssText;
                    }
                    if (ret.indexOf(classes[x].selectorText) == -1) {
                        ret = classes[x].selectorText + "{" + ret + "}";
                    }
                    return ret;
                }
            }
        }
    }


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

    function extractPopulationInfo(reportList, popuplationList, type) {
        var population = 0;
        var denominator = 0;
        var i = 0;
        var repLen = reportList.length;
        var popuLen = popuplationList.length;

        for (i; i < repLen; i++) {
            var j = 0;

            var repKey = getPopulationKey(reportList[i], type) + "_" + parseInt($scope.staticYear, 10);

            for (j; j < popuLen; j++) {
                population = 0;
                denominator = 0;
                var currentKey = getPopulationKey(popuplationList[j], type) + "_" + parseInt(popuplationList[j].year, 10);

                if (repKey === currentKey) {

                    population = popuplationList[j].population;
                    denominator = popuplationList[j].denominator;

                    break;
                }

            }
            //reportList[i].population = population;
            reportList[i].target = population;
            //reportList[i].denominator = denominator;
        }
        return population;
    }

    function findMonthValue(reportList, type) {
        var formattedData = [];
        var grayCount = {};
        if (utils.isEmpty(reportList)) {
            return reportList;
        }

        if ($scope.staticYear !== '0') {
            var len = reportList.length;

            var distrctList = {};
            var periodList = utils.generatePeriodNamesWithDashForVaccineYear($scope.staticYear);
            grayCount = intializeGrayCount(periodList);
            reportList.forEach(function (value) {
                var district = getPopulationKey(value, type);
                if (!(district in distrctList)) {
                    distrctList[district] = value;
                }
            });
            for (var key in distrctList) {
                for (var i = 0; i < 12; i++) {
                    var hasValue = false;
                    for (var j = 0; j < len; j++) {
                        var date=new Date(reportList[j].period_name);

                       var reportMonth=periodList[date.getMonth()];
                        if (angular.equals(getPopulationKey(reportList[j], type), key) &&reportMonth ===periodList[i]) {
                            formattedData.push(reportList[j]);
                            hasValue = true;
                            break;
                        }
                    }
                    if (!hasValue) {
                        var object = {
                            target: distrctList[key].target,
                            //denominator: distrctList[key].denominator,
                            month: i + 1,
                            year: $scope.staticYear,
                            period_name: periodList[i],
                            region_name: distrctList[key].region_name,
                            district_name: distrctList[key].district_name,
                            facility_name: distrctList[key].facility_name,
                            bcg_vaccinated: 0,
                            mr_vaccinated:0,
                            bcg_mr_dropout:0,
                            generated: true

                        };
                        grayCount[$scope.staticYear + "_" + i].count++;
                        formattedData.push(object);
                    }
                }
            }
        } else {
            formattedData = reportList;
        }

        return {reportList: formattedData, grayCount: grayCount};

    }

    function intializeGrayCount(periods) {
        var grayCount = {};
        for (var i = 0; i < 12; i++) {
            grayCount[$scope.staticYear + "_" + i] = {count: 0};
        }

        return grayCount;
    }

    function aggregateSubTotal(reportList, type) {
        var subAggregateTotal = {};
        var len = reportList.length;
        var totalObj={target:0,bcg_vaccinated:0,mr_vaccinated:0};
        var i = 0;
        for (i; i < len; i++) {
            totalObj.target=totalObj.target+reportList[i].target;
            totalObj.bcg_vaccinated=totalObj.bcg_vaccinated+reportList[i].bcg_vaccinated;
            totalObj.mr_vaccinated=totalObj.mr_vaccinated+reportList[i].mr_vaccinated;
            var keyName = getPopulationKey(reportList[i], type);
            if (keyName in subAggregateTotal) {
                subAggregateTotal[keyName] = {
                    target: subAggregateTotal[keyName].target + reportList[i].target,
                    bcg_vaccinated: subAggregateTotal[keyName].bcg_vaccinated + reportList[i].bcg_vaccinated,
                    mr_vaccinated: subAggregateTotal[keyName].mr_vaccinated + reportList[i].mr_vaccinated
                };

            } else {

                var obj = {
                    target: reportList[i].target,
                    bcg_vaccinated: reportList[i].bcg_vaccinated,
                    mr_vaccinated: reportList[i].mr_vaccinated
                };
                subAggregateTotal[keyName] = obj;
            }
        }
        subAggregateTotal.total_row = totalObj;

        return subAggregateTotal;
    }
}
