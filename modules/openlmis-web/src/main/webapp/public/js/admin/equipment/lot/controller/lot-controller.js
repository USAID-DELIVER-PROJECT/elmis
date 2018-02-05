function LotController( $scope,GetLotById,$routeParams,$location,messageService,SaveLOt){
    $scope.lot = {};
    $scope.message = {};

        if ($routeParams.id) {
            GetLotById.get({id: $routeParams.id}, function (data) {
                $scope.lot = data.lotsById;
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
            $scope.lot = response.lots;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.id = $scope.lot.id;

            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

        if (!$scope.lotForm.$invalid) {
            SaveLOt.save($scope.lot, successHandler, errorHandler);
        }

        return true;
    };


}