function ViewTransferIssueVoucherController($scope,homeFacility,programs,DistributionByVoucherNumber,
                                    GetAllOneLevelFacilities,$window,
                                    messageService,FacilityTypeAndProgramProducts,VaccineProgramProducts,GetSameLevelFacilities,GetDistributionsByDateRangeForFacility){

    $scope.viewIssueVoucher = "Issue Voucher";

    var homeFacilityId = parseInt(homeFacility.id, 10);
    var programId = parseInt(programs[0].id, 10);

    $scope.pageSize = parseInt(10, 10);
    var pageLineItems = [];

    //$scope.supervisedFacilities = [{"id":1,"name":"Wangingombe"},{"id":2,"name":"Nyamagana"}];
    $scope.filteredRequisitions = [];
    $scope.loadRequisitions = function(){
        //console.log($scope.startDate);
        if ($scope.viewRequisitionForm && $scope.viewRequisitionForm.$invalid) {
            $scope.errorShown = true;
            return;
        }

        GetDistributionsByDateRangeForFacility.get({facilityId:$scope.selectedFacilityId,
            startDate:$scope.startDate, endDate:$scope.endDate}, function(data){
console.log(data);
            if(!isUndefined(data.distributions) && data.distributions.length > 0){
                $scope.allData = data.distributions;
                $scope.showDistribute = false;

            }else{
                $scope.showDistribute = true;
                $scope.distributionFoundMessage = 'No Previous Issue Voucher Found';
            }

        }, function () {
        });


    };

    $scope.distributionListGrid = { data: 'allData',
        displayFooter: false,
        multiSelect: false,
        /*     selectedItems: $scope.selectedItems,
         afterSelectionChange: selectionFunc,*/
        displaySelectionCheckbox: false,
        enableColumnResize: true,
        showColumnMenu: false,
        showFilter: false,
        enableSorting: true,
        sortInfo: { fields: ['distributionDate'], directions: ['desc'] },
        columnDefs: [
            {field: '', displayName: '#', cellTemplate: '<div class="ngCellText ng-scope col1 colt1" style="width: 20px !important;" ng-class="col.colIndex()"><span ng-cell-text="">{{row.rowIndex + 1}}</span></div>'},
            {field: 'voucherNumber', displayName: messageService.get("label.voucherNumber") },
            {field: 'distributionDate', displayName: messageService.get("label.vaccine.issue.date"), cellFilter: 'date:\'dd-MM-yyyy\''},
            {field: 'distributionType', displayName: messageService.get("header.issue.type")},
            {field: 'status', displayName: messageService.get("label.status")},
            {field:' ',
                cellTemplate: '<div align="center"><button style="width:80px; text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7); background-image: linear-gradient(to bottom, #42a7ad, #356b6f);background-repeat: repeat-x;border-color: rgba(255, 255, 255, 0.3) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);  background-color: #356b6f;"  type="button" class="btn btn-primary btn-small" ng-click="viewDistribution(row.entity)" >View</button>' +
                '</div> '

            }

        ]
    };
    $scope.openIssueModal = false;

    $scope.viewDistribution = function(row){
        console.log(row);
        $scope.openIssueModal = true;

        $scope.all = row;
        //console.log(row.voucherNumber);


        $scope.distribution=undefined;
        DistributionByVoucherNumber.get({voucherNumber:row.voucherNumber},function(data){
            // console.log(data);
            if(data.distribution !==null){
                $scope.distribution=data.distribution;
                categoriseDistributionLineItems($scope.distribution);
                //console.log(JSON.stringify($scope.distribution));
            }
            else{
                $scope.distribution=undefined;
                $scope.voucherNumberSearched=true;
            }
        });


        FacilityTypeAndProgramProducts.get({facilityId:homeFacilityId, programId:programId},function(data){
            var allProducts=data.facilityProduct;
            $scope.allProducts=_.sortBy(allProducts,function(product){
                return product.programProduct.product.id;
            });
        });

        VaccineProgramProducts.get({programId:programId},function(data){
            $scope.productsWithPresentation=data.programProductList;
        });


        var categoriseDistributionLineItems=function(distribution){
            distribution.lineItems.forEach(function(l){

                var programProduct= _.filter($scope.allProducts, function(obj) {
                    return obj.programProduct.product.primaryName === l.product.primaryName;
                });
                if(programProduct !==undefined)
                {
                    l.productCategory=programProduct[0].programProduct.productCategory;
                }else{
                    l.productCategory ={"name":"Uncategorised"};
                }
            });

            var byCategory = _.groupBy(distribution.lineItems, function (l) {
                return l.productCategory.name;
            });
            $scope.distribution.categorisedLineItems= $.map(byCategory, function (value, index) {
                return [{"productCategory": index, "lineItems": value}];
            });
        };

        $scope.print = function (){
            //console.log(JSON.stringify(row.lineItems[0].distributionId));
            var url = '/vaccine/orderRequisition/issue/print/'+ parseInt(row.lineItems[0].distributionId,10);
            $window.open(url, '_blank');
        };

    };

    $scope.cancelIssueModal = function(){
        $scope.openIssueModal = false;
    };

    $scope.print1 = function (distributionId){
        console.log(distributionId);
        /*  var url = '/vaccine/orderRequisition/issue/print/'+ parseInt(distributionId,10);
         $window.open(url, '_blank');*/
    };

    GetSameLevelFacilities.get({}, function(data){
        if(!isUndefined(data.facilities)) {
            $scope.supervisedFacilities = data.facilities;
        }
    });

    $scope.sumLots = function (c) {
        $scope.showNoProductErrorMessage=false;
        var total = 0;
        c.lotsOnHand.forEach(function (l) {
            var x = ((l.quantity === '' || l.quantity === undefined) ? 0 : parseInt(l.quantity, 10));
            total = total + x;

        });
        $scope.total = total;
        c.sum = parseInt(c.quantityRequested, 10) - total;
    };

}

ViewTransferIssueVoucherController.resolve = {

    homeFacility: function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility = {};

        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    },
    programs: function ($q, $timeout, VaccineHomeFacilityPrograms) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineHomeFacilityPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    }
};
