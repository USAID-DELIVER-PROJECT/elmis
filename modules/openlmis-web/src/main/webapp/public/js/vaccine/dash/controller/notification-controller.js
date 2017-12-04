function NotificationControllerFunc($scope,VaccineDashboardSummary){

    VaccineDashboardSummary.get({}, function (data) {
        $scope.reportingPerformance = data.summary.reportingSummary;

        $scope.repairing = data.summary.repairing;
        $scope.investigating = data.summary.investigating;
    });


}
NotificationControllerFunc.resolve = {



};