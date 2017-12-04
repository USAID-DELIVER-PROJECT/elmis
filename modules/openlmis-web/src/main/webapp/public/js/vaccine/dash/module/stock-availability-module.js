/**
 * Created by hassan on 10/30/17.
 */

/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

var app  = angular.module('dash', ['openlmis','ui.router', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion',
    'ui.bootstrap.modal','ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle',
    'angularUtils.directives.uiBreadcrumbs','ng-breadcrumbs','ncy-angular-breadcrumb','angularCombine',
    'ngTable','ui.bootstrap.pagination', 'tree.dropdown'
]);
///Start

app.config(function($stateProvider, $urlRouterProvider, $breadcrumbProvider){

    var states = [
        {
            name: 'home',
            url: '/home',
            templateUrl: 'partials/list.html',
            controller:StockAvailabilityControllerFunc,
            resolve:StockAvailabilityControllerFunc.resolve,
            ncyBreadcrumb: {
                label: 'Stock Chart'
            }
        },

        {
            name: 'notification',
            url: '/notification',
            templateUrl: 'partials/notification-list.html',
            controller: 'NotificationControllerFunc',
            ncyBreadcrumb: {
                label: 'All Notifications'
            }
        }


        /*,
        {
            name: 'element',
            url: '/element:idElement/:facilityId',
            templateUrl: 'partials/stock-ledger.html',
            controller: 'MyFacilityStockLedgerFunction',
            ncyBreadcrumb: {
                label: 'Stock Ledger',
                parent: 'home'
            }
        },
        {
            name: 'detail',
            url: '/detail:referer',
            templateUrl: 'partials/detail.html',
            ncyBreadcrumb: {
                label: 'Details'
            }
        },
        {
            name:'toState',
            url: '/supervisedFacility:facilityId/:facilityName',
            templateUrl:'partials/stock-on-hand2.html',
            controller:'ManageStockOnHandControllerFunc',
            ncyBreadcrumb: {
                label: 'Stock on Hand for {{facilityName}}',
                parent: 'supervisedFacility'
            }
        },

        {
            name: 'ledger',
            url: '/ledger:productId/:facilityId/:facilityName/:product',
            templateUrl: 'partials/stock-ledger.html',
            controller: 'StockLedgerFunction2',
            ncyBreadcrumb: {
                label: 'Stock Ledger of {{productName}} in {{facilityName}} Store',
                parent: 'toState'
            }
        },
        {
            name:'supervisedFacility',
            url: '/supervisedFacility?:etc',
            templateUrl:'partials/stock-on-hand3.html',
            controller:'SupervisedFacilityControllerFunc',
            resolve:SupervisedFacilityControllerFunc.resolve,
            ncyBreadcrumb: {
                label: 'My Supervised Stores',
                parent: 'home'
            }
        }*/

    ];

    states.forEach($stateProvider.state);


    $urlRouterProvider.otherwise('/home');
}).config(function(angularCombineConfigProvider) {
    angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
})
    .filter('routeActive', function($state, $breadcrumb) {
        return function(route, steps) {
            for(var i = 0, j = steps.length; i < j; i++) {
                if(steps[i].name === route.name) {
                    return steps[i];
                }
            }

            return false;
        };
    }).filter('positive', function() {
        return function(input) {
            if (!input) {
                return 0;
            }

            return Math.abs(input);
        };
    })
    .controller('ElementCtrl', function($scope, $stateParams){
        $scope.idElement = $stateParams.idElement;
    });


