/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */
    function ViewOrderRequisitionList($scope,programs,$window,$rootScope,facility,VaccineOrderRequisitionsForViewing,VaccineOrderRequisitionLastReport, facilities, RequisitionsForViewing, ProgramsToViewVaccineOrderRequisitions, $location, messageService, navigateBackService) {

        $scope.facilities = facilities;

        $scope.program =  programs;
        if($scope.program.length === 1){
            $scope.selectedProgramId = programs[0].id;
            var selectedFacilityId = facilities.id;

        }

        $scope.facilityLabel = (!$scope.facilities.length) ? messageService.get("label.none.assigned") : messageService.get("label.select.facility");
        $scope.programLabel = messageService.get("label.none.assigned");
        $scope.selectedItems = [];


        $scope.loadRequisitions = function () {
            if ($scope.viewRequisitionForm && $scope.viewRequisitionForm.$invalid) {
                $scope.errorShown = true;
                return;
            }
            var requisitionQueryParameters = {
                facilityId: selectedFacilityId,
                dateRangeStart: $scope.startDate,
                dateRangeEnd: $scope.endDate
            };

            if ($scope.selectedProgramId) requisitionQueryParameters.programId = $scope.selectedProgramId;

            VaccineOrderRequisitionsForViewing.get(requisitionQueryParameters, function (data) {
                $scope.requisitions = $scope.filteredRequisitions = data.search;
                setRequisitionsFoundMessage();
            }, function () {
            });

        };

        $scope.selectedFacilityId = navigateBackService.facilityId;
        $scope.startDate = navigateBackService.dateRangeStart;
        $scope.endDate = navigateBackService.dateRangeEnd;
        programs = navigateBackService.programs;

        if (navigateBackService.programId) {
            $scope.selectedProgramId = navigateBackService.programId;
            $scope.program = _.findWhere(programs, {id: utils.parseIntWithBaseTen($scope.selectedProgramId)});
           // setOptions();
        }
        if ($scope.selectedFacilityId && $scope.startDate && $scope.endDate) {
            $scope.loadRequisitions();
        }

        var selectionFunc = function () {
            $scope.$parent.Status = $scope.selectedItems[0].status;
            $rootScope.viewOrder = true;
            $scope.openRequisition();
        };

        $scope.viewButton = '<button id="editBtn" type="button" class="btn btn-primary" ng-click="edit(row)" >View</button> ';


        $scope.rnrListGrid = { data: 'filteredRequisitions',
            displayFooter: false,
            multiSelect: false,
            selectedItems: $scope.selectedItems,
            afterSelectionChange: selectionFunc,
            displaySelectionCheckbox: false,
            enableColumnResize: true,
            showColumnMenu: false,
            showFilter: false,
            enableSorting: true,
            sortInfo: { fields: ['orderDate'], directions: ['asc'] },
            columnDefs: [
                {field: 'programName', displayName: messageService.get("program.header") },
                {field: 'facilityName', displayName: messageService.get("option.value.facility.name")},
                {field: 'periodStartDate', displayName: messageService.get("label.period.start.date"), cellFilter: 'date:\'dd-MM-yyyy\''},
                {field: 'periodEndDate', displayName: messageService.get("label.period.end.date"), cellFilter: 'date:\'dd-MM-yyyy\''},
                {field: 'createdDate', displayName: messageService.get("label.date.submitted"), cellFilter: 'date:\'dd-MM-yyyy\''},
                {field: 'status', displayName: messageService.get("label.status")},
                {field: 'emergency', displayName: messageService.get("requisition.type.emergency"),
                    cellTemplate: '<div id="emergency{{$parent.$index}}" class="ngCellText checked"><i ng-class="{\'icon-ok\': row.entity.emergency}"></i></div>',
                    width: 110
                },
                {field:' ',
                    cellTemplate: '<button style="width:100px; text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7); background-image: linear-gradient(to bottom, #42a7ad, #356b6f);background-repeat: repeat-x;border-color: rgba(255, 255, 255, 0.3) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);  background-color: #356b6f;"  type="button" class="btn btn-primary btn-small" >View</button> '

}
            ]
        };

        $scope.openRequisition = function () {
            var data = {
                facilityId: $scope.selectedFacilityId,
                dateRangeStart: $scope.startDate,
                dateRangeEnd: $scope.endDate,
                programs: programs
            };
            if ($scope.selectedProgramId) data.programId = $scope.selectedProgramId;
            navigateBackService.setData(data);
            $window.location = '/public/pages/vaccine/order-requisition/index.html#/view-requisition/'+parseInt($scope.selectedItems[0].id,10)+'/'+parseInt($scope.selectedProgramId,10);
        };

        function setProgramsLabel() {
            $scope.selectedProgramId = undefined;
            $scope.programLabel = (!programs.length) ? messageService.get("label.none.assigned") : messageService.get("label.all");
        }

     /*   function setOptions() {
            $scope.options = (programs.length) ? [
                {field: "All", name: "All"}
            ] : [];
        }*/

        $scope.loadProgramsForFacility = function () {
            ProgramsToViewVaccineOrderRequisitions.get({facilityId: $scope.selectedFacilityId},
                function (data) {
                    programs = data.programList;
                   // setOptions();
                    setProgramsLabel();
                }, function () {
                    programs = [];
                    setProgramsLabel();
                });
        };

        function setRequisitionsFoundMessage() {
            $scope.requisitionFoundMessage = ($scope.requisitions.length) ? "" : messageService.get("msg.no.rnr.found");
        }

        $scope.filterRequisitions = function () {
            $scope.filteredRequisitions = [];
            var query = $scope.query || "";

            $scope.filteredRequisitions = $.grep($scope.requisitions, function (rnr) {
                return contains(rnr.requisitionStatus, query);
            });
        };

        function contains(string, query) {
            return string.toLowerCase().indexOf(query.toLowerCase()) != -1;
        }

        $scope.setEndDateOffset = function () {
            if ($scope.endDate < $scope.startDate) {
                $scope.endDate = undefined;
            }
            $scope.endDateOffset = Math.ceil((new Date($scope.startDate.split('-')).getTime() + oneDay - Date.now()) / oneDay);
        };

    }

    var oneDay = 1000 * 60 * 60 * 24;

ViewOrderRequisitionList.resolve = {

    facilities: function ($q, $timeout, UserFacilityWithViewVaccineOrderRequisition) {
        var deferred = $q.defer();
        $timeout(function () {
            UserFacilityWithViewVaccineOrderRequisition.get({}, function (data) {
                deferred.resolve(data.facilities);
            }, {});
        }, 100);
        return deferred.promise;
    },
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