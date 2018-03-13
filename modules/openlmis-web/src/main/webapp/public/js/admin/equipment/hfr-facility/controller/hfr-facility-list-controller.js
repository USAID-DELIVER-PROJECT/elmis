function HFRControllerFunc($scope, GetHFRFacilities) {
    GetHFRFacilities.get({}, function (data) {
        console.log(data);
        $scope.facilities = data.facilities;
    });
}