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

angular.module('ivd-form-module', ['openlmis', 'ngGrid', 'ui.bootstrap.modal', 'ui.bootstrap.dropdownToggle', 'ui.bootstrap.dialog']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/create/:id', {
                controller: CreateIvdFormController,
                templateUrl: 'partials/create/index.html',
                resolve: CreateIvdFormController.resolve
            }).
            when('/list', {
                controller: InitIvdFormController,
                templateUrl: 'partials/create/list.html',
                resolve: InitIvdFormController.resolve
            }).
            when('/view/:id', {
                controller: ViewIvdFormDetailController,
                templateUrl: 'partials/view/index.html',
                resolve: ViewIvdFormDetailController.resolve
            }).
            when('/view', {
                controller: ViewIvdFormController,
                templateUrl: 'partials/view/list.html',
                resolve: ViewIvdFormController.resolve
            }).
            when('/approve', {
                controller : ApproveIvdFormController,
                templateUrl: 'partials/approve/index.html',
                resolve: ApproveIvdFormController.resolve
            }).
            when('/approve/:id', {
                controller : ApproveIvdFormDetailController,
                templateUrl: 'partials/approve/approve.html',
                resolve: ApproveIvdFormDetailController.resolve
            }).
            when('/preview/:id', {
                controller : ViewIvdFormDetailController,
                templateUrl: 'partials/print/view.html',
                resolve: ViewIvdFormDetailController.resolve
            }).
            otherwise({redirectTo: '/list'});
    }]);

