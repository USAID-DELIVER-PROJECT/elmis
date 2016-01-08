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
function ApproveIvdFormDetailController($scope, $dialog, $location, report, discardingReasons, operationalStatuses, VaccineReportApprove, VaccineReportReject) {

  // initial state of the display
  $scope.report = new VaccineReport(report);
  $scope.discardingReasons = discardingReasons;
  // populate scope with tab visibility info
  $scope.tabVisibility = {};
  _.chain(report.tabVisibilitySettings).groupBy('tab').map(function(key, value){
                                                                $scope.tabVisibility[value] =  key[0].visible;
                                                              });

  $scope.cancel = function () {
    $location.path('/approve');
  };

  $scope.operationalStatuses = operationalStatuses;

  $scope.showAdverseEffect = function (effect, editMode) {
    $scope.currentEffect = effect;
    $scope.currentEffectMode = editMode;
    $scope.adverseEffectModal = true;
  };


  $scope.closeAdverseEffectsModal = function () {
    $scope.adverseEffectModal = false;
  };

  $scope.showCampaignForm = function (campaign, editMode) {
    $scope.currentCampaign = campaign;
    $scope.currentCampaignMode = editMode;
    $scope.campaignsModal = true;
  };

  $scope.closeCampaign = function () {
    $scope.campaignsModal = false;
  };

  $scope.rowRequiresExplanation = function (product) {
    if (!isUndefined(product.discardingReasonId)) {
      var reason = _.findWhere($scope.discardingReasons, {id: parseInt(product.discardingReasonId, 10)});
      return reason.requiresExplanation;
    }
    return false;
  };

  $scope.approve = function () {
    var callBack = function (result) {
      if (result) {
        VaccineReportApprove.update($scope.report, function () {
          $scope.message = "msg.ivd.approved.successfully";
          $location.path('/approve');
        });
      }
    };
    var options = {
      id: "confirmDialog",
      header: "label.confirm.approve.action",
      body: "msg.question.approve.ivd.confirmation"
    };
    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };


  $scope.reject = function () {
    var callBack = function (result) {
      if (result) {
        VaccineReportReject.update($scope.report, function () {
          $scope.message = "msg.ivd.rejected.successfully";
          $location.path('/approve');
        });
      }
    };
    var options = {
      id: "confirmDialog",
      header: "label.confirm.reject.action",
      body: "msg.question.reject.ivd.confirmation"
    };
    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

}

ApproveIvdFormDetailController.resolve = {
  report: function ($q, $timeout, $route, VaccineReport) {
    var deferred = $q.defer();

    $timeout(function () {
      VaccineReport.get({id: $route.current.params.id}, function (data) {
        deferred.resolve(data.report);
      });
    }, 100);
    return deferred.promise;
  },
  discardingReasons: function ($q, $timeout, $route, VaccineDiscardingReasons) {
    var deferred = $q.defer();

    $timeout(function () {
      VaccineDiscardingReasons.get(function (data) {
        deferred.resolve(data.reasons);
      });
    }, 100);
    return deferred.promise;
  },
  operationalStatuses : function ($q, $timeout, $route, ColdChainOperationalStatus) {
    var deferred = $q.defer();

    $timeout(function () {
      ColdChainOperationalStatus.get(function (data) {
        deferred.resolve(data.status);
      });
    }, 100);
    return deferred.promise;
  }

};
