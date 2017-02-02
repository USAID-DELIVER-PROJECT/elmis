function OnTimeInFullListFunction($scope, OnTimeInFull) {

    $scope.distributedOrders = OnTimeInFull;





}

OnTimeInFullListFunction.resolve = {

    OnTimeInFull: function ($q, $route, $timeout, GetVaccineOnTimeInFullList) {
        var deferred = $q.defer();
        $timeout(function () {
            if (isUndefined($route.current.params.id) && isUndefined($route.current.params.facilityId)
            ) {
                return null;
            } else {

                var queryParameters = {
                    facilityId: parseInt($route.current.params.facilityId, 10),
                    orderId: parseInt($route.current.params.id, 10),
                    period: parseInt($route.current.params.periodId, 10)
                };

                GetVaccineOnTimeInFullList.get(queryParameters,
                    function (data) {
                        console.log(data);
                        if (!isUndefined(data.OnTimeInFull) || data.OnTimeInFull.length > 0)
                            deferred.resolve(data.OnTimeInFull);
                    });
            }


        }, 100);

        return deferred.promise;
    }

};
