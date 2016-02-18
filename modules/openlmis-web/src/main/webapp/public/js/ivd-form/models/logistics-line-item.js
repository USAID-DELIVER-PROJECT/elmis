var LogisticsLineItem = function(lineItem) {
  $.extend(this, lineItem);


  LogisticsLineItem.prototype.isValid = function(){
    return this.isClosingBalanceValid();
  };

  LogisticsLineItem.prototype.isClosingBalanceValid = function(){
    if(utils.parseIntWithBaseTen(this.closingBalance) < 0){
      return false;
    }
    return (this.skipped || utils.parseIntWithBaseTen( this.closingBalance ) === (utils.parseIntWithBaseTen(this.openingBalance) + utils.parseIntWithBaseTen(this.quantityReceived) - utils.parseIntWithBaseTen(this.quantityIssued)) - utils.parseIntWithBaseTen(this.quantityDiscardedUnopened));
  };

  LogisticsLineItem.prototype.calculateClosingBalance = function(){
    if(utils.isNumber(this.openingBalance) && utils.isNumber(this.quantityReceived) && utils.isNumber(this.quantityIssued)){
      var discarded = 0;
      if(utils.isNumber(this.quantityDiscardedUnopened)){
        discarded = this.quantityDiscardedUnopened;
      }else{
        this.quantityDiscardedUnopened = 0;
      }
      this.closingBalance = (utils.parseIntWithBaseTen(this.openingBalance) + utils.parseIntWithBaseTen(this.quantityReceived) - utils.parseIntWithBaseTen(this.quantityIssued)) - utils.parseIntWithBaseTen(discarded);
    }
  };

};