function StockLedgerFunction($scope, $window, $filter, $stateParams, StockLedgerReport) {

    $scope.stateParams = $stateParams;
    var endYear = new Date(new Date().getFullYear(), 11, 31);
    var starYear = new Date(new Date().getFullYear(), 0, 1);


    var param = {
        product: parseInt($scope.stateParams.idElement, 10),
        facility: parseInt($scope.stateParams.facilityId, 10),
        startDate: $filter('date')(starYear, "yyyy-MM-dd"),
        endDate: $filter('date')(endYear, "yyyy-MM-dd"), max: 10000
    };

    if ($scope.filter === undefined || $scope.filter === undefined) {

        $scope.data = $scope.datarows = [];

        StockLedgerReport.get(param, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

        $scope.isFilterChanged = false;

    }

    $scope.exportReport1 = function (type) {
        $scope.pdformat = 1;
        var params = jQuery.param(param);
        var url = '/reports/download/stock_ledger/' + type + '?' + params;
        $window.open(url, "_BLANK");
    };


    $scope.OnFilterChanged = function () {

        $scope.isFilterChanged = true;

        var endYear = new Date($scope.filter.year, 11, 31);
        var starYear = new Date($scope.filter.year, 0, 1);

        var param2 = {
            product: parseInt($scope.stateParams.idElement, 10),
            facility: parseInt($scope.stateParams.facilityId, 10),
            startDate: $filter('date')(starYear, "yyyy-MM-dd"),
            endDate: $filter('date')(endYear, "yyyy-MM-dd"), max: 10000
        };

        $scope.exportReport2 = function (type) {
            $scope.pdformat = 1;
            var params = jQuery.param(param2);
            var url = '/reports/download/stock_ledger/' + type + '?' + params;
            $window.open(url, "_BLANK");
        };


        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        StockLedgerReport.get(param, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

    };

    $scope.formatNumber = function (value) {
        return utils.formatNumber(value, '0,0.00');
    };


    $scope.showPopover = false;

    $scope.popover = {
        title: 'Manufacturer Name',
        message: 'Message'
    };

}