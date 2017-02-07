function DistributionSummaryReportController($scope,DistributionSummaryReport) {

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/distribution_summary_report/' + type + '?' + params;
        window.open(url, "_BLANK");
    };

    var preparedData = [];
    $scope.datarows = $scope.dataToShow = [];


    DistributionSummaryReport.get({}, function (data) {

        if (data.pages != undefined) {

            preparedData = data.pages.rows;

            $scope.headCells = _.keys(_.groupBy(preparedData, function (item) {
                return item.product
            }));
            $scope.rows = _.groupBy(preparedData, function (item) {
                return item.storeName;
            });

            $scope.sortByProductProp = function (values) {

                return _.sortBy(values, function (value) {
                    return value.product;
                });
            }
        }


    })
}
