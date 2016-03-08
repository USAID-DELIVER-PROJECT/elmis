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
