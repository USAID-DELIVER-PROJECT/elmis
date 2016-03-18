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

function CompletenesssAndTimelinessReportController($scope, $routeParams, CompletenessAndTimeliness, Settings,
                                                    ReportProductsByProgram, TreeGeographicZoneList, messageService,
                                                    GetUserUnassignedSupervisoryNode) {

    $scope.perioderror = "";

    $scope.OnFilterChanged = function () {

        // prevent first time loading
        if (utils.isEmpty($scope.periodStartDate) || utils.isEmpty($scope.periodEnddate) || !utils.isEmpty($scope.perioderror))
            return;


        CompletenessAndTimeliness.get(
            {

                periodStart: $scope.periodStartDate,
                periodEnd:   $scope.periodEnddate,
                range:       $scope.range,
                district:    utils.isEmpty($scope.filter.zone) ? 0 : $scope.filter.zone,
            },

            function (data) {

                     $scope.error = "";
                     $scope.datarows = mainReportDataWithSubAndGrandTotal(data.completenessAndTimeliness.mainreport);
                     $scope.summary = data.completenessAndTimeliness.summary;
                     $scope.summaryPeriodLists = data.completenessAndTimeliness.summaryPeriodLists;
                     $scope.aggregateSummary = data.completenessAndTimeliness.aggregateSummary;

                     // Get a unique periods for the header
                     var uniquePeriods = _.chain($scope.summary).indexBy("period").values().value();
                     $scope.sortedPeriods = _.sortBy(uniquePeriods, function (up) {
                         return up.row;
                     });

                     if($scope.summary !== null) {
                        pivotResultSet($scope.summary);
                     }

                    if(angular.isUndefined($scope.filter.zone) || (!angular.isUndefined($scope.filter.zone) && $scope.filter.zone === 0)) {
                        $scope.allRegionSelection = true;
                        generateDistrictStoreAggregateData($scope.aggregateSummary);
                    }
                   else{
                        $scope.allRegionSelection = false;
                    }
            });
    };

    $scope.groupBy = "district_name";

    function mainReportDataWithSubAndGrandTotal(reportData){

       if(reportData.length === 0)
           return;

        // get unique district
        var uniqueDistrictName = _.uniq(_.pluck(reportData,  $scope.groupBy));
        var reportDataWithAggregates = [];

        _.each(uniqueDistrictName, function(districtName){
            //main report rows
            reportDataWithAggregates.push({data: _.where(reportData, {district_name: districtName})});
            //sub total
            reportDataWithAggregates.push({subtotal : {
                target_total:   getColumnSubTotal(reportData, districtName, "target"),
                expected_total: getColumnSubTotal(reportData, districtName, "expected"),
                reported_total: getColumnSubTotal(reportData, districtName, "reported"),
                late_total:     getColumnSubTotal(reportData, districtName, "late"),
                fixed_total:    getColumnSubTotal(reportData, districtName, "fixed"),
                outreach_total: getColumnSubTotal(reportData, districtName, "outreach"),
                session_total:  getColumnSubTotal(reportData, districtName, "session_total")
            }});
        });
            // grand total
           reportDataWithAggregates.push({grandtotal : {
                target_total:   getGrandTotal(reportData, "target"),
                expected_total: getGrandTotal(reportData, "expected"),
                reported_total: getGrandTotal(reportData, "reported"),
                late_total:     getGrandTotal(reportData, "late"),
                fixed_total:    getGrandTotal(reportData, "fixed"),
                outreach_total: getGrandTotal(reportData, "outreach"),
                session_total:  getGrandTotal(reportData, "session_total")
            }});

        return reportDataWithAggregates;
    }

    function getColumnSubTotal(reportData, districtName, columnToBeAgregated){
        return _.chain(reportData).where({district_name:  districtName}).pluck(columnToBeAgregated).reduce(function(memo, num){ return memo + num; }, 0).value();
    }

    function getGrandTotal(reportData, columnToBeAgregated){
        return _.chain(reportData).pluck(columnToBeAgregated).reduce(function(memo, num){ return memo + num; }, 0).value();
    }

    function generateDistrictStoreAggregateData(summary){
        $scope.aggregateTableData = [];
        $scope.aggregateExpectedStoresCount;

        _.each(summary, function(item, index) {

            var col = [];

            for(i=0; i<$scope.summaryPeriodLists.length; i++)
            {
                if(i === 0) {
                    col.push({val: item.district_name});
                    $scope.aggregateExpectedStoresCount = item.expected;
                }

                if(item.year === $scope.summaryPeriodLists[i].year && item.month === $scope.summaryPeriodLists[i].month)
                    col.push({val : item.reporting_status});

                else
                   col.push({val : '-'});

            }
            $scope.aggregateTableData.push(col);

        });

    }

    function pivotResultSet(summary){

        $scope.completeness = [] ;
        $scope.expected = [] ; $scope.reported = [] ; $scope.ontime = [] ; $scope.timelines = [];

         var expected = _.map(summary, function(num){ return num.expected; })[0];

        _.each($scope.summaryPeriodLists, function(item, index) {

            for(i=0; i<summary.length; i++)
            {

                if(item.year === summary[i].year && item.month === summary[i].month) {
                    $scope.expected.push     ({total:summary[i].expected});
                    $scope.reported.push     ({total:summary[i].reported});
                    $scope.ontime.push       ({total:summary[i].ontime});
                    $scope.completeness.push ({total:Math.round(((summary[i].reported/summary[i].expected) * 100)*100)/100});
                    $scope.timelines.push    ({total:Math.round(((summary[i].ontime/summary[i].reported) * 100)*100)/100});
                    break;
                }
                // if no match is found add a dummy object as a place holder
                else if(i+1 === $scope.summary.length) {
                    $scope.expected.push    ({total:expected});
                    $scope.reported.push    ({total:0});
                    $scope.ontime.push      ({total:0});
                    $scope.completeness.push({total:0});
                    $scope.timelines.push   ({total:0});
                }
            }
        });
    }


}
