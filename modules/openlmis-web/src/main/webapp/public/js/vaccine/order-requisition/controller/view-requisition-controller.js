/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

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