/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */
angular.module('vaccine_stock_movement', ['openlmis', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion', 'ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle','angularCombine'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/stock-movement-view/:programId/:periodId/:facilityId/:facilityName/:id', {controller:StockMovementViewController, templateUrl:'partials/distribute.html',resolve:StockMovementViewController.resolve,  reloadOnSearch: false}).
            when('/view-pending', {controller:StockMovementViewController, templateUrl:'partials/view.html',resolve:StockMovementViewController.resolve,  reloadOnSearch: false})
            .otherwise({redirectTo: '/partials/view.html'});
    }]).directive('select2Blur', function () {
        return function (scope, elm, attrs) {
            angular.element("body").on('mousedown', function (e) {
                $('.select2-dropdown-open').each(function () {
                    if (!$(this).hasClass('select2-container-active')) {
                        $(this).data("select2").blur();
                    }
                });
            });
        };
    }).run(function ($rootScope, AuthorizationService) {

    }).config(function(angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter('positive', function() {
        return function(input) {
            if (!input) {
                return 0;
            }

            return Math.abs(input);
        };
    }).filter('expirationDate', [
        '$filter', function($filter) {
            return function(input, format) {
                return $filter('date')(new Date(input), format);
            };
        }
    ]);