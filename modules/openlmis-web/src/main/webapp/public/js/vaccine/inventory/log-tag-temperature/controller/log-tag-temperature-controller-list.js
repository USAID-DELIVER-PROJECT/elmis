function LogTagTemperatureListController($scope,logTags){
console.log(logTags);
    $scope.logTags = logTags;

}

LogTagTemperatureListController.resolve = {

    logTags: function ($q, $timeout, GetAllLogTagTemperature) {
        var deferred = $q.defer();
        $timeout(function () {
            GetAllLogTagTemperature.get({}, function (data) {
                deferred.resolve(data.logTags);
            }, {});
        }, 100);
        return deferred.promise;
    }


};