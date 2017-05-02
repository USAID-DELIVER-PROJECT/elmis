var VaccineReport = function (report) {

  $.extend(this, report);

  report.period.days = Math.abs((report.period.endDate - report.period.startDate) / (1000 * 60 * 60 * 24));

  VaccineReport.prototype.init = function () {

    this.month = new Date(this.period.startDate).getMonth() + 1;

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

    this.products = _.pluck(this.logisticsLineItems, 'product');

    this.mainProducts = _.where(this.products, {fullSupply: true});
    this.coverageLineItems = getCoverageLineItems(this.coverageLineItems, this);
    this.logisticsLineItems = getLogisticsLineItems(this.logisticsLineItems, this);
    this.vaccineLogisticsLineItems = _.filter(this.logisticsLineItems, function(obj) {
      return obj.product.fullSupply;
    });
    this.coverageLineItemViews = [];
    var coverages = _.groupBy(this.coverageLineItems, 'productId');
    var productIdsInOrder = _.uniq(_.pluck(this.coverageLineItems,'productId'));
    //insert these groups in the order of products
    for (var i = 0; i < productIdsInOrder.length; i++) {
      var coverageGroup = coverages[productIdsInOrder[i]];
      if (coverageGroup !== undefined) {
        this.coverageLineItemViews.push(coverageGroup);
      }
   }
    this.initializeDropouts();
    this.editable = (this.status === 'DRAFT' || this.status === 'REJECTED');
    this.ready = true;
  };

  VaccineReport.prototype.initializeDropouts = function () {
    var bcg = _.filter(this.coverageLineItems, function (obj) {
      return (obj.product.code === 'V001');
    });
    this.bcg = bcg[0];

    var mr = _.filter(this.coverageLineItems, function (obj) {
      return (obj.product.code === 'V009');
    });
    this.mr1 = mr[0];

    var dpt = _.filter(this.coverageLineItems, function (obj) {
      return (obj.product.code === 'V010');
    });

    this.dpt1 = dpt[0];
    this.dpt3 = dpt[2];
  };

  VaccineReport.prototype.getBcgDropout = function () {
    return (this.bcg.getTotalRegular() === 0) ? 0 :
      ((this.bcg.getTotalRegular() - this.mr1.getTotalRegular()) / this.bcg.getTotalRegular()) * 100;
  };

  VaccineReport.prototype.getDptDropout = function () {
    return (this.dpt1.getTotalRegular() === 0) ? 0 :
      ((this.dpt1.getTotalRegular() - this.dpt3.getTotalRegular()) / this.dpt1.getTotalRegular() ) * 100;
  };

  VaccineReport.prototype.setSkip = function (collectionName, value) {
    angular.forEach(this[collectionName], function (item) {
      item.skipped = value;
    });
  };

  VaccineReport.prototype.isCoverageTabValid = function () {
    return this.coverageLineItems.checkIfRequiredFieldsAreValid(['regularMale', 'regularFemale', 'outreachMale', 'outreachFemale']);
  };

  VaccineReport.prototype.isReportingDateValid = function () {
    return !(this.submissionDate === undefined || this.submissionDate === null);
  };

  VaccineReport.prototype.isLogisticsTabValid = function () {
    if (!this.logisticsLineItems.checkIfRequiredFieldsAreValid(['openingBalance', 'quantityReceived', 'quantityIssued', 'closingBalance'])) {
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
      submissionDate: this.isReportingDateValid(),
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
