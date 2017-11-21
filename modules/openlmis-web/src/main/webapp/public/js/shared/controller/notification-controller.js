function VimsNotificationController($scope, BatchExpiryNotification, VaccineDashboardSummary, ReceiveDistributionAlert, MinimumStockNotification, VaccinePendingRequisitions, UserFacilityList) {

    $scope.reportingPerformance = {};

    $scope.repairing = {};
    $scope.supplying = {};
    $scope.investigating = {};

function dashboardSummaryCallBack() {

    VaccineDashboardSummary.get({}, function (data) {

        $scope.reportingPerformance = data.summary.reportingSummary;

        $scope.repairing = data.summary.repairing;
        $scope.investigating = data.summary.investigating;
        $scope.notificationCount = parseInt($scope.reportingPerformance.expected, 10) +
            parseInt($scope.investigating.count, 10) +
            parseInt($scope.reportingPerformance.late, 10);


    });
}
    dashboardSummaryCallBack();

    var homeFacility = {};

    UserFacilityList.get({}, function (data) {

        homeFacility = data.facilityList[0];
        VaccinePendingRequisitions.get({facilityId: parseInt(homeFacility.id, 10)}, function (data) {
            $scope.supplyingPendingReceive = {};
            $scope.supplyingAllPendingOrders = data.pendingRequest;
            $scope.supplyingPendingReceive.supplyingPendingToReceive = data.pendingToReceive;
            $scope.supplyingPendingReceive.supplyingPendingToReceiveLowerLevel = data.pendingToReceiveLowerLevel;
            //  $scope.supplyingPendingReceive.daysForUnreceivedNotification= daysNotReceive;

            if (data.pendingRequest !== undefined)
                $scope.supplying.orders = data.pendingRequest.length;
            else {
                $scope.supplying.orders = 0;
            }
        });
    });


    MinimumStockNotification.get({}, function (data) {
        var summary = [];
        if (!isUndefined(data)) {
            summary = data;
            $scope.totalMinimumStock = summary.minimumStock.length;

        }

    });


    ReceiveDistributionAlert.get(function (data) {

        if (data !== undefined) {

            $scope.totalReceive = data.receiveNotification.length;
        }

    });


    function expiryCallBack() {

        BatchExpiryNotification.get({}, function (data) {
            var expires = [];
            if (!isUndefined(data.expiries)) {
                expires = data.expiries;
                $scope.totalBatchToExpire = expires.length;
            }

        });
    }

    expiryCallBack();

}

