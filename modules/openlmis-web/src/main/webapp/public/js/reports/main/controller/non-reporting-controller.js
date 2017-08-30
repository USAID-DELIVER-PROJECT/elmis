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
function NonReportingController($scope, NonReportingFacilities, ngTableParams) {

    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;
        NonReportingFacilities.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {

                $scope.summaries = data.pages.rows[0].summary;
                $scope.periodListForChart = data.pages.rows[0].keyValueSummary.periodsForChart;
                $scope.data = _.select(data.pages.rows[0].details, {'reportingStatus': 'NON_REPORTING'});
                $scope.reportingStatus = data.pages.rows[0].details;

                $scope.nonReportingStackedChartData = [];
                $scope.nonReportingFacilitiesPieChartData = [
                    {
                        data: getOrderedTransformedCountByMonthChartData({'reportingStatus': 'REPORTED'}),
                        label: "Reported",
                        code: "REPORTED",
                        color: 'green'
                    },
                    {
                        data: getOrderedTransformedCountByMonthChartData({'reportingStatus': 'NON_REPORTING'}),
                        label: "Non-Reported",
                        code: "NON_REPORTING",
                        color: 'red'
                    }
                ];

                $scope.ticks = _.chain( $scope.periodListForChart)
                    .map(function(item){return [item.rank, item.name]; } )
                    .value();

                $scope.initChart();
                $("#non-reporting-facilities-summary").bind("plotclick", $scope.populateReportingStatusByFacilityData);
                $("#non-reporting-facilities-summary").bind("plothover", $scope.hover);

                $scope.paramsChanged($scope.tableParams);

                $scope.summary = {
                    nonReporting: _.findWhere($scope.summaries, {name: 'TOTAL_NON_REPORTING'}).count,
                    reporting: _.findWhere($scope.summaries, {name: 'REPORTING_FACILITIES'}).count
                };

                $scope.summary.total = parseInt($scope.summary.nonReporting,10) +
                    parseInt($scope.summary.reporting,10);
            }
        });
    };

    function getOrderedTransformedCountByMonthChartData(reportTypeFilter){
        return _.chain($scope.reportingStatus)
            .select(reportTypeFilter)
            .map(function(item){return {order:$scope.getPeriodOrder(item.period) , period:item.period }; })
            .countBy('order')
            .map(function(key, value) { return [parseInt(value, 10), key]; } )
            .value();
    }

    $scope.getPeriodOrder = function(periodName){
        var periodOrder =  _.findWhere($scope.periodListForChart, {name : periodName}) ;
        return periodOrder !== undefined ?  periodOrder.rank : -1;
    };

    $scope.populateReportingStatusByFacilityData = function(event, pos, item){
        if(item) {

            $scope.modalData = _.chain($scope.reportingStatus)
                .select({'reportingStatus': item.series.code})
                .map(function (data) {
                    data.order = $scope.getPeriodOrder(data.period);
                    return data;
                })
                .select({'order': item.datapoint[0]}).value();

            $scope.tableParamsModal = new ngTableParams({
                page: 1,
                total: $scope.modalData.length,
                count: 50
            });

            $scope.paramsChanged($scope.tableParamsModal);

            $scope.title = (item.series.code === 'REPORTED') ? 'Reporting Facilities' :
                'Non Reporting Facilities';

            $scope.successModal = true;
        }
    };

    $scope.exportReport = function (type) {
        var paramString = jQuery.param($scope.filter);
        var url = '/reports/download/non_reporting/' + type + '?' + paramString;
        window.open(url, "_BLANK");
    };

    $scope.hover = function(event, pos, item){
            if (item) {
                $("#tooltip").html(item.series.label + " : " +
                    (item.datapoint[1].toFixed(2) - item.datapoint[2].toFixed(2)))
                    .css({top: item.pageY+5, left: item.pageX+5})
                    .fadeIn(200);
            } else {
                $("#tooltip").hide();
            }
    };

    $scope.initChart = function () {

        $scope.nonReportingReportSummaryPieChartOption = {
            xaxis: {
                minTickSize: 9,
                ticks: $scope.ticks
            },
            series: {
                bars: {
                    show: true,
                    barWidth: 0.9,
                    align: "center"
                },
                stack: true
            },
            grid: {
                clickable: true,
                hoverable: true
            }
        };
    };

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }
}
