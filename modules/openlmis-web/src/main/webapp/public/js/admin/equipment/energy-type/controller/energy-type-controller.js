function EnergyController( $scope,GetEnergyTypeById,$routeParams,$location,messageService,SaveEnergyType){
    $scope.energyType = {};
    $scope.message = {};

        if ($routeParams.id) {
            GetEnergyTypeById.get({id: $routeParams.id}, function (data) {
                $scope.energyType = data.energyType;
                console.log(data.energyType);
                $scope.showError = true;
            }, {});
        }


    $scope.cancelAddEdit = function () {
        $scope.$parent.message = {};
        $scope.$parent.id = null;
        $location.path('#/list');
    };

    $scope.saveEnergyType = function () {

        var successHandler = function (response) {
            $scope.energyType = response.energy_types;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.id = $scope.energyType.id;

            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

        if (!$scope.energyTypeForm.$invalid) {
            SaveEnergyType.save($scope.energyType, successHandler, errorHandler);
        }

        return true;
    };


}