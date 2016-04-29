/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */
var OrderRequisitionLineItem = function(stockCards,report){

    $.extend(this, stockCards);

    OrderRequisitionLineItem.prototype.getQuantityToRequest = function() {
       var quantity = (this.maximumStock - Number(parseInt(this.stockOnHand,10)) );
           this.quantityRequested = quantity;
        return quantity;

    };

    OrderRequisitionLineItem.prototype.getMaximumStock = function(){
     var max= (Number(parseInt(this.overriddenisa,10)) * Number(this.maxmonthsofstock));
        this.maximumStock = max;
        return max;
    };
    OrderRequisitionLineItem.prototype.getMinimumStock = function(){
       var min = ( Number(this.overriddenisa) * Number(this.minmonthsofstock));
        this.minimumStock = min;
        return min;
    };

    OrderRequisitionLineItem.prototype.getReorderLevel= function(){
         var reorder = (Number(this.overriddenisa) * Number(this.eop));
        this.reOrderLevel = reorder;
        return reorder;
    };

    OrderRequisitionLineItem.prototype.getBufferStock = function(){
        var buffer = (this.getReorderLevel() - this.getMaximumStock());
        this.bufferStock = buffer;
        return buffer;

    };

    OrderRequisitionLineItem2.prototype.totalStockOnHand = function(){
        return Math.ceil(Number(this.stockOnHand) / this.getTotalByVial());
    };

    //Added for forecast purpose
    OrderRequisitionLineItem.prototype.getIsaValue = function(){

        var isaValue = (this.getReorderLevel() - this.getMaximumStock());
        this.bufferStock = buffer;
        return buffer;

    };

    OrderRequisitionLineItem.prototype.calculate = function(){

       var isaVal = parseInt(Math.ceil(Number(1000) *
            (this.whoRatio / 100) * this.dosesPerYear * this.wastageFactor / 12 *
            (1 + this.bufferPercentage / 100) + this.adjustmentValue ),10);
        this.isaValue = isaVal;
        return isaVal;

    };

    OrderRequisitionLineItem.prototype.getTotalByVial = function(){
        return Number(this.product.dosesPerDispensingUnit);
    };

};
