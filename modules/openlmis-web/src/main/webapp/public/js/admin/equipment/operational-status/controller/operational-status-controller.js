function EquipmentOperationalStatusController($scope, $location,messageService, $routeParams,SaveOperationalStatus,GetOperationalStatus,RemoveOperationalStatus){
    $scope.operationalStatus = {};
    $scope.message = {};

    if ($routeParams.statusId) {
        GetOperationalStatus.get({id: $routeParams.statusId}, function (data) {
            $scope.operationalStatus = data.operationalStatus;
            $scope.showError = true;
        }, {});
    }

    $scope.cancelAddEdit = function () {
        $scope.$parent.message = {};
        $scope.$parent.statusId = null;
        $location.path('#/list');
    };

    $scope.saveOperationalStatus = function () {
        var successHandler = function (response) {
            $scope.operationalStatus = response.operationalStatus;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.statusId = $scope.operationalStatus.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

        if (!$scope.operationalStatusForm.$invalid) {
            SaveOperationalStatus.save($scope.operationalStatus, successHandler, errorHandler);
        }

        return true;
    };

}


EquipmentOperationalStatusController.resolve = {

};