
function RejectionByZoneControllerFunction($scope,$state,$stateParams,GetRejectedRnR) {
    "use strict";
    $scope.zone = $stateParams.zone;

    GetRejectedRnR.get($stateParams,function (data) {

        if(data.length !==null){
            $scope.rejects = data.rejected;
            $scope.pagination = data.pagination;
        }

        console.log(JSON.stringify($scope.pagination));

    });

}