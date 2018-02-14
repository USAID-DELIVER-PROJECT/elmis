angular.module('hfr_mapping',  ['openlmis','ngGrid','ui.bootstrap', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.pagination','tree.dropdown']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
    when('/create', {controller: HfrMappingController, templateUrl: 'partials/create.html', resolve: HfrMappingController.resolve}).
    when('/list', {controller: HfrMappingController, templateUrl: 'partials/list.html', resolve: HfrMappingController.resolve}).
    when('/edit/:id', {controller: HfrMappingController, templateUrl: 'partials/create.html', resolve: HfrMappingController.resolve}).
    otherwise({redirectTo: '/list'});
}]);