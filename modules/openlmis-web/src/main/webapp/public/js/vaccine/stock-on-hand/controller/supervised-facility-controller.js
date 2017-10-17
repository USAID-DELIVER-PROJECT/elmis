function SupervisedFacilityControllerFunc($scope,$state,$window, homeFacilityId,$location, StockCards, GetStockCards, GetByDistrict, facilityTypeCode, GeoDistrictTree) {
    "use strict";
    $scope.superVised = true;
   // $scope.homeFacility = homeFacilityId;
    $scope.facilityCode = facilityTypeCode;

    $scope.showMyFacility = facilityTypeCode === 'dvs';

    $scope.stockCards = [];


    $scope.print = function(param){
        var url = '/vaccine/inventory/distribution/stock-on-hand/print/'+param;
        $window.open(url, "_BLANK");
    };

    if (GetStockCards !== undefined)
       $scope.stockCards = GetStockCards.stockCards;

    var getDataForDisplay = function (data) {
        var district;
        // $state.go('/home', { 'referer':'jimbob', 'param2':37, 'etc':'bluebell' });

        if (data.id === null) {
            district = data.regionId;
        } else
            district = data.id;

        var facilityName = data.text;

        GetByDistrict.get({districtId: parseInt(district, 10)}, function (data) {

            $state.go('toState', { 'facilityId':parseInt(data.facility.id, 10), 'facilityName':facilityName, 'etc':'bluebell' });


        });
    };

    GeoDistrictTree.get({}, function (data) {

        var data2 = data.regionFacilityTree;
        $('#tree').treeview({
            data: data2,
            levels: 2,
            color: "#398085",
            onhoverColor: 'lightblue',
           // href: "#node-1",
            //selectable: false,
            state: {
                //checked: true,
                disabled: true,
               // expanded: true,
                selected: true
            },
            //silent: true,
            onNodeSelected: function (event, data) {
                getDataForDisplay(data);
            }



        },'disableNode',  {levels: 2, silent: true } );

    });

}

SupervisedFacilityControllerFunc.resolve = {

    homeFacilityId: function ($q, $timeout, UserHomeFacility) {
        var deferred = $q.defer();

        $timeout(function () {

            UserHomeFacility.get({}, function (data) {
                deferred.resolve(data.homeFacility.id);
            });

        }, 100);

        return deferred.promise;
    },
    facilityTypeCode: function ($q, $timeout, UserHomeFacility, FacilityTypeByFacility) {
        var deferred = $q.defer();

        $timeout(function () {
            UserHomeFacility.get({}, function (data) {

                FacilityTypeByFacility.get({facilityId: data.homeFacility.id},
                    function (data) {
                        console.log(data);
                        deferred.resolve(data.facilityTypes.code);

                    }
                );

            });

        }, 100);

        return deferred.promise;
    },

    GetStockCards: function ($q, $timeout, UserHomeFacility, StockCards) {
        var deferred = $q.defer();

        $timeout(function () {

            UserHomeFacility.get({}, function (data) {

                StockCards.get({facilityId: parseInt(data.homeFacility.id, 10)},
                    function (data) {
                        deferred.resolve(data);
                    });
            });

        }, 100);

        return deferred.promise;
    }

};