
function RejectionByZoneControllerFunction($scope,$state,$stateParams) {
    "use strict";
    console.log($stateParams);
    $scope.zone = $stateParams.zone;

}