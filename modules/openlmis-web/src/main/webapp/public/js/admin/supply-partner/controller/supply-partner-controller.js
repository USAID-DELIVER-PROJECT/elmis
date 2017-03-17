/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function SupplyPartnerController($scope, $routeParams, $location, programs, supervisoryNodes, requisitionGroups, UpdateSupplyPartner, CreateSupplyPartner, SupplyPartner, FacilityListByCode, ProductListByCode) {
  $scope.supervisoryNodes = supervisoryNodes;
  $scope.programs = programs;
  $scope.requisitionGroups = requisitionGroups;

  $scope.supplyPartner = {subscribedPrograms: [], isActive: true};

  if ($routeParams.id !== undefined) {
    SupplyPartner.get({id: $routeParams.id}, function (data) {
      $scope.supplyPartner = data.supply_partner;
      if ($scope.supplyPartner.subscribedPrograms.length > 0) {
        $scope.supplyPartner.primaryProgramId = $scope.supplyPartner.subscribedPrograms[0].destinationProgramId;
      }
    });
  }

  $scope.addNewPrograms = function () {
    $scope.supplyPartner.subscribedPrograms.push({destinationProgramId: $scope.supplyPartner.primaryProgramId});
  };


  $scope.save = function () {
    if ($scope.supplyPartner.id === undefined) {
      CreateSupplyPartner.post($scope.supplyPartner, function (data) {
        $location.path('/');
      });
    }
    else {
      UpdateSupplyPartner.update($scope.supplyPartner, function (data) {
        $location.path('/');
      });
    }
  };

  $scope.closeFacilitiesModal = function () {
    $scope.facilitiesModal = false;
  };

  $scope.addFacilitiesByCode = function (facilityCodeList) {
    if (facilityCodeList === undefined) {
      return;
    }
    var facilityCodes = facilityCodeList.split('\n');
    if (facilityCodes.length > 0) {
      FacilityListByCode.post(facilityCodes, function (data) {
        if (isUndefined($scope.currentProgramSubscription.facilities)) {
          data.facilities.forEach(function (d) {
              d.active = true;
              d.facilityId = d.id;
            }
          );

          $scope.currentProgramSubscription.facilities = data.facilities;
        } else {
          var productCodes = _.pluck($scope.currentProgramSubscription.facilities, 'code');

          data.facilities.forEach(function (f) {
            if (!_.contains(productCodes, f.code)) {
              f.active = true;
              f.facilityId = f.id;
              $scope.currentProgramSubscription.facilities.push(f);
              productCodes.push(f.code);
            }
          });
        }
      });
    }
  };

  $scope.showFacilitiesModal = function (currentSubscription) {
    $scope.currentProgramSubscription = currentSubscription;
    $scope.facilitiesModal = true;
  };

  $scope.closeProductsModal = function () {
    $scope.productsModal = false;
  };

  $scope.addProductsByCode = function (productCodeList) {
    if (productCodeList === undefined) {
      return;
    }
    var productCodes = productCodeList.split('\n');
    if (productCodes.length > 0) {
      ProductListByCode.post(productCodes, function (data) {
        if (isUndefined($scope.currentProgramSubscription.products)) {
          data.products.forEach(function (d) {
            d.active = true;
            d.productId =  d.id;
          });
          $scope.currentProgramSubscription.products = data.products;
        } else {
          var productCodes = _.pluck($scope.currentProgramSubscription.products, 'code');
          data.products.forEach(function (f) {
            if (!_.contains(productCodes, f.code)) {
              f.active = true;
              f.productId =  f.id;
              $scope.currentProgramSubscription.products.push(f);
              productCodes.push(f.code);
            }
          });
        }
      });
    }
  };

  $scope.showProductsModal = function (currentSubscription) {
    $scope.currentProgramSubscription = currentSubscription;
    $scope.productsModal = true;
  };

}

SupplyPartnerController.resolve = {

  supervisoryNodes: function ($q, $timeout, SupervisoryNodes) {
    var deferred = $q.defer();
    $timeout(function () {
      SupervisoryNodes.get({}, function (data) {
        deferred.resolve(data.supervisoryNodes);
      }, {});
    }, 100);
    return deferred.promise;
  },
  requisitionGroups: function ($q, $timeout, RequisitionGroups) {
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionGroups.get({columnName: 'name',searchParam : '%', page: 1}, function (data) {
        deferred.resolve(data.requisitionGroupList);
      }, {});
    }, 100);
    return deferred.promise;
  },
  programs: function ($q, $timeout, Programs) {
    var deferred = $q.defer();
    $timeout(function () {
      Programs.get({}, function (data) {
        deferred.resolve(data.programs);
      }, {});
    }, 100);
    return deferred.promise;
  }
};