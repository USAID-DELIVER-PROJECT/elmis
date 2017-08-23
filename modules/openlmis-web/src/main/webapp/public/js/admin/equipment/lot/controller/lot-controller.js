function LotController( $scope,GetLotById,$routeParams,$location,messageService,SaveLOt){
    $scope.lot = {};
    $scope.message = {};

        if ($routeParams.id) {
            GetLotById.get({id: $routeParams.id}, function (data) {
                $scope.lot = data.lot;
                console.log(data.lot);
                $scope.showError = true;
            }, {});
        }


    $scope.cancelAddEdit = function () {
        $scope.$parent.message = {};
        $scope.$parent.id = null;
        $location.path('#/list');
    };

    $scope.saveLot = function () {

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