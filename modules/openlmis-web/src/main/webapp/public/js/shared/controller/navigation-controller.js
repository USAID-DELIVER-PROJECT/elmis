/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function NavigationController($scope, ConfigSettingsByKey, localStorageService, Locales, $location, $window, CustomReportList, $timeout,

                              BatchExpiryNotification, VaccineDashboardSummary, ReceiveDistributionAlert, MinimumStockNotification, VaccinePendingRequisitions, UserFacilityList

) {

  ConfigSettingsByKey.get({key: 'LOGIN_SUCCESS_DEFAULT_LANDING_PAGE'}, function (data){
    $scope.homePage =  data.settings.value;
  });

  $scope.loadRights = function () {
    $scope.rights = localStorageService.get(localStorageKeys.RIGHT);

    $(".navigation > ul").show();
  }();

  $scope.showSubmenu = function () {
    $(".navigation li:not(.navgroup)").on("click", function () {
      $(this).children("ul").show();
    });
  }();

  $scope.hasReportingPermission = function () {
    if ($scope.rights !== undefined && $scope.rights !== null) {
      var rights = JSON.parse($scope.rights);
      var rightTypes = _.pluck(rights, 'type');
      return rightTypes.indexOf('REPORTING') > -1;
    }
    return false;
  };
$scope.homeLinkClicked=function(){
    $window.location.href= $scope.homePage;
};
  $scope.hasPermission = function (permission) {
    if ($scope.rights !== undefined && $scope.rights !== null) {
      var rights = JSON.parse($scope.rights);
      var rightNames = _.pluck(rights, 'name');
      return rightNames.indexOf(permission) > -1;
    }
    return false;
  };

  $scope.goOnline = function () {
    Locales.get({}, function (data) {
      if (data.locales) {
        var currentURI = $location.absUrl();
        if (currentURI.endsWith('offline.html')) {
          $window.location = currentURI.replace('public/pages/offline.html', '');
        }
        else {
          $window.location = currentURI.replace('offline.html', 'index.html').replace('#/list', '#/manage');
        }
        $scope.showNetworkError = false;
        return;
      }
      $scope.showNetworkError = true;
    }, {});
  };

  ConfigSettingsByKey.get({key: 'USE_NEW_REPORT_MENU'}, function (data){
    if(data.settings !== null)
      $scope.useFlatReportMenu =  data.settings.value  == 'true' ? true : false;

  });

  $scope.getCustomReportsList = function() {
      CustomReportList.get({}, function (list) {
          $scope.customReports =_.groupBy(list.reports,'category');
      });
  }();

  function hideEmptyReportCategory(){
      $("#all-report-lists li.report-children ul").each(
          function() {
              var elem = $(this);
              if(elem.children().filter(".ng-hide").length == elem.children().length)
                  elem.parent().hide();
          }
      );
  }
  $timeout(hideEmptyReportCategory, 0);





  $scope.reportingPerformance = {};

  $scope.repairing = {};
  $scope.supplying = {};
  $scope.investigating = {};

  function dashboardSummaryCallBack() {

    VaccineDashboardSummary.get({}, function (data) {

      $scope.reportingPerformance = data.summary.reportingSummary;

      $scope.repairing = data.summary.repairing;
      $scope.investigating = data.summary.investigating;
      $scope.notificationCount = parseInt($scope.reportingPerformance.expected, 10) +
          parseInt($scope.investigating.count, 10) +
          parseInt($scope.reportingPerformance.late, 10);


    });
  }
  dashboardSummaryCallBack();

  var homeFacility = {};

  UserFacilityList.get({}, function (data) {

    homeFacility = data.facilityList[0];
    VaccinePendingRequisitions.get({facilityId: parseInt(homeFacility.id, 10)}, function (data) {
      $scope.supplyingPendingReceive = {};
      $scope.supplyingAllPendingOrders = data.pendingRequest;
      $scope.supplyingPendingReceive.supplyingPendingToReceive = data.pendingToReceive;
      $scope.supplyingPendingReceive.supplyingPendingToReceiveLowerLevel = data.pendingToReceiveLowerLevel;
      //  $scope.supplyingPendingReceive.daysForUnreceivedNotification= daysNotReceive;

      if (data.pendingRequest !== undefined)
        $scope.supplying.orders = data.pendingRequest.length;
      else {
        $scope.supplying.orders = 0;
      }
    });
  });


  MinimumStockNotification.get({}, function (data) {
    var summary = [];
    if (!isUndefined(data)) {
      summary = data;
      $scope.totalMinimumStock = summary.minimumStock.length;

    }

  });


  ReceiveDistributionAlert.get(function (data) {

    if (data !== undefined) {

      $scope.totalReceive = data.receiveNotification.length;
    }

  });


  function expiryCallBack() {

    BatchExpiryNotification.get({}, function (data) {
      var expires = [];
      if (!isUndefined(data.expiries)) {
        expires = data.expiries;
        $scope.totalBatchToExpire = expires.length;
      }

    });
  }

  expiryCallBack();



}
