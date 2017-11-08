(function() {
  'use strict';
  angular.module('breadcrumbs', []);

  angular.module('breadcrumbs').directive('breadcrumbs', function($rootScope, $location) {
    return {
      replace: true,
      transclude: true,
      restrict: 'E',
      scope: {},
      template: '<div>\
                  <ul class="breadcrumb">\
                    <li>\
                      <ng-switch on="breadcrumbs.length === 0">\
                        <span ng-switch-when="true">Home</span>\
                        <span ng-switch-default><a href="/">Home</a></span>\
                      </ng-switch>\
                    </li>\
                    <li ng-repeat="breadcrumb in breadcrumbs">\
                      <span class="divider">/</span>\
                      <ng-switch on="$last">\
                        <span ng-switch-when="true">{{breadcrumb.path}}</span>\
                        <span ng-switch-default><a href="{{breadcrumb.url}}">{{breadcrumb.path}}</a></span>\
                      </ng-switch>\
                    </li>\
                  </ul>\
                  <span ng-transclude class="breadcrumb"></span>\
                </div>',
      link: function(scope, element, attrs) {
        element.css({
          position: 'relative'
        });
        element.find('span.breadcrumb').css({
          position: 'absolute',
          right: 0,
          top: 0
        });
        return $rootScope.$on('$routeChangeSuccess', function() {
          var paths;
          if ($location.path() === '/') {
            return scope.breadcrumbs = [];
          } else {
            paths = $location.path().split('/').slice(1);
            return scope.breadcrumbs = paths.map(function(path) {
              var index;
              index = paths.indexOf(path);
              return {
                path: path,
                url: "/" + (paths.slice(0, +index + 1 || 9e9).join('/'))
              };
            });
          }
        });
      }
    };
  });

}).call(this);
