var VaccineReport = function(report){

  $.extend(this, report);

  VaccineReport.prototype.init = function(){
    function getLineItems(collection, r){
      var lineItems = [];
      angular.forEach(collection, function(coverage){
          lineItems.push(new CoverageLineItem(coverage, r));
      });
      return lineItems;
    }
    this.coverageLineItems = getLineItems(this.coverageLineItems, this);
    this.coverageLineItemViews = _.groupBy(this.coverageLineItems, 'productId');
    this.editable = (this.status === 'DRAFT' || this.status === 'REJECTED');
  };



  this.init();
};
