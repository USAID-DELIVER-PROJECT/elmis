function ManageStockOnHandControllerFunc($window, $scope, $state, StockCards, $stateParams) {
    "use strict";
    $scope.print = function (param) {
        var url = '/vaccine/inventory/distribution/stock-on-hand/print/' + param;
        $window.open(url, "_BLANK");
    };

    $scope.stateParams = $stateParams;
    $scope.stockCards = [];
    $scope.facilityName = $stateParams.facilityName;
    $scope.facilityId = $stateParams.facilityId;
    $scope.product = $stateParams.product;

    StockCards.get({facilityId: parseInt($stateParams.facilityId, 10)},
        function (data) {
            $scope.stockCards = data.stockCards;
        });

}

ManageStockOnHandControllerFunc.resolve = {};