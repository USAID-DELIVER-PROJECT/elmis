function CCEInventoryReport($scope,$window,ColdChainEquipmentService){


    $scope.cceReportParams =  {
        facilityLevel: $scope.facilityLevel
    };
    $scope.OnFilterChanged = function() {
        ColdChainEquipmentService.get($scope.cceReportParams, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });
    };
    $scope.filter = {};
    $scope.filter.max = 10000;

    $scope.exportReport = function (type)
    {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);

        var sortOrderParams = jQuery.param($scope.tableParams.sorting);
        sortOrderParams = sortOrderParams.split('=');
        sortOrderParams = { sortBy:sortOrderParams[0], order:sortOrderParams[1] };
        sortOrderParams = jQuery.param(sortOrderParams);

        var url = '/reports/download/cold_chain_equipment/' + type + '?' + sortOrderParams +'&'+ params;
        $window.open(url);
    };




 /*   $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
        $scope.filter.max = 10000;


        ColdChainEquipmentService.get($scope.cceReportParams, function (data) {
            console.log(data);
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;
                $scope.paramsChanged($scope.tableParams);
            }
        });

    };*/


    $scope.getRowFacilityAddress = function(row)
    {
        var ret = '';
        if (row.facilityAddress1)
            ret = row.facilityAddress1;
        if (row.facilityAddress2)
            ret += ' ' +  row.facilityAddress2;
        return ret;
    };



    $scope.getLargestRecordShown = function()
    {
        var max = $scope.tableParams.page * $scope.tableParams.count;
        if($scope.pages)
            return ($scope.pages.total > max) ? max : $scope.pages.total;
        else
            return max;
    };
}