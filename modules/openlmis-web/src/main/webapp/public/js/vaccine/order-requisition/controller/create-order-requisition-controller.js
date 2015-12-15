


function CreateVaccineOrderRequisition($scope, $dialog, $window, report, VaccineOrderRequisitionSubmit, $location) {

    $scope.report = new VaccineOrderRequisition(report);

    $scope.orderModal = false;

    $scope.selectedType = 0;

    $scope.productFormChange = function () {
        $scope.selectedType = 0;
        $scope.report = new VaccineOrderRequisition(report);

    };

    $scope.productFormChange1 = function () {
        $scope.selectedType = 1;
        $scope.report = new VaccineOrderRequisition2(report);

    };

    $scope.print = function (reportId) {

        VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
            $scope.$parent.print = data.report;
        });
        var url = '/vaccine/orderRequisition/' + reportId + '/print';
        $window.open(url, '_blank');
    };

    $scope.submit = function () {

        if ($scope.report.emergency === true) {
            $scope.orderModal = true;
        } else {

            var callBack = function (result) {

                if (result) {

                    VaccineOrderRequisitionSubmit.update($scope.report, function (data) {

                        var url = '/vaccine/orderRequisition/' + data.report.id + '/print';
                        $window.open(url, '_blank');

                        $scope.disableButton = true;

                        $window.location = '/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';

                       // $location.path('/initiate');
                    });
                    $scope.message = "label.form.Submitted.Successfully";
                }
            };
            var options = {
                id: "confirmDialog",
                header: "label.confirm.order.submit.action",
                body: "msg.question.submit.order.confirmation"
            };
            OpenLmisDialog.newDialog(options, callBack, $dialog);

        }

    };

    $scope.closeOrderModal = function () {
        $scope.orderModal = false;
    };

    $scope.submitEmergency = function () {

        VaccineOrderRequisitionSubmit.update($scope.report, function (data) {

            var url = '/vaccine/orderRequisition/' + data.report.id + '/print';
            $window.open(url, '_blank');

            $scope.message = "label.form.Submitted.Successfully";

            $window.location = '/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';

        });


    };

    $scope.cancel = function () {
        $window.location = '/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
    };


}


CreateVaccineOrderRequisition.resolve = {

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