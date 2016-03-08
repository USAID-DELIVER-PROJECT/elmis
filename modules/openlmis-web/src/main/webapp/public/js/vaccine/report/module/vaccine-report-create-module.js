/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('vaccine-report-create', ['openlmis', 'ngGrid', 'angularCombine', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/bundled-distribution-vaccination-supplies', {
                controller: ViewbundledDistributionVacinationSuppliesController,
                templateUrl: 'partials/view/bundled-distribution-vacination-supplies.html'
            }).
            when('/performance-by-dropout-rate-by-district', {
                controller: ViewPerformanceByDropoutRateByDistrictController,
                templateUrl: 'partials/view/performance-by-dropout-rate-by-district.html',reloadOnSearch:false
            }).
            when('/trend-min-max-cold-range', {
                controller: TrendMinMaxColdRangeController,
                templateUrl: 'partials/view/trend-of-min-max-cold-rane.html',reloadOnSearch:false
            }).
            when('/performance-coverage', {
            controller: PerformanceCoverageReportController,
            templateUrl:'partials/view/performance-coverage.html'
            }).
            when('/completeness-and-timeliness', {
                controller: CompletenesssAndTimelinessReportController,
                templateUrl:'partials/view/completeness-and-timeliness.html'
            }).
            when('/status-vaccination-supply-receive', {
                controller: StatusVaccinationReceiceController,
                templateUrl:'partials/view/status-vaccination-received-per-month.html'
            }).
            when('/adequacy-level-of-supply', {
                 controller: AdequacyLevelOfSupplyController,
                 templateUrl:'partials/view/adequacy-level-of-supply.html'
             }).
            when('/classification-vaccine-utilization-performance', {
                controller: ClassificationVaccineUtilizationPerformanceController,
                templateUrl:'partials/view/classification-vaccine-utilization-performance.html'
            }).
            when('/categorization-vaccine-utilization-performance', {
                controller: CategorizationVaccineUtilizationPerformanceController,
                templateUrl:'partials/view/categorization-vaccine-utilization-performance.html'
            }).
            otherwise({redirectTo: '/list'});
    }]).config(function (angularCombineConfigProvider) {
        angularCombineConfigProvider.addConf(/filter-/, '/public/pages/reports/shared/filters.html');
    });

