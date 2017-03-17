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
function ReportingStatusController($scope, NonReportingFacilities) {

  $scope.OnFilterChanged = function () {
    // clear old data if there was any
    $scope.data = $scope.datarows = [];
    $scope.filter.max = 10000;

    $scope.updateFacilityList = function () {
      if ($scope.reporting) {
        $scope.data = _.select($scope.responseData.pages.rows[0].details, {'reportingStatus': 'REPORTED'});
      } else {
        $scope.data = _.select($scope.responseData.pages.rows[0].details, {'reportingStatus': 'NON_REPORTING'});
      }
      $scope.paramsChanged($scope.tableParams);
    };

    $scope.updateSummaries = function () {
      $scope.summary = {};
      $scope.summary.total = utils.parseIntWithBaseTen(_.findWhere($scope.responseData.pages.rows[0].summary, {name: "TOTAL_FACILITIES"}).count);
      $scope.summary.nonReporting = utils.parseIntWithBaseTen(_.findWhere($scope.responseData.pages.rows[0].summary, {name: "TOTAL_NON_REPORTING"}).count);
      $scope.summary.reporting = utils.parseIntWithBaseTen(_.findWhere($scope.responseData.pages.rows[0].summary, {name: "REPORTING_FACILITIES"}).count);
      $scope.summary.reportingPercent = ($scope.summary.reporting !== 0) ? ($scope.summary.reporting * 100 / $scope.summary.total) : 0;
      $scope.summary.nonReportingPercent = ($scope.summary.reporting !== 0) ? ($scope.summary.nonReporting * 100 / $scope.summary.total) : 0;
    };

    NonReportingFacilities.get($scope.getSanitizedParameter(), function (data) {
      if (data.pages !== undefined && data.pages.rows !== undefined) {
        $scope.summaries = data.pages.rows[0].summary;
        $scope.responseData = data;
        $scope.updateFacilityList();
        $scope.updateSummaries();
        $scope.nonReportingFacilitiesPieChartData = [];

        $scope.nonReportingFacilitiesPieChartData.push({
          label: 'Reported',
          data: $scope.summary.reporting,
          color: '#A3CC29'
        });

        $scope.nonReportingFacilitiesPieChartData.push({
          label: 'Did not Report',
          data: $scope.summary.nonReporting,
          color: '#FFB445'
        });
      }
    });
  };


  $scope.exportReport = function (type) {
    var paramString = jQuery.param($scope.filter);
    var url = '/reports/download/non_reporting/' + type + '?' + paramString;
    window.open(url, "_BLANK");
  };

  // Summary pie chart options
  $scope.nonReportingReportSummaryPieChartOption = {
    series: {
      pie: {
        show: true,
        radius: 1,
        label: {
          show: true,
          radius: 2 / 3,
          formatter: function (label, series) {
            return '<div style="font-size:8pt;text-align:center;padding:2px;color:black;">' + Math.round(series.percent) + '%</div>';
          },
          threshold: 0.1
        }
      }
    },
    legend: {
      container: $("#nonReportingReportSummary"),
      noColumns: 0,
      labelBoxBorderColor: "none",
      sorted: "descending",
      backgroundOpacity: 1,
      labelFormatter: function (label, series) {
        var percent = Math.round(series.percent);
        var number = series.data[0][1];
        return ('<b>' + label + '</b>');
      }
    },
    grid: {
      hoverable: true,
      clickable: true,
      borderWidth: 1,
      borderColor: "#000",
      backgroundColor: {
        colors: ["red", "green", "yellow"]
      }
    },
    tooltip: true,
    tooltipOpts: {
      content: "%p.0%, %s",
      shifts: {
        x: 20,
        y: 0
      },
      defaultTheme: false
    }
  };
}
