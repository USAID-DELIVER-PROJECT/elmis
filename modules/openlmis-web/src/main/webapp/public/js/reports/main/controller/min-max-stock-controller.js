function MinMaxVaccineStockReportController($scope, $window, MinMaxStockReport) {

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/min_max_vaccine_report/' + type + '?' + params;
        $window.open(url, "_BLANK");
    };

    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;

        MinMaxStockReport.get($scope.getSanitizedParameter(), function (data) {
            console.log(data);
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

    };


}
