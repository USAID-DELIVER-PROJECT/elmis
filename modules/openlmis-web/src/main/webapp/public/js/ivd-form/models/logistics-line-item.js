var LogisticsLineItem = function(lineItem, report) {
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

  LogisticsLineItem.prototype.childrenImmunized = function(){
    if(report.coverageLineItemViews === undefined || report.coverageLineItemViews[this.productId] === undefined){
      return  '-';
    }
    return _.reduce(report.coverageLineItemViews[this.productId],
      function(sum, item2){
        return sum + item2.getMonthlyTotal();
      }, 0);
  };

  LogisticsLineItem.prototype.usageRate = function(){
    if(this.childrenImmunized() === '-'){
      return '-';
    }
    var discardedUnopened = utils.parseIntWithBaseTen(this.quantityDiscardedUnopened);
    var issued = utils.parseIntWithBaseTen(this.quantityIssued);
    var total = 0;
    if(utils.isNumber(issued)){
      total += issued;
    }
    if(utils.isNumber(discardedUnopened) ){
      total += discardedUnopened;
    }
    if(total === 0){
      return 0;
    }
    return ((this.childrenImmunized() / (total)) * 100).toFixed(2);
  };

  LogisticsLineItem.prototype.wastageRate = function(){
    if(this.usageRate() === '-'){
      return '-';
    }
    return 100 - this.usageRate();
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