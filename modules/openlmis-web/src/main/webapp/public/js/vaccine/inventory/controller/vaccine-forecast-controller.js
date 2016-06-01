/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

function VaccineForecastingController($scope,$window,$routeParams,$location,programs,homeFacility,StockRequirementsData){

    $scope.pageLineItems1 = [];
    $scope.pageLineItems = [];
    var dataToDisplay = [];
    $scope.pageSize = 10;
    var program = 0;
    var facilityId =0;

    facilityId = homeFacility;
    $scope.homeFacility=homeFacility;
    if(programs.length == 1){
        program = programs[0].id;

    }

    $scope.exportCSV = function(param) {
        var url = '/vaccine/inventory/demandForecasting/print/'+param;
        $window.open(url, '_blank');
    };


 var refreshPageLineItems = function(){

    StockRequirementsData.get(parseInt(program,10), parseInt(homeFacility.id,10)).then(function (data) {
        dataToDisplay = data;
        $scope.numberOfPages = Math.ceil(dataToDisplay.length / $scope.pageSize) || 1;
        $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
        $scope.pageLineItems = dataToDisplay.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
        console.log($scope.pageLineItems);
    });
 };
    refreshPageLineItems();

    $scope.$watch('currentPage', function () {
        $location.search('page', $scope.currentPage);
    });

    $scope.greaterThan = function(prop,val){
        return function(item){
            return item[prop] > val;
        };
    };


}

VaccineForecastingController.resolve = {

    homeFacility: function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility = {};

        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    },
    programs: function ($q, $timeout, VaccineHomeFacilityPrograms) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineHomeFacilityPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    }

};