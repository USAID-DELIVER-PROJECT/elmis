/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

function VaccineDashboardController($scope,VaccineDashboardSummary,VaccineDashboardCoverage,VaccineDashboardWastage,SettingsByKey) {
    $scope.actionBar = {openPanel: true};
    $scope.performance = {openPanel: true};
    $scope.stockStatus = {openPanel: true};

    $scope.reportingPerformance = {};

    $scope.repairing = {};
    $scope.supplying = {};
    $scope.investigating = {};
$scope.filterd ={};


    $scope.periods = [{id:1, name:'custom'},{id:2, name:'last 3 months'}, {id:3, name: 'last 6 months'}, {id:4, name:'last 1 year'}, {id:5, name:'current period'}];
    $scope.OnFilterChanged = function() {

      /*  console.log("start date "+$scope.filterd.startDate +" and end date "+$scope.filterd.endDate);
        console.log("start date3 "+$scope.filterd.startDate3 +" and end date3 "+$scope.filterd.endDate3);
        console.log("vaccine product "+$scope.filterd.vaccineProduct)
        console.log("vaccine product2 "+$scope.filterd.vaccineProduct2)*/
        $scope.data = $scope.datarows = [];
       // $scope.filter.max = 10000;
    };

    $scope.periodTrends = [{}]



    SettingsByKey.get({key: 'DASHBOARD_SLIDES_TRANSITION_INTERVAL_MILLISECOND'}, function(data){
        $scope.defaultSlideTransitionInterval = data.settings.value;
        $scope.consumptionSlideInterval = $scope.stockSlideInterval = $scope.lossesSlideInterval = $scope.defaultSlideTransitionInterval;


        var carousel = function(id){
            return {id: id,
                interval: $scope.defaultSlideTransitionInterval,
                isPlaying:  function(){ return this.interval >= 0;},
                play: function(){ this.interval = $scope.defaultSlideTransitionInterval; this.isPlaying = true;},
                pause: function(){this.interval = -1; this.isPlaying = false; }};
        };

        $scope.carousels = [carousel('trend'), carousel('district'), carousel('facility')];
    });

    VaccineDashboardSummary.get({}, function(data){
       $scope.reportingPerformance = data.summary.reportingSummary;
       $scope.repairing = data.summary.repairing;
       $scope.investigating = data.summary.investigating;
    });

    VaccineDashboardCoverage.get({}, function(data){
       $scope.coverageMonthly = data.coverage.coverageMonthly;
        $scope.coverageMonthName =  _.pluck($scope.coverageMonthly, 'period_name').toString();

        //alert('coverage monthly '+JSON.stringify($scope.coverageMonthName))
        $scope.coverage = _.pluck($scope.coverageMonthly, 'coverage').toString();
        $scope.actual = _.pluck($scope.coverageMonthly, 'actual').toString();
        $scope.target = _.pluck($scope.coverageMonthly, 'target').toString();
       $scope.coverageDistrict = data.coverage.coverageDistrict;
    });
    VaccineDashboardWastage.get({}, function(data){
       $scope.wastageMonthly = data.wastage.wastageMonthly;
       $scope.wastageDistrict = data.wastage.wastageDistrict;

    });

}
VaccineDashboardController.resolve = {
    programs: function ($q, $timeout, VaccineSupervisedIvdPrograms) {
        var deferred = $q.defer();

        $timeout(function () {
            VaccineSupervisedIvdPrograms.get({}, function (data) {
                deferred.resolve(data.programs);
            });
        }, 100);

        return deferred.promise;
    }
};
