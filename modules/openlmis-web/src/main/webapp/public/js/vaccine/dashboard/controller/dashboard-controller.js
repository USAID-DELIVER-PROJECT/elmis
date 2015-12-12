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

function VaccineDashboardController($scope,VaccineDashboardSummary,VaccineDashboardCoverage,VaccineDashboardDistrictSessions,VaccineDashboardSessions,VaccineDashboardWastage,SettingsByKey, messageService) {
    $scope.actionBar = {openPanel: true};
    $scope.performance = {openPanel: true};
    $scope.stockStatus = {openPanel: true};
     $scope.sessions = {
        openPanel: true
    };

//Instantiate Monthly Sessions charting options
     $scope.monthlySessions = {
        dataPoints: [],
            dataColumns: [{
            "id": "outreach_sessions",
            "name": messageService.get('label.outreach.sessions'),
            "type": "bar"
        },
            {"id": "fixed_sessions", "name": messageService.get('label.outreach.sessions'), "type": "bar"}],
            dataX: {"id": "period_name"}
    };
//Instantiate District Sessions charting options
    $scope.districtSessions = {
        dataPoints: [],
        dataColumns: [{
            "id": "outreach_sessions",
            "name": messageService.get('label.outreach.sessions'),
            "type": "bar"
        },
            {"id": "fixed_sessions", "name": messageService.get('label.outreach.sessions'), "type": "bar"}],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.$watchCollection('[filter.monthlySessions.startDate, filter.monthlySessions.endDate]', function(newValues, oldValues){

        if(!isUndefined( $scope.filter.monthlySessions.startDate) && !isUndefined( $scope.filter.monthlySessions.endDate)){
            VaccineDashboardSessions.get({startDate: $scope.filter.monthlySessions.startDate, endDate: $scope.filter.monthlySessions.endDate}, function(data){

                $scope.monthlySessions.dataPoints =   data.monthlySessions;
            });
        }
    });

    $scope.$watch('filter.districtSessions.period', function(newValues, oldValues){
        console.log('period is '+$scope.filter.districtSessions.period)
        if(!isUndefined( $scope.filter.districtSessions.period) ) {
            VaccineDashboardDistrictSessions.get({period: $scope.filter.districtSessions.period}, function(data){

                $scope.districtSessions.dataPoints =   data.districtSessions;
            });
        }

    });


        $scope.OnFilterChanged = function() {

      /*  if(!isUndefined( $scope.filter.monthlySessions.startDate) && !isUndefined( $scope.filter.monthlySessions.endDate)){
            VaccineDashboardSessions.get({startDate: $scope.filter.monthlySessions.startDate, endDate: $scope.filter.monthlySessions.endDate}, function(data){

                $scope.monthlySessions.dataPoints =   data.monthlySessions;
            });
        }
     if(!isUndefined( $scope.filter.districtSessions.startDate) && !isUndefined( $scope.filter.districtSessions.endDate)) {
         VaccineDashboardDistrictSessions.get({startDate: $scope.filter.districtSessions.startDate, endDate: $scope.filter.districtSessions.endDate}, function(data){

             $scope.districtSessions.dataPoints =   data.districtSessions;
         });
     }*/


        $scope.data = $scope.datarows = [];
       // $scope.filter.max = 10000;
    };

    $scope.periodTrends = [{}];


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

    $scope.reportingPerformance = {};

    $scope.repairing = {};
    $scope.supplying = {};
    $scope.investigating = {};
    $scope.filter ={sessions:{}};

    $scope.datapoints=[];
    $scope.datacolumns=[{"id":"outreach_sessions", "name": messageService.get('label.outreach.sessions'),"type":"bar"},
        {"id":"fixed_sessions","name": messageService.get('label.outreach.sessions'),"type":"bar"}
    ];

    $scope.datax={"id":"period_name"};

}
VaccineDashboardController.resolve = {

};
