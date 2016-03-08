function newVaccineOrderRequisitionController($scope,StockCards,$rootScope,settings,getFacilityTypeCode,VaccineOrderRequisitionReportPeriods,localStorageService,VaccineOrderRequisitionLastReport,VaccineOrderRequisitionReportInitiateEmergency, programs, facility, messageService, VaccineOrderRequisitionReportInitiate, $location) {

    $rootScope.viewOrder = false;
    $rootScope.receive = false;
    $scope.programs = programs;
    $scope.facility = facility;
    $scope.emergency = false;

    $scope.selectedType = 0;

    var id = parseInt($scope.programs[0].id,10);
    var facilityId = parseInt($scope.facility.id,10);
    var facilityTypeCode = getFacilityTypeCode;


    $scope.stockLoaded=false;

    StockCards.get({facilityId: parseInt(facilityId, 10)}, function (data) {
        if (!isUndefined(data.stockCards) || data.stockCards.length > 0)
            $scope.stockCards = data.stockCards;
        else
            $scope.stockLoaded=true;
    });



    if(!isUndefined(settings) && settings !== null) {
        var rFacilityTypeCode = settings;
    }
    $scope.requisitionTypes = [];
    $scope.requisitionTypes = [{id: '0', name: 'Unscheduled Reporting'}, {id: '1', name: 'Scheduled Reporting'}];


    VaccineOrderRequisitionReportPeriods.get({

                facilityId: parseInt(facilityId,10),
                programId: parseInt(id,10)
            }, function (data) {
                $scope.periodGridData = data.periods;
                if ($scope.periodGridData.length > 0 && $scope.periodGridData[0].emergency !== true) {
                    $scope.emergency = false;
                    $scope.periodGridData[0].showButton = true;
                }else{

                    VaccineOrderRequisitionLastReport.get({facilityId:parseInt(facilityId,10),programId:parseInt(id,10)}, function(data) {

                        var lastReport = data.lastReport;

                            if( (lastReport.status !== null) &&
                                (lastReport.status === 'ISSUED') || lastReport.status === 'DRAFT'){

                            VaccineOrderRequisitionReportInitiateEmergency.get({
                                periodId: lastReport.periodId,
                                programId: lastReport.programId,
                                facilityId: lastReport.facilityId
                            }, function (data) {
                                $location.path('/create/' + data.report.id + '/' + data.report.programId);
                            });
                        }
                        else

                            {

                            $location.path('/details');

                                if(facilityTypeCode === rFacilityTypeCode){
                                    $scope.showMessage = true;
                                    $scope.message = "Your Previous Requisition Submitted On " + lastReport.orderDate + "" +
                                        " has not yet been Processed. ";

                                }else{
                                    $scope.message = "Your Previous Requisition Submitted On " + lastReport.orderDate + "" +
                                        " has not yet Received.   Please:";
                                }

                        }

                    });



                }


            });

    function getActionButton(showButton) {
        return '<input type="button" ng-click="initiate(row.entity)" openlmis-message="button.proceed" class="btn btn-primary btn-small grid-btn" ng-show="' + showButton + '"/>';
    }

    $scope.periodGridOptions = {
        data: 'periodGridData',
        canSelectRows: false,
        displayFooter: false,
        displaySelectionCheckbox: false,
        enableColumnResize: false,
        enableColumnReordering: true,
        enableSorting: false,
        showColumnMenu: false,
        showFilter: false,
        columnDefs: [
            {field: 'periodName', displayName: messageService.get("label.periods")},
            {field: 'status', displayName: messageService.get("label.status")},
            {field: '', displayName: '', cellTemplate: getActionButton('row.entity.showButton')}
        ]
    };

    $scope.initiate = function (period) {

        VaccineOrderRequisitionLastReport.get({facilityId:parseInt(facilityId,10),programId:parseInt(id,10)}, function(data) {

            if(angular.isObject(data.lastReport) && data.lastReport !== null){

                var lastReport = data.lastReport;

                if (!angular.isUndefined(lastReport) && lastReport.emergency === false && lastReport.status === 'SUBMITTED') {




                    if(facilityTypeCode === rFacilityTypeCode){
                            $scope.showMessage = true;

                            $scope.message = "Your Previous Requisition Submitted On " + lastReport.orderDate + "" +
                                " has not yet been Processed. ";

                        }else{
                            $scope.message = "Your Previous Requisition Submitted On " + lastReport.orderDate + "" +
                                " has not yet Received.   Please:";
                        }

                    return $scope.message;
                }
            }


            if (!angular.isUndefined(period.id) && (period.id !== null)) {
                $location.path('/create/' + period.id + '/' + period.programId);

            } else {
                // initiate
                VaccineOrderRequisitionReportInitiate.get({
                    periodId: period.periodId,
                    programId: period.programId,
                    facilityId: period.facilityId
                }, function (data) {
                    $location.path('/create/' + data.report.id + '/' + data.report.programId);
                });
            }


        });


    };

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



}

newVaccineOrderRequisitionController.resolve = {

    programs: function ($q, $timeout, VaccineHomeFacilityPrograms) {
        var deferred = $q.defer();

        $timeout(function () {
            VaccineHomeFacilityPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    },
    facility: function ($q, $timeout, UserHomeFacility) {
        var deferred = $q.defer();

        $timeout(function () {
            UserHomeFacility.get({}, function (data) {
                deferred.resolve(data.homeFacility);
            });
        }, 100);

        return deferred.promise;
    },

    getFacilityTypeCode: function ($q, $timeout, UserHomeFacility,GetFacilityForVaccineOrderRequisition) {
        var deferred = $q.defer();

        $timeout(function () {

            UserHomeFacility.get({}, function (data) {

                GetFacilityForVaccineOrderRequisition.get({facilityId: parseInt(data.homeFacility.id, 10)},
                    function (data) {
                        deferred.resolve(data.facility.facilityType.code);
                    });
            });

        }, 100);

        return deferred.promise;
    },

    settings: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();

        $timeout(function () {

            SettingsByKey.get({key:'VACCINE_FACILITY_TYPE_CODE'}, function (data) {
                deferred.resolve(data.settings.value);
            });

        }, 100);

        return deferred.promise;
    }


};