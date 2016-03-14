function FacilityEstimateModel() {

  FacilityEstimateModel.prototype.getByCategory = function (category, year) {
    var categoryValue = _.findWhere(this.facilityEstimates, {
      'demographicEstimateId': category.id
    });

    if (angular.isUndefined(categoryValue)) {
      var programId = (this.facilityEstimates !== undefined && this.facilityEstimates.length > 0) ? this.facilityEstimates[0].programId : undefined;
      categoryValue = {
        'demographicEstimateId': category.id,
        'programId': programId,
        'year': year,
        'conversionFactor': category.defaultConversionFactor,
        'value': 0
      };
      this.facilityEstimates.push(categoryValue);
    }
    return categoryValue;
  };

  FacilityEstimateModel.prototype.populationChanged = function (autoCalculate) {
    if (autoCalculate) {
      var population = _.findWhere(this.facilityEstimates, {
        'demographicEstimateId': 1
      });
      var pop = Number(population.value);
      angular.forEach(this.facilityEstimates, function (estimate) {
        if (population.demographicEstimateId !== estimate.demographicEstimateId) {
          estimate.value = Math.round(estimate.conversionFactor * pop / 100);
        }
      });
    }
  };

}


function AggregateFacilityEstimateModel(facilityList, districts, categories, year) {

  this.categories = categories;

  this.indexedList = _.groupBy(facilityList, 'parentId');

  AggregateFacilityEstimateModel.prototype.getSummary = function (district, category, year) {
    var facilities = this.indexedList[district];
    var sum = 0;
    angular.forEach(facilities, function (facility) {
      var val = facility.getByCategory(category, year);
      sum = sum + Number(val.value);
    });
    return sum;
  };

  AggregateFacilityEstimateModel.prototype.getDistrictEntry = function (district, category) {
    var districtLineItem = _.findWhere(districts.estimates.estimateLineItems, {id: district});
    if (districtLineItem) {
      var estimateEntry = _.findWhere(districtLineItem.districtEstimates, {
        'demographicEstimateId': category.id
      });
      if (estimateEntry) {
        return estimateEntry.value;
      }
    }
    return 0;
  };

  AggregateFacilityEstimateModel.prototype.isValid = function (district) {
    for (var i = 0; i < this.categories.length; i++) {
      if (this.getSummary(district, this.categories[i], year) !== this.getDistrictEntry(district, this.categories[i])) {
        return false;
      }
    }
    return true;
  };
}


function FacilityDemographicsForm($scope, facilities, districts) {

  FacilityDemographicsForm.prototype.init = function ($scope, facilities, districts) {
    $scope.lineItems = [];
    var finalizedCount = 0;
    var draftCount = 0;
    angular.forEach(facilities.estimates.estimateLineItems, function (item) {
      $.extend(item, new FacilityEstimateModel());
      if (item.facilityEstimates[0].isFinal) {
        finalizedCount++;
      } else {
        draftCount++;
      }
      $scope.lineItems.push(item);
    });

    $scope.pageCount = Math.round($scope.lineItems.length / $scope.pageSize);

    facilities.estimates.estimateLineItems = [];
    $scope.form = facilities.estimates;
    $scope.currentPage = 1;
    $scope.pageLineItems();


    if (finalizedCount > 0 && draftCount === 0) {
      $scope.formStatus = 'Finalized';
    } else {
      $scope.formStatus = (finalizedCount > 0 && draftCount > 0) ? 'Partial' : 'Draft';
    }


    this.districtIds = _.pluck(facilities.estimates.estimateLineItems, 'parentId');
    this.districts = districts;
    this.districts.estimates.estimateLineItems = _.filter(districts.estimates.estimateLineItems, function (item) {
      return _.contains(this.ids, item.id);
    }, {"ids": this.districtIds});
    $scope.districtSummary = new AggregateFacilityEstimateModel($scope.lineItems, this.districts, $scope.categories, $scope.year);
    this.districtSummary = $scope.districtSummary;
  };

  FacilityDemographicsForm.prototype.isValid = function () {
    for (var i = 0; i < this.districts.estimates.estimateLineItems.length; i++) {
      var district = this.districts.estimates.estimateLineItems[i];
      if (!this.districtSummary.isValid(district.id)) {
        return false;
      }
    }
    return true;
  };

  this.init($scope, facilities, districts);
}
