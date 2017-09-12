function StockLedgerFunction($scope,$window, $filter, $stateParams,StockLedgerReport){
   // $scope.userName='Hassan';
    $scope.idElement = $stateParams.idElement;
    console.log($stateParams);

console.log($stateParams);


    /*if($scope.filter==undefined || $scope.filter == undefined ){

        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        $scope.filter.startDate = $filter('date')($scope.filter.startTime, "yyyy-MM-dd");

        $scope.filter.endDate = $filter('date')($scope.filter.endTime, "yyyy-MM-dd");
        $scope.product= $stateParams.idElement;
        StockLedgerReport.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

    }*/





    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/stock_ledger/' + type + '?' + params;
        $window.open(url, "_BLANK");
    };

    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        $scope.filter.startDate = $filter('date')($scope.filter.startTime, "yyyy-MM-dd");

        $scope.filter.endDate = $filter('date')($scope.filter.endTime, "yyyy-MM-dd");

        StockLedgerReport.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

    };

    $scope.formatNumber = function (value) {
        return utils.formatNumber(value, '0,0.00');
    };


    $scope.showPopover=false;

    $scope.popover = {
        title: 'Manufacturer Name',
        message: 'Message'
    };

}