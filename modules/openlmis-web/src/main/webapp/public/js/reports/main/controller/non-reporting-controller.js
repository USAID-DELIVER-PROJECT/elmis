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
        $scope.datarows = [];
        $scope.data =  $scope.datarows;
        $scope.filter.max = 10000;
        NonReportingFacilities.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {

                $scope.summaries = data.pages.rows[0].summary;
                $scope.periods = data.pages.rows[0].keyValueSummary.periodsForChart;
                $scope.chartDataRows = getDataWithRankedPeriod(data.pages.rows[0].keyValueSummary.chartData);
                $scope.data = _.select(data.pages.rows[0].details, {'reportingStatus': 'NON_REPORTING'}); //data.pages.rows[0].details;

                $scope.rawChartData = getChartData();
                $scope.chart = {};
                $scope.chart.data   = convertFacilityCountToReportingRate(); // chart data with reporting rate
                $scope.chart.ticks  = getChartTicks();
                $scope.chart.option = getChartOptions();

                $("#non-reporting-facilities-summary").bind("plotclick", chartItemClick);
                $("#non-reporting-facilities-summary").bind("plothover", chartItemHover);

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

    /** Calculate and Replace Y value of the series data to percentage of combined series of the same data point **/
    function convertFacilityCountToReportingRate() {
       var x=0, y=1;
       var REPORTING_SERIES = 0, NON_REPORTING_SERIES = 1;

       var data = getChartData();

        _.map(data[NON_REPORTING_SERIES].data, function(dataPoint){
            dataPoint[y] = Math.round((dataPoint[y] / (dataPoint[y] + getChartDataPointYvalue(data[REPORTING_SERIES], dataPoint[x]))) *100); });

        _.map(data[REPORTING_SERIES].data, function(dataPoint){
          dataPoint[y] = (100 - getChartDataPointYvalue(data[NON_REPORTING_SERIES], dataPoint[x])); });

        return data;

    }

    function getChartDataPointYvalue(series, dataPointXValue){
       return _.chain(series.data)
            .filter(function(dataPoint){  return dataPoint[0] == dataPointXValue; })
            .map(function(item) { return item[1]; })
            .value()[0] || 0;
    }


    function getChartData(){
       return [
            {
                data: getFacilityCountPerPeriodAndByReportingStatus({'reportingstatus': 'REPORTED'}),
                label: "Reported",
                code: "REPORTED",
                color: 'green'
            },
            {
                data: getFacilityCountPerPeriodAndByReportingStatus({'reportingstatus': 'NON_REPORTING'}),
                label: "Non-Reported",
                code: "NON_REPORTING",
                color: 'red'
            }
        ];
    }

    $scope.getNonReportingTableData = function(dataPointXValue){
        return  getChartDataPointYvalue($scope.rawChartData[1], dataPointXValue);
    };
    $scope.getReportingTableData = function(dataPointXValue){
        return  getChartDataPointYvalue($scope.rawChartData[0], dataPointXValue);
    };
    $scope.getExpectedFacilitiesTableData = function(dataPointXValue){
        return  getChartDataPointYvalue($scope.rawChartData[1], dataPointXValue)  +
            getChartDataPointYvalue($scope.rawChartData[0], dataPointXValue);

    };

    function getDataWithRankedPeriod(data){
      return  _.chain(data)
                .map(function(item){
                    item.order = getPeriodOrder(item.period);
                    return item; })
                .value();
    }

    function getFacilityCountPerPeriodAndByReportingStatus(reportTypeFilter){
        return _.chain($scope.chartDataRows)
                .select(reportTypeFilter)
                .countBy('order')
                .map(function(key, value) { return [parseInt(value, 10), key]; } )
                .value();
    }

    function getPeriodOrder(periodName){
        var periodOrder =  _.findWhere($scope.periods, {name : periodName}) ;
        return periodOrder !== undefined ?  periodOrder.rank : -1;
    }

    function getChartTicks(){
       return _.chain( $scope.periods)
                .map(function(item){return [item.rank, item.name]; } )
                .value();
    }

    function chartItemClick(event, pos, item){
        if(item) {

            $scope.modalData = _.chain($scope.chartDataRows)
                .select({'reportingstatus': item.series.code, 'order': item.datapoint[0]})
                .value();

            $scope.title = (item.series.code === 'REPORTED') ? 'Reporting Facilities' :
                'Non Reporting Facilities';

            $scope.successModal = true;
        }
    }

    function chartItemHover(event, pos, item){
        if (item) {
            $("#tooltip").html(item.series.label + " : " +
                (item.datapoint[1].toFixed(2) - item.datapoint[2].toFixed(2))+ " %")
                .css({top: item.pageY+5, left: item.pageX+5})
                .fadeIn(200);
        } else {
            $("#tooltip").hide();
        }
    }

    function getChartOptions() {
        return {
            xaxis: {
                minTickSize: 9,
                ticks: $scope.chart.ticks
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
    }

    function bindChartEvent(elementSelector, eventType, callback){
        $(elementSelector).bind(eventType, callback);
    }

    $scope.exportReport = function (type) {
        var paramString = jQuery.param($scope.filter);
        var url = '/reports/download/non_reporting/' + type + '?' + paramString;
        window.open(url, "_BLANK");
    };
}
