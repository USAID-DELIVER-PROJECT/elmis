/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

angular.module('vaccine-inventory', ['openlmis','ngGrid', 'ngTable','ui.bootstrap','angularCombine','ui.bootstrap.modal','ui.bootstrap.pagination','barcodeGenerator'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/stock-adjustment', {controller:StockAdjustmentController, templateUrl:'partials/stock-adjustment.html',resolve:StockAdjustmentController.resolve}).
            when('/stock-adjustment-barcode', {controller:BarcodeStockAdjustmentController, templateUrl:'partials/stock-adjustment-barcode.html',resolve:BarcodeStockAdjustmentController.resolve}).
            when('/receive', {controller:ReceiveStockController, templateUrl:'partials/receive-stock.html',reloadOnSearch:false,resolve:ReceiveStockController.resolve}).
            when('/receive_barcode', {controller:BarcodeReceiveStockController, templateUrl:'partials/receive-stock-barcode.html',reloadOnSearch:false,resolve:BarcodeReceiveStockController.resolve}).
            when('/transfer-in', {controller:ReceiveStockController, templateUrl:'partials/receive-stock.html',reloadOnSearch:false,resolve:ReceiveStockController.resolve}).
            when('/mass-distribution', {controller:MassDistributionController, templateUrl:'partials/mass-distribution.html',reloadOnSearch:false,resolve:MassDistributionController.resolve}).
            when('/distribute_barcode', {controller:BarcodeMassDistributionController, templateUrl:'partials/mass-distribution-barcode.html',reloadOnSearch:false,resolve:BarcodeMassDistributionController.resolve}).
            when('/facility-distribution', {controller:FacilityDistributionController, templateUrl:'partials/facility-distribution.html',reloadOnSearch:false,resolve:FacilityDistributionController.resolve}).
            when('/configuration', {controller:VaccineInventoryConfigurationController, templateUrl:'partials/configuration.html',reloadOnSearch:false,resolve:VaccineInventoryConfigurationController.resolve}).
            when('/vaccine-forecasting', {controller:VaccineForecastingController, templateUrl:'partials/vaccine-forecast.html',reloadOnSearch:false,resolve:VaccineForecastingController.resolve}).
            when('/transfer-out', {controller:TransferOutController, templateUrl:'partials/transfer-out-partial.html',reloadOnSearch:false,resolve:TransferOutController.resolve}).
            when('/view-issue-voucher', {controller:ViewIssueVoucherController, templateUrl:'partials/view-issue-voucher.html',reloadOnSearch:false,resolve:ViewIssueVoucherController.resolve}).
            otherwise({redirectTo:'/public/pages/vaccine/dashboard/index.html#/dashboard'});
    }]).run(function ($rootScope, AuthorizationService) {

     }).config(function(angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    }).filter('positive', function() {
               return function(input) {
                   if (!input) {
                       return 0;
                   }

                   return Math.abs(input);
               };
    });
