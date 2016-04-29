/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

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
