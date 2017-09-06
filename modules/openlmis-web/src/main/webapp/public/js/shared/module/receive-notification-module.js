angular.module('openlmis', ['angular-notification-icons']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/receive', {controller: ReceiveNotificationController, resolve: ReceiveNotificationController.resolve}).
            otherwise({redirectTo: '/receive'});
    }]);
