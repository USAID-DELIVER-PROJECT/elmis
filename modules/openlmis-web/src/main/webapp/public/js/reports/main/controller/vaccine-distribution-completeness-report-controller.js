function VaccineDistributionCompletenessReportController($scope, $routeParams, VaccineDistributionCompletenessReport, Settings,
                                                         ReportProductsByProgram, TreeGeographicZoneList,
                                                         messageService, VaccineDistributedFacilitiesReport,
                                                         VaccineHomeFacilityIvdPrograms, FacilityTypeAndProgramProducts,
                                                         UserFacilityList) {

    $scope.perioderror = "";

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
    $scope.currentPage = 1;
    $scope.pageSize = 50;
    $scope.typeValue = "ROUTINE";

    //  $scope.type ='ROUTINE';

    $scope.distributionTypes = [{'name': 'ROUTINE', 'id': 1}, {'name': 'EMERGENCE', 'id': 2}];


    $scope.OnFilterChanged = function () {

        if (utils.isEmpty($scope.getSanitizedParameter().year))
            return;
        var periodFilter = ($scope.getSanitizedParameter().period === null || isUndefined($scope.getSanitizedParameter().period) || utils.isEmpty($scope.getSanitizedParameter().period)) ? 0 : $scope.getSanitizedParameter().period;
        VaccineDistributionCompletenessReport.get(
            {

                year: $scope.getSanitizedParameter().year,
                period: periodFilter,
                range: $scope.range,
                page: $scope.page,
                district: utils.isEmpty($scope.getSanitizedParameter().zone) ? 0 : $scope.getSanitizedParameter().zone.id,
                type: utils.isEmpty($scope.typeValue) ? 'ROUTINE' : $scope.typeValue,
                product: 0,
                limit: $scope.pageSize
            },

            function (data) {
                $scope.dataR = [];
                $scope.dataR = data.distributionCompleteness;
                var d = $scope.dataR;
                var arr = [];
                angular.forEach(d, function (value, key) {
                    var ed = {'percentage': (percentage(value.issued, value.expected) * 100).toFixed(0)};
                    var ed2 = {'percentageNotIssued': 100 - (percentage(value.issued, value.expected) * 100).toFixed(0)};
                    arr.push(angular.extend(value, ed, ed2));
                });

                $scope.pagination = data.pagination;
                $scope.dataRows = arr;
                $scope.totalItems = $scope.pagination.totalRecords;

                /*
                 $scope.pageCount=
                 */
                $scope.currentPage = $scope.pagination.page;


            });
    };

    function percentage(num, per) {
        return (num / per);
    }


    $scope.$watch('currentPage', function () {
        if ($scope.currentPage > 0) {
            $scope.page = $scope.currentPage;
            $scope.OnFilterChanged();
        }
    });


    $scope.loadDistributedFacilities = function () {

        VaccineDistributedFacilitiesReport.get({
                periodId: $scope.query.periodid,
                facilityId: $scope.query.facilityid,
                type: $scope.typeValue,
                page: $scope.dPage
            },
            function (data) {

                var distributedFacilities = data.distributedFacilities;
                $scope.dPagination = data.pagination;

                $scope.dTotalItems = $scope.dPagination.totalRecords;
                $scope.dCurrentPage = $scope.dPagination.page;

                var byFacility = _.groupBy(distributedFacilities, function (f) {
                    return f.tofacility;
                });
                $scope.distributedFacilities = $.map(byFacility, function (value, index) {
                    return [{"facilityName": index, "products": value}];
                });

            });

    };

    $scope.getProductQuantity = function (facilityName, productName) {
        var f = _.findWhere($scope.distributedFacilities, {facilityName: facilityName});
        if (f !== undefined)
            p = _.findWhere(f.products, {product: productName});
        if (p !== undefined)
            return p.quantity;
        else
            return null;
    };
    $scope.showDistributionModal = function (row) {
        $scope.distributionModal = true;
        $scope.query = row;
        $scope.loadDistributedFacilities();

    };
    $scope.$watch('dCurrentPage', function () {
        if ($scope.dCurrentPage > 0) {
            $scope.dPage = $scope.dCurrentPage;
            $scope.loadDistributedFacilities();
        }
    });
    $scope.closeDistributionModal = function () {
        $scope.distributionModal = false;
    };


}
