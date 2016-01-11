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

function VaccineDashboardController($scope,VaccineDashboardSummary,$filter,VaccineDashboardMonthlyCoverage,
                                    VaccineDashboardDistrictCoverage,
                                    VaccineDashboardMonthlyDropout,
                                    VaccineDashboardDistrictDropout,
                                    VaccineDashboardDistrictSessions,
                                    VaccineDashboardSessions,
                                    VaccineDashboardMonthlyStock,
                                    VaccineDashboardDistrictStock,
                                    VaccineDashboardMonthlyWastage,
                                    VaccineDashboardDistrictWastage,
                                    VaccineDashboardFacilityTrend,
                                    VaccineDashboardBundle,
                                    dashboardSlidesHelp,
                                    VaccineDashboardFacilityCoverageDetails,
                                    VaccineDashboardFacilityCoverage,
                                    defaultProduct,
                                    defaultPeriodTrend,
                                    defaultMonthlyPeriod,
                                    SettingsByKey, messageService) {
    $scope.actionBar = {openPanel: true};
    $scope.performance = {openPanel: true};
    $scope.stockStatus = {openPanel: true};
     $scope.sessions = {
        openPanel: true
    };

    $scope.dashboardHelps = dashboardSlidesHelp;
    $scope.defaultPeriodTrend = parseInt(defaultPeriodTrend, 10);
    $scope.defaultProduct = defaultProduct;
    $scope.defaultMonthlyPeriod = defaultMonthlyPeriod;
    $scope.label ={ zone: messageService.get('label.zone'), period: messageService.get('label.period')};


//Instantiate Monthly Sessions charting options
     $scope.monthlySessions = {
        dataPoints: [],
            dataColumns: [{
            "id": "outreach_sessions",
            "name": messageService.get('label.outreach.sessions'),
            "type": "bar"
        },
            {"id": "fixed_sessions", "name": messageService.get('label.fixed.sessions'), "type": "bar"}],
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
            {"id": "fixed_sessions", "name": messageService.get('label.fixed.sessions'), "type": "bar"}],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.monthlyCoverage = {
        dataPoints: [],
        dataColumns: [{
                        "id": "coverage", "name": messageService.get('label.coverage'), "type": "line"},
                      {"id": "actual", "name": messageService.get('label.actual'), "type": "bar"},
                      {"id": "target", "name": messageService.get('label.target'), "type": "bar"}
                    ],
        dataX: {"id": "period_name"}
    };
    $scope.facilityCoverage = {
        dataPoints: [],
        dataColumns: [{
            "id": "coverage", "name": messageService.get('label.coverage'), "type": "line"},
            {"id": "actual", "name": messageService.get('label.actual'), "type": "bar"},
            {"id": "target", "name": messageService.get('label.target'), "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };

    $scope.districtCoverage = {
        dataPoints: [],
        dataColumns: [{
            "id": "coverage", "name": messageService.get('label.coverage'), "type": "line"},
            {"id": "actual", "name": messageService.get('label.actual'), "type": "bar"},
            {"id": "target", "name": messageService.get('label.target'), "type": "bar"}
        ],
        dataX: {"id": "geographic_zone_name"}
    };


    $scope.monthlyWastage = {
        dataPoints: [],
        dataColumns: [{
            "id": "wastage_rate", "name": messageService.get('label.wastage.rate'), "type": "bar"}
        ],
        dataX: {"id": "period_name"}
    };

    $scope.districtWastage= {
        dataPoints: [],
        dataColumns: [{
            "id": "wastage_rate", "name": messageService.get('label.wastage.rate'), "type": "bar"}
        ],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.monthlyDropout= {
        dataPoints: [],
        dataColumns: [{
            "id": "bcg_mr_dropout", "name": messageService.get('label.bcg.mr.dropout'), "type": "bar"},
            {"id": "dtp1_dtp3_dropout", "name": messageService.get('label.dtp.dropout'), "type": "bar"}
        ],
        dataX: {"id": "period_name"}
    };
    $scope.districtDropout= {
        dataPoints: [],
        dataColumns: [{
            "id": "bcg_mr_dropout", "name": messageService.get('label.bcg.mr.dropout'), "type": "bar"},
            {"id": "dtp1_dtp3_dropout", "name": messageService.get('label.dtp.dropout'), "type": "bar"}
        ],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.monthlyStock= {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"}
        ],
        dataX: {"id": "period_name"}
    };
    $scope.districtStock= {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"}
        ],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.bundling= {
        dataPoints: [],
        dataColumns: [{
            "id": "minlimit", "name": messageService.get('label.min.limit'), "type": "line"},
            {"id": "maxlimit", "name": messageService.get('label.max.limit'), "type": "line"},
            {"id": "bund_issued", "name": messageService.get('label.bundle.issued'), "type": "bar"},
            {"id": "bund_received", "name": messageService.get('label.bundle.received'), "type": "bar"}
        ],
        dataX: {"id": "period_name"}
    };


    $scope.facilitySessions = {
        dataPoints: [],
        dataColumns: [{
            "id": "fixed_sessions", "name": messageService.get('label.fixed.sessions'), "type": "line"},
            {"id": "outreach_sessions", "name": messageService.get('label.outreach.sessions'), "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };
    $scope.facilityWastage = {
        dataPoints: [],
        dataColumns: [{
            "id": "wastage_rate", "name": messageService.get('label.wastage'), "type": "line"},
            {"id": "usage_rate", "name": messageService.get('label.actual'), "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };
    $scope.facilityDropout = {
        dataPoints: [],
        dataColumns: [{
            "id": "bcg_mr_dropout", "name": messageService.get('label.bcg.mr.dropout'), "type": "line"},
            {"id": "dtp1_dtp3_dropout", "name": messageService.get('label.dtp.dropout'), "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };

    $scope.formatValue = function (value, ratio, id) {
        return $filter('number')(value);
    };
    $scope.facilityCoveragePagination = function(){
         var s = parseInt($scope.filter.facilityCoverageOffset,10)+ parseInt($scope.filter.facilityCoverageRange,10);
        if(!isUndefined($scope.filter.facilityCoverageOffset) ){
            $scope.facilityCoverage.dataPoints = $scope.facilityCoverage.data.slice(parseInt($scope.filter.facilityCoverageOffset,10), s );
        }
    };
    $scope.monthlyCoverageCallback = function(){
        if(!isUndefined( $scope.filter.monthlyCoverage.startDate) &&
            !isUndefined( $scope.filter.monthlyCoverage.endDate) &&
            !isUndefined($scope.filter.monthlyCoverage.product) && $scope.filter.monthlyCoverage.product !== 0){
            VaccineDashboardMonthlyCoverage.get({startDate: $scope.filter.monthlyCoverage.startDate, endDate: $scope.filter.monthlyCoverage.endDate,
                product: $scope.filter.monthlyCoverage.product}, function(data){
                $scope.monthlyCoverage.dataPoints = data.monthlyCoverage;
            });

        }
    };

    $scope.coverageDetailCallback = function(){
        if(!isUndefined( $scope.filter.detailCoverage.startDate) &&
            !isUndefined( $scope.filter.detailCoverage.endDate) &&
            !isUndefined($scope.filter.detailCoverage.product) && $scope.filter.detailCoverage.product !== 0){

           // VaccineDashboardFacilityCoverageDetails.get({startDate: $scope.filter.detailCoverage.startDate, endDate: $scope.filter.detailCoverage.endDate,
            VaccineDashboardFacilityTrend.coverageDetails({startDate: $scope.filter.detailCoverage.startDate, endDate: $scope.filter.detailCoverage.endDate,
                product: $scope.filter.detailCoverage.product}, function(data){

                $scope.coverageDetails = data.facilityCoverageDetails;
                $scope.periodsList = _.uniq(_.pluck(data.facilityCoverageDetails,'period_name'));
                var facilities = _.uniq(_.pluck(data.facilityCoverageDetails,'facility_name'));
                $scope.facilityDetails  = [];
                angular.forEach(facilities, function(facility){
                    var district =_.findWhere($scope.coverageDetails, {facility_name: facility}).district_name;

                    $scope.facilityDetails.push({district: district, facilityName: facility, indicator: 'target', indicatorValues: $scope.getIndicatorValues(facility, 'target',$scope.coverageDetails)});
                    $scope.facilityDetails.push({district: district,facilityName: facility, indicator: 'actual', indicatorValues: $scope.getIndicatorValues(facility, 'actual', $scope.coverageDetails)});
                    $scope.facilityDetails.push({district: district,facilityName: facility, indicator: 'coverage', indicatorValues: $scope.getIndicatorValues(facility, 'coverage', $scope.coverageDetails)});

                });
            });

        }
    };
    $scope.sessionDetailCallback = function(){
        if(!isUndefined( $scope.filter.detailSessions.startDate) &&
            !isUndefined( $scope.filter.detailSessions.endDate) ){

            VaccineDashboardFacilityTrend.sessionsDetails({startDate: $scope.filter.detailSessions.startDate, endDate: $scope.filter.detailSessions.endDate}, function(data){

                $scope.sessionsDetails = data.facilitySessionsDetails;
                $scope.sessionsPeriodsList = _.uniq(_.pluck($scope.sessionsDetails,'period_name'));
                var facilities = _.uniq(_.pluck($scope.sessionsDetails,'facility_name'));
                $scope.facilitySessionsDetails  = [];
                angular.forEach(facilities, function(facility){
                    var district =_.findWhere($scope.sessionsDetails, {facility_name: facility}).district_name;

                    $scope.facilitySessionsDetails.push({district: district, facilityName: facility, indicator: 'fixed_sessions', indicatorValues: $scope.getIndicatorValues(facility, 'fixed_sessions',$scope.sessionsDetails)});
                    $scope.facilitySessionsDetails.push({district: district,facilityName: facility, indicator: 'outreach_sessions', indicatorValues: $scope.getIndicatorValues(facility, 'outreach_sessions', $scope.sessionsDetails)});

                });
            });

        }
    };
    $scope.facilitySessionsPagination = function(){
        var s = parseInt($scope.filter.facilitySessionsOffset,10)+ parseInt($scope.filter.facilitySessionsRange,10);
        if(!isUndefined($scope.filter.facilitySessionsOffset) ){
            $scope.facilitySessions.dataPoints = $scope.facilitySessions.data.slice(parseInt($scope.filter.facilitySessionsOffset,10), s );
        }
    };
    $scope.facilitySessionsCallback = function(){
        if(!isUndefined( $scope.filter.facilitySessions.period)){
           VaccineDashboardFacilityTrend.sessions({period: $scope.filter.facilitySessions.period}, function(data){
                $scope.facilitySessions.data = data.facilitySessions;
                if(!isUndefined($scope.facilitySessions.data)){
                    $scope.filter.totalFacilitySessions = $scope.facilitySessions.data.length;
                }else{
                    $scope.filter.totalFacilitySessions= 0;
                }
            });


        }
    };
    $scope.facilityWastagePagination = function(){
        var s = parseInt($scope.filter.facilityWastageOffset,10)+ parseInt($scope.filter.facilityWastageRange,10);
        if(!isUndefined($scope.filter.facilityWastageOffset) ){
            $scope.facilityWastage.dataPoints = $scope.facilityWastage.data.slice(parseInt($scope.filter.facilityWastageOffset,10), s );
        }
    };
    $scope.wastageDetailCallback = function(){
        if(!isUndefined( $scope.filter.detailWastage.startDate) &&
            !isUndefined( $scope.filter.detailWastage.endDate) &&
            !isUndefined($scope.filter.detailWastage.product) && $scope.filter.detailWastage.product !== 0){

            VaccineDashboardFacilityTrend.wastageDetails({startDate: $scope.filter.detailWastage.startDate, endDate: $scope.filter.detailWastage.endDate,
                product: $scope.filter.detailWastage.product}, function(data){

                $scope.wastageDetails = data.facilityWastageDetails;
                $scope.wastagePeriodsList = _.uniq(_.pluck($scope.wastageDetails,'period_name'));
                var facilities = _.uniq(_.pluck($scope.wastageDetails,'facility_name'));
                $scope.facilityWastageDetails  = [];
                angular.forEach(facilities, function(facility){
                    var district =_.findWhere($scope.wastageDetails, {facility_name: facility}).district_name;

                    $scope.facilityWastageDetails.push({district: district, facilityName: facility, indicator: 'wastage_rate', indicatorValues: $scope.getIndicatorValues(facility, 'wastage_rate',$scope.wastageDetails)});
                    $scope.facilityWastageDetails.push({district: district,facilityName: facility, indicator: 'usage_rate', indicatorValues: $scope.getIndicatorValues(facility, 'usage_rate', $scope.wastageDetails)});

                });
            });

        }
    };

    $scope.facilityWastageCallback = function(){
        if(!isUndefined( $scope.filter.facilityWastage.period) &&
            !isUndefined($scope.filter.facilityWastage.product) && $scope.filter.facilityWastage.product !== 0){
            VaccineDashboardFacilityTrend.wastage({period: $scope.filter.facilityWastage.period,
                product: $scope.filter.facilityWastage.product}, function(data){
                $scope.facilityWastage.data = data.facilityWastage;
                if(!isUndefined($scope.facilityWastage.data)){

                    $scope.filter.totalfacilityWastage = $scope.facilityWastage.data.length;
                   // $scope.facilityWastagePagination();
                }else{
                    $scope.filter.totalfacilityWastage= 0;
                }
            });


        }
    };
    $scope.facilityDropoutPagination = function(){
        var s = parseInt($scope.filter.facilityDropoutOffset,10)+ parseInt($scope.filter.facilityDropoutRange,10);
        if(!isUndefined($scope.filter.facilityDropoutOffset) ){
            $scope.facilityDropout.dataPoints = $scope.facilityDropout.data.slice(parseInt($scope.filter.facilityDropoutOffset,10), s );
        }
    };
    $scope.dropoutDetailCallback = function(){
        if(!isUndefined( $scope.filter.detailDropout.startDate) &&
            !isUndefined( $scope.filter.detailDropout.endDate) &&
            !isUndefined($scope.filter.detailDropout.product) && $scope.filter.detailDropout.product !== 0){

            VaccineDashboardFacilityTrend.dropoutDetails({startDate: $scope.filter.detailDropout.startDate, endDate: $scope.filter.detailDropout.endDate,
                product: $scope.filter.detailDropout.product}, function(data){

                $scope.dropoutDetails = data.facilityDropoutDetails;
                $scope.dropoutPeriodsList = _.uniq(_.pluck($scope.dropoutDetails,'period_name'));
                var facilities = _.uniq(_.pluck($scope.dropoutDetails,'facility_name'));
                $scope.facilityDropoutDetails  = [];
                angular.forEach(facilities, function(facility){
                    var district =_.findWhere($scope.dropoutDetails, {facility_name: facility}).district_name;

                    $scope.facilityDropoutDetails.push({district: district, facilityName: facility, indicator: 'bcg_mr_dropout', indicatorValues: $scope.getIndicatorValues(facility, 'bcg_mr_dropout',$scope.dropoutDetails)});
                    $scope.facilityDropoutDetails.push({district: district,facilityName: facility, indicator: 'dtp1_dtp3_dropout', indicatorValues: $scope.getIndicatorValues(facility, 'dtp1_dtp3_dropout', $scope.dropoutDetails)});

                });
            });

        }
    };

    $scope.facilityDropoutCallback = function(){
        if(!isUndefined( $scope.filter.facilityDropout.period) &&
            !isUndefined($scope.filter.facilityDropout.product) && $scope.filter.facilityDropout.product !== 0){
            VaccineDashboardFacilityTrend.dropout({period: $scope.filter.facilityDropout.period,
                product: $scope.filter.facilityDropout.product}, function(data){
                $scope.facilityDropout.data = data.facilityDropout;
                if(!isUndefined($scope.facilityDropout.data)){
                    $scope.filter.totalfacilityDropout = $scope.facilityDropout.data.length;
                }else{
                    $scope.filter.totalfacilityDropout= 0;
                }
            });


        }
    };

    $scope.getIndicatorValues = function (facility, indicator, data) {
        var facilityDetail = _.where(data, {facility_name: facility});
        var values = _.pluck(facilityDetail, indicator);
        var tot = _.reduce(values, function(res, num){return res + num;}, 0);
        values.push(tot);
        return values;
    };
    $scope.getDetail = function(facility, period){
        return _.findWhere($scope.coverageDetails, {facility_name: facility, period_name: period});
    };

    $scope.facilityCoverageCallback = function(){
        if(!isUndefined( $scope.filter.facilityCoverage.period) &&
            !isUndefined($scope.filter.facilityCoverage.product) && $scope.filter.monthlyCoverage.product !== 0){
            //VaccineDashboardFacilityCoverage.get({period: $scope.filter.facilityCoverage.period,
            VaccineDashboardFacilityTrend.coverage({period: $scope.filter.facilityCoverage.period,
                product: $scope.filter.facilityCoverage.product}, function(data){
                $scope.facilityCoverage.data = data.facilityCoverage;
                if(!isUndefined($scope.facilityCoverage.data)){
                    $scope.filter.totalFacilityCoverage = $scope.facilityCoverage.data.length;
                }else{
                    $scope.filter.totalFacilityCoverage= 0;
                }
            });


        }
    };

    $scope.districtCoverageCallback = function(){
        if(!isUndefined( $scope.filter.districtCoverage.period) &&
            !isUndefined($scope.filter.districtCoverage.product) && $scope.filter.districtCoverage.product !== 0){
            VaccineDashboardDistrictCoverage.get({period: $scope.filter.districtCoverage.period,
                product: $scope.filter.districtCoverage.product}, function(data){
                $scope.districtCoverage.dataPoints = data.districtCoverage;
            });
        }
    };
    $scope.monthlyWastageCallback = function(){
        if(!isUndefined( $scope.filter.monthlyWastage.startDate) &&
            !isUndefined( $scope.filter.monthlyWastage.endDate) &&
            !isUndefined($scope.filter.monthlyWastage.product) &&  $scope.filter.monthlyWastage.product !== 0 ){
            VaccineDashboardMonthlyWastage.get({startDate: $scope.filter.monthlyWastage.startDate, endDate: $scope.filter.monthlyWastage.endDate,
                product: $scope.filter.monthlyWastage.product}, function(data){
                $scope.monthlyWastage.dataPoints = data.wastageMonthly;
            });
        }
    };

    $scope.districtWastageCallback = function(){
        if(!isUndefined( $scope.filter.districtWastage.period) &&
            !isUndefined( $scope.filter.districtWastage.product) && $scope.filter.districtWastage.product !== 0 ){
            VaccineDashboardDistrictWastage.get({period: $scope.filter.districtWastage.period,  product: $scope.filter.districtWastage.product}, function(data){
                $scope.districtWastage.dataPoints = data.districtWastage;
            });
        }
    };

    $scope.monthlyDropoutCallback = function(){
        if(!isUndefined( $scope.filter.monthlyDropout.startDate) &&
            !isUndefined( $scope.filter.monthlyDropout.endDate) &&
            !isUndefined($scope.filter.monthlyDropout.product) && $scope.filter.monthlyDropout.product !== 0){
            VaccineDashboardMonthlyDropout.get({startDate: $scope.filter.monthlyDropout.startDate, endDate: $scope.filter.monthlyDropout.endDate,
                product: $scope.filter.monthlyDropout.product}, function(data){
                $scope.monthlyDropout.dataPoints = data.monthlyDropout;
            });
        }
    };
    $scope.districtDropoutCallback = function(){
        if(!isUndefined( $scope.filter.districtDropout.period) &&
            !isUndefined($scope.filter.districtDropout.product) && $scope.filter.districtDropout.product !== 0){
            VaccineDashboardDistrictDropout.get({period: $scope.filter.districtDropout.period,
                product: $scope.filter.districtDropout.product}, function(data){
                $scope.districtDropout.dataPoints = data.districtDropout;
            });
        }
    };

    $scope.monthlyStockCallback = function () {
        if(!isUndefined( $scope.filter.monthlyStock.startDate) &&
            !isUndefined( $scope.filter.monthlyStock.endDate) &&
            !isUndefined($scope.filter.monthlyStock.product) && $scope.filter.monthlyStock.product !== 0){
            VaccineDashboardMonthlyStock.get({startDate: $scope.filter.monthlyStock.startDate, endDate: $scope.filter.monthlyStock.endDate,
                product: $scope.filter.monthlyStock.product}, function(data){
                $scope.monthlyStock.dataPoints = data.monthlyStock;
            });
        }
    };

    $scope.districtStockCallback = function(){
        if(!isUndefined( $scope.filter.districtStock.period) &&
            !isUndefined( $scope.filter.districtStock.product) && $scope.filter.districtStock.product !== 0 ){
            VaccineDashboardDistrictStock.get({period: $scope.filter.districtStock.period,  product: $scope.filter.districtStock.product}, function(data){
                $scope.districtStock.dataPoints = data.districtStock;
            });
        }
    };

    $scope.monthlySessionsCallback = function(){
        if(!isUndefined( $scope.filter.monthlySessions.startDate) && !isUndefined( $scope.filter.monthlySessions.endDate)){
            VaccineDashboardSessions.get({startDate: $scope.filter.monthlySessions.startDate, endDate: $scope.filter.monthlySessions.endDate}, function(data){

                $scope.monthlySessions.dataPoints =   data.monthlySessions;
            });
        }
    };
    $scope.districtSessionsCallback = function(){
        if(!isUndefined( $scope.filter.districtSessions.period) ) {
            VaccineDashboardDistrictSessions.get({period: $scope.filter.districtSessions.period}, function(data){

                $scope.districtSessions.dataPoints =   data.districtSessions;
            });
        }
    };
   /* $scope.$watchCollection('[filter.monthlyCoverage.startDate, filter.monthlyCoverage.endDate, filter.monthlyCoverage.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.monthlyCoverage.startDate) &&
            !isUndefined( $scope.filter.monthlyCoverage.endDate) &&
            !isUndefined($scope.filter.monthlyCoverage.product)){
            VaccineDashboardMonthlyCoverage.get({startDate: $scope.filter.monthlyCoverage.startDate, endDate: $scope.filter.monthlyCoverage.endDate,
                product: $scope.filter.monthlyCoverage.product}, function(data){
                $scope.monthlyCoverage.dataPoints = data.monthlyCoverage;
            });
        }
    });*/

   /* $scope.$watchCollection('[filter.districtCoverage.period, filter.districtCoverage.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.districtCoverage.period) &&
            !isUndefined($scope.filter.districtCoverage.product)){
            VaccineDashboardDistrictCoverage.get({period: $scope.filter.districtCoverage.period,
                product: $scope.filter.districtCoverage.product}, function(data){
                $scope.districtCoverage.dataPoints = data.districtCoverage;
            });
        }
    });*/

  /*  $scope.$watchCollection('[filter.monthlyWastage.startDate, filter.monthlyWastage.endDate, filter.monthlyWastage.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.monthlyWastage.startDate) &&
            !isUndefined( $scope.filter.monthlyWastage.endDate) &&
            !isUndefined($scope.filter.monthlyWastage.product)){
            VaccineDashboardMonthlyWastage.get({startDate: $scope.filter.monthlyWastage.startDate, endDate: $scope.filter.monthlyWastage.endDate,
                product: $scope.filter.monthlyWastage.product}, function(data){
                $scope.monthlyWastage.dataPoints = data.wastageMonthly;
            });
        }
    });

    $scope.$watchCollection('[filter.districtWastage.period, filter.districtWastage.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.districtWastage.period) &&
            !isUndefined( $scope.filter.districtWastage.product) ){
            VaccineDashboardDistrictWastage.get({period: $scope.filter.districtWastage.period,  product: $scope.filter.districtWastage.product}, function(data){
                $scope.districtWastage.dataPoints = data.districtWastage;
            });
        }
    });
*/
  /*  $scope.$watchCollection('[filter.monthlyDropout.startDate, filter.monthlyDropout.endDate, filter.monthlyDropout.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.monthlyDropout.startDate) &&
            !isUndefined( $scope.filter.monthlyDropout.endDate) &&
            !isUndefined($scope.filter.monthlyDropout.product)){
            VaccineDashboardMonthlyDropout.get({startDate: $scope.filter.monthlyDropout.startDate, endDate: $scope.filter.monthlyDropout.endDate,
                product: $scope.filter.monthlyDropout.product}, function(data){
                $scope.monthlyDropout.dataPoints = data.monthlyDropout;
            });
        }
    });

    $scope.$watchCollection('[filter.districtDropout.period, filter.districtDropout.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.districtDropout.period) &&
            !isUndefined($scope.filter.districtDropout.product)){
            VaccineDashboardDistrictDropout.get({period: $scope.filter.districtDropout.period,
                product: $scope.filter.districtDropout.product}, function(data){
                $scope.districtDropout.dataPoints = data.districtDropout;
            });
        }
    });

    $scope.$watchCollection('[filter.monthlyStock.startDate, filter.monthlyStock.endDate, filter.monthlyStock.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.monthlyStock.startDate) &&
            !isUndefined( $scope.filter.monthlyStock.endDate) &&
            !isUndefined($scope.filter.monthlyStock.product)){
            VaccineDashboardMonthlyStock.get({startDate: $scope.filter.monthlyStock.startDate, endDate: $scope.filter.monthlyStock.endDate,
                product: $scope.filter.monthlyStock.product}, function(data){
                $scope.monthlyStock.dataPoints = data.monthlyStock;
            });
        }
    });

    $scope.$watchCollection('[filter.districtStock.period, filter.districtStock.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.districtStock.period) &&
            !isUndefined( $scope.filter.districtStock.product) ){
            VaccineDashboardDistrictStock.get({period: $scope.filter.districtStock.period,  product: $scope.filter.districtStock.product}, function(data){
                $scope.districtStock.dataPoints = data.districtStock;
            });
        }
    });


    $scope.$watchCollection('[filter.monthlySessions.startDate, filter.monthlySessions.endDate]', function(newValues, oldValues){

        if(!isUndefined( $scope.filter.monthlySessions.startDate) && !isUndefined( $scope.filter.monthlySessions.endDate)){
            VaccineDashboardSessions.get({startDate: $scope.filter.monthlySessions.startDate, endDate: $scope.filter.monthlySessions.endDate}, function(data){

                $scope.monthlySessions.dataPoints =   data.monthlySessions;
            });
        }
    });


    $scope.$watchCollection('[filter.bundling.startDate, filter.bundling.endDate, filter.bundling.product]', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.bundling.startDate) &&
            !isUndefined( $scope.filter.bundling.endDate) &&
            !isUndefined($scope.filter.bundling.product)){
            VaccineDashboardBundle.get({startDate: $scope.filter.bundling.startDate, endDate: $scope.filter.bundling.endDate,
                product: $scope.filter.bundling.product}, function(data){
                $scope.bundling.dataPoints = data.bundling;
            });
        }
    });

    $scope.$watch('filter.districtSessions.period', function(newValues, oldValues){
        if(!isUndefined( $scope.filter.districtSessions.period) ) {
            VaccineDashboardDistrictSessions.get({period: $scope.filter.districtSessions.period}, function(data){

                $scope.districtSessions.dataPoints =   data.districtSessions;
            });
        }

    });

*/

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


    $scope.setInterval = function(carouselId){
        var cr = _.findWhere($scope.carousels, {id: carouselId});
        if(!isUndefined(cr)){
            return cr.interval;
        }
        return -1;
    };

    $scope.OnFilterChanged = function() {

        $scope.data = $scope.datarows = [];
       // $scope.filter.max = 10000;
    };


    VaccineDashboardSummary.get({}, function(data){
       $scope.reportingPerformance = data.summary.reportingSummary;
       $scope.repairing = data.summary.repairing;
       $scope.investigating = data.summary.investigating;
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
    dashboardSlidesHelp: function($q, $timeout, HelpContentByKey){
      var deferred = $q.defer();
        var helps =  {};
        $timeout(function(){
            HelpContentByKey.get({content_key: 'Coverage Dashboard'}, function(data){
                helps.coverageHelp = data.siteContent;
            });
            HelpContentByKey.get({content_key: 'Wastage Dashboard'}, function(data){
                helps.wastageHelp = data.siteContent;
            });
            HelpContentByKey.get({content_key: 'Sessions Dashboard'}, function(data){
                helps.sessionsHelp = data.siteContent;
            });
            HelpContentByKey.get({content_key: 'Dropout Dashboard'}, function(data){
                helps.dropoutHelp= data.siteContent;
            });

            deferred.resolve(helps);

        },100);
        return deferred.promise;
    },
    defaultProduct : function($q, $timeout, SettingsByKey){
        var deferred = $q.defer();
        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_DASHBOARD_DEFAULT_PRODUCT'}, function(data){
                if(isUndefined(data.settings) || isUndefined(data.settings.value)){
                    deferred.resolve(0);
                }else{

                    deferred.resolve(data.settings.value);
                }

            });

        },100);

        return deferred.promise;

    },
    defaultPeriodTrend : function($q, $timeout, SettingsByKey){
        var deferred = $q.defer();
        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_DASHBOARD_DEFAULT_PERIOD_TREND'}, function(data){
                if(isUndefined(data.settings) || isUndefined(data.settings.value)){
                    deferred.resolve(1);
                }else{

                    deferred.resolve(data.settings.value);
                }

            });

        },100);

        return deferred.promise;

    },
    defaultMonthlyPeriod: function($q, $timeout, SettingsByKey){
        var deferred = $q.defer();
        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_DASHBOARD_DEFAULT_MONTHLY_PERIOD'}, function(data){
                if(isUndefined(data.settings) || isUndefined(data.settings.value)){
                    deferred.resolve(0);
                }else{

                    deferred.resolve(data.settings.value);
                }

            });

        },100);

        return deferred.promise;

    }


};
