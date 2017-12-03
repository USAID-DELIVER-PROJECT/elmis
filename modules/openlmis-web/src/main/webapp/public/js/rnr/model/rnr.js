/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var Rnr = function (rnr, programRnrColumns, numberOfMonths, operationalStatuses) {
  
  // separate the skipped products from the not so skipped. 
  rnr.allSupplyLineItems = rnr.fullSupplyLineItems;
  if(rnr.program && rnr.program.hideSkippedProducts){
    rnr.skippedLineItems = _.where(rnr.allSupplyLineItems, { skipped:true});
    rnr.fullSupplyLineItems =  _.where(rnr.allSupplyLineItems, {skipped: false});
  }
  rnr.operationalStatusList = operationalStatuses;

  $.extend(true, this, rnr);
  var thisRnr = this;
  this.skipAll = false;
  this.numberOfMonths = numberOfMonths;

  var getInvalidLineItemIndexes = function (lineItems) {
    var errorLineItems = [];
    $(lineItems).each(function (i, lineItem) {
      if (!lineItem.valid()) errorLineItems.push(i);
    });
    return errorLineItems;
  };

  Rnr.prototype.getFullSupplyErrorLineItemIndexes = function () {
    return getInvalidLineItemIndexes(this.fullSupplyLineItems);
  };

  Rnr.prototype.getNonFullSupplyErrorLineItemIndexes = function () {
    return getInvalidLineItemIndexes(this.nonFullSupplyLineItems);
  };

  Rnr.prototype.getRegimenErrorLineItemIndexes = function () {

    var errorLineItems = [];
    $(this.regimenLineItems).each(function (i, lineItem) {
      if(lineItem.hasError){
        errorLineItems.push(i);
      }
    });
    return errorLineItems;
  };

  Rnr.prototype.getErrorPages = function (pageSize) {
    function getErrorPages(lineItems) {
      var pagesWithErrors = [];
      $(lineItems).each(function (i, lineItem) {
        pagesWithErrors.push(Math.ceil((lineItem + 1) / pageSize));
      });
      return _.uniq(pagesWithErrors, true);
    }

    function getFullSupplyPagesWithError() {
      var fullSupplyErrorLIneItems = thisRnr.getFullSupplyErrorLineItemIndexes();
      return getErrorPages(fullSupplyErrorLIneItems);
    }

    function getNonFullSupplyPagesWithError() {
      var nonFullSupplyErrorLIneItems = thisRnr.getNonFullSupplyErrorLineItemIndexes();
      return getErrorPages(nonFullSupplyErrorLIneItems);
    }

    function getRegimenPagesWithError(){
      var regimenErrorLineItems = thisRnr.getRegimenErrorLineItemIndexes();
      return getErrorPages(regimenErrorLineItems);
    }

    var errorPages = {};
    errorPages.fullSupply = getFullSupplyPagesWithError();
    errorPages.nonFullSupply = getNonFullSupplyPagesWithError();
    errorPages.regimen = getRegimenPagesWithError();
    return errorPages;
  };

  Rnr.prototype.validateFullSupply = function () {
    var errorMessage = "";

    function validateRequiredFields(lineItem) {
      if (lineItem.validateRequiredFieldsForFullSupply()) return true;
      errorMessage = "error.rnr.validation";

      return false;
    }

    function validateFormula(lineItem) {
      if (lineItem.formulaValid()) return true;
      errorMessage = "error.rnr.validation";

      return false;
    }

    function validateEquipmentStatus(lineItem){
      lineItem.isEquipmentValid = true;
      if(!isUndefined(lineItem.equipments) && ((lineItem.calculatedOrderQuantity > 0 && utils.isEmpty(lineItem.quantityRequested)) || utils.parseIntWithBaseTen(lineItem.quantityRequested) > 0 )){
        angular.forEach(lineItem.equipments, function(equipment){
          var status = _.findWhere(operationalStatuses, {'id': equipment.operationalStatusId});
          if(!isUndefined(status) && status.isBad && utils.isEmpty(equipment.remarks)){
            lineItem.isEquipmentValid = false;
          }
        });
      }
      return lineItem.isEquipmentValid;
    }

    $(this.fullSupplyLineItems).each(function (i, lineItem) {
      if (lineItem.skipped)
        return;
      if (!validateRequiredFields(lineItem))
        return false;
      if (!validateFormula(lineItem))
        return false;
      if (!validateEquipmentStatus(lineItem))
        return false;
    });
    return errorMessage;
  };

  Rnr.prototype.validateEquipments = function(){
    var errorMessage = null;
    var rnr = this;
    var invalidLineItems = _.map(this.equipmentLineItems, function(equipmentLineItem){
      var currentStatus = _.findWhere(operationalStatuses, {'id': utils.parseIntWithBaseTen(equipmentLineItem.operationalStatusId)});

      if(equipmentLineItem.isEquipmentValid === false){
            subErrorMessage = 'error.rnr.equipment.non.functional.days.invalid';
        return true;
      }
      else if (!isUndefined(currentStatus)  && (!currentStatus.isBad || ! utils.isEmpty( equipmentLineItem.remarks)) ) {
        return false;
      }

      return _.any(equipmentLineItem.relatedProducts, function (product) {
        var lineItem = _.findWhere(rnr.fullSupplyLineItems, {productCode: product.code});
        if (!isUndefined(lineItem) && utils.parseIntWithBaseTen(lineItem.quantityRequested) > 0) {
            subErrorMessage = 'error.rnr.equipment.non.functional.requires.remarks';
          return true;
        }
        return false;
      });
    });

    var hasInvalidLineItems = _.any(invalidLineItems, function(invalid){
      return invalid;
    });

    if(true === hasInvalidLineItems){
      errorMessage = subErrorMessage;
    }
    return errorMessage;
  };

  Rnr.prototype.validateNonFullSupply = function () {
    var errorMessage = "";

    var validateRequiredFields = function (lineItem) {
      if (lineItem.validateRequiredFieldsForNonFullSupply()) return true;
      errorMessage = "error.rnr.validation";

      return false;
    };

    $(this.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (!validateRequiredFields(lineItem)) return false;
    });
    return errorMessage;
  };

  Rnr.prototype.validateFullSupplyForApproval = function () {
    var error = '';
    $(this.fullSupplyLineItems).each(function (i, lineItem) {
      if (!lineItem.skipped && isUndefined(lineItem.quantityApproved)) {
        error = 'error.rnr.validation';
        return false;
      }
    });
    return error;
  };

  Rnr.prototype.validateNonFullSupplyForApproval = function () {
    var error = '';
    $(this.nonFullSupplyLineItems).each(function (i, lineItem) {
      if (isUndefined(lineItem.quantityApproved)) {
        error = 'error.rnr.validation';
        return false;
      }
    });
    return error;
  };

  Rnr.prototype.validateManualTest = function(){
      var error = '';
      $(this.manualTestLineItems).each(function (i, lineItem) {
          if (isUndefined(lineItem.testCount)) {
              error = 'error.rnr.validation';
              return false;
          }
      });
      return error;
  };

  var calculateTotalCost = function (rnrLineItems) {
    if (rnrLineItems === null) return;

    var cost = 0;
    for (var lineItemIndex in rnrLineItems) {
      var lineItem = rnrLineItems[lineItemIndex];
      if (utils.isNumber(lineItem.cost) && !lineItem.skipped) {
        cost += parseFloat(lineItem.cost);
      }
    }
    return cost.toFixed(2);
  };

  Rnr.prototype.calculateFullSupplyItemsSubmittedCost = function () {
    this.fullSupplyItemsSubmittedCost = calculateTotalCost(this.fullSupplyLineItems);
  };

  Rnr.prototype.calculateNonFullSupplyItemsSubmittedCost = function () {
    this.nonFullSupplyItemsSubmittedCost = calculateTotalCost(this.nonFullSupplyLineItems);
  };

  Rnr.prototype.calculateTotalLineItemCost = function () {
    var cost = parseFloat(parseFloat(this.fullSupplyItemsSubmittedCost) + parseFloat(this.nonFullSupplyItemsSubmittedCost)).toFixed(2);
    if (this.allocatedBudget && this.program.budgetingApplies) {
      this.costExceedsBudget = this.allocatedBudget < cost;
    }
    return cost;
  };

  Rnr.prototype.fillCost = function (isFullSupply) {
    if (isFullSupply)
      this.calculateFullSupplyItemsSubmittedCost();
    else
      this.calculateNonFullSupplyItemsSubmittedCost();
  };

  Rnr.prototype.fillConsumptionOrStockInHand = function (rnrLineItem) {
    rnrLineItem.fillConsumptionOrStockInHand();
    this.fillCost(rnrLineItem.fullSupply);
  };

  Rnr.prototype.fillNormalizedConsumption = function (rnrLineItem) {
    rnrLineItem.fillNormalizedConsumption();
    this.fillCost(rnrLineItem.fullSupply);
  };

  Rnr.prototype.fillPacksToShip = function (rnrLineItem) {
    rnrLineItem.fillPacksToShip();
    this.fillCost(rnrLineItem.fullSupply);
  };

  Rnr.prototype.periodDisplayName = function () {
    return this.period.stringStartDate + ' - ' + this.period.stringEndDate;
  };

  Rnr.prototype.reduceForApproval = function () {
    var rnr = _.pick(this, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
    rnr.fullSupplyLineItems = _.map(rnr.fullSupplyLineItems, function (rnrLineItem) {
      return rnrLineItem.reduceForApproval();
    });
    rnr.nonFullSupplyLineItems = _.map(rnr.nonFullSupplyLineItems, function (rnrLineItem) {
      return rnrLineItem.reduceForApproval();
    });
    return rnr;
  };

  Rnr.prototype.initEquipments = function(){
    var rnr = this;
    angular.forEach(this.equipmentLineItems, function(eLineItem){
      angular.forEach(eLineItem.relatedProducts, function(product){
        var lineItem = _.findWhere(rnr.fullSupplyLineItems, {productCode: product.code});
        if(!isUndefined(lineItem) && isUndefined(lineItem.equipments)){
          lineItem.equipments = [];
          lineItem.equipments.push(eLineItem);
        }else if(!isUndefined(lineItem)){
          lineItem.equipments.push(eLineItem);
        }
      });
    });
  };

  Rnr.prototype.init = function () {
    var thisRnr = this;
    function prepareLineItems(lineItems) {
      var regularLineItems = [];
      $(lineItems).each(function (i, lineItem) {
        var regularLineItem = new RegularRnrLineItem(lineItem, thisRnr.numberOfMonths, programRnrColumns, thisRnr.status);
        regularLineItems.push(regularLineItem);
      });
      return regularLineItems;
    }

    this.fullSupplyLineItems = prepareLineItems(this.fullSupplyLineItems);
    this.nonFullSupplyLineItems = prepareLineItems(this.nonFullSupplyLineItems);
    this.nonFullSupplyLineItems.sort(function (lineItem1, lineItem2) {
      if (isUndefined(lineItem1))
        return 1;
      return lineItem1.compareTo(lineItem2);
    });
    this.programRnrColumnList = programRnrColumns;

    this.calculateFullSupplyItemsSubmittedCost();
    this.calculateNonFullSupplyItemsSubmittedCost();
    this.initEquipments();
  };

  this.init();

};
