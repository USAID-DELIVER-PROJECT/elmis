var VaccineDistribution = function (existingDistribution,receivedProducts,orderNumber,orderDate, supervisorId, homeFacilityId, programId) {
    var distribution={};

    VaccineDistribution.prototype.init = function () {
        if(supervisorId !== undefined)
        {
            distribution.id=(existingDistribution === undefined)?null:existingDistribution.id;
            distribution.fromFacilityId =(supervisorId === null)?homeFacilityId:supervisorId ;
            distribution.toFacilityId=homeFacilityId ;
            distribution.voucherNumber=orderNumber;
            distribution.programId=programId;
            distribution.distributionDate = (existingDistribution === undefined)?orderDate:existingDistribution.distributionDate;
            distribution.distributionType= (existingDistribution === undefined)?"ROUTINE":existingDistribution.distributionType;
            distribution.status="RECEIVED";
            distribution.lineItems=[];
            receivedProducts.forEach(function(product){
                if(product.quantity >0)
                {
                   var productItem = {};
                   var existingProduct;
                   if(existingDistribution !==undefined)
                   {
                       existingProduct=_.findWhere(existingDistribution.lineItems,{productId:product.product.id});
                   }

                   productItem.id=(existingProduct ===undefined)?null:existingProduct.id;
                   productItem.productId = product.product.id;
                   productItem.quantity=product.quantity;

                   if(product.lots !==undefined && product.lots.length >0)
                   {
                       productItem.lots = [];
                       product.lots.forEach(function(l)
                       {

                           if(l.quantity !==null && l.quantity >0)
                           {
                               var lotItem = {};
                               var existingLot;
                               if(existingProduct !==undefined && existingProduct.lots !==undefined && existingProduct.lots.length >0)
                               {
                                   existingLot=_.findWhere(existingProduct.lots,{lotId:l.lot.id});
                               }
                               lotItem.id=(existingLot === undefined)?null:existingLot.id;
                               lotItem.lotId = l.lot.id;
                               lotItem.vvmStatus=l.vvmStatus;
                               lotItem.quantity = l.quantity;
                               productItem.lots.push(lotItem);
                           }
                       });
                   }
                   distribution.lineItems.push(productItem);
                }
            });
        }
         $.extend(this, distribution);
    };
    this.init();
};
