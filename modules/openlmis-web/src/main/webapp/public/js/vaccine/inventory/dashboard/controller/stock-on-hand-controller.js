/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

function StockOnHandController($scope,$window,$filter,settings,pendingNotificationForMyStore,receiveConsignmentNotification,UpdateDistributionsForNotification,GetDistributionNotification,EquipmentNonFunctional,VaccinePendingRequisitions,programs,$location,homeFacility,VaccineOrderRequisitionLastReport, localStorageService,StockCardsByCategory,Forecast) {

    $scope.receiveNotification = [];
    $scope.pendingReceiveNotification = pendingNotificationForMyStore;
    $scope.receiveNotification = receiveConsignmentNotification;
    $scope.number_of_days =  settings;
    $scope.createOrder = false;
    $scope.receiveConsignment = false;
    $scope.selectedProgramId = null;
    $scope.init=true;
    if (homeFacility) {
        $scope.homeFacilityId = homeFacility.id;
        $scope.selectedFacilityId = homeFacility.id;
        $scope.facilityDisplayName = homeFacility.name;
    }
    $scope.userPrograms = programs;
    $scope.date = new Date();
    $scope.selectedType = "0";//My facility selected by default;
    $scope.stockLoaded=false;


    $scope.data = {"stockcards": null};//Set default chart stock cards data to null;
    $scope.panel = {alerts: false};//Close Alert Accordion by default



    var loadStockCards=function(programId, facilityId){
        StockCardsByCategory.get(programId ,facilityId).then(function(data){
               $scope.stockCardsByCategory=data;
               $scope.stockLoaded=true;
               if( $scope.stockCardsByCategory[0] !== undefined){

                    Forecast.get({programId:programId ,facilityId:facilityId},
                    function(data){
                            $scope.data = {"stockcards": $scope.stockCardsByCategory[0].stockCards};
                            $scope.data.forecasts=data.stock_requirements;
                            $scope.showGraph=true;
                            $scope.init=false;

                     });

               }

        });
    };

    if($scope.userPrograms.length > 1)
    {
            $scope.showPrograms=true;
            //TODO: load stock cards on program change
            $scope.selectedProgramId=$scope.userPrograms[0].id;
    }
    else if($scope.userPrograms.length === 1){
            $scope.showPrograms=false;
            $scope.selectedProgramId=$scope.userPrograms[0].id;
    }

    //Clear and Hide Chart and table when Radio switch from my facility to supervised facility and show vise versa
    $scope.changeFacilityType  = function () {
        //If Select My facility reload data with home facility Id
        if ($scope.selectedType === "0") {
            $scope.showGraph=false;
            $scope.filter.facilityId=null;
            $scope.selectedFacilityId= $scope.homeFacilityId;
            if($scope.selectedProgramId !== null && $scope.selectedFacilityId !== null){
                   loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
            }


        }
        else if($scope.selectedType === "1")
        {
            //Clear Chart data
            $scope.showGraph=false;
            $location.url($location.path());
            $scope.data={"stockcards": null};
            $scope.filter={};
        }
    };
    $scope.OnFilterChanged = function () {
            $scope.showGraph=false;
            $scope.data={"stockcards": null};
            if($scope.selectedType === "1")
            {
               $scope.selectedFacilityId = $scope.filter.facilityId;
            }

                loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
     };

    //Load Right to check if user level can Send Requisition ond do stock adjustment
     $scope.loadRights = function () {
            $scope.rights = localStorageService.get(localStorageKeys.RIGHT);
     }();

     $scope.hasPermission = function (permission) {
            if ($scope.rights !== undefined && $scope.rights !== null) {
              var rights = JSON.parse($scope.rights);
              var rightNames = _.pluck(rights, 'name');
              return rightNames.indexOf(permission) > -1;
            }
            return false;
      };

    $scope.Adjustment=function(){
        $window.location='/public/pages/vaccine/inventory/index.html#/stock-adjustment';
    };

     $scope.Requisition = function(){
            $window.location='/public/pages/vaccine/order-requisition/index.html#/initiate';
     };

    $scope.ReceiveConsignment = function(){
        $window.location='/public/pages/vaccine/inventory/index.html#/receive';
    };

     EquipmentNonFunctional.get({},function(data){
        $scope.equipmentNonFunctionalAlerts=data.Alerts;
     });

    VaccineOrderRequisitionLastReport.get({
        facilityId: parseInt($scope.homeFacilityId, 10),
        programId: parseInt($scope.selectedProgramId, 10)
    }, function (data) {
        if (!isUndefined(data.lastReport) || data.lastReport !== null) {
            var lastReport = data.lastReport;

            if (lastReport.status === 'SUBMITTED')
                $scope.receiveConsignment = true;
            else
                $scope.createOrder = true;
        }
        else{
            $scope.createOrder = true;
        }

    });

    VaccinePendingRequisitions.get({
            facilityId: parseInt($scope.homeFacilityId, 10),
            programId: parseInt($scope.selectedProgramId, 10)
        },
        function (data) {
            if (!isUndefined(data.pendingRequest) || data.pendingRequest.length > 0) {
                $scope.messageInfo = 'You have ' + data.pendingRequest.length + ' Pending Request(s)';
                $scope.pendingRequisition = data.pendingRequest;
            }
        });

    GetDistributionNotification.get({}, function(data){
        if(!isUndefined(data.remarks)){
            $scope.messageInfo2 = 'Comments On the Order Requisition Submitted On :  '+data.remarks.orderDate;
            $scope.remarks = data.remarks;
        }
    });

    $scope.update = function(distributionId) {
      if(distributionId !== null){
        UpdateDistributionsForNotification.get({id:parseInt(distributionId,10)}, function (data) {

            $location.path('/');
        });
    }
    };



}
StockOnHandController.resolve = {

    homeFacility: function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility = {};

        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    },
    programs: function ($q, $timeout, VaccineInventoryPrograms) {
        var deferred = $q.defer();
        var programs = {};

        $timeout(function () {
            VaccineInventoryPrograms.get({}, function (data) {
                programs = data.programs;
                deferred.resolve(programs);
            });
        }, 100);
        return deferred.promise;
    },
    receiveConsignmentNotification: function ($q, $timeout, PendingConsignmentNotification) {
        var deferred = $q.defer();
        var programs = {};

        $timeout(function () {
            PendingConsignmentNotification.get({}, function (data) {
                programs = data.pendingConsignments;
                deferred.resolve(programs);
            });
        }, 100);
        return deferred.promise;
    },

    settings: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        $timeout(function () {
            SettingsByKey.get({key:'NUMBER_OF_DAYS_PANDING_TO_RECEIVE_CONSIGNMENT'}, function (data) {
                deferred.resolve(data.settings.value);
            });
        }, 100);

        return deferred.promise;
    },

    pendingNotificationForMyStore: function ($q, $timeout, PendingNotificationForLowerLevel) {
        var deferred = $q.defer();
        var programs = {};

        $timeout(function () {
            PendingNotificationForLowerLevel.get({}, function (data) {
                programs = data.pendingConsignmentNotification;
                deferred.resolve(programs);
            });
        }, 100);
        return deferred.promise;
    }

};