

angular.module('EquipmentCategory', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/list', {controller: EquipmentCategoryController, templateUrl: 'partials/list.html'}).
            when('/new', {controller: EquipmentCategoryController, templateUrl: 'partials/create.html',resolve:false}).
            when('/edit/:id', {controller: EquipmentCategoryController, templateUrl: 'partials/create.html',resolve:false}).
            otherwise({redirectTo: '/list'});
    }]).directive('onKeyup', function () {
        return function (scope, elm, attrs) {
            elm.bind("keyup", function () {
                scope.$apply(attrs.onKeyup);
            });
        };
    })
    .directive('select2Blur', function () {
        return function (scope, elm, attrs) {
            angular.element("body").on('mousedown', function (e) {
                $('.select2-dropdown-open').each(function () {
                    if (!$(this).hasClass('select2-container-active')) {
                        $(this).data("select2").blur();
                    }
                });
            });
        };
    })
    .run(function ($rootScope, AuthorizationService) {
        $rootScope.equipmentCategorySelected = "selected";
        AuthorizationService.preAuthorize('MANAGE_EQUIPMENT_SETTINGS');
    });


