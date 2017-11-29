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

function CreateEquipmentModelController($scope, $routeParams, $location, EquipmentModel, EquipmentTypes, $route, messageService, $dialog) {
    $scope.viewMode = $route.current.$$route.mode;




    if($scope.viewMode === 'LIST'){
         $scope.equipment_models = EquipmentModel.query();
    }
    else if($scope.viewMode === 'EDIT' && $routeParams.id){
        $scope.$parent.message = '';
        EquipmentTypes.get({},
              function(data){
                  $scope.equipmentTypes = data.equipment_types;
              });
        EquipmentModel.get({
             'modelid': $routeParams.id
           }, function (data) {
             $scope.equipment_model = data;
           });
    }
    else{
         EquipmentTypes.get({},
              function(data){
                  $scope.equipmentTypes = data.equipment_types;
              });
        $scope.$parent.message = '';
        }


  $scope.saveEquipmentModel = function () {
    // clear the error message
    $scope.error = undefined;

    var onSuccess = function(data){
      $scope.$parent.message = messageService.get(data.success);
      $location.path('');
    };

    var onError = function(data){
      $scope.showError = true;
      $scope.error = data.error;
    };

    if(!$scope.equipmentModelForm.$invalid){
        if(isUndefined($routeParams.id))
            EquipmentModel.save( $scope.equipment_model, onSuccess, onError );
        else
            EquipmentModel.update( $scope.equipment_model, onSuccess, onError );
    }
  };

  $scope.cancelCreateEquipmentModel = function () {
    $location.path('');
  };

  $scope.showRemoveEquipmentModelTypeConfirmDialog = function () {
          var options = {
              id: "removeManualTestTypeMemberConfirmDialog",
              header: "Confirmation",
              body: "Are you sure you want to remove the equipment model: " + $scope.equipment_model.name
          };
          OpenLmisDialog.newDialog(options, $scope.removeEquipmentModelConfirm, $dialog, messageService);
      };

  $scope.removeEquipmentModelConfirm = function (result) {
          if (result) {
              EquipmentModel.delete({'modelid': $scope.equipment_model.id}, function (data) {
                  $scope.$parent.message = messageService.get(data.success);
                  $location.path('');
                  $scope.equipment_model = undefined;
              }, function (error) {
                  $scope.error = messageService.get(error.data.error);
              });

          }

      };
}