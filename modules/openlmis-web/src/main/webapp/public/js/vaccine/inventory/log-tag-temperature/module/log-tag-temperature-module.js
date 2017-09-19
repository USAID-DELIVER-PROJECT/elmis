angular.module('log_tag_temperature', ['openlmis','ngGrid', 'ngTable','ui.bootstrap','angularCombine','ui.bootstrap.modal','ui.bootstrap.pagination','tree.dropdown'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/create', {controller:LogTagTemperatureController, templateUrl:'partials/create.html',resolve:LogTagTemperatureController.resolve}).
            when('/edit/:id', {controller:LogTagTemperatureController, templateUrl:'partials/create.html',resolve:LogTagTemperatureController.resolve}).
            when('/list', {controller:LogTagTemperatureListController, templateUrl:'partials/list.html',resolve:LogTagTemperatureListController.resolve}).
            otherwise({redirectTo:'/list'});
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
