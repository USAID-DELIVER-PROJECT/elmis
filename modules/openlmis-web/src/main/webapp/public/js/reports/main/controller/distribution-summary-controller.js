function DistributionSummaryReportController($scope,DistributionSummaryReport){

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/distribution_summary_report/' + type + '?' + params;
        window.open(url,"_BLANK");
    };
    var arr = [];
    $scope.datarows = [];
    DistributionSummaryReport.get({}, function(data) {
        if (data.pages !== undefined && data.pages.rows !== undefined) {

            var groupByFacility = _.groupBy(data.pages.rows,'storeName');

            arr =  $.map(groupByFacility,function(value,index){
                return [value];
            }).sort(function(a, b) {
                return  b.length - a.length ;
            });
            console.log(JSON.stringify(arr));

            $scope.data = arr;
            $scope.datarows = arr;


         //   $scope.data = data.pages.rows;
            $scope.paramsChanged($scope.tableParams);
        }
    });

    $scope.OnFilterChanged = function() {
        // clear old data if there was any
 /*       $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;
        DistributionSummaryReport.get($scope.getSanitizedParameter(), function(data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                   console.log(data);
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });*/
    };


}
