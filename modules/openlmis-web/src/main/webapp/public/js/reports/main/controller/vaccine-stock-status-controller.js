/*
* Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
*
* Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
*/


function VaccineStockStatusReportController($scope,$filter, $window, VaccineStockStatusReport,VaccineHomeFacilityIvdPrograms,UserFacilityList,FacilityTypeAndProgramProducts) {

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/vaccine_stock_status/' + type + '?' + params;
        $window.open(url, "_BLANK");
    };

    VaccineHomeFacilityIvdPrograms.get({}, function (p) {
        var programId = p.programs[0].id;
        UserFacilityList.get({}, function (f) {
            var facilityId = f.facilityList[0].id;
            FacilityTypeAndProgramProducts.get({facilityId: facilityId, programId: programId}, function (data) {
                var facilityProduct = data.facilityProduct;
                    $scope.facilityProduct = facilityProduct.sort(function (a, b) {
                        return (a.programProduct.product.id > b.programProduct.product.id) ? 1 : ((b.programProduct.product.id > a.programProduct.product.id) ? -1 : 0);
                    });

            });
        });

    });

    $scope.OnFilterChanged = function () {

        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        var arr = [];

        $scope.filter.max = 10000;

        $scope.filter.statusDate = $filter('date')($scope.filter.dateStatus, "yyyy-MM-dd");

        VaccineStockStatusReport.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
          var groupByFacility = _.groupBy(data.pages.rows,function(f){
              return f.facilityName;
          });

                $scope.distributedFacilities = $.map(groupByFacility, function (value, index) {
                    return [{"facilityName": index, "products": value}];
                });

             $scope.paramsChanged($scope.tableParams);
            }
        });

        $scope.getTotal = function(product){

            var total = 0;

            for(var i = 0; i <product.length; i++){
                total += product[i].adequacy;
            }

            return (total / product.length) * 100;
        };

    };

    $scope.formatNumber = function (value) {
        return utils.formatNumber(value, '0,0.00');
    };

    $scope.getProductQuantity = function (facilityName, productName) {
        console.log(facilityName);

        var f = _.findWhere($scope.distributedFacilities, {facilityName: facilityName});

        if (f !== undefined)
            p = _.findWhere(f.products, {product: productName});
        if (p !== undefined)
        {

            return p;

        }
        else
            return null;
    };

}