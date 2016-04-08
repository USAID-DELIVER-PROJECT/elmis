var FacilitiesWithProducts = function (facility,stockCards,distributionForecastAndPeriod,report) {
    $.extend(this, facility);
    var productsToIssue=[];
    var distribution=distributionForecastAndPeriod.distribution;
    var currentPeriod=distributionForecastAndPeriod.currentPeriod;
    var forecast=distributionForecastAndPeriod.forecast;
    var programProducts=distributionForecastAndPeriod.programProductList;

    FacilitiesWithProducts.prototype.init = function () {

              var facilityDistribution=(distribution ===null)?undefined:distribution;

              this.status=(facilityDistribution !== undefined)?facilityDistribution.status:undefined;
              this.voucherNumber=(facilityDistribution !== undefined)?facilityDistribution.voucherNumber:undefined;
              this.distributionDate=(facilityDistribution !== undefined)?facilityDistribution.distributionDate:undefined;
              this.distributionId=(facilityDistribution !== undefined)?facilityDistribution.id:undefined;
              this.toFacilityId=(facilityDistribution !== undefined)?facilityDistribution.toFacilityId:undefined;
              this.fromFacilityId=(facilityDistribution !== undefined)?facilityDistribution.fromFacilityId:undefined;
              this.currentPeriod=currentPeriod;
              this.equipments=report.status.equipments;

              //Add Products array to facility object
              stockCards.forEach(function(stockCard){
                  var product={};
                  var distributedProduct;
                  if(facilityDistribution !== undefined)
                  {
                      distributedProduct=_.findWhere(facilityDistribution.lineItems,{productId:stockCard.product.id});
                  }

                  product.name=stockCard.product.primaryName;
                  product.productId=stockCard.product.id;
                  product.productCode=stockCard.product.code;
                  product.totalQuantityOnHand=stockCard.totalQuantityOnHand;
                  product.dosageUnit=stockCard.product.dosageUnit.code;
                  var programProduct= _.filter(programProducts, function(obj) {
                        return obj.product.primaryName === stockCard.product.primaryName;
                  });
                  product.displayOrder=programProduct[0].id;
                  product.productCategory=programProduct[0].productCategory;
                  product.presentation=programProduct[0].product.dosesPerDispensingUnit;

                  //Set Quantity Required
                  reportProduct=_.findWhere(report.status.products,{productCode:stockCard.product.code});
                  forecastProduct=_.findWhere(forecast,{productId:stockCard.product.id});
                  if(reportProduct !== undefined && forecastProduct !==undefined )
                  {
                    var quantityRequired=forecastProduct.maximumStock-reportProduct.stockStatus;
                    product.quantityRequired=(quantityRequired >0)?quantityRequired:0;
                  }
                  else{
                    product.quantityRequired="-";
                  }
                  product.quantity=(distributedProduct===undefined || facilityDistribution.status==="RECEIVED")?null:distributedProduct.quantity;
                  product.lineItemId=(distributedProduct===undefined)?null:distributedProduct.id;
                  if(facilityDistribution !== undefined && facilityDistribution.status==="PENDING" && distributedProduct !== undefined)
                  {
                      product.isPOD=true;
                      product.maximum=product.quantity + product.totalQuantityOnHand;
                      product.originalIssueQuantity=product.quantity;
                  }

                  //Add lots to product to be added to facility object
                  if(stockCard.lotsOnHand !== undefined && stockCard.lotsOnHand.length >0){
                       product.lots=[];
                       stockCard.lotsOnHand.forEach(function(lot){
                           var lotOnHand={};
                           var distributedLot;
                           if(distributedProduct !== undefined)
                           {
                               distributedLot=_.findWhere(distributedProduct.lots,{lotId:lot.lot.id});
                           }
                           lotOnHand.lotId=lot.lot.id;
                           lotOnHand.lotCode=lot.lot.lotCode;
                           lotOnHand.quantityOnHand=lot.quantityOnHand;
                           lotOnHand.quantity=(distributedLot === undefined || facilityDistribution.status==="RECEIVED" )?null:distributedLot.quantity;
                           lotOnHand.lineItemLotId=(distributedLot === undefined)?null:distributedLot.id;
                           lotOnHand.vvmStatus=(lot.customProps !== undefined && lot.customProps !== null && lot.customProps.vvmstatus !== undefined)?lot.customProps.vvmstatus:null;
                           lotOnHand.expirationDate=lot.lot.expirationDate;
                            product.lots.push(lotOnHand);
                       });

                       var lotsAscExpiration=product.lots.sort(function(a,b){
                           return (a.expirationDate > b.expirationDate) ? 1 : ((b.expirationDate > a.expirationDate) ? -1 : 0);
                       });
                       var lotsDescVvmStatus=lotsAscExpiration.sort(function(a,b){
                            return (a.vvmStatus > b.vvmStatus) ? -1 : ((b.vvmStatus > a.vvmStatus) ? 1 : 0);
                       });

                       product.lots=lotsDescVvmStatus;
                  }
                  //Add lot object for POD to make sure zero lotOnHand appear during POD
                  if(facilityDistribution !== undefined && facilityDistribution.status ==="PENDING" && distributedProduct !== undefined && distributedProduct.lots.length >0){
                      product.podLots=[];
                      distributedProduct.lots.forEach(function(lot){
                            var podLot={};
                            var sohLot;
                            if(stockCard.lotsOnHand !== null)
                            sohLot=_.findWhere(stockCard.lotsOnHand,{lotId:lot.lotId});

                            podLot.lineItemLotId=lot.id;
                            podLot.lotId=lot.lotId;
                            podLot.lotCode=lot.lot.lotCode;
                            podLot.quantityOnHand=(sohLot === undefined)?0:sohLot.quantityOnHand;
                            podLot.quantity=lot.quantity;
                            podLot.originalIssueQuantity=lot.quantity;
                            podLot.vvmStatus=lot.vvmStatus;
                            podLot.maximum= podLot.quantityOnHand + podLot.quantity;
                            product.podLots.push(podLot);

                      });
                  }
                  productsToIssue.push(product);
           });
           productsToIssue=_.sortBy(productsToIssue,'displayOrder');
           var byCategory=_.groupBy(productsToIssue,function(p){
               return p.productCategory.name;
           });

           var byCategoryArray = $.map(byCategory, function(value, index) {
                return [{"productCategory":index,"productsToIssue":value}];
           });
           this.productsToIssue=productsToIssue;
           this.productsToIssueByCategory=byCategoryArray;
    };
    this.init();
};
