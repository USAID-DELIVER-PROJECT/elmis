'use strict';
/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

angular.module('stock_on_hand', ['openlmis','ui.router', 'ngGrid', 'ui.bootstrap.dialog', 'ui.bootstrap.accordion',
    'ui.bootstrap.modal','ui.bootstrap.pagination', 'ui.bootstrap.dropdownToggle','kendo.directives',
    'angularUtils.directives.uiBreadcrumbs','ng-breadcrumbs','ncy-angular-breadcrumb','angularCombine'
    , 'ngTable','ui.bootstrap.pagination', 'tree.dropdown'
    ]).
///Start

    config(function($stateProvider, $urlRouterProvider, $breadcrumbProvider){

        var states = [
            {
                name: 'home',
                url: '/home',
                templateUrl: 'partials/list.html',
                controller:StockOnHandControllerFunc,
                resolve:StockOnHandControllerFunc.resolve,
                ncyBreadcrumb: {
                    label: 'Home'
                }
            },
            {
                name: 'element',
                url: '/element:idElement?:facilityId',
                templateUrl: 'partials/stock-ledger.html',
                controller: 'StockLedgerFunction',
                ncyBreadcrumb: {
                    label: 'Stock Ledger for {{idElement}}',
                    parent: 'home'
                }
            },
            {
                name: 'element.detail',
                url: '/detail',
                templateUrl: 'partials/detail.html',
                ncyBreadcrumb: {
                    label: 'Details'
                }
            }
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



//End
    /*config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/initiate', {
                controller:StockOnHandControllerFunc,
                templateUrl:'partials/list.html',label: 'Home',
                resolve:StockOnHandControllerFunc.resolve,
                reloadOnSearch: false
            }).when('/stock', { controller: 'StockLedgerFunction', templateUrl: 'partials/home.html' })

            .when('/create', {
                controller:StockOnHandControllerFunc,
                templateUrl:'partials/view/view.html',
                resolve:StockOnHandControllerFunc.resolve,
                reloadOnSearch: false
            }).
            otherwise({redirectTo: '/initiate'});
    }]);
*/
 /*   config(['$stateProvider','$routeProvider','$urlRouterProvider', function($stateProvider,$routeProvider, $urlRouterProvider){
        $stateProvider
            .state('/initiate', {
                url: '/initiate',
                templateUrl: 'partials/list.html',
                controller: 'StockOnHandControllerFunc',
                abstract: false,
                resolve:StockOnHandControllerFunc.resolve,
                data: {
                    displayName: 'Home'
                }
            })
            .state('/initiate.stocks', {
                url: '/stocks',
                templateUrl:'partials/home.html',
                controller: 'StockLedgerFunction',
                data: {
                    displayName: 'stocks'

                }});

            state('/initiate.stock', {
                url: '/stocks',
                templateUrl: 'partials/home.html',
                controller: 'StockLedgerFunction',
                data: {
                    displayName: 'stocks'
                }
            });


    }]);*//*.directive('breadcrumbs', ['$log', '$parse', '$interpolate', function ($log, $parse) {
        return {
            restrict: 'EA',
            replace: false,
            scope: {
                itemDisplayNameResolver: '&'
            },
            templateUrl: 'directives/breadcrumbsDirective.html',
            controller: ['$scope', '$state', '$stateParams', function ($scope, $state, $stateParams) {

                var defaultResolver = function (state) {

                    var displayName = state.data.settings.displayName || state.name;

                    return displayName;
                };

                var isCurrent = function(state){
                    return $state.$current.name === state.name;
                };

                var setNavigationState = function () {
                    $scope.$navigationState = {
                        currentState: $state.$current,
                        params: $stateParams,
                        getDisplayName: function (state) {

                            if ($scope.hasCustomResolver) {
                                return $scope.itemDisplayNameResolver({
                                    defaultResolver: defaultResolver,
                                    state: state,
                                    isCurrent: isCurrent(state)
                                });
                            }
                            else {
                                return defaultResolver(state);
                            }
                        },
                        isCurrent: function (state) {

                            return isCurrent(state);
                        }
                    }
                };

                $scope.$on('$stateChangeSuccess', function () {
                    setNavigationState();
                });

                setNavigationState();
            }],
            link: function (scope, element, attrs, controller) {
                scope.hasCustomResolver = angular.isDefined(attrs['itemDisplayNameResolver']);
            }
        };
    }]);
*/