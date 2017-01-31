

function VaccineDistributionCompletenessReportController($scope, $routeParams, VaccineDistributionCompletenessReport, Settings,
                                                    ReportProductsByProgram, TreeGeographicZoneList,
                                                    messageService,VaccineDistributedFacilitiesReport,
                                                    VaccineHomeFacilityIvdPrograms,FacilityTypeAndProgramProducts,
                                                    UserFacilityList
                                                   ) {

    $scope.perioderror = "";

   VaccineHomeFacilityIvdPrograms.get({},function(p){
       var programId=p.programs[0].id;
       UserFacilityList.get({},function(f){
         var facilityId=f.facilityList[0].id;
         FacilityTypeAndProgramProducts.get({facilityId:facilityId,programId:programId},function(data){
                 var facilityProduct=data.facilityProduct;

                 $scope.facilityProduct=facilityProduct.sort(function(a,b){
                      return (a.programProduct.product.id > b.programProduct.product.id) ? 1 : ((b.programProduct.product.id > a.programProduct.product.id) ? -1 : 0);
                  });
          });
       });


   });
    $scope.OnFilterChanged = function () {

        // prevent first time loading
        if (utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEndDate) || !utils.isEmpty($scope.perioderror))
            return;
        VaccineDistributionCompletenessReport.get(
            {

                periodStart: $scope.periodStartDate,
                periodEnd:   $scope.periodEndDate,
                range:       $scope.range,
                page:        $scope.page,
                district:    utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
                product:     0
            },

            function (data) {
                $scope.dataRows=data.distributionCompleteness;

                $scope.pagination = data.pagination;
                $scope.totalItems = $scope.pagination.totalRecords;
                $scope.currentPage = $scope.pagination.page;

                console.log(data);
            });
    };

    $scope.$watch('currentPage', function () {
         if ($scope.currentPage > 0) {
               $scope.page = $scope.currentPage;
                $scope.OnFilterChanged();
         }
    });

    $scope.loadDistributedFacilities=function(){
        VaccineDistributedFacilitiesReport.get({periodId: $scope.query.periodid,
                                                      facilityId: $scope.query.facilityid,
                                                      page:$scope.dPage,},
                                                      function(data){

                                                        var distributedFacilities=data.distributedFacilities;
                                                          console.log(distributedFacilities);
                                                        $scope.dPagination = data.pagination;
                                                        $scope.dTotalItems = $scope.dPagination.totalRecords;
                                                        $scope.dCurrentPage = $scope.dPagination.page;

                                                        var byFacility=_.groupBy(distributedFacilities,function(f){
                                                            return f.tofacility;
                                                        });
                                                        $scope.distributedFacilities=$.map(byFacility,function(value, index){
                                                           return [{"facilityName":index,"products":value}];
                                                        });

                                                      });

    };

    $scope.getProductQuantity=function(facilityName,productName){
        var f=_.findWhere($scope.distributedFacilities,{facilityName:facilityName});
        if(f !== undefined)
        p =_.findWhere(f.products,{product:productName});
        if(p !== undefined)
        return p.quantity;
        else
        return null;
    };
   $scope.showDistributionModal=function(row){
      $scope.distributionModal=true;
      $scope.query=row;
       console.log(row);
      $scope.loadDistributedFacilities();

   };
   $scope.$watch('dCurrentPage', function () {
            if ($scope.dCurrentPage > 0) {
                  $scope.dPage = $scope.dCurrentPage;
                   $scope.loadDistributedFacilities();
            }
   });
   $scope.closeDistributionModal=function(){
       $scope.distributionModal=false;
   };



}
