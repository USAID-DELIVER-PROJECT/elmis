/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

angular.module('vaccine_on_time_in_full', ['openlmis','angularCombine', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion', 'ui.bootstrap.modal','ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/search', {
                controller:vaccineOnTimeInFullFunction,
                templateUrl:'partials/search.html',
                resolve:vaccineOnTimeInFullFunction.resolve,
                reloadOnSearch: false
            }).
            when('/received/:facilityId/:periodId/:id',{
                controller:OnTimeInFullListFunction,
                templateUrl: 'partials/reporting.html',
                resolve:OnTimeInFullListFunction.resolve,
                reloadOnSearch: false
            }).
            otherwise({redirectTo: '/search'});
    }]).config(function(angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).directive('select2Blur', function () {
        return function (scope, elm, attrs) {
            angular.element("body").on('mousedown', function (e) {
                $('.select2-dropdown-open').each(function () {
                    if (!$(this).hasClass('select2-container-active')) {
                        $(this).data("select2").blur();
                    }
                });
            });
        };
    }).filter('orderObjectBy', function(){
        return function(input, attribute) {
            if (!angular.isObject(input)) return input;

            var array = [];
            for(var objectKey in input) {
                array.push(input[objectKey]);
            }
            array.sort(function(a, b){
                a = a[attribute];
                b = b[attribute];
                return a - b;
            });
            return array;
        };
    });