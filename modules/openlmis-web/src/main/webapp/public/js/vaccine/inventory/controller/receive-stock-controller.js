/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


function ReceiveStockController($scope, StockCards,$timeout,$window,$dialog,configurations,homeFacility,SaveDistribution,VaccineProgramProducts,Distribution, ProductLots,StockEvent,localStorageService,$location, $anchorScroll) {

    $scope.hasStock=homeFacility.hasStock;
    console.log($scope.hasStock);
    $scope.userPrograms=configurations.programs;
    $scope.facilityDisplayName=homeFacility.name;
    $scope.selectedProgramId=null;
    $scope.receivedProducts=[];
    $scope.productToAdd={};
    $scope.productToAdd.lots=[];
    $scope.lotToAdd={};
    $scope.vvmStatuses=[{"value":1,"name":" 1 "},{"value":2,"name":" 2 "}];
    $scope.voucherNumberSearched=false;
    $scope.productsConfiguration=configurations.productsConfiguration;
    $scope.period=configurations.period;
    $scope.loadProducts=function(programId){
        VaccineProgramProducts.get({programId:programId},function(data){
            $scope.allProducts=data.programProductList;
            $scope.productsToDisplay=$scope.allProducts;
        });
    };
    $scope.loadProductLots=function(product)
    {
         $scope.lotsToDisplay=null;


         if(product !==null)
         {
             var id=product.id;
             config=_.filter(configurations.productsConfiguration, function(obj) {
                   return obj.product.id===id;
             });
             if(config.length > 0)
             {
               $scope.productToAdd.batchTracked=config[0].batchTracked;
               $scope.productToAdd.vvmTracked=config[0].vvmTracked;
             }
             else if(config.length ===0){
                $scope.productToAdd.batchTracked=true;
                $scope.productToAdd.vvmTracked=false;
             }
             if($scope.productToAdd.batchTracked)
             {
                ProductLots.get({productId:product.id},function(data){
                     $scope.allLots=data.lots;
                     $scope.lotsToDisplay=$scope.allLots;
                });
             }

         }
    };
    $scope.receive=function()
    {

        var callBack=function(result)
        {
            if(result)
            {
                var events=[];
                $scope.distribution.lineItems.forEach(function(p){
                if(p.lots !==undefined && p.lots.length >0)
                {
                    p.lots.forEach(function(l){
                        var event={};
                        event.type="RECEIPT";
                        event.facilityId=homeFacility.id;
                        event.productCode=p.product.code;
                        event.quantity=l.quantity;
                        event.lot={};
                        event.lot.lotCode=l.lot.lotCode;
                        event.lot.manufacturerName=l.lot.manufacturerName;
                        var expirationDate=new Date(l.lot.expirationDate);
                        expirationDate.setDate(expirationDate.getDate() +1);
                        event.lot.expirationDate=expirationDate;
                        event.customProps={};
                        if(l.vvmStatus !==undefined)
                        {
                            event.customProps.vvmStatus=l.vvmStatus;
                        }
                        event.customProps.receivedFrom=$scope.distribution.fromFacility.name;
                        events.push(event);
                    });
                }
                else{
                        var event={};
                        event.type="RECEIPT";
                        event.facilityId=homeFacility.id;
                        event.productCode=p.product.code;
                        event.quantity=p.quantity;
                        if(p.vvmStatus !==undefined)
                        {
                            event.customProps={"vvmStatus":p.vvmStatus};
                        }
                        event.customProps.receivedFrom=$scope.distribution.fromFacility.name;
                        events.push(event);
                }

             });

            StockEvent.update({facilityId:homeFacility.id},events, function (data) {
                 if(data.success)
                 {
                     $scope.message=true;
                      $scope.distribution.status='RECEIVED';
                     SaveDistribution.save($scope.distribution,function(distribution){
                         $timeout(function(){
                               $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
                         },900);
                     });

                 }
            });

            }
        };
        var options = {
                    id: "confirmDialog",
                    header: "label.confirm.receive.stock.action",
                    body: "msg.question.receive.stock.confirmation"
                };
        OpenLmisDialog.newDialog(options, callBack, $dialog);

    };

    $scope.openReceive=function(){
        console.log(JSON.stringify($scope.receivedProducts));
        var callBack=function(result)
                {
                    if(result)
                    {
                        var events=[];
                        $scope.receivedProducts.forEach(function(p){
                        if(p.lots !==undefined && p.lots.length >0)
                        {
                            p.lots.forEach(function(l){
                                var event={};
                                event.type="RECEIPT";
                                event.facilityId=homeFacility.id;
                                event.productCode=p.product.code;
                                event.quantity=l.quantity;
                                event.lot={};
                                event.lot.lotCode=l.lot.lotCode;
                                event.lot.manufacturerName=l.lot.manufacturerName;
                                var expirationDate=new Date(l.lot.expirationDate);
                                expirationDate.setDate(expirationDate.getDate() +1);
                                event.lot.expirationDate=expirationDate;
                                event.customProps={};
                                if(l.vvmStatus !==undefined)
                                {
                                    event.customProps.vvmStatus=l.vvmStatus;
                                }
                                event.customProps.receivedFrom="";
                                events.push(event);
                            });
                        }
                        else{
                                var event={};
                                event.type="RECEIPT";
                                event.facilityId=homeFacility.id;
                                event.productCode=p.product.code;
                                event.quantity=p.quantity;
                                if(p.vvmStatus !==undefined)
                                {
                                    event.customProps={"vvmStatus":p.vvmStatus};
                                }
                                events.push(event);
                        }

                     });


                    StockEvent.update({facilityId:homeFacility.id},events, function (data) {
                         if(data.success)
                         {
                             $scope.message=true;
                            $timeout(function(){
                             $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
                            },900);

                         }
                    });

                    }
                };
                var options = {
                            id: "confirmDialog",
                            header: "label.confirm.receive.stock.action",
                            body: "msg.question.receive.stock.confirmation"
                        };
                OpenLmisDialog.newDialog(options, callBack, $dialog);
    };




    $scope.cancel=function(){
       $window.location='/public/pages/vaccine/inventory/dashboard/index.html#/stock-on-hand';
    };
    if($scope.userPrograms.length > 1)
    {
           $scope.showPrograms=true;
           //TODO: load stock cards on program change
           $scope.loadProducts($scope.userPrograms[0].id);
    }
     else if($scope.userPrograms.length === 1){
          $scope.showPrograms=false;
          $scope.loadProducts($scope.userPrograms[0].id);
     }

    $scope.removeProduct=function(product)
    {
         var index = $scope.receivedProducts.indexOf(product);
         $scope.receivedProducts.splice(index, 1);
         updateProductToDisplay($scope.receivedProducts);
    };

    $scope.addProduct=function(productToAdd){
        $scope.receivedProducts.push(productToAdd);
        $scope.productToAdd={};
        $scope.productToAdd.lots=[];
        updateProductToDisplay($scope.receivedProducts);
        $location.hash('scroll-to-lot');
        $anchorScroll();
    };
    $scope.addLot=function(lotToAdd){
            $scope.productToAdd.lots.push(lotToAdd);
            $scope.lotToAdd={};
            updateLotsToDisplay($scope.productToAdd.lots);
            $location.hash('scroll-to-lot');
            $anchorScroll();
    };

    $scope.removeProductLot=function(lot){
            var index = $scope.productToAdd.lots.indexOf(lot);
            $scope.productToAdd.lots.splice(index, 1);
            updateLotsToDisplay($scope.productToAdd.lots);
    };


    $scope.removeReceivedLot=function(product,lot)
    {
            if(product.lots.length ===1)
            {
                $scope.removeProduct(product);
            }
            else{
                 var productIndex = $scope.receivedProducts.indexOf(product);
                 var lotIndex = $scope.receivedProducts[productIndex].lots.indexOf(lot);
                 $scope.receivedProducts[productIndex].lots.splice(lotIndex, 1);
            }
     };


    function updateProductToDisplay(receivedProducts)
    {
             var toExclude = _.pluck(_.pluck(receivedProducts, 'product'), 'primaryName');
             $scope.productsToDisplay = $.grep($scope.allProducts, function (productObject) {
                  return $.inArray(productObject.product.primaryName, toExclude) == -1;
              });
    }

    function updateLotsToDisplay(lotsToAdd)
    {
             var toExclude = _.pluck(_.pluck(lotsToAdd, 'lot'), 'lotCode');
             $scope.lotsToDisplay = $.grep($scope.allLots, function (lotObject) {
                   return $.inArray(lotObject.lotCode, toExclude) == -1;
             });
    }



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

     $scope.sumLots = function(product) {
            var total=0;
            angular.forEach(product.lots , function(lot){
              total+= parseInt(lot.quantity,10);
            });
            product.quantity=total;
            return total;
     };
     $scope.loadDistribution=function(){
        $scope.distribution=undefined;
        Distribution.get({voucherNumber:$scope.receivedProducts.voucherNumber},function(data){
            if(data.distribution !==null){
                 $scope.distribution=data.distribution;
                }
            else{
                $scope.distribution=undefined;
                $scope.voucherNumberSearched=true;
            }
        });

     };

     $scope.clear=function(){
        $scope.distribution=undefined;
        $scope.voucherNumberSearched=false;
     };

}
ReceiveStockController.resolve = {

        homeFacility: function ($q, $timeout,UserFacilityList,StockCards) {
            var deferred = $q.defer();
            var homeFacility={};

            $timeout(function () {
                   UserFacilityList.get({}, function (data) {
                           homeFacility = data.facilityList[0];
                           StockCards.get({facilityId:homeFacility.id},function(data){
                             if(data.stockCards.length> 0)
                             {
                                homeFacility.hasStock=true;
                             }
                             else{
                               homeFacility.hasStock=false;
                             }
                             deferred.resolve(homeFacility);
                           });

                   });

            }, 100);
            return deferred.promise;
         },
        configurations:function($q, $timeout, VaccineInventoryConfigurations) {
          var deferred = $q.defer();
          var configurations={};
          $timeout(function () {
          VaccineInventoryConfigurations.get(function(data)
             {
                 configurations=data;
                 deferred.resolve(configurations);
             });
          }, 100);
          return deferred.promise;
        }
};