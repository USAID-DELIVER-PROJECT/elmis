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

services.factory('StockCardsByCategory', function($resource,StockCards,$q, $timeout, VaccineProgramProducts){

 var programId;
 var facilityId;
 var programProducts=[];

 function get(pId,fId) {
    var deferred =$q.defer();
                $timeout(function(){
                    if(!isNaN(pId)){
                        VaccineProgramProducts.get({programId:pId},function(data){
                                 programProducts=data.programProductList;
                                 StockCards.get({facilityId:fId},function(data){
                                        var stockCards=data.stockCards;
                                        stockCards.forEach(function(s){
                                              var product= _.filter(programProducts, function(obj) {
                                                  return obj.product.primaryName === s.product.primaryName;
                                              });
                                                s.displayOrder=product[0].id;
                                                s.productCategory=product[0].productCategory;
                                                s.presentation=product[0].product.dosesPerDispensingUnit;
                                        });
                                        stockCards=_.sortBy(stockCards,'displayOrder');

                                        var byCategory=_.groupBy(stockCards,function(s){
                                                   return s.productCategory.name;
                                        });

                                        var stockCardsToDisplay = $.map(byCategory, function(value, index) {
                                                  return [{"productCategory":index,"stockCards":value}];
                                        });

                                        deferred.resolve(stockCardsToDisplay);
                                 });
                        });
                    }
                    else{
                            var stockCardsToDisplay=[];
                            StockCards.get({facilityId:fId},function(data){
                                var stockCards=data.stockCards;
                                if(stockCards.length > 0){
                                     stockCardsToDisplay=[{"productCategory":"no-category","stockCards":stockCards}];
                                }
                                deferred.resolve(stockCardsToDisplay);
                            });

                    }


                },100);
     return deferred.promise;

  }
return {
  get: get,
 };
});

services.factory('FacilityWithProducts', function($resource,$timeout,$q,StockCards,FacilityDistributionForecastAndLastPeriod,QuantityRequired){
     function get(program,facility, homeFacilityId) {
         var deferred =$q.defer();
                     $timeout(function(){
                         if(program !== null){
                                StockCards.get({facilityId:homeFacilityId},function(s){
                                    FacilityDistributionForecastAndLastPeriod.get({facilityId:facility.id,programId:program.id},function(distributionForecastAndPeriod){
                                        QuantityRequired.get({facilityCode:facility.code,programCode:program.code,periodId:distributionForecastAndPeriod.lastPeriod[0].id},function(report){
                                             var facilityWithProducts=new FacilitiesWithProducts(facility,s.stockCards,distributionForecastAndPeriod,report);
                                             deferred.resolve(facilityWithProducts);
                                        });
                                    });
                                });
                         }
                     },100);
          return deferred.promise;

       }
     return {
       get: get,
      };
});


services.factory('VaccineOrderRequisitionByCategory', function ($resource, VaccineOrderRequisitionGetReport, $q, $timeout, VaccineProgramProducts) {

    var programId;
    var facilityId;
    var programProducts = [];

    function get(OrderId,programId) {
        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(OrderId)) {
                VaccineProgramProducts.get({programId: parseInt(programId,10)}, function (data) {
                    programProducts = data.programProductList;

                    VaccineOrderRequisitionGetReport.get({id: parseInt(OrderId,10)}, function (data) {

                        var overallData = data.report;

                        var lineItems = data.report.lineItems;
                        lineItems.forEach(function(s){

                           // s.displayOrder=s.productId;
                            var product= _.filter(programProducts, function(obj) {
                                return obj.product.primaryName === s.product.primaryName;
                            });
                            s.displayOrder=product[0].productCategory.displayOrder;
                            s.productCategory=product[0].productCategory;

                        });

                        lineItems=_.sortBy(lineItems,'displayOrder');

                        var byCategory=_.groupBy(lineItems,function(s){
                            return s.productCategory.name;
                        });

                        var stockCardsToDisplay = $.map(byCategory, function(value, index) {
                            return {"productCategory":index,"lineItem":value};
                        });

                        var object = angular.extend({},overallData,stockCardsToDisplay);

                        deferred.resolve(object);
                    });

                });
            }
            else {
                var stockCardsToDisplay = [];
                VaccineOrderRequisitionGetReport.get({id:parseInt(OrderId,10)}, function (data) {
                    var stockCards = data.report.lineItems;
                    if (stockCards.length > 0) {
                        stockCardsToDisplay = [{"productCategory": "no-category", "stockCards": stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        }, 100);
        return deferred.promise;

    }

    return {
        get: get,
    };
});



services.factory('StockRequirementsData', function ($resource, StockRequirements, $q, $timeout, VaccineOrderRequisitionProgramProduct) {

    var programProducts = [];
    var pageLineItems = [];

    function getData(program, homeFacility) {

        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(program) && !isUndefined(homeFacility)) {

                    StockRequirements.get({facilityId: homeFacility, programId: program}, function (data) {
                        pageLineItems = data.stock_requirements;

                        var byCategory = _.groupBy(pageLineItems, function (s) {
                            return s.productCategory;

                        });
                        var stockCardsToDisplay = $.map(byCategory, function (value, index) {
                            if (index !== 'undefined') {
                                return [{"productCategory": index, "stockRequirements": value}];
                            }
                        });

                        deferred.resolve(stockCardsToDisplay);

                    });

            } else {
                var stockCardsToDisplay = [];
                StockRequirements.get({facilityId: homeFacility, programId: program}, function (data) {
                    var stockRequirements = data;
                    if (stockRequirements.length > 0) {
                        stockCardsToDisplay = [{
                            "productCategory": "no-category",
                            "stockRequirements": stockRequirements
                        }];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }
        }, 100);
        return deferred.promise;

    }

    return {
        get: getData,
    };


});





services.factory('StockCardsByCategoryAndRequisition', function ($resource, StockCards, $q, $timeout, VaccineOrderRequisitionProgramProduct, RequisitionForFacility) {

    var programId;
    var facilityId;
    var programProducts = [];
    var quantityRequested = [];

    function get(pId, fId, periodId, toFacilityId) {
        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(pId)) {
                VaccineOrderRequisitionProgramProduct.get({programId: pId}, function (data) {
                    programProducts = data.programProductList;

                    RequisitionForFacility.get({
                        programId: pId,
                        periodId: periodId,
                        facilityId: toFacilityId
                    }, function (data) {
                        quantityRequested = data.requisitionList;

                        StockCards.get({facilityId: fId}, function (data) {
                            var stockCards = data.stockCards;

                            stockCards.forEach(function (s) {

                                var product = _.filter(programProducts, function (obj) {
                                    return obj.product.primaryName === s.product.primaryName;
                                });

                                var quantityToRequest = _.filter(quantityRequested, function (obj) {
                                    return obj.productId === s.product.id;
                                });


                                s.productCategory = product[0].productCategory;


                                s.quantityRequested = quantityToRequest[0].quantityRequested;

                            });
                            var byCategory = _.groupBy(stockCards, function (s) {
                                return s.productCategory.name;
                            });

                            var stockCardsToDisplay = $.map(byCategory, function (value, index) {
                                return [{"productCategory": index, "stockCards": value}];
                            });

                            deferred.resolve(stockCardsToDisplay);
                        });

                    });

                });
            }
            else {
                var stockCardsToDisplay = [];
                StockCards.get({facilityId: fId}, function (data) {
                    var stockCards = data.stockCards;
                    if (stockCards.length > 0) {
                        stockCardsToDisplay = [{"productCategory": "no-category", "stockCards": stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        }, 100);
        return deferred.promise;

    }

    return {
        get: get,
    };
});

services.factory('StockCardsForProgramByCategory', function ($resource,StockCards, VaccineOrderRequisitionReport, RequisitionForFacility, $q, $timeout, VaccineOrderRequisitionProgramProduct) {

    var programProducts = [];
    var programId;
    var facilityId;
    var quantityRequested = [];

    function get(pId, fId, periodId, toFacilityId) {
        var deferred = $q.defer();
        $timeout(function () {
            if (!isNaN(pId)) {
                VaccineOrderRequisitionProgramProduct.get({programId: pId}, function (data) {
                    programProducts = data.programProductList;

                    RequisitionForFacility.get({
                        programId: pId,
                        periodId: periodId,
                        facilityId: toFacilityId
                    }, function (data) {
                        if(!isUndefined(data.requisitionList) || data.requisitionList !== null){

                            quantityRequested = data.requisitionList;

                            StockCards.get({facilityId: fId}, function (data) {
                                var stockCards = data.stockCards;
                                stockCards.forEach(function (s) {

                                     var lotsAscExpiration=_.sortBy(s.lotsOnHand,'lot.expirationDate');
                                     var lotsDescExpiration=lotsAscExpiration.reverse();
                                     //s.lotsOnHand=lotsDescExpiration;
                                     var lotAscVVM=_.sortBy(s.lotsOnHand,'customProps.vvmstatus');

                                     s.lotsOnHand=lotAscVVM.reverse();

                                    if (s.product.id !== undefined && quantityRequested !==undefined) {

                                        var product = _.filter(programProducts, function (obj) {
                                            if (s.product.primaryName !== undefined) {
                                                return obj.product.primaryName === s.product.primaryName;
                                            }
                                        });

                                        var quantityToRequest = _.filter(quantityRequested, function (obj) {
                                            if (obj.productId !== undefined) {
                                                return obj.productId === s.product.id;
                                            }
                                        });

                                        //console.log(product[0]);

                                        if (quantityToRequest.length > 0 && product[0].productCategory !==undefined) {
                                            s.productCategory = product[0].productCategory;
                                            s.presentation = product[0].product.dosesPerDispensingUnit;
                                            s.quantityRequested = quantityToRequest[0].quantityRequested;

                                        }
                                    }
                                });

                                var byCategory = _.groupBy(stockCards, function (s) {
                                    if(!isUndefined(s.productCategory))
                                        return s.productCategory.name;
                                });

                                var stockCardsToDisplay = $.map(byCategory, function (value, index) {
                                    if (index !== 'undefined')
                                        return [{"productCategory": index, "stockCards": value}];
                                });
                                deferred.resolve(stockCardsToDisplay);
                            });

                        } });


                });
            }
            else {
                var stockCardsToDisplay = [];
                StockCards.get({facilityId: fId}, function (data) {
                    var stockCards = data.stockCards;
                    if (stockCards.length > 0) {
                        stockCardsToDisplay = [{"productCategory": "no-category", "stockCards": stockCards}];
                    }
                    deferred.resolve(stockCardsToDisplay);
                });

            }


        }, 100);
        return deferred.promise;

    }

    return {
        get: get,
    };
});



services.factory('RequisitionForFacility', function ($resource) {
    return $resource('/vaccine/orderRequisition/getAllBy/:programId/:periodId/:facilityId.json', {
        facilityId: '@facilityId',
        programId: '@programId'
    }, {});
});



services.factory('VaccineOrderRequisitionGetReport', function ($resource) {
    return $resource('/vaccine/orderRequisition/get/:id.json', {id: '@id'}, {});
});



services.factory('VaccineOrderRequisitionReport', function ($resource) {
    return $resource('/vaccine/orderRequisition/facilities/:facilityId/programs/:programId/stockCards.json', {
        facilityId: '@facilityId',
        programId: '@programId'
    }, {});
});


services.factory('VaccineOrderRequisitionProgramProduct', function ($resource) {
    return $resource('/vaccine/orderRequisition/:programId.json', {programId: '@programId'}, {});
});

services.factory('StockRequirements', function ($resource) {
    return $resource('/rest-api/facility/:facilityId/program/:programId/stockRequirements.json',{facilityId: '@facilityId', programId: '@programId'},{});
});

