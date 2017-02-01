/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */


function vaccineOnTimeInFullFunction($scope,$filter,$location,$window,$routeParams,VaccineOnTimeInFullReporting,navigateBackService,messageService){

    $scope.OnFilterChanged = function() {

        var queryParameters = {
            facility: $scope.filter.facility,
            periodStartDate: $scope.filter.periodStartDate,
            periodEndDate: $scope.filter.periodEndDate
        };
        VaccineOnTimeInFullReporting.get(queryParameters, function(data){
            $scope.receivedOrders = $scope.filteredReceivedOrders = data.search;
           console.log(data.search);
            setRequisitionsFoundMessage();
        }, function(){

        });

        function setRequisitionsFoundMessage() {
            $scope.requisitionFoundMessage = ($scope.receivedOrders.length) ? "" : 'No Reporting Data found';
        }

    };

    $scope.facility = navigateBackService.facility;
    $scope.periodStartDate = navigateBackService.periodStartDate;
    $scope.periodEndDate = navigateBackService.periodEndDate;

    if ($scope.facility && $scope.periodStartDate && $scope.periodEndDate) {
        $scope.OnFilterChanged();
    }
    var selectionFunc = function () {
      /*  $scope.$parent.Status = $scope.selectedItems[0].status;
        $rootScope.viewOrder = true;
        $scope.openRequisition();*/
    };
    $scope.viewRequest = function(data){
        console.log("data");
        console.log(data);

      //  var url = '/vaccine/orderRequisition/' + reportId + '/print';

      var url= '#/received/' +parseInt(data.facilityId,10)+'/'+parseInt(data.periodId,10)+'/'+parseInt(data.id,10);
        $window.open(url, '_blank');

        //  $location.path('/received/'+parseInt(data.facilityId,10)+'/'+parseInt(data.periodId,10)+'/'+parseInt(data.id,10));
    };
    $scope.viewButton = '<button id="editBtn" type="button" class="btn btn-primary" ng-click="edit(row)" >View</button> ';


    $scope.receivedOrderListGrid = { data: 'filteredReceivedOrders',
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
            {field: 'receivedDate', displayName: messageService.get("label.receivedDate"), cellFilter: 'date:\'dd-MM-yyyy\''},
            {field: 'status', displayName: messageService.get("label.status")},
            {field: 'distributionType', displayName: 'distribution Type'},
            {field:' ',
                cellTemplate: '<button style="width:100px; text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.7); background-image: linear-gradient(to bottom, #42a7ad, #356b6f);background-repeat: repeat-x;border-color: rgba(255, 255, 255, 0.3) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);  background-color: #356b6f;"  type="button" class="btn btn-primary btn-small"  ng-click="viewRequest(row.entity)" >View</button> '

            }
        ]
    };

}

vaccineOnTimeInFullFunction.resolve = {


};