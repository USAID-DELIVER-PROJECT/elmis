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


function ManualTestTypeController($scope, $routeParams, ManualTestType, $location, $dialog, $route, messageService) {


    $scope.viewMode = $route.current.$$route.mode;

    if ($scope.viewMode === 'LIST') {
        ManualTestType.get(function(data){
            $scope.manualTestTypeList = data.manualTestTypeList;
        });
    }

    else if($scope.viewMode === 'EDIT' && $routeParams.id){
        $scope.$parent.message = '';
        var manualTestTypeId = $routeParams.id;
        ManualTestType.get({'tid':manualTestTypeId},
        function(response){
               $scope.testType = response.manualTestType;
        });
    }
    else //NEW
    {
        $scope.$parent.message = '';
    }


    $scope.saveManualTestType = function(){
        ManualTestType.save($scope.testType, function(response){
            $scope.$parent.message = response.success;
            $location.path('');
        },
       function(errorResponse){
                $scope.error =  messageService.get(errorResponse.data.error);
            });
    };

    $scope.showRemoveManualTestTypeConfirmDialog = function () {


        var options = {
            id: "removeManualTestTypeMemberConfirmDialog",
            header: "Confirmation",
            body: "Are you sure you want to remove the manual test type: " + $scope.testType.name
        };
        OpenLmisDialog.newDialog(options, $scope.removeManualTestTypeConfirm, $dialog, messageService);
    };

    $scope.removeManualTestTypeConfirm = function (result) {
        if (result) {

            ManualTestType.delete({'tid': $scope.testType.id}, function (data) {
                $scope.$parent.message = data.success;
                $location.path('');
                $scope.testType = undefined;
            }, function (error) {
                $scope.error = error.data.error;
            });

        }

    };

}