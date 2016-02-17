function ConsolidateOrderController($scope, orders, stockCards, UpdateOrderRequisitionStatus, SaveDistributionList, $filter, $location, $window, $routeParams, StockEvent) {
    $scope.inputClass = false;

    $scope.consolidatedOrders = $scope.stockCards = [];
    $scope.stockCards = stockCards;
    if (!isUndefined(orders)) {
        var groupByFacility = _.groupBy(orders, 'facilityId');

        $scope.consolidatedOrders = $.map(groupByFacility, function (value, index) {
            return [value];

        });
    }
    $scope.date = new Date();

    $scope.formDisabled = true;


    $scope.getTotalStockOnHand = function (product) {

        if (!isUndefined($scope.stockCards)) {
            var total = 0;

            angular.forEach($scope.stockCards, function (s) {

                if (s.product.primaryName === product.productName) {

                    total = s.totalQuantityOnHand;

                }

            });

            return total;
        }

    };


    $scope.sumQuantityRequested = function (product) {

        var sum = 0;

        angular.forEach($scope.consolidatedOrders, function (facility) {
            var p = _.findWhere(facility, {productName: product.productName});
            if (p !== undefined && p.quantityRequested >= 0)
                sum = sum + p.quantityRequested;

        });
        return sum;

    };

    var print = function (distributionList) {

        var url = '/vaccine/orderRequisition/consolidate/print/' + distributionList;
        $window.open(url, '_blank');
    };


    $scope.cancel = function () {
        $location.path('/');
    };

    $scope.highlightRequired = function (value) {

        if ($scope.inputClass && (isUndefined(value)) || (value === 0) ) {
            return "required-error";
        }
        return null;
    };

    $scope.validate = function(val,val2){
     return val > val2;
    };


    $scope.totalQuantityHighlighted = function(val1, val2){
        if((val1 > val2)){
            return "required-error";

        }
        return null;
    };

    var validateForm = function () {

        if ($scope.consolidatedListForm.$error.required) {
            $scope.inputClass = true;
            $scope.error = "form.error";
            $scope.message = "";
            return false;
        }
    return true;

    };
    $scope.submitConsolidated = function () {

        if(!validateForm())
            return;



        var events = [];

        var distributionLineItemList = [];

        angular.forEach($scope.consolidatedOrders, function (facility) {

            var distribution = {};
            distribution.fromFacilityId = $routeParams.homeFacility;
            distribution.toFacilityId = facility[0].facilityId;
            distribution.distributionDate = $filter('date')($scope.date, 'dd-MM-yyyy');
            distribution.periodId = facility[0].periodId;
            distribution.orderId = facility[0].orderId;
            distribution.status = "PENDING";
            distribution.distributionType = "SCHEDULED";
            distribution.remarks = facility[0].remarks;
            distribution.programId = $routeParams.program;
            distribution.lineItems = [];

            angular.forEach(facility, function (product) {

                var lineItem = {};


                if (product.quantityRequested > 0) {
                    var event = {};
                    event.type = "ISSUE";
                    event.facilityId = product.facilityId;
                    event.productCode = product.productCode;
                    event.quantity = product.quantityRequested;
                    event.customProps = {"occurred": $scope.date};
                    events.push(event);

                    lineItem.productId = product.productId;
                    lineItem.quantity = product.quantityRequested;

                }
                if (lineItem.quantity > 0) {
                    distribution.lineItems.push(lineItem);
                }


            });

            distributionLineItemList.push(distribution);


        });


        StockEvent.save({facilityId: $routeParams.homeFacility}, events, function (data) {

            SaveDistributionList.save(distributionLineItemList, function (distribution) {

                var printList = [];

                angular.forEach(distribution.distributionIds, function (distributionId) {

                    UpdateOrderRequisitionStatus.update({orderId: distributionId.orderId}, function () {
                        $scope.message = "label.form.Submitted.Successfully";


                    });
                    printList.push(parseInt(distributionId.id, 10));

                });
                print(printList);
                $scope.cancel();

            });

        });
    };


}

ConsolidateOrderController.resolve = {

    orders: function ($q, $timeout, ConsolidatedOrdersList, $routeParams) {
        var deferred = $q.defer();
        $timeout(function () {
            if (isUndefined($routeParams.program) && isUndefined($routeParams.facilityId)) {
                return null;
            } else {
                ConsolidatedOrdersList.get({
                        program: $routeParams.program,
                        facilityId: $routeParams.facilityId
                    },
                    function (data) {
                        if (!isUndefined(data.consolidatedOrders) || data.consolidatedOrders.length > 0)
                            deferred.resolve(data.consolidatedOrders);
                    });
            }


        }, 100);

        return deferred.promise;
    },


    stockCards: function ($q, $timeout, StockCards, $routeParams,$rootScope) {
        var deferred = $q.defer();
        $timeout(function () {
            if (isUndefined($routeParams.program) && isUndefined($routeParams.facilityId)) {



                return  null;
            } else {
                StockCards.get({facilityId: $routeParams.homeFacility},

                    function (data) {
                        if (!isUndefined(data.stockCards) || data.stockCards.length > 0)
                            deferred.resolve(data.stockCards);
                        else
                            $rootScope.noStockCardMessage = ' No Stock Found in your store.';
                    });
            }


        }, 100);

        return deferred.promise;
    }



};