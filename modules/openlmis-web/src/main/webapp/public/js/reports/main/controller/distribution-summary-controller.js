function DistributionSummaryReportController($scope, DistributionSummaryReport, VaccineHomeFacilityIvdPrograms, UserFacilityList, FacilityTypeAndProgramProducts) {


    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url = '/reports/download/distribution_summary_report/' + type + '?' + params;
        window.open(url, "_BLANK");
    };

    VaccineHomeFacilityIvdPrograms.get({}, function (p) {
        var programId = p.programs[0].id;
        UserFacilityList.get({}, function (f) {
            var facilityId = f.facilityList[0].id;
            FacilityTypeAndProgramProducts.get({facilityId: facilityId, programId: programId}, function (data) {
                var facilityProduct = data.facilityProduct;
                $scope.facilityProduct = facilityProduct.sort(function (a, b) {
                    return (a.programProduct.product.id > b.programProduct.product.id) ? 1 : ((b.programProduct.product.id > a.programProduct.product.id) ? -1 : 0);
                });
            });
        });


    });

    $scope.OnFilterChanged = function () {

        $scope.datarows = $scope.dataToShow = [];
        $scope.data = $scope.datarows = [];
        // $scope.filter.max = 10000;
        // $scope.filter.page = 1;

        DistributionSummaryReport.get($scope.getSanitizedParameter(), function (data) {

            if (data.pages !== undefined) {

                $scope.datarows = data.pages.rows;
                var distributedFacilities = data.pages.rows;
                var byFacility = _.groupBy(distributedFacilities, function (f) {
                    return f.facilityName;
                });
                $scope.distributedFacilities = $.map(byFacility, function (value, index) {
                    return [{"facilityName": index, "products": value}];
                });

                $scope.paramsChanged($scope.tableParams);

            }

        });
    };

    $scope.getProductQuantity = function (facilityName, productName) {

        var f = _.findWhere($scope.distributedFacilities, {facilityName: facilityName});
        if (f !== undefined)
            p = _.findWhere(f.products, {product: productName});
        if (p !== undefined)
            return p.quantityIssued;
        else
            return null;
    };

}
