function ViewVaccineOrderRequisitionController($scope,StockCards, $window, $rootScope, SupervisoryNodeByFacilityAndRequisition, VaccinePendingRequisitions, programs, $routeParams, facility, $location, messageService) {
    var program = programs;
    var facilit = facility;

    $scope.stockLoaded=false;

    StockCards.get({facilityId: parseInt(facilit.id, 10)}, function (data) {
            if (!isUndefined(data.stockCards) || data.stockCards.length > 0)
             $scope.stockCards = data.stockCards;
            else
                $scope.stockLoaded=true;
    });

    $scope.pageSize = parseInt(10, 10);

    $scope.noRequisitions = false;

    var refreshPageLineItems = function () {
        VaccinePendingRequisitions.get({
            facilityId: parseInt(facilit.id, 10),
            programId: parseInt(program[0].id, 10)
        }, function (data) {
            $scope.pendingRequisition = data.pendingRequest;
            $scope.numberOfPages = Math.ceil($scope.pendingRequisition.length / $scope.pageSize) || 1;
            $scope.currentPage = (utils.isValidPage($routeParams.page, $scope.numberOfPages)) ? parseInt($routeParams.page, 10) : 1;
            $scope.pageLineItems = $scope.pendingRequisition.slice($scope.pageSize * ($scope.currentPage - 1), $scope.pageSize * $scope.currentPage);
            if (!$scope.pendingRequisition.length)   $scope.noRequisitions = true;


        });
    };
    refreshPageLineItems();
    $scope.$watch('currentPage', function () {
        $location.search('page', $scope.currentPage);
    });

    $scope.$on('$routeUpdate', function () {
        refreshPageLineItems();
    });

    var refreshGrid = function () {
        $scope.noRequisitionSelectedMessage = "";
        $scope.selectedItems.length = 0;

    };

    $scope.message = "";
    $scope.maxNumberOfPages = 10;
    $scope.selectedItems = [];

    $scope.searchOptions = [
        {value: "facilityName", name: "label.store"}
    ];
    $scope.selectedSearchOption = $scope.searchOptions[0];
    $scope.sortOptions = {fields: ['name'], directions: ['desc']};


    $scope.filterOptions = {
        filterText: ''
    };
    var ct_nocheck = "<div class=\"ngSelectionCell\"><input style=\"display:none\" tabindex=\"-1\" class=\"ngSelectionCheckbox\" type=\"checkbox\" ng-checked=\"row.selected\"/></div>";

    $scope.gridOptionsForNationalLevel = {

        data: 'pageLineItems',
        selectedItems: $scope.selectedItems,
        displayFooter: false,
        multiSelect: true,
        showSelectionCheckbox: true,
        enableColumnResize: true,
        sortInfo: $scope.sortOptions,
        enableSorting: true,
        useExternalSorting: true,
        beforeSelectionChange: function (row) {
            row.changed = true;
            return true;
        },

        afterSelectionChange: function (rowItem, event) {
            var consolidated = [];
            if (rowItem.length) {

                _.each(rowItem, function (itm) {

                    if (itm.selected) {
                        consolidated.push(itm.entity.facilityId);
                    }
                });
                updateItemOnSelectionChange(consolidated);
            } else {
                if (rowItem.selected) {
                    updateItemOnSelectionChange($scope.selectedItems);

                }
            }
            return true;
        },

        columnDefs: [
            {field: 'facilityName', displayName: messageService.get("label.store")},
            {field: 'periodName', displayName: messageService.get("label.period")},
            {field: 'status', displayName: messageService.get("label.status")},
            {
                field: 'orderDate',
                displayName: messageService.get("label.date.submitted"),
                cellFilter: 'date:\'dd-MM-yyyy\''
            },
            {
                field: ' ',
                cellTemplate: '<button style="width:100px; text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7); background-image: linear-gradient(to bottom, #42a7ad, #356b6f);background-repeat: repeat-x;border-color: rgba(255, 255, 255, 0.3) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);  background-color: #356b6f;"  type="button" class="btn btn-primary btn-small grid-btn" ng-click="viewRequest(row.entity)">View</button> ',

                width: 150
            }
        ], filterOptions: $scope.filterOptions
    };


    $scope.gridOptionsForRegionLevel = {

        data: 'pageLineItems',
        selectedItems: $scope.selectedItems,
        multiSelect: false,
        showSelectionCheckbox: false,
        enableColumnResize: true,
        sortInfo: $scope.sortOptions,
        enableSorting: true,
        useExternalSorting: true,
        beforeSelectionChange: function (row) {
            row.changed = true;
            return true;
        },

        afterSelectionChange: function (rowItem, event) {
            if (rowItem.length) {
                _.each(rowItem, function (item) {
                    updateItemOnSelectionChange(item);
                });
            } else {
                updateItemOnSelectionChange(rowItem);
            }
            return true;
        },

        columnDefs: [
            {field: 'facilityName', displayName: messageService.get("label.store")},
            {field: 'periodName', displayName: messageService.get("label.period")},
            {field: 'status', displayName: messageService.get("label.status")},
            {
                field: 'orderDate',
                displayName: messageService.get("label.date.submitted"),
                cellFilter: 'date:\'dd-MM-yyyy\''
            },
            {
                field: ' ',
                cellTemplate: '<button style="width:100px; text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7); background-image: linear-gradient(to bottom, #42a7ad, #356b6f);background-repeat: repeat-x;border-color: rgba(255, 255, 255, 0.3) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);  background-color: #356b6f;"  type="button" class="btn btn-primary btn-small grid-btn" ng-click="distributeToFacility(row.entity)" >Issue</button> ',

                width: 150
            }
        ], filterOptions: $scope.filterOptions
    };


    SupervisoryNodeByFacilityAndRequisition.get({facilityId: parseInt(facility.id, 10)}, function (data) {
        $scope.supervisoryNode = data.supervisoryNodes;
        if (data.supervisoryNodes === null) {
            $scope.gridOption = true;
        } else {
            $scope.gridOpt = true;

        }


    });


    //This event will update the filter text.
    $scope.filterName = function () {
        var filterText = 'name:' + $scope.query;
        if (filterText !== 'name:') {
            $scope.filterOptions.filterText = filterText;
        } else {
            $scope.filterOptions.filterText = '';
        }
    };

    $scope.$watch('sortOptions', function (newValue, oldValue) {
        if (newValue.fields[0] != oldValue.fields[0] || newValue.directions[0] != oldValue.directions[0])
            refreshGrid();
    }, true);

    $scope.$on('$routeUpdate', refreshGrid);

    refreshGrid();

    $scope.inputKeypressHandler = function ($event) {
        if ($event.keyCode == 13) {
            $event.preventDefault();
            $scope.updateSearchParams();
        }
    };

    $scope.selectSearchType = function (searchOption) {
        $scope.selectedSearchOption = searchOption;
    };


    var updateItemOnSelectionChange = function (item) {


        $scope.facilitySelected = [];

        angular.forEach($scope.selectedItems, function (itm, idx) {
            $scope.facilitySelected.push(itm.facilityId);
        });
        $routeParams.facilityId = $scope.facilitySelected;
        $routeParams.program = parseInt(program[0].id, 10);
        $routeParams.homeFacility = parseInt(facilit.id,10);

    };

    $scope.consolidateOrder = function () {

        $location.path('/consolidate/' + $routeParams.program + '/' + $routeParams.facilityId + '/'+$routeParams.homeFacility);

    };

    $scope.distributeToFacility = function (row) {

        $window.location = '/public/pages/vaccine/inventory/stock-movement/index.html#/stock-movement-view/' + row.programId + '/' + row.periodId + '/' + row.facilityId + '/' + row.facilityName + '/' + row.id;

    };

    $scope.viewRequest = function (row) {

        $rootScope.viewOrder = true;
        $rootScope.cancelOrderRequest = true;
        $rootScope.reportId = row.id;
        $location.path('/create/' + row.id + '/' + row.programId);

    };

    $rootScope.cancelViewOrder = function () {
        $location.path('/view');
    };
}


ViewVaccineOrderRequisitionController.resolve = {

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
    }

};
