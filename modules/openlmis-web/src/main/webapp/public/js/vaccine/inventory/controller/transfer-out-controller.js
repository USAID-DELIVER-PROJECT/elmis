/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI).
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


function TransferOutController($scope,$location, $document,$window,configurations,$timeout,$dialog, homeFacility,FacilitiesSameType,StockCardsByCategory,StockEvent,SaveDistribution,localStorageService) {

     $scope.userPrograms=configurations.programs;
     $scope.period=configurations.period;
     $scope.homeFacility=homeFacility;
     $scope.userPrograms=configurations.programs;
     $scope.facilityDisplayName=homeFacility.name;

     var loadStockCards=function(programId, facilityId){
         StockCardsByCategory.get(programId,facilityId).then(function(data){
             $scope.stockCards=data;
             $scope.stockCardsToDisplay= angular.copy($scope.stockCards);
         });
     };

     $scope.showFacilitySearchResults=function(){
         if ($scope.query === undefined || $scope.query.length < 3) return;

            if (compareQuery()) {
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

     $scope.setSelectedFacility = function (facility) {
         $scope.facilityToIssue = facility;
         $scope.facilitySelected = facility;
         loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.homeFacility.id,10));
         $scope.query = undefined;
         $scope.clearErrorMessages();
         $scope.hasSearch=false;
     };

     $scope.clearSelectedFacility = function () {
         $scope.facilitySelected = undefined;
         $scope.facilityToIssue = undefined;
         $scope.stockCardsToDisplay = undefined;
         $scope.clearErrorMessages();
         $scope.hasSearch=false;
     };

     var reloadStockCards=function(){
        if($scope.stockCards)
        {
            $scope.stockCardsToDisplay=angular.copy($scope.stockCards);
        }
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
        $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
      };

     if($scope.userPrograms.length > 1)
     {
          $scope.showPrograms=true;
          //TODO: load stock cards on program change
          $scope.selectedProgramId=$scope.userPrograms[0].id;
          loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.homeFacility.id,10));
     }
     else if($scope.userPrograms.length === 1){
          $scope.showPrograms=false;
          $scope.selectedProgramId=$scope.userPrograms[0].id;
          loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.homeFacility.id,10));
     }

     $scope.updateStockCardTotal=function(stockCard){
         var totalCurrentLots = 0;
         $scope.clearErrorMessages();
         if(stockCard.lotsOnHand.length >0)
         {
             $(stockCard.lotsOnHand).each(function (index, lotObject) {
                if(lotObject.quantity !== undefined && lotObject.quantity !== null){
                      totalCurrentLots = totalCurrentLots + parseInt(lotObject.quantity,10);
                }
              });
             stockCard.quantity=totalCurrentLots;
         }

     };
     $scope.distribute=function(){
        $scope.allProductsZero=true;
        $scope.clearErrorMessages();
        var printWindow;
        $scope.stockCardsToDisplay.forEach(function(category){
           category.stockCards.forEach(function(product){
               if(product.quantity > 0)
               {
                   $scope.allProductsZero=false;
               }
           });
        });
        if($scope.transferOutForm.$invalid)
        {
             $scope.showFormErrorMessage=true;
              return;
        }
        if($scope.allProductsZero){
            $scope.showNoProductErrorMessage=true;
            return;
        }

        var callBack=function(result)
        {
              if(result)
              {
                 var events=[];
                 var distribution = {};

                 distribution.fromFacilityId = $scope.homeFacility.id;
                 distribution.toFacilityId= $scope.facilitySelected.id;
                 distribution.programId=$scope.selectedProgramId;
                 distribution.distributionDate = new Date();
                 distribution.lineItems=[];
                 distribution.distributionType="TRANSFER";
                 distribution.status="PENDING";

                 $scope.stockCardsToDisplay.forEach(function(category){
                   category.stockCards.forEach(function(s){

                       if(s.quantity >0)
                       {
                           var lineItem = {};
                           lineItem.productId = s.product.id;
                           lineItem.quantity=s.quantity;
                           if(s.lotsOnHand !==undefined && s.lotsOnHand.length>0){
                               lineItem.lots = [];
                               s.lotsOnHand.forEach(function(l){
                                   if(l.quantity > 0)
                                   {
                                       var event={};
                                       var lot={};

                                       event.type= "ISSUE";
                                       event.facilityId=$scope.facilitySelected.id;
                                       event.productCode=s.product.code;
                                       event.quantity=l.quantity;
                                       event.lotId=l.lot.id;
                                       event.occurred=new Date();
                                       events.push(event);

                                       lot.lotId = l.lot.id;
                                       lot.vvmStatus=(l.customProps !==undefined && l.customProps.vvmstatus !==undefined)?l.customProps.vvmstatus:null;
                                       lot.quantity = l.quantity;
                                       lineItem.lots.push(lot);
                                   }
                                });
                           }
                           else{
                                   if(s.quantity > 0)
                                   {
                                       var event={};
                                       event.type= "ISSUE";
                                       event.facilityId=$scope.facilitySelected.id;
                                       event.productCode=s.product.code;
                                       event.quantity=s.quantity;
                                       events.push(event);


                                   }
                               }
                               distribution.lineItems.push(lineItem);
                           }
                   });
              });
              StockEvent.save({facilityId:homeFacility.id},events, function (data) {
                  if(data.success !==null)
                  {
                      $scope.message=data.success;
                      SaveDistribution.save(distribution,function(distribution){
                         $scope.distributionId=distribution.distributionId;
                         var url = '/vaccine/orderRequisition/issue/print/'+$scope.distributionId;
                         printWindow.location.href=url;
                         $timeout(function(){
                            $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
                         },900);
                      });

                   }
                   else{
                        $scope.message=data.error;
                   }
              });
              printWindow= $window.open('about:blank','_blank');

           }
        };

        var options = {
           id: "confirmDialog",
           header: "label.confirm.issue.stock.action",
           body: "msg.question.issue.stock.confirmation"
        };
        OpenLmisDialog.newDialog(options, callBack, $dialog);

     };
     $scope.clearErrorMessages=function(){
         $scope.showFormErrorMessage = false;
         $scope.showNoProductErrorMessage=false;
     };

}

TransferOutController.resolve = {

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