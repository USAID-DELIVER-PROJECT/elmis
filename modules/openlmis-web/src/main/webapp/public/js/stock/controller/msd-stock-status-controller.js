function MsdStockStatusFunc($scope, GetMsdStockStatusReport, GeoZoneFilteredData,GetMsdStockStatus,GetMsdStockStatusColor) {

    GetMsdStockStatus.get({}, function (data) {
        if(!isUndefined(data)){

            $scope.stockStatusData = data.msd_status;


        }
    });
    $scope.cellValues = {msd_central:'MSD Central Zone', dar:'Dar Es Salaam Zone',mnz:'Mwanza Zone',
                  muleba:'Muleba Zone', tanga:'Tanga Zone',tabr:'Tabora Zone',mtr:'Mtwara Zone',
                  mosh:'Moshi Zone',mbeya:'Mbeya Zone',iringa:'Iringa Zone',doodoma:'Dodoma Zone',mwnz:'Mwanza Zone'};

    $scope.getColor=function (mos,levelId) {
        GetMsdStockStatusColor.get({mos:parseInt(mos,10),levelId:levelId},function (data) {
            if(!isUndefined(data))
                return {'background-color':data.colors};

            });
    };

$scope.getStockColor=function(mos,levelId){

    if((9 < parseInt(mos, 10) && parseInt(levelId, 10) === 1) || (mos >3 && levelId===2)){
      return {'background-color':'#568AFA','text-align': 'center !important;'};
    }else if(((mos >7 && mos<=9) && levelId===1) || ((2 <mos && mos<=3) && levelId ===2 )){
        return {'background-color':'#006600','text-align': 'center !important;'};
    }else if(((0<mos && mos<=7) && levelId ===1) || ((0<mos && mos<=2) && levelId===2)){
        return {'background-color':'#ffdb00','text-align': 'center !important;'};
    }else
        return {'background-color':'#ff0d00','text-align': 'center !important;'};

};


    $scope.getHeaderValues =function (data) {
        var headers=[];
        if(_.contains( $scope.cellValues,data)){
            headers.push(data);
        }

            console.log(data);
        return data.cellValues;
    };
    GetMsdStockStatusReport.get({programId: 1, periodId: 121, productCode: 'ABC123'}, function (data) {
        var geo = _.pluck(GeoZoneFilteredData, 'code');
        $scope.stock = data.msd_status;
        var stockData = [];
        $scope.allData = [];
        angular.forEach($scope.stock, function (data) {
            if (_.contains(geo, data.geoCode)) {
                stockData.push(data);
            }
        });
        $scope.allData = stockData;
        $scope.headers = _.uniq(_.pluck(stockData, 'msdZone'));
        var groupByZone = _.groupBy(stockData, function (data) {
            return data.msdZone;
        }) ;
        var groupByPr = _.groupBy(stockData, function (data) {
            return data.productName;
        }) ;
        $scope.productColumns = $.map(groupByZone, function (value, index) {
            return {'zone':index,"products": value};
        });
        $scope.productRows = $.map(groupByPr, function (value, index) {
            return {"products": value};
        });
        console.log(JSON.stringify($scope.productRows));

    });
    $scope.set_color = function (color) {
            return { 'background-color':color,'text-align':'center' };

    };
}

MsdStockStatusFunc.resolve = {

    GeoZoneFilteredData: function ($q, $timeout, GetGoZoneByLevelCode) {
        var deferred = $q.defer();
        $timeout(function () {
            GetGoZoneByLevelCode.get({geoLevelCode: 'reg'}, function (data) {
                deferred.resolve(data.geographicZoneList);
            }, {});
        }, 100);
        return deferred.promise;
    }
};