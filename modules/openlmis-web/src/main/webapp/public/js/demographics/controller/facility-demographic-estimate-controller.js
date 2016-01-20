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
function FacilityDemographicEstimateController($scope, $dialog, $filter, rights, categories, years, programs, FacilityDemographicEstimates, DistrictDemographicEstimates, SaveFacilityDemographicEstimates, FinalizeFacilityDemographicEstimates, UndoFinalizeFacilityDemographicEstimates) {

  $.extend(this, new BaseDemographicEstimateController($scope, rights, categories, programs, years, $filter));

  $scope.bindEstimates = function (facilities, districts) {
    $scope.forms = new FacilityDemographicsForm($scope, facilities, districts);
  };

  $scope.onParamChanged = function () {
    if (angular.isUndefined($scope.program) || $scope.program === null || angular.isUndefined($scope.year)) {
      return;
    }
    FacilityDemographicEstimates.get({year: $scope.year, program: $scope.program}, function (facilities) {
      DistrictDemographicEstimates.get({year: $scope.year, program: $scope.program}, function (districts) {
        $scope.districts = districts;
        $scope.bindEstimates(facilities, districts);
      });
    });
  };

  $scope.save = function () {
    $scope.clearMessages();
    SaveFacilityDemographicEstimates.update($scope.form, function () {
      $scope.message = "message.facility.demographic.estimates.saved";
    }, function (response) {
      $scope.error = response.data.error;
    });
  };

  $scope.finalize = function () {
    var callBack = function (result) {
      if (result) {
        var form = angular.copy($scope.form);
        form.estimateLineItems = $scope.lineItems;
        FinalizeFacilityDemographicEstimates.update(form, function (facilities) {
          $scope.bindEstimates(facilities, $scope.districts);
          $scope.message = 'label.message.estimates.finalized';
        }, function(response){
          $scope.error = response.data.error;
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.finalize.title",
      body: "label.confirm.finalize.demographic.estimate"
    };

    $scope.clearMessages();

    if($scope.forms.isValid()){
      OpenLmisDialog.newDialog(options, callBack, $dialog);
    }else{
      $scope.error = 'label.error.validate.facility.aggregate';
    }
  };

  $scope.undoFinalize = function () {
    var callBack = function (result) {
      if (result) {
        var form = angular.copy($scope.form);
        form.estimateLineItems = $scope.lineItems;
        UndoFinalizeFacilityDemographicEstimates.update(form, function (facilities) {
          $scope.bindEstimates(facilities, $scope.districts);
          $scope.message = 'label.message.estimates.available.for.editing';
        }, function(response){
          $scope.error = response.data.error;
        });
      }
    };

    var options = {
      id: "confirmDialog",
      header: "label.confirm.undo.finalize.title",
      body: "label.confirm.undo.finalize.demographic.estimate"
    };

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.init();
}

