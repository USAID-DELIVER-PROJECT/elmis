/*

* Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
*
* Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
*/


function ColdChainEquipmentReportController($scope, $log,$window, ColdChainEquipmentService, ngTableParams) {

    $scope.log = $log;

    //Add this temporarily. It should really be set by filters on the page
    $scope.filter = {};
    $scope.filter.max = 10000;


    $scope.exportReport = function (type)
    {
        $scope.filter.pdformat = 1;
        var CCparam = {
           zone: utils.isEmpty($scope.getSanitizedParameter().zone) ? 0 : $scope.getSanitizedParameter().zone.id,
           program: $scope.getSanitizedParameter().program,
           facilityLevel: $scope.getSanitizedParameter().facilityLevel,
           max: $scope.getSanitizedParameter().max
         };

        var params = jQuery.param(CCparam);
        var sortOrderParams = jQuery.param($scope.tableParams.sorting);
        sortOrderParams = sortOrderParams.split('=');
        sortOrderParams = { sortBy:sortOrderParams[0], order:sortOrderParams[1] };
        sortOrderParams = jQuery.param(sortOrderParams);

        var url = '/reports/download/cold_chain_equipment/' + type + '?' + sortOrderParams +'&'+ params;
        $window.open(url);
    };


    $scope.OnFilterChanged  = function () {

        $scope.filter.max = 10000;
        //alert(JSON.stringify( $scope.filter, null, 2)); // spacing level = 2
        var CCparam = {
                        zone: utils.isEmpty($scope.getSanitizedParameter().zone) ? 0 : $scope.getSanitizedParameter().zone.id,
                        program: $scope.getSanitizedParameter().program,
                        facilityLevel: $scope.getSanitizedParameter().facilityLevel,
                        max: $scope.getSanitizedParameter().max
                      };

        ColdChainEquipmentService.get
        (
            CCparam,
            function (data)
            {   $scope.data = $scope.datarows = data.pages.rows;
                $scope.pages = data.pages;
                $scope.tableParams.total = $scope.pages.total;
            }
        );

    };

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10           // count per page
    });

    $scope.getRowFacilityAddress = function(row)
    {
        var ret = '';
        if (row.facilityAddress1)
            ret = row.facilityAddress1;
        if (row.facilityAddress2)
            ret += ' ' +  row.facilityAddress2;
        return ret;
    };

    $scope.getLargestRecordShown = function()
    {
        var max = $scope.tableParams.page * $scope.tableParams.count;
        if($scope.pages)
            return ($scope.pages.total > max) ? max : $scope.pages.total;
        else
            return max;
    };

     $scope.replaceAll = function(string, omit, place, prevstring) {
     if (prevstring && string === prevstring)
        return string;
        prevstring = string.replace(omit, place);
        return $scope.replaceAll(prevstring, omit, place, string);
    };

}
