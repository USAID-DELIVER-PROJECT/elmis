/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

var OrderRequisitionLineItem2 = function(stockCards,report){

    $.extend(this, stockCards);

    OrderRequisitionLineItem2.prototype.getQuantityToRequest = function() {
        var quantity = Math.ceil(this.maximumStock - Number(parseInt(this.stockOnHand,10)) / this.getTotalByVial());
        this.quantityRequested = quantity;
        return quantity;

    };

    OrderRequisitionLineItem2.prototype.getMaximumStock = function(){
        var max= Math.ceil((Number(parseInt(this.overriddenisa,10) / this.getTotalByVial()) * Number(this.maxmonthsofstock)));
        this.maximumStock = max;
        return max;
    };
    OrderRequisitionLineItem2.prototype.getMinimumStock = function(){
        var min = Math.ceil( (Number(this.overriddenisa) / this.getTotalByVial()) * (Number(this.minmonthsofstock)/ this.getTotalByVial()));
        this.minimumStock = min;
        return min;
    };

    OrderRequisitionLineItem2.prototype.getReorderLevel= function(){
        var reorder = Math.ceil(((Number(this.overriddenisa) ) * (Number(this.eop)))/ this.getTotalByVial());
        this.reOrderLevel = reorder;
        return reorder;
    };

    OrderRequisitionLineItem2.prototype.getBufferStock = function(){
        var buffer = (this.getReorderLevel() - this.getMaximumStock());
        this.bufferStock = buffer;
        return buffer;

    };

    OrderRequisitionLineItem2.prototype.getTotalByVial = function(){
        return Number(this.product.dosesPerDispensingUnit);
    };

    OrderRequisitionLineItem2.prototype.getTotalStockOnHand = function(){
        return Math.ceil(Number(this.stockOnHand) / this.getTotalByVial());
    };


};
