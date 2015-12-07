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

function TrendMinMaxColdRangeController($scope,TrendOfMinMasColdRange){
    $scope.OnFilterChanged = function () {

        $scope.data = $scope.datarows = [];
        $scope.filter.facilityId='' ;
        $scope.filter.geographicZoneId = $scope.filter.zone;
        $scope.filter.productId = $scope.filter.product;
        $scope.filter.periodId = 0;
        $scope.filter.programId = $scope.filter.program;
        $scope.reportType=false;

        var param=   $scope.filter;

        $scope.error_message='';
        TrendOfMinMasColdRange.get(param, function (data) {

            if (data !== undefined) {

                $scope.data = data.trendMinMaxColdRangeReport.chainTempratureDetailReportTree;

                $scope.datarows = $scope.data;
                $scope.regionrows = data.trendMinMaxColdRangeReport.chainTempratureDetailRegionReportTree;
                $scope.reportType = data.trendMinMaxColdRangeReport.facilityReport;
                $scope.columnVals = data.trendMinMaxColdRangeReport.columnNames;
                $scope.regionColumnVals =  data.trendMinMaxColdRangeReport.regionSummaryColumnList;
                $scope.report = data.trendMinMaxColdRangeReport;
                $scope.colValueList = data.trendMinMaxColdRangeReport.districtFacilitySummaryColumnList;


            }
        });
    };
    $scope.getBackGroundColorForTd=function(value) {
        var bgColor='blue';
        if(value<2){
            bgColor='lightblue';
        }else if(value>8){
            bgColor='red';
        }else{
            bgColor='white';
        }
        return bgColor;
    };
    $scope.getColumnNameSummary=function(value) {

        var bgColor='';
        if(value=='6_alarm_episode_greater_min'){
            bgColor='Disitrict with alarm episode >8';
        }else if(value=='5_alarm_episode_less_min'){
            bgColor='Disitrict with alarm episode >2';
        }else if(value=='4_max_temp_recorded'){
        bgColor=' Max. Temp Recorded';
        }else if(value=='3_min_temp_recorded'){
            bgColor='Min. Temp Recorded';
        }else if(value=='2_temp_min_greater'){
            bgColor='Districts with t min > 8';
        }
        else{
            bgColor='Districts with t min < 2';
        }

        return bgColor;
    };
    $scope.getBackGroundColorSummary=function(value) {
        var bgColor='blue';
        if(value=='6_alarm_episode_greater_min'){
            bgColor='red';
        }else if(value=='5_alarm_episode_less_min'){
            bgColor='lightblue';
        }else if(value=='4_max_temp_recorded'){
            bgColor='white';
        }else if(value=='3_min_temp_recorded'){
            bgColor='white';
        }else if(value=='2_temp_min_greater'){
            bgColor='lightblue';
        }else{
            bgColor='red';
        }
        return bgColor;
    };
}
