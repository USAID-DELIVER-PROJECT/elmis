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
function DistrictDemographicEstimateController($scope, $dialog, $filter, rights, categories, programs , years, DistrictDemographicEstimates, UndoFinalizeDistrictDemographicEstimates, FinalizeDistrictDemographicEstimates , SaveDistrictDemographicEstimates) {

  $.extend(this, new BaseDemographicEstimateController($scope, rights, categories, programs , years, $filter));

  $scope.bindEstimates = function(data){
    $scope.lineItems = [];
    // initiate all objects.
    var finalizedCount = 0, draftCount = 0;
    angular.forEach(data.estimates.estimateLineItems, function(district){
      $.extend(district, new DistrictEstimateModel());
      $scope.lineItems.push(district);
      if(district.districtEstimates[0].isFinal){
        finalizedCount ++;
      }else{
        draftCount++;
      }
    });

    data.estimates.estimateLineItems = [];
    $scope.form = data.estimates;
    $scope.currentPage = 1;
    if(finalizedCount > 0 && draftCount === 0){
      $scope.formStatus = 'Finalized';
    }else{
      $scope.formStatus = (finalizedCount > 0 && draftCount > 0)? 'Partial' : 'Draft';
    }
    $scope.districtSummary = new AggregateRegionEstimateModel($scope.lineItems);
    $scope.pageLineItems();
  };

  $scope.onParamChanged = function(){
    if(angular.isUndefined($scope.program) || $scope.program === null || angular.isUndefined($scope.year)){
      return;
    }
    DistrictDemographicEstimates.get({year: $scope.year, program: $scope.program}, function(data){
      $scope.bindEstimates(data);
    });
  };

  $scope.save = function(){
    $scope.clearMessages();
    SaveDistrictDemographicEstimates.update($scope.form, function(){
      $scope.message = "message.district.demographic.estimates.saved";
    }, function(response){
      $scope.error = response.data.error;
    });
  };

  $scope.finalize = function(){
    $scope.clearMessages();
    var callBack = function (result) {
      if (result) {
        var form = angular.copy($scope.form);
        form.estimateLineItems = $scope.lineItems;
        FinalizeDistrictDemographicEstimates.update(form, function (data) {
          $scope.bindEstimates(data);
          $scope.message = 'Estimates are now finalized';
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

    OpenLmisDialog.newDialog(options, callBack, $dialog);
  };

  $scope.undoFinalize = function(){
    $scope.clearMessages();
    var callBack = function (result) {
      if (result) {
        var form = angular.copy($scope.form);
        form.estimateLineItems = $scope.lineItems;
        UndoFinalizeDistrictDemographicEstimates.update(form, function (data) {
          $scope.bindEstimates(data);
          $scope.message = 'Estimates are now available for editing.';
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
