function  EquipmentTestItemsController($rootScope, $scope, EquipmentTestItems, EquipmentFunctionalTestTypes, $routeParams, $location){
    $scope.EquipmentTestItems = getAllEquipmentTestItems();
    $scope.EquipmentTestItemsId = $routeParams.id || 0;
    $scope.cmd = $routeParams.cmd || '';
    $scope.functionalTestTypes = EquipmentFunctionalTestTypes.query();


    function getAllEquipmentTestItems() {
        return EquipmentTestItems.query();
    }

    if($scope.EquipmentTestItemsId)
        EquipmentTestItems.get({'id':$routeParams.id},
            function(data){ $scope.equipmentTestItem = data;} , function(data){});


    $scope.saveEquipmentTestItems = function(){

        if($scope.EquipmentTestItemsId)
            EquipmentTestItems.update($scope.equipmentTestItem , SaveEquipmentTestItemsSuccessCallback, SaveEquipmentTestItemsErrorCallback);
        else
            EquipmentTestItems.save($scope.equipmentTestItem , SaveEquipmentTestItemsSuccessCallback, SaveEquipmentTestItemsErrorCallback);

        function SaveEquipmentTestItemsSuccessCallback(result) {
            $scope.errors = null;
            $rootScope.message = result.success;
            $location.path("list");
        }

        function SaveEquipmentTestItemsErrorCallback(error) {
            $scope.errors = error;
            $rootScope.message = null;
        }
    };

    $scope.deleteEquipmentTestItems = function(id){
        EquipmentTestItems.delete({'id': id}, deleteEquipmentTestItemsSuccessCallback, deleteEquipmentTestItemsErrorCallback);
        function deleteEquipmentTestItemsSuccessCallback(successReponse){
            $rootScope.message = successReponse.success;
            $scope.EquipmentTestItems  = getAllEquipmentTestItems();
        }
        function deleteEquipmentTestItemsErrorCallback(errorResponse){
            $scope.errorMessage = errorResponse.data.error;
        }

    };

}