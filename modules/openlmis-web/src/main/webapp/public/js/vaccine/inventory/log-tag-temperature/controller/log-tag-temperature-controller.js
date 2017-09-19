function LogTagTemperatureController($scope,$location,logTag,SaveLogTagTemperatureInfo){
var zoneId;
    $scope.OnFilterChanged = function () {
        zoneId= $scope.filter.zone.id;

        /*    if($scope.filter.zone !==undefined || $scope.filter.zone.id !==null )
             zoneId= $scope.filter.zone.id;

            $scope.log = logTag;
            if(logTag !==null){
               // console.log($scope.log);
               // zoneId = $scope.log.facility.geographicZone.id;
               // $scope.filter = {};
                //$scope.filter.zone=zoneId;
               // console.log($scope.filter);

            }*/

    };

    $scope.save = function(form){
  /*      var z ;
        if($scope.filter.zone.id === 437){
            z=$scope.log.facility.geographicZone.id}
        else
            z=zoneId;
        console.log(z);*/
        var params= {'zoneId':zoneId,'serialNumber':$scope.log.serialNumber,'description':$scope.log.description};
        var data= angular.extend(params,$scope.log);

        if(form.$valid){
            console.log(params);
            SaveLogTagTemperatureInfo.save(params, function(){
                $location.path('/list');
            });
        }
    };

}
LogTagTemperatureController.resolve={


    logTag : function($q, $timeout, GetAllLogTagTemperatureById, $route){
        if(!$route.current.params.id){
            return {};
        }
        var deferred = $q.defer();
        $timeout(function(){
            GetAllLogTagTemperatureById.get({id:$route.current.params.id}, function(data){
                deferred.resolve(data.logTags);
            },{});
        }, 100);
        return deferred.promise;
    }
};
