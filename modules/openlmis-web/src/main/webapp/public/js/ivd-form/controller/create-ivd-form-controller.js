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
function CreateIvdFormController($scope, $location, operationalStatuses, $dialog, manufacturers, report, discardingReasons, VaccineReportSave, VaccineReportSubmit) {

  // initial state of the display
  $scope.report = new VaccineReport(report);
  $scope.manufacturers = manufacturers;
  $scope.discardingReasons = discardingReasons;

  //prepare tab visibility settings
  $scope.tabVisibility = {};
  _.chain(report.tabVisibilitySettings).groupBy('tab').map(function (key, value) {
    $scope.tabVisibility[value] = key[0].visible;
  });

  $scope.operationalStatuses = operationalStatuses;

  $scope.highlightRequired = function (showError, value, skipped) {
    if (showError && isUndefined(value) && !skipped) {
      return "required-error";
    }
    return null;
  };

  $scope.save = function () {
    VaccineReportSave.update($scope.report, function () {
      $scope.error = '';
      $scope.message = "msg.ivd.saved.successfully";
    });
  };

  $scope.submit = function () {
    $scope.message = '';
    $scope.error = '';

    if (!$scope.report.isValid($scope)) {
      $scope.error = 'You are attempting to save invalid values. Please make sure all information is valid. ';
      return;
    }

    var callBack = function (result) {
      if (result) {
        VaccineReportSubmit.update($scope.report, function () {
          $scope.message = "msg.ivd.submitted.successfully";
          $location.path('/');
        });
      }
    };
    var options = {
      id: "confirmDialog",
      header: "label.confirm.submit.action",
      body: "msg.question.submit.ivd.confirmation"
    };
    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.cancel = function () {
    $location.path('/');
  };


  $scope.showAdverseEffect = function (effect, editMode) {

    $scope.currentEffect = effect;
    $scope.currentEffectMode = editMode;
    $scope.adverseEffectModal = true;
  };

  $scope.applyAdverseEffect = function (adverseEffectForm) {
    if(adverseEffectForm.$valid){
      var product = _.findWhere($scope.report.products, {'id': utils.parseIntWithBaseTen($scope.currentEffect.productId)});
      $scope.currentEffect.productName = product.primaryName;
      if (!$scope.currentEffectMode) {
        $scope.report.adverseEffectLineItems.push($scope.currentEffect);
      }
      $scope.adverseEffectModal = false;
    }

  };

  $scope.closeAdverseEffectsModal = function () {
    $scope.adverseEffectModal = false;
  };

  $scope.showCampaignForm = function (campaign, editMode) {
    $scope.currentCampaign = campaign;
    $scope.currentCampaignMode = editMode;

    $scope.campaignsModal = true;
  };

  $scope.deleteAdverseEffectLineItem = function (lineItem) {

    var callBack = function (result) {
      if (result) {
        $scope.report.adverseEffectLineItems = _.without($scope.report.adverseEffectLineItems, lineItem);
      }
    };
    var options = {
      id: "confirmDialog",
      header: "label.confirm.delete.adverse.effect.action",
      body: "msg.question.delete.adverse.effect.confirmation"
    };
    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.applyCampaign = function () {
    if (!$scope.currentCampaignMode) {
      $scope.report.campaignLineItems.push($scope.currentCampaign);
    }
    $scope.campaignsModal = false;
  };

  $scope.closeCampaign = function () {
    $scope.campaignsModal = false;
  };

  $scope.toNumber = function (val) {
    if (utils.isNumber(val)) {
      return utils.parseIntWithBaseTen(val);
    }
    return 0;
  };


  $scope.rowRequiresExplanation = function (product) {
    if (!isUndefined(product.discardingReasonId)) {
      var reason = _.findWhere($scope.discardingReasons, {id: utils.parseIntWithBaseTen(product.discardingReasonId)});
      return reason.requiresExplanation;
    }
    return false;
  };

}

CreateIvdFormController.resolve = {

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
  manufacturers: function ($q, $timeout, $route, ManufacturerList) {
    var deferred = $q.defer();

    $timeout(function () {
      ManufacturerList.get(function (data) {
        deferred.resolve(data.manufacturers);
      });
    }, 100);
    return deferred.promise;
  },
  operationalStatuses: function ($q, $timeout, $route, ColdChainOperationalStatus) {
    var deferred = $q.defer();

    $timeout(function () {
      ColdChainOperationalStatus.get(function (data) {
        deferred.resolve(_.filter(data.status, function (d) {
          return d.category.indexOf('CCE') >= 0;
        }));
      });
    }, 100);
    return deferred.promise;
  }
};
