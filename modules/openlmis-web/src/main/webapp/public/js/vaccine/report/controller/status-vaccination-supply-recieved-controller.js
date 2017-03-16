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

function StatusVaccinationReceiceController($window,$scope, StatuVaccinationSupply, SettingsByKey){
    $scope.minTemp;
    $scope.maxTemp;
    $scope.minEpisode;
    $scope.maxEpisode;
    $scope.minColorCode;
    $scope.maxColorCode;
    SettingsByKey.get({key:'TREND_TEMP_MIN_VALUE'},function(data){
        $scope.minTemp=data.settings.value;
    });
    SettingsByKey.get({key:'TREND_TEMP_MAX_VALUE'},function(data){
        $scope.maxTemp=data.settings.value;
    });
    SettingsByKey.get({key:'TREND_MIN_EPISODE_VALUE'},function(data){
        $scope.minEpisode=data.settings.value;
    });
    SettingsByKey.get({key:'TREND_MAX_EPISODE_VALUE'},function(data){
        $scope.maxEpisode=data.settings.value;
    });
    SettingsByKey.get({key:'VCP_RED'},function(data){
        $scope.maxColorCode=data.settings.value;
    });
    SettingsByKey.get({key:'VCP_BLUE'},function(data){
        $scope.minColorCode=data.settings.value;
    });

    function getParam(){
        return {facilityId :'',
            geographicZoneId : $scope.filter.zone.id,
            zone : $scope.filter.zone.id,
            zoneId:$scope.filter.zone.id,
            productId : $scope.filter.product,
            periodId :0,
            programId : $scope.filter.program,
            reportType : false,
            periodStart:$scope.filter.periodStart,
            periodEnd:$scope.filter.periodEnd};

    }
    $scope.OnFilterChanged = function () {

        $scope.data = $scope.datarows = [];


        var param=  getParam();

        $scope.error_message='';
        if(!utils.isNullOrUndefined($scope.filter)&&!utils.isNullOrUndefined($scope.filter.periodStart)&&!utils.isNullOrUndefined($scope.filter.periodEnd)&&!utils.isNullOrUndefined($scope.filter.product)&& !utils.isNullOrUndefined($scope.filter.product)&&$scope.filter.product!==0) {
            StatuVaccinationSupply.get(param, function (data) {

                if (data !== undefined || data !== null) {

                    var columnKeysToBeAggregated = ["targetpopulation", "received", "onhand", "issued", "used", "wasted", "administered","vaccinated"];
                    var districtNameKey = "district_name";
                    var facilityNameKey = "facility_name";
                    var regionNameKey = "region_name";
                    var includeGrandTotal = false;

                    $scope.data = data.statusOfVaccinationSupplyReceiveReport.facilityDistrictVaccineStatusList;
                    $scope.reportType = data.statusOfVaccinationSupplyReceiveReport.facilityReport;

                    if( $scope.reportType===true){
                        $scope.datarows = utils.getReportDataWithSubAndGrandTotal($scope.data, facilityNameKey, columnKeysToBeAggregated, includeGrandTotal,1);
                    }else {
                        $scope.datarows = utils.getReportDataWithSubAndGrandTotal($scope.data, districtNameKey, columnKeysToBeAggregated, includeGrandTotal,2);
                    }
                    $scope.regionrows = data.statusOfVaccinationSupplyReceiveReport.regionVaccineStatusList;
                    $scope.regionrows =   utils.getReportDataWithSubAndGrandTotal( $scope.regionrows , regionNameKey, columnKeysToBeAggregated, includeGrandTotal,3);

                    $scope.report = data.statusOfVaccinationSupplyReceiveReport;
                }
            });
        }
    };


    $scope.getBackGroundColorForTd=function(value) {
        var bgColor='blue';
        if(value< $scope.minTemp){
            bgColor=$scope.minColorCode;
        }else if(value>$scope.maxTemp){
            bgColor= $scope.maxColorCode;
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
            bgColor=$scope.maxColorCode;
        }else if(value=='5_alarm_episode_less_min'){
            bgColor=$scope.minColorCode;
        }else if(value=='4_max_temp_recorded'){
            bgColor='white';
        }else if(value=='3_min_temp_recorded'){
            bgColor='white';
        }else if(value=='2_temp_min_greater'){
            bgColor=$scope.minColorCode;
        }else{
            bgColor=$scope.maxColorCode;
        }
        return bgColor;
    };
    $scope.calculateWastageRate=function(vaccinated,issued,wasted){
        var wastagePercentage;
        if(issued+wasted>0 && vaccinated>0){
            wastagePercentage= 100*(1-(vaccinated/(issued+wasted)));
            return wastagePercentage.toFixed(2);
        }
        return 0;

    };
    $scope.exportReport = function(type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);

        var url = '/reports/download/vaccine_supply_status/' + type + '?' + params;
        $window.open(url, '_blank');
    };
}
