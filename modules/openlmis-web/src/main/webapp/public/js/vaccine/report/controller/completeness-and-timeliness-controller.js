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

function CompletenesssAndTimelinessReportController($scope, CompletenessAndTimeliness, Settings) {
    Settings.get({}, function (data) {

        _.each(data.settings.list, function (item) {

           if (item.key === "VCP_NON_REPORTING") {
                $scope.color_non_reporting = item.value;
            }
        });
    });
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

                    var columnKeysToBeAggregated = ["target", "expected", "reported", "late", "fixed", "outreach", "session_total"];
                    var districtNameKey = "district_name";
                    var includeGrandTotal = true;

                     $scope.error = "";
                     $scope.datarows = utils.getDistrictBasedReportDataWithSubAndGrandTotal(data.completenessAndTimeliness.mainreport,
                                                                                            districtNameKey,
                                                                                            columnKeysToBeAggregated,
                                                                                            includeGrandTotal);
                     $scope.summary = data.completenessAndTimeliness.summary;
                     $scope.summaryPeriodLists = data.completenessAndTimeliness.summaryPeriodLists;
                     $scope.aggregateSummary = data.completenessAndTimeliness.aggregateSummary;

                     // Get a unique periods for the header
                     var uniquePeriods    = _.chain($scope.summary).indexBy("period").values().value();
                     $scope.sortedPeriods = _.sortBy(uniquePeriods, function (up) { return up.row; });

                     if($scope.summary !== null) {
                        pivotResultSet($scope.summary);
                     }
            });
    };

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
                    $scope.completeness.push ({total:summary[i].reported === 0 ? 0 : Math.round(((summary[i].reported/summary[i].expected) * 100)*100)/100});
                    $scope.timelines.push    ({total:summary[i].ontime === 0 ? 0 : Math.round(((summary[i].ontime/summary[i].reported) * 100)*100)/100});
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

    $scope.bgColorCode = function (value) {

        if ( value.reporting_status !== 'REPORTING') {
            return $scope.color_non_reporting;
        }else{
            return "white";
        }

    };
}
