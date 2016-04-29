/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */


function CreateVaccineOrderRequisition($scope, $dialog,$routeParams, $window, report, VaccineOrderRequisitionSubmit, $location) {


    $scope.report = new VaccineOrderRequisition(report);
console.log($scope.report);
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

        VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
            $scope.$parent.print = data.report;
        });
        var url = '/vaccine/orderRequisition/' + reportId + '/print';
        $window.open(url, '_blank');
    };

    $scope.submit = function () {
        var printWindow;
        if ($scope.report.emergency === true) {
            $scope.orderModal = true;
        } else {

            var callBack = function (result) {

                if (result) {

                    VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
                        var url = '/vaccine/orderRequisition/' + data.report.id + '/print';
                        printWindow.location.href=url;
                        $scope.disableButton = true;
                        $window.location = '/public/pages/vaccine/dashboard/index.html#/dashboard';
                    });
                    printWindow= $window.open('about:blank','_blank');
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
        var printWindow;
        VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
            var url = '/vaccine/orderRequisition/' + data.report.id + '/print';
            printWindow.location.href=url;
            $scope.message = "label.form.Submitted.Successfully";
            $window.location = '/public/pages/vaccine/dashboard/index.html#/dashboard';
        });
        printWindow= $window.open('about:blank','_blank');


    };

    $scope.cancel = function () {
        $window.location = '/public/pages/vaccine/dashboard/index.html#/dashboard';
    };

    $scope.viewOrderPrint = function () {

        var url = '/vaccine/orderRequisition/' +  parseInt($routeParams.id,10) + '/print';
        $window.open(url, '_blank');
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