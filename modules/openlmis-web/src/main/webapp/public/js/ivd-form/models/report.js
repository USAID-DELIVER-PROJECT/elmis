var VaccineReport = function (report) {

  $.extend(this, report);

  VaccineReport.prototype.init = function () {

    function getCoverageLineItems(collection, r) {
      var lineItems = [];
      angular.forEach(collection, function (coverage) {
        lineItems.push(new CoverageLineItem(coverage, r));
      });
      return lineItems;
    }

    function getLogisticsLineItems(collection, r) {
      var lineItems = [];
      angular.forEach(collection, function (lineItem) {
        lineItems.push(new LogisticsLineItem(lineItem, r));
      });
      return lineItems;
    }

    this.products  = _.pluck(this.logisticsLineItems, 'product');

    this.mainProducts = _.where(this.products, {fullSupply: true});
    this.coverageLineItems = getCoverageLineItems(this.coverageLineItems, this);
    this.logisticsLineItems = getLogisticsLineItems(this.logisticsLineItems, this);
    this.coverageLineItemViews = _.groupBy(this.coverageLineItems, 'productId');
    this.editable = (this.status === 'DRAFT' || this.status === 'REJECTED');
  };


  VaccineReport.prototype.setSkip = function (collectionName, value) {
    angular.forEach(this[collectionName], function (item) {
      item.skipped = value;
    });
  };

  VaccineReport.prototype.isCoverageTabValid = function () {
    return this.coverageLineItems.checkIfRequiredFieldsAreValid(['regularMale', 'regularFemale','outreachMale','outreachFemale']);
  };

  VaccineReport.prototype.isLogisticsTabValid = function () {
    if(!this.logisticsLineItems.checkIfRequiredFieldsAreValid(['openingBalance', 'quantityReceived', 'quantityIssued','closingBalance'])){
      return false;
    }
    for (var i = 0; i < this.logisticsLineItems.length; i++) {
      if (!this.logisticsLineItems[i].skipped && !this.logisticsLineItems[i].isValid()) {
        return false;
      }
    }
    return true;
  };

  VaccineReport.prototype.isDiseaseTabValid = function () {
    return this.diseaseLineItems.checkIfRequiredFieldsAreValid(['cases', 'death']);
  };

  VaccineReport.prototype.isVitaminTabValid = function () {
    return this.vitaminSupplementationLineItems.checkIfRequiredFieldsAreValid(['maleValue', 'femaleValue']);
  };

  VaccineReport.prototype.isColdChainTabValid = function () {
    return this.coldChainLineItems.checkIfRequiredFieldsAreValid(['minTemp', 'maxTemp', 'minEpisodeTemp', 'maxEpisodeTemp', 'operationalStatusId']);
  };

  VaccineReport.prototype.isValid = function () {
    this.validate = true;
    this.validations = {
        logisticsTab: this.isLogisticsTabValid(),
        diseaseTab: this.isDiseaseTabValid(),
        coverageTab: this.isCoverageTabValid(),
        coldChainTab: this.isColdChainTabValid(),
        vitaminTab: this.isVitaminTabValid()
    };
    return (_.without(_.values(this.validations), true).length === 0);
  };
  this.init();
};


Array.prototype.checkIfRequiredFieldsAreValid = function (fields) {
  for (var i = 0; i < this.length; i++) {
    if (this[i].skipped) {
      continue;
    }
    for (var f = 0; f < fields.length; f++) {
      if (!utils.isNumber(this[i][fields[f]])) {
        return false;
      }
    }
  }
  return true;
};