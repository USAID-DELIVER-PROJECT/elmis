function LogTagFunction($scope,LogTagInfo,$filter){

    $scope.exportReport = function (type) {
        console.log( $scope.reportParams);
        var paramString = jQuery.param($scope.reportParams);
        var url = '/reports/download/log_tag/' + type + '?' + paramString;
        window.open(url, "_BLANK");
    };

    $scope.OnFilterChanged = function(){
        // clear old data if there was any
        $scope.data = $scope.datarows = [];


        $scope.reportParams =  {
            startDate: $filter('date')($scope.getSanitizedParameter().startTime, "yyyy-MM-dd"),
            endDate: $filter('date')($scope.getSanitizedParameter().endTime, "yyyy-MM-dd")
        };
        console.log( $scope.reportParams);


        LogTagInfo.get($scope.reportParams, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });
    };
}