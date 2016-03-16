function ViewRequisitionController($scope,$window,$routeParams,$location,report){


    $scope.report = new VaccineOrderRequisition(report);

    $scope.orderModal = false;

    $scope.selectedType = 0;

    $scope.productFormChange = function () {
        $scope.selectedType = 0;
        $scope.calculateVial = false;
        $scope.report = new VaccineOrderRequisition(report);

    };

    $scope.productFormChange1 = function () {
        $scope.selectedType = 1;
        $scope.calculateVial = true;
        $scope.report = new VaccineOrderRequisition2(report);

    };

    $scope.print = function (reportId) {

        var url = '/vaccine/orderRequisition/' + reportId + '/print';
        $window.open(url, '_blank');
    };


    $scope.cancelOrder = function () {
        $location.path('/search');

    };

    $scope.viewOrderPrint = function () {

        var url = '/vaccine/orderRequisition/' +  parseInt($routeParams.id,10) + '/print';
        $window.open(url, '_blank');
    };

}

ViewRequisitionController.resolve= {

    report: function ($q, $timeout, $route, VaccineOrderRequisitionByCategory) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineOrderRequisitionByCategory.get(parseInt($route.current.params.id, 10), parseInt($route.current.params.programId, 10)).then(function (data) {
                deferred.resolve(data);
            });
        }, 100);
        return deferred.promise;
    }
};