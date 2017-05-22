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
function CustomReportController($scope, $sce, $window, $location, reports, CustomReportValue, $routeParams, $timeout) {

  var allMonths = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  function findMonth(columnValues, indx) {

    return _.find(columnValues, function (a) {
      return a.startsWith(allMonths[indx]);
    });
  }

  $scope.reports = reports;
  $scope.displayReports = _.groupBy(reports, 'category');
  $scope.categories = _.uniq(_.pluck(reports, 'category'));

  $scope.OnReportTypeChanged = function (value) {
    $scope.filter.report_key = value;
    $scope.OnFilterChanged();
  };

  $scope.onLinkClicked = function (col, row) {
    for (var i = 0; i < col.link.rowParams.length; i++) {
      var valueField = _.values(col.link.rowParams[i])[0];
      var key = _.keys(col.link.rowParams[i])[0];
      $scope.filter[key] = row[valueField];
    }
    $scope.filter.report_key = col.link.report;
    $location.search($scope.filter);
    $scope.$broadcast('filter-changed');
  };

  function updateFilterSection($scope) {

    // avoid having the blinking effect if the report has not been changed.
    if ($scope.previous_report_key !== $scope.filter.report_key) {
      $scope.previous_report_key = $scope.filter.report_key;

      $scope.report = _.findWhere($scope.reports, {reportkey: $scope.filter.report_key});

      $scope.report.columns = angular.fromJson($scope.report.columnoptions);
      if ($scope.report.filters !== null && $scope.report.filters !== '') {
        $scope.report.currentFilters = angular.fromJson($scope.report.filters);
        var required = _.pluck($scope.report.currentFilters, 'name');
        $scope.requiredFilters = [];
        angular.forEach(required, function (r) {
          $scope.requiredFilters[r] = r;
        });
      } else {
        $scope.report.currentFilters = [];
        $scope.requiredFilters = [];
      }
    }
  }


  $scope.postProcess = function (d) {
    var rows = [];
    var columnOptions = JSON.parse($scope.report.columnoptions);
    $scope.report.trusted_meta = $sce.trustAsHtml($scope.report.meta);
    $scope.report.pivotRowColumn = _.findWhere(columnOptions, {"pivotRow": "true"});
    $scope.report.pivotColumnDetail = _.findWhere(columnOptions, {"pivotColumn": "true"});
    $scope.report.pivotValueColumn = _.findWhere(columnOptions, {"pivotValue": "true"});
    $scope.report.pivotSummary = [];

    if (!isUndefined($scope.report.pivotRowColumn) && !isUndefined($scope.report.pivotColumnDetail) && !isUndefined($scope.report.pivotValueColumn)) {
      $scope.report.pivot = true;
      var rowName = $scope.report.pivotRowColumn.name;
      var columnName = $scope.report.pivotColumnDetail.name;

      $scope.report.hasPivot = true;
      var columns = [];
      $scope.report.pivotSummary = {};
      for (var i = 0; i < d.length; i++) {
        var current = d[i];

        if (columns[current[columnName]] === undefined) {
          columns[current[columnName]] = current[columnName];
        }

        if (rows[current[rowName]] === undefined) {
          current.p = [];
          rows[current[rowName]] = current;
        }

        //classify here. &&
        if ($scope.report.pivotValueColumn.classification !== undefined) {
          if ($scope.report.pivotSummary[current[$scope.report.pivotValueColumn.classification]] === undefined) {
            $scope.report.pivotSummary[current[$scope.report.pivotValueColumn.classification]] = {
              classification: current[$scope.report.pivotValueColumn.classification],
              p: {}
            };
          }
          if ($scope.report.pivotSummary[current[$scope.report.pivotValueColumn.classification]].p[current[columnName]] === undefined) {
            $scope.report.pivotSummary[current[$scope.report.pivotValueColumn.classification]].p[current[columnName]] = 0;
          }

          $scope.report.pivotSummary[current[$scope.report.pivotValueColumn.classification]].p[current[columnName]] += 1;

        }


        var row = rows[current[rowName]];
        row.p[current[columnName]] = current;
      }

      $scope.report.pivotSummaryArray = _.values($scope.report.pivotSummary);


      $scope.report.pivotColumns = [];
      var columnVals = _.values(columns);

      if ($scope.report.pivotColumnDetail.pivotType != 'custom') {
        for (i = 0; i < allMonths.length; i++) {
          // try to find
          var mont = findMonth(columnVals, i);
          if (!mont) {
            $scope.report.pivotColumns.push(allMonths[i] + ' ' + $scope.filter.year);
          } else {
            $scope.report.pivotColumns.push(mont);
          }
        }
      } else {
        $scope.report.pivotColumns = columnVals.sort();
      }

      $scope.data = _.sortBy(_.values(rows), $scope.report.columns[0].name);
    }
    else {
      $scope.data = d;
    }
  };

  $scope.OnFilterChanged = function () {

    if (angular.isUndefined($scope.filter) || angular.isUndefined($scope.filter.report_key) || !$scope.isReady) {
      return;
    }
    $scope.applyUrl();
    updateFilterSection($scope);

    //clear existing data
    $scope.data = [];
    $scope.meta = undefined;
    CustomReportValue.get($scope.getSanitizedParameter(), function (data) {
      $scope.meta = data;
      $scope.postProcess(data.values);
    });
  };

  $scope.exportExcel = function () {
    var params = jQuery.param($scope.getSanitizedParameter());
    var url = '/report-api/excel.xlsx?' + params;
    $window.open(url, '_blank');
  };

  $scope.isReady = true;
  if (!angular.isUndefined($scope.filter) && angular.isUndefined($scope.filter.report_key)) {
    $scope.OnFilterChanged();
  }

  if (angular.isUndefined($scope.filter)) {
    $scope.filter = {};
  }

  $scope.loadReportFromExternalUrl = function () {
    if (!angular.isUndefined($routeParams.report_key))
      $scope.filter.report_key = $routeParams.report_key;
    $scope.OnFilterChanged();
  };

  $timeout($scope.loadReportFromExternalUrl, 0);
}

CustomReportController.resolve = {
  reports: function ($q, $timeout, CustomReportList) {
    var deferred = $q.defer();
    $timeout(function () {
      CustomReportList.get(function (data) {
        deferred.resolve(data.reports);
      });
    }, 100);
    return deferred.promise;
  }
};
