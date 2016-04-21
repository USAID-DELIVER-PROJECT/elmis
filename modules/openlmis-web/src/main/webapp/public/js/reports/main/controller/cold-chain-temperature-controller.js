/*
* Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
*
* Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
*/

function ColdChainTemperatureController($scope, $log, ColdChainTemperaturesReport, ngTableParams){

    $scope.tableParams = new ngTableParams({
        page: 1,
        count: 10
    });



    $scope.OnFilterChanged = function(){
        $scope.data = $scope.datarows = [];
        $scope.filter.page = 1;
        $scope.filter.max = 10000;

        ColdChainTemperaturesReport.get(
            $scope.getSanitizedParameter(),
            function (data)
            {
                $scope.coldchainreportDataRows = $scope.coldchainReportDatarows = data.coldchainreportdata.rows;
                $scope.coldchainsubreportDataRows = data.coldchainSubreportdata.rows;
                $scope.ccrminmaxaggregateDataRows = data.coldchainreportminmaxaggregate.rows[0];
                $scope.ccrminmaxtemprecordeddataRows = data.coldchainreportminmaxtemprecorded.rows[0];
                $scope.ccrtotaltemprecorded = data.coldchainreporttotaltemprecorded.rows[0];

                setTimeout(function(){
                    $("#fixTable").tableHeadFixer({"foot":true, "head":true, left:4});
                }, 0);
            }
        );



    };




}