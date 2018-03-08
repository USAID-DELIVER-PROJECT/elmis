/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

function DashboardHelperModalInstanceCtrl($scope, $modalInstance,items,  VaccineDashboardFacilityTrend) {

    $scope.facilityDetails=items.facilityDetails;
    $scope.periodsList= items.periodsList;
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';
        var fColor='#a6a6a6';
        /*
                if (_index % 2 === 0) {
                    //bgColor = 'lightblue';
                    //fColor='white';
                    bgColor = 'white';
                    fColor='#a6a6a6';
                } else {
                    bgColor = 'white';
                    fColor='#a6a6a6';
                }
        */

        return {fColor:fColor, bgColor:bgColor};
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}
function DashboardDropoutModalInstanceCtrl($scope, $modalInstance,items,  VaccineDashboardFacilityTrend) {

    $scope.facilityDropoutDetails=items.facilityDetails;
    $scope.dropoutPeriodsList= items.periodsList;
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';
        var fColor='#a6a6a6';

        /*
                if (_index % 2 === 0) {
                    bgColor = 'lightblue';
                    fColor='white';
                } else {
                    bgColor = 'white';
                }

        */
        return {fColor:fColor, bgColor:bgColor};
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}
function DashboardSessionModalInstanceCtrl($scope, $modalInstance,items,  VaccineDashboardFacilityTrend) {

    $scope.facilitySessionsDetails=items.facilityDetails;
    $scope.sessionsPeriodsList= items.periodsList;
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';
        var fColor='#a6a6a6';
        /*
                if (_index % 2 === 0) {
                    bgColor = 'lightblue';
                    fColor='white';
                } else {
                    bgColor = 'white';
                }

        */
        return {fColor:fColor, bgColor:bgColor};
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}
function DashboardWastageModalInstanceCtrl($scope, $modalInstance,items,  VaccineDashboardFacilityTrend) {

    $scope.facilityWastageDetails=items.facilityDetails;
    $scope.wastagePeriodsList= items.periodsList;
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';
        var fColor='#a6a6a6';

        /*
                if (_index % 2 === 0) {
                    bgColor = 'lightblue';
                    fColor='white';
                } else {
                    bgColor = 'white';
                }

        */
        return {fColor:fColor, bgColor:bgColor};
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}
function DashboardStockStatusModalInstanceCtrl($scope, $modalInstance,items) {
    console.log(items);
    $scope.facilityStockStatusDetails=items.facilityDetails;
    $scope.stockstatusPeriodsList= items.periodsList;
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';
        var fColor='#a6a6a6';
        /*
                if (_index % 2 === 0) {
                    bgColor = 'lightblue';
                    fColor='white';
                } else {
                    bgColor = 'white';
                }

        */
        return {fColor:fColor, bgColor:bgColor};
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}
function DashboardHelpModalInstanceCtrl($scope, $modalInstance,items) {

    $scope.dashboardHelps=items.dashboardHelps;

    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}

function DashboardStockInventoryStatusModalInstanceCtrl($scope, $modalInstance,items) {

    $scope.stockByCategory=items;
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';
        var fColor='#a6a6a6';
        /*
                if (_index % 2 === 0) {
                    bgColor = 'lightblue';
                    fColor='white';
                } else {
                    bgColor = 'white';
                }

        */
        return {fColor:fColor, bgColor:bgColor};
    };

    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

}