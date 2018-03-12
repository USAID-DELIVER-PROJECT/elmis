
function  EquipmentFunctionalTestTypesController($rootScope, $scope, EquipmentFunctionalTestTypes, $routeParams,
                                                 EquipmentCategories, EquipmentTestItems, $location){

    $scope.EquipmentFunctionalTestTypes  = getAllEquipmentFunctionalTestTypes();
    $scope.EquipmentFunctionalTestTypesId   	= $routeParams.id || 0;
    $scope.cmd = $routeParams.cmd || '';
    $scope.equipmentCategories = EquipmentCategories.query();
    $scope.equipmentTestitems = EquipmentTestItems.query();


    function getAllEquipmentFunctionalTestTypes() {
        return EquipmentFunctionalTestTypes.query();
    }

    if($scope.EquipmentFunctionalTestTypesId)
        EquipmentFunctionalTestTypes.get({'id':$routeParams.id},
         function(data){
                $scope.functionalTestTypes = data;
    } , function(data){});


    $scope.saveEquipmentFunctionalTestTypes = function(){

        if($scope.EquipmentFunctionalTestTypesId)
            EquipmentFunctionalTestTypes.update($scope.functionalTestTypes, SaveEquipmentFunctionalTestTypesSuccessCallback, SaveEquipmentFunctionalTestTypesErrorCallback);
        else
            EquipmentFunctionalTestTypes.save($scope.functionalTestTypes, SaveEquipmentFunctionalTestTypesSuccessCallback, SaveEquipmentFunctionalTestTypesErrorCallback);

        function SaveEquipmentFunctionalTestTypesSuccessCallback(result) {
            $scope.errors = null;
            $rootScope.message = result.success;
            $location.path("list");
        }

        function SaveEquipmentFunctionalTestTypesErrorCallback(error) {
            $scope.errors = error;
            $scope.message = null;
        }
    }

    $scope.deleteEquipmentFunctionalTestTypes = function(id){
        EquipmentFunctionalTestTypes.delete({'id': id}, deleteEquipmentFunctionalTestTypesSuccessCallback, deleteEquipmentFunctionalTestTypesErrorCallback);
        function deleteEquipmentFunctionalTestTypesSuccessCallback(successReponse){
            $scope.message = successReponse.success;
            $scope.EquipmentFunctionalTestTypes  = getAllEquipmentFunctionalTestTypes();
        }
        function deleteEquipmentFunctionalTestTypesErrorCallback(errorResponse){
            $scope.errorMessage = errorResponse.data.error;
        }

    };

}