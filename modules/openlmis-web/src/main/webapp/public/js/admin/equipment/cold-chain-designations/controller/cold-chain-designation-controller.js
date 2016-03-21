function DesignationController( $scope,GetColdChainDesignationsById,$routeParams,$location,messageService,SaveColdChainDesignations){
    $scope.designation = {};
    $scope.message = {};

    if ($routeParams.id) {
        GetColdChainDesignationsById.get({id: $routeParams.id}, function (data) {
            $scope.designation = data.designationsById;
            $scope.showError = true;
        }, {});
    }

    $scope.cancelAddEdit = function () {
        $scope.$parent.message = {};
        $scope.$parent.id = null;
        $location.path('#/list');
    };

    $scope.saveDesignation = function () {

        var successHandler = function (response) {
            $scope.designation = response.designations;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.id = $scope.designation.id;

            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = messageService.get(response.data.error);
        };

        if (!$scope.designationForm.$invalid) {
            SaveColdChainDesignations.save($scope.designation, successHandler, errorHandler);
        }

        return true;
    };
}