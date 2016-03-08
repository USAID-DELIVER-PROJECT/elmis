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

function AdequacyLevelOfSupplyController($scope, $routeParams, AdequacyLevelOfSupply, ReportProductsByProgram, messageService) {

    ReportProductsByProgram.get({programId: 82}, function (data) {
        $scope.product = data.productList;
    });

    $scope.perioderror = "";
    var isFixTableInitialized = false;


    var DISTRICT_AGGREGATE_UPPER_TRESHOLD = 150;
    var DISTRICT_AGGREGATE_LOWER_TRESHOLD = 100;
    var REGION_AGGREGATE_UPPER_TRESHOLD = 150;
    var REGION_AGGREGATE_LOWER_TRESHOLD = 100;
    var reportdataKeys =  ["supplied_over_needs", "mos", "consumption_rate", "wasted_opened", "wasted_unopened", "wasted_global"];
    var ABOVE = 'ABOVE', BELOW = 'BELOW';

    $scope.OnFilterChanged = function () {

        // prevent first time loading
        if (utils.isEmpty($scope.filter.product) || utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;

        AdequacyLevelOfSupply.get(
            {
                periodStart: $scope.periodStartDate,
                periodEnd:   $scope.periodEnddate,
                range:       $scope.range,
                district:    utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
                product:     $scope.filter.product
            },

            function (data) {

                     $scope.error = "";
                     $scope.datarows = data.adequaceyLevel.bydistrict;
                     $scope.datarowsByRegion = data.adequaceyLevel.byregion;
                     $scope.periodCols = data.adequaceyLevel.summaryPeriodLists;


                $scope.uniqueDistr = _.uniq(_.pluck($scope.datarows, 'district_name'));
                $scope.districtReport = formatReportTablesData($scope.uniqueDistr, $scope.datarows, 'district');

                $scope.uniqueRegions = _.uniq(_.pluck($scope.datarowsByRegion, 'region_name'));
                $scope.regionalReport = formatReportTablesData($scope.uniqueRegions, $scope.datarowsByRegion, 'region');

                $scope.rowOneAggregate =   generateMainReportAggregateData($scope.districtReport, DISTRICT_AGGREGATE_UPPER_TRESHOLD,    ABOVE);
                $scope.rowTwoAggregate =   generateMainReportAggregateData($scope.districtReport, DISTRICT_AGGREGATE_LOWER_TRESHOLD,    BELOW);
                $scope.rowThreeAggregate = generateMainReportRegionalAggregateData($scope.districtReport, REGION_AGGREGATE_UPPER_TRESHOLD,    ABOVE);
                $scope.rowFourAggregate =  generateMainReportRegionalAggregateData($scope.districtReport, REGION_AGGREGATE_LOWER_TRESHOLD,    BELOW);

               if(!isFixTableInitialized) {
                   setTimeout(function () {
                       $("#fixTableDistrict").tableHeadFixer({"foot": true, "head": true, left: 4});
                       $("#fixTableRegion").tableHeadFixer({"foot": true, "head": true, left: 2});
                   }, 0);
               }
                isFixTableInitialized = true;

            });

        function generateMainReportAggregateData(reportData, threshold, thresholdType){

           var aggregateResult = [];

             _.each(reportdataKeys, function(key){

                 $.merge(aggregateResult, _.map(_.zip.apply(_, _.pluck(reportData, key)), function(pieces) {
                    return angular.equals(thresholdType, ABOVE) ?
                        _.reduce(pieces, function(m, p) { return (_.isNumber(p) ? p : 0) > threshold ? m+1 : m; }, 0) :
                        _.reduce(pieces, function(m, p) { return (_.isNumber(p) && p < threshold) ? m+1 : m--; }, 0);
                }));

            });
            return aggregateResult;
        }

        function generateMainReportRegionalAggregateData(reportData, threshold, thresholdType){

            var aggregateResult = [];

            _.each(reportdataKeys, function(reportdataKey){

                $.merge(aggregateResult,
                    _.map(
                        _.zip.apply(_, regionCountByDataKeyAndThreshold(reportdataKey, threshold, thresholdType)), function(pieces)
                                {
                                    return  _.reduce(pieces, function(m, p) { return m+p; }, 0);
                                }));

            });
            return aggregateResult;
        }

        //returns array data of number of regions  with periodic data per each datakey
        function regionCountByDataKeyAndThreshold(datakey, threshold, thresholdType){

            var temp = [];

            _.each($scope.uniqueRegions, function(region)
            {
                temp.push(_.map(_.zip.apply(_, _.pluck(_.where($scope.districtReport, {region_name: region}), datakey)), function(pieces) {

                    return angular.equals(thresholdType, ABOVE) ?
                        _.reduce(pieces, function(m, p) { return p > threshold ? 1 : m; }, 0) :
                        _.reduce(pieces, function(m, p) { return p < threshold ? 1 : m; }, 0);
                }));
            });

            return temp;
        }

        function formatReportTablesData(uniqueDistr, reportDataRows, level){

            var mainResult = [];

            // for each unique districts
            _.each(uniqueDistr, function(item){

                var districtPeriodsData = angular.equals(level, 'district') ? _.where(reportDataRows, {district_name: item}) : _.where(reportDataRows, {region_name: item});

                var supplied_over_needs = [], mos = [], consumption_rate = [], wasted_opened = [], wasted_unopened = [], wasted_global = [];
                var zoneName = '';
                var regionName = '';

                //initialize the dynamic cols
                _.each($scope.periodCols, function(pcols, index) {
                    supplied_over_needs[index] = '-';
                    mos[index] = '-';
                    consumption_rate[index] = '-';
                    wasted_opened[index] = '-';
                    wasted_unopened[index] = '-';
                    wasted_global[index] = '-';
                });

                // selected district data colllection
                _.each(districtPeriodsData, function(datarow, i){

                    // for each unique selected periods
                    _.each($scope.periodCols, function(pcols, index) {

                        zoneName =  datarow.zone_name;
                        regionName = datarow.region_name;

                        if(datarow.report_month === pcols.month && datarow.report_year ===  pcols.year){
                            supplied_over_needs[index] = datarow.supplied_over_needs;
                            mos[index]                 = datarow.mos;
                            consumption_rate[index]    = datarow.consumption_rate;
                            wasted_opened[index]       = datarow.wasted_opened;
                            wasted_unopened[index]     = datarow.wasted_unopened;
                            wasted_global[index]       = datarow.wasted_global;
                        }

                    });


                });

                mainResult.push({zone_name: zoneName, region_name: regionName, district_name: item,
                    supplied_over_needs: supplied_over_needs, mos:mos, consumption_rate:consumption_rate, wasted_opened:wasted_opened,
                    wasted_unopened:wasted_unopened, wasted_global:wasted_global});
            });

           return mainResult;
        }


    };



}
