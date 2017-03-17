function SupplyPartnerSearchController($scope, supplyPartners) {

  $scope.list = supplyPartners;
}

SupplyPartnerSearchController.resolve = {
  supplyPartners: function ($q, $timeout, SupplyPartners) {
    var deferred = $q.defer();
    $timeout(function () {
      SupplyPartners.get({}, function (data) {
        deferred.resolve(data.supply_partners);
      }, {});
    }, 100);
    return deferred.promise;
  }
};