/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */


function FacilityDistributionController($scope,$location,FacilitiesSameType, $document,$window,configurations,$timeout,homeFacility,OneLevelSupervisedFacilities,FacilityWithProducts,DistributionsByDateRange,StockCards,StockCardsByCategory,StockEvent,SaveDistribution,localStorageService,$anchorScroll) {

    $scope.userPrograms=configurations.programs;
    $scope.period=configurations.period;
    $scope.homeFacility=homeFacility;
    $scope.facilityDisplayName=homeFacility.name;
    $scope.toIssue=[];
    $scope.distributionType='ROUTINE';
    $scope.UnScheduledFacility=undefined;
    $scope.toDay=new Date();
    $scope.maxModalBodyHeight='max-height:'+parseInt($document.height() * 0.35,10)+'px !important;height:'+parseInt($document.height() * 0.35,10)+'px !important';
    $scope.loadSupervisedFacilities=function(programId){
        OneLevelSupervisedFacilities.get({programId:programId},function(data){
            $scope.supervisedFacilities=data.facilities;
        });
    };

    $scope.loadFacilityDistributionData=function(){
        $scope.routineFacility=undefined;
        $scope.message=false;
        $scope.podMessage=false;
        if($scope.selectedRoutineFacility !== null)
            FacilityWithProducts.get($scope.selectedProgram,$scope.selectedRoutineFacility,$scope.homeFacility.id).then(function(data){
                $scope.routineFacility=data;
            });
    };

    $scope.loadStockCards=function(){
        StockCards.get({facilityId:$scope.homeFacility.id},function(data){
            $scope.stockCards=data.stockCards;
        });
    };
    $scope.loadDistributionsByDate=function(){
        console.log($scope.searchDate);
        console.log($scope.type);

        $scope.distributionsByDate=[];
        DistributionsByDateRange.get({facilityId:$scope.homeFacility.id,date:$scope.searchDate, endDate:$scope.endDate,type:$scope.type},function(data){
            console.log(data);
            $scope.distributionsByDate=data.distributions;
        });
    };

    $scope.searchDistributions=function(){
        $scope.dateChange=true;
        $scope.loadDistributionsByDate();
    };

    $scope.getMinDate=function(period){
        var max=new Date(period.startDate);
        max.setDate(max.getDate() -30);
        return max;
    };

    $scope.enableSearchChange=function(){

        if($scope.enableSearch ===false)
        {
            $scope.searchDate=$scope.toDay;
            $scope.loadDistributionsByDate($scope.toDay);
        }
    };

    $scope.showFacilitySearchResults=function(){
        if ($scope.query === undefined || $scope.query.length < 3) return;

        if (compareQuery()) {
            $scope.distributionsByDate=[];
            DistributionsByDateRange.get({facilityId:$scope.homeFacility.id,date:$scope.searchDate, endDate:$scope.endDate,type:$scope.type},function(data){
                console.log(data);
                $scope.distributionsByDate=data.distributions;
            });

            FacilitiesSameType.get({facilityId:homeFacility.id,query:$scope.query}, function(data){
                $scope.facilities = data.facilities;
                $scope.filteredFacilityList = $scope.facilities;
                $scope.previousQuery = $scope.query;
                $scope.facilityResultCount = $scope.filteredFacilityList.length;
                $scope.hasSearch=true;
            });

        }
        else {
            $scope.filteredFacilityList = _.filter($scope.facilities, function (facility) {
                return facility.name.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1;
            });
            $scope.facilityResultCount = $scope.filteredFacilityList.length;
            $scope.hasSearch=true;
        }
    };

    var compareQuery=function(){
        if (!isUndefined($scope.previousQuery)) {
            return $scope.query.substr(0, 3) !== $scope.previousQuery.substr(0, 3);
        }
        return true;
    };

    $scope.printAll=function(distributions){
        var ids='';
        distributions.forEach(function(d){
            if(d.isSelected)
                ids=ids + d.id + ',';
        });
        ids = ids.slice(0, -1);
        if(ids !=='')
        {
            var url='/vaccine/inventory/distribution/summary/print/'+ids+'.json';
            $window.open(url,'_blank');
        }
        else{alert('No facility selected');}
    };
    $scope.getQuantityDistributionForProduct=function(distribution,stockCard){
        var product= _.findWhere(distribution.lineItems,{productId:stockCard.product.id});
        var quantity=(product !== undefined)?product.quantity:null;
        return quantity;
    };

    $scope.getTotalDistributionForProduct=function(stockCard){
        var total=0;
        $scope.distributionsByDate.forEach(function(distribution){
            if(distribution.isSelected)
            {
                var product= _.findWhere(distribution.lineItems,{productId:stockCard.product.id});
                var quantity=(product !== undefined)?product.quantity:0;
                total=total+quantity;
            }
        });
        return total;
    };

    $scope.closeModal=function(){
        $scope.currentProduct.lots=$scope.oldProductLots;
        evaluateTotal($scope.currentProduct);
        evaluateSOH($scope.currentProduct);
        $scope.currentFacility=undefined;
        $scope.lotsModal=false;
    };
    $scope.saveCurrent=function(){
        evaluateTotal($scope.currentProduct);
        evaluateSOH($scope.currentProduct);
        $scope.currentFacility=undefined;
        $scope.lotsModal=false;
    };
    $scope.updateCurrentTotal=function(product){
        var totalCurrentLots = 0;
        if(product.lots !== undefined)
        {
            $(product.lots).each(function (index, lotObject) {
                if(lotObject.quantity !== undefined && lotObject.quantity !== null){
                    totalCurrentLots = totalCurrentLots + parseInt(lotObject.quantity,10);
                }
            });
            product.quantity=totalCurrentLots;
        }
        else{
            product.quantity=product.quantity;
        }

    };
    $scope.updateCurrentPOD=function(product){
        var totalCurrentLots = 0;
        product.podLots.forEach(function (lot) {
            if(lot.quantity !== undefined){
                totalCurrentLots = totalCurrentLots + parseInt(lot.quantity,10);
            }
        });
        product.quantity=totalCurrentLots;
    };
    function evaluateTotal(product){
        var totalLots = 0;
        if(product.lots !== undefined)
        {
            $(product.lots).each(function (index, lotObject) {
                if(lotObject.quantity !== undefined){
                    totalLots = totalLots + parseInt(lotObject.quantity,10);
                }

            });
            $scope.currentProduct.quantity=totalLots;
        }
        else{
            //$scope.currentProduct.quantity=totalLots;
        }

    }
    function getLotSum(_product,_lot){
        var total=0;
        ($scope.allScheduledFacilities).forEach(function(facility){
            var product=_.find(facility.productsToIssue,function(p){
                return p.productId ===_product.productId;
            });
            if(product.lots !== undefined){
                var lot=_.find(product.lots,function(l){
                    return l.lotId === _lot.lotId;
                });
                if(lot && lot.quantity !==undefined)
                {
                    total=total+parseInt(lot.quantity,10);
                }
            }
            else{
                if(product.quantity !== undefined){
                    total=total+parseInt(product.quantity,10);
                }

            }

        });
        return total;
    }
    function evaluateSOH(_product)
    {
        ($scope.allScheduledFacilities).forEach(function(facility){
            facility.productsToIssue.forEach(function(product){
                if(product.lots !== undefined)
                {
                    product.lots.forEach(function(lot){
                        lot.quantityOnHand=lot.quantityOnHand2-getLotSum(product,lot);
                    });
                }
                else{
                    product.totalQuantityOnHand=product.totalQuantityOnHand2-getLotSum(product,undefined);
                }

            });

        });
    }
    if($scope.userPrograms.length > 1)
    {
        $scope.showPrograms=true;
        //TODO: load stock cards on program change
        $scope.selectedProgram=$scope.userPrograms[0];
        $scope.loadSupervisedFacilities($scope.userPrograms[0]);
        $scope.loadStockCards();
        $scope.searchDate=$scope.toDay;
        $scope.loadDistributionsByDate($scope.toDay);
    }
    else if($scope.userPrograms.length === 1){
        $scope.showPrograms=false;
        $scope.selectedProgram=$scope.userPrograms[0];
        $scope.loadSupervisedFacilities($scope.userPrograms[0].id);
        $scope.loadStockCards();
        $scope.searchDate=$scope.toDay;
        $scope.loadDistributionsByDate($scope.toDay);
    }

    $scope.showIssueModal=function(facility, type){
        $scope.facilityToIssue=angular.copy(facility);
        $scope.facilityToIssue.type=type;
        $scope.issueModal=true;
    };
    $scope.closeIssueModal=function(){
        $scope.facilityToIssue=undefined;
        $scope.clearErrorMessages();
        $scope.issueModal=false;
    };
    $scope.resetUnscheduledFacility=function(){
        $scope.UnScheduledFacilityId=undefined;
        $scope.UnScheduledFacility=undefined;
    };
    $scope.showNoProductError=function(){
        $scope.showNoProductErrorMessage=true;
    };
    $scope.showFormError=function(){
        $scope.showFormErrorMessage=true;
    };
    $scope.clearErrorMessages=function(){
        $scope.showFormErrorMessage = false;
        $scope.showNoProductErrorMessage=false;
    };

    $scope.showPODModal=function(facility){
        $scope.podModal=true;
        $scope.facilityPOD=facility;
    };

    $scope.closePODModal=function(){
        $scope.podModal=false;
        $scope.facilityPOD=undefined;
    };

    $scope.print = function(distributionId){
        var url = '/vaccine/orderRequisition/issue/print/'+distributionId;
        $window.open(url, '_blank');
    };

    $scope.loadRights = function () {
        $scope.rights = localStorageService.get(localStorageKeys.RIGHT);
    }();

    $scope.hasPermission = function (permission) {
        if ($scope.rights !== undefined && $scope.rights !== null) {
            var rights = JSON.parse($scope.rights);
            var rightNames = _.pluck(rights, 'name');
            return rightNames.indexOf(permission) > -1;
        }
        return false;
    };

    $scope.cancel=function(){
        $window.location='/public/pages/vaccine/dashboard/index.html#/dashboard';
    };

    $scope.setSelectedFacility=function(facility)
    {
        if(facility)
        {
            $scope.selectedFacilityId=facility.id;
        }
        else
        {
            $scope.selectedFacilityId=null;
        }

    };

    $scope.getSelectedFacilityColor = function (facility) {
        if(facility !== undefined)
        {
            if (!$scope.selectedFacilityId) {
                return 'none';
            }

            if ($scope.selectedFacilityId== facility.id) {
                return "background-color :#dff0d8; color: white !important";
            }
            else {
                return 'none';
            }
        }

    };
    $scope.loadUnScheduledFacilities=function(){
        $scope.UnScheduledFacilities=_.where($scope.allUnScheduledFacilities,{name:$scope.facilityQuery});
    };
    $scope.hasProductToIssue=function(facility)
    {
        var hasAtLeastOne=false;
        var hasError=false;

        if(facility !==undefined && facility.productsToIssue !== undefined)
        {
            facility.productsToIssue.forEach(function(p)
            {
                if(p.quantity >0 )
                {
                    hasAtLeastOne=true;
                }
                if(p.quantity >0 && p.quantityOnHand < p.quantity)
                {
                    hasError=true;
                }
            });
            return (hasAtLeastOne && !hasError);
        }

    };
    $scope.updateCurrentScheduledFacilities = function () {
        $scope.allScheduledFacilities = [];
        $scope.query = $scope.query.trim();

        if (!$scope.query.length) {
            $scope.allScheduledFacilities = $scope.allScheduledFacilitiesCopy;
            //$scope.page();
            return;
        }

        $($scope.allScheduledFacilitiesCopy).each(function (index, facility) {
            var searchString = $scope.query.toLowerCase();
            if (facility.name.toLowerCase().indexOf(searchString) >= 0) {
                $scope.allScheduledFacilities.push(facility);
            }
        });
        //  $scope.page();
    };
    $scope.saveAll=function(){
        $scope.allScheduledFacilities.forEach(function(facility){
            if($scope.hasProductToIssue(facility) && facility.status !== "PENDING"){
                $scope.showIssueModal(facility,"SCHEDULED");
            }
        });
    };
    $scope.showMessages=function(){
        $scope.message=true;
        $scope.selectedRoutineFacility = null;
        $scope.routineFacility=false;
    };
    $scope.showPODMessages=function(){
        $scope.podMessage=true;
        $scope.selectedRoutineFacility = null;
        $scope.routineFacility=false;
    };

}
FacilityDistributionController.resolve = {

    homeFacility: function ($q, $timeout,UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility={};

        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    },
    configurations:function($q, $timeout, AllVaccineInventoryConfigurations) {
        var deferred = $q.defer();
        var configurations={};
        $timeout(function () {
            AllVaccineInventoryConfigurations.get(function(data)
            {
                configurations=data;
                deferred.resolve(configurations);
            });
        }, 100);
        return deferred.promise;
    }
};