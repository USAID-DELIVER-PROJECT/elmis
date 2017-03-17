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
app.directive('filterContainer', ['$routeParams', '$location', '$timeout', 'messageService', function ($routeParams, $location, $timeout, messageService) {
    return {
        restrict: 'EA',
        scope: true,
        controller: function ($scope, $routeParams, $location) {
            $scope.filter = angular.copy($routeParams);
            $scope.requiredFilters = [];

            $scope.notifyFilterChanged = function (event) {
                $scope.$broadcast(event);
                $scope.$broadcast('filter-changed');
            };

            $scope.subscribeOnChanged = function (subscriber, propertyToSubscribe, func, initialize) {
                $scope.$on(propertyToSubscribe + '-changed', func);
                if (initialize && $routeParams[propertyToSubscribe]) {
                    func();
                }
            };

            $scope.registerRequired = function (filter, attr) {
                if (attr.required) {
                    $scope.requiredFilters[filter] = filter;
                }
            };

            $scope.$parent.getSanitizedParameter = function () {
                var params = angular.copy($scope.filter);

                //properly serialize the multi product filter
                if (params.products && params.products.length > 0) {
                    var numericArray = [];
                    for (var i = 0; i < params.products.length; i++) {
                        numericArray.push(parseInt(params.products[i], 10));
                    }
                    params.products = JSON.stringify(numericArray).replace('[', '').replace(']', '');
                } else if (params.products && params.products.length === 0) {
                    params.products = 0;
                }
                return params;
                //end of multi product stuff
            };

            $scope.$parent.getUrlParams = function () {
                var params = $scope.$parent.getSanitizedParameter();
                return jQuery.param(params, true);
            };

            $scope.$parent.applyUrl = function () {
                // update the url so users could take it, book mark it etc...
                var params = $scope.$parent.getUrlParams();
                if (params !== $scope.currentUrlParams) {
                    var url = $location.url();
                    url = url.substring(0, url.indexOf('?'));
                    url = url + '?' + params;
                    $scope.currentUrlParams = params;
                    $location.url(url);
                }
            };

            function isValid() {
                if($scope.noRequiredParameter){
                    return true;
                }
                var all_required_fields_set = false;
                // check if all of the required parameters have been specified
                if (!angular.isUndefined($scope.requiredFilters)) {
                    all_required_fields_set = true;
                    var requiredFilters = _.values($scope.requiredFilters);
                    if(requiredFilters.length === 0){
                      all_required_fields_set = false;
                    }
                    for (var i = 0; i < requiredFilters.length; i++) {
                        var field = requiredFilters[i];
                        if (isUndefined($scope.filter[field]) || $scope.filter[field] === '' || $scope.filter[field] === 0 || $scope.filter[field] === -1) {
                            all_required_fields_set = false;
                            break;
                        }
                    }
                }
                return all_required_fields_set;
            }

            $scope.filterChanged = function () {
                $scope.$parent.applyUrl();
                if (!isValid()) {
                    return;
                }
                $scope.$parent.filter = $scope.filter;
                // call on Filter Changed
                $scope.$parent.OnFilterChanged();
            };


            $scope.$on('filter-changed', $scope.filterChanged);
            $timeout($scope.filterChanged, 100);
        },
        link: function (scope, elm, attr) {
            scope.noRequiredParameter = ( "true" === (attr.noRequiredParam) );
            angular.extend(scope, {
                showMoreFilters: false,

                unshift: function (array, displayKey) {
                    if (angular.isArray(array) && array.length > 0) {
                        array.unshift({
                            name: messageService.get(displayKey)
                        });
                    } else if (angular.isArray(array) && array.length === 0) {
                        array.push({name: messageService.get(displayKey)});
                    }
                    return array;
                },
                toggleMoreFilters: function () {
                    scope.showMoreFilters = !scope.showMoreFilters;
                }
            });
        }
    };
}]);

app.directive('programFilter', ['ReportUserPrograms', 'ReportProgramsWithBudgeting', 'ReportRegimenPrograms', '$routeParams',
    function (ReportUserPrograms, ReportProgramsWithBudgeting, ReportRegimenPrograms, $routeParams) {
        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('program', attr);

                function bindPrograms(list) {

                    if (!attr.required && !$routeParams.program) {
                        scope.programs = scope.unshift(list, 'report.filter.all.programs');
                    } else {
                        scope.programs = scope.unshift(list, 'report.filter.select.program');
                    }

                    makeVaccineProgramDefault(list);
                }

                function makeVaccineProgramDefault(list){
                    if(utils.isEmpty(scope.filter.program)){
                        vaccineProgram = _.find(list, function(program){ return program.code == 'Vaccine'; });
                        if(!utils.isNullOrUndefined(vaccineProgram))
                            scope.filter.program = vaccineProgram.id;
                    }
                }

                var Service = (attr.regimen) ? ReportRegimenPrograms : (attr.budget) ? ReportProgramsWithBudgeting : ReportUserPrograms;
                Service.get(function (data) {
                    bindPrograms(data.programs);
                });
            },
            templateUrl: 'filter-program-template'
        };
    }
]);

app.directive('yearFilter', ['OperationYears',
    function (OperationYears) {
        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('year', attr);

                OperationYears.get(function (data) {
                    scope.years = data.years;
                    if (scope.filter.year === undefined) {
                        scope.filter.year = data.years[data.years.length - 1];
                    }
                });
            },
            templateUrl: 'filter-year-template'
        };
    }
]);

app.directive('quarterFilter', [function () {
    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {
            scope.registerRequired('quarter', attr);
            scope.quarters = [1, 2, 3, 4];
            scope.filter.quarter = 1;
        },
        templateUrl: 'filter-quarter-template'
    };
}]);

app.directive('facilityTypeFilter', ['ReportFacilityTypes', 'ReportFacilityTypesByProgram',
    function (ReportFacilityTypes, ReportFacilityTypesByProgram) {

        var onCascadedPVarsChanged = function ($scope, attr) {
            if ($scope.filter && $scope.filter.program !== undefined) {
                ReportFacilityTypesByProgram.get({
                    program: $scope.filter.program
                }, function (data) {
                    $scope.facilityTypes = (attr.required) ? $scope.unshift(data.facilityTypes, 'report.filter.select.facility.types') : $scope.unshift(data.facilityTypes, 'report.filter.all.facility.types');
                });
            } else {
                ReportFacilityTypes.get(function (data) {
                    $scope.facilityTypes = (attr.required) ? $scope.unshift(data.facilityTypes, 'report.filter.select.facility.types') : $scope.unshift(data.facilityTypes, 'report.filter.all.facility.types');
                });
            }
        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('facilityType', attr);

                var onParentChanged = function () {
                    onCascadedPVarsChanged(scope, attr);
                };
                scope.subscribeOnChanged('facilityType', 'program', onParentChanged, true);
            },
            templateUrl: 'filter-facility-type-template'
        };
    }
]);

app.directive('facilityLevelFilter', ['ReportFacilityLevels',
    function (ReportFacilityLevels) {

        var onCascadedPVarsChanged = function ($scope) {
            if ($scope.filter.program !== undefined || $scope.filter.program !== '') {
                ReportFacilityLevels.get({
                    program: $scope.filter.program
                }, function (data) {
                    $scope.facilityLevels = [];
                    if (data.facilityLevels.length > 0) {
                        $scope.facilityLevels.unshift({
                            'id': 'hf',
                            'name': 'Health Facilities (HF)'
                        });
                        _.each(data.facilityLevels, function (item) {
                            if (item.code === 'cvs' ||
                                item.code === 'rvs' ||
                                item.code === 'dvs') {
                                $scope.facilityLevels.unshift({
                                    'id': item.code,
                                    'name': item.name + ' (' + item.code.toUpperCase() + ')',
                                    'display_order': item.displayOrder
                                });
                            }
                        });
                        $scope.facilityLevels.unshift({
                            'id': '',
                            'name': '-- Select Facility Level --',
                            'display_order': 0
                        });
                    }
                });
            }
        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('facilityLevel', attr);
                scope.$on('program-changed', function () {
                    onCascadedPVarsChanged(scope);
                });
            },
            templateUrl: 'filter-facility-level-template'
        };
    }
]);

app.directive('scheduleFilter', ['ReportSchedules', 'ReportProgramSchedules', '$routeParams',
    function (ReportSchedules, ReportProgramSchedules, $routeParams) {

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('schedule', attr);

                var loadSchedules = function () {
                    ReportProgramSchedules.get({
                        program: scope.filter.program
                    }, function (data) {
                      if(data.schedules.length === 1){
                        scope.filter.schedule = data.schedules[0].id;
                        scope.notifyFilterChanged('schedule-changed');
                      }
                      else{
                          makeMonthlyScheduleDefault(data.schedules);
                      }
                      scope.schedules = scope.unshift(data.schedules, 'report.filter.select.group');
                    });
                };

                function makeMonthlyScheduleDefault(list){
                    if(utils.isEmpty(scope.filter.schedule)){
                        monthlySchedule = _.find(list, function(program){ return program.code == 'Monthly'; });
                        if(!utils.isNullOrUndefined(monthlySchedule))
                            scope.filter.schedule = monthlySchedule.id;
                    }
                }

                if (!$routeParams.schedule) {
                    scope.schedules = scope.unshift([], 'report.filter.select.schedule');
                }
                scope.subscribeOnChanged('schedule', 'program', loadSchedules, true);

            },
            templateUrl: 'filter-schedule-template'
        };
    }
]);

app.directive('zoneFilter', ['TreeGeographicZoneList', 'TreeGeographicZoneListByProgram', 'GetUserUnassignedSupervisoryNode', 'messageService',
    function (TreeGeographicZoneList, TreeGeographicZoneListByProgram, GetUserUnassignedSupervisoryNode, messageService) {

        var onCascadedVarsChanged = function ($scope, attr) {
            if (!angular.isUndefined($scope.filter) && !angular.isUndefined($scope.filter.program)) {
                TreeGeographicZoneListByProgram.get({
                    program: $scope.filter.program
                }, function (data) {
                    $scope.zones = [data.zone];
                    if($scope.selectedZone === undefined || $scope.selectedZone === null){
                      $scope.selectedZone = data.zone;
                    }
                });
            } else {
                TreeGeographicZoneList.get(function (data) {
                    $scope.zones = [data.zone];
                    if($scope.selectedZone === undefined){
                        $scope.selectedZone = data.zone;
                    }
                });
            }
        };

        var categoriseZoneBySupervisoryNode = function ($scope) {
            GetUserUnassignedSupervisoryNode.get({
                program: $scope.filter.program
            }, function (data) {
                $scope.user_geo_level = messageService.get('report.filter.all.geographic.zones');
                if (!angular.isUndefined(data.supervisory_nodes)) {
                    if (data.supervisory_nodes === 0)
                        $scope.user_geo_level = messageService.get('report.filter.national');
                }
            });
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.$watch('selectedZone', function(){
                  if(scope.$parent.filter !== undefined && scope.selectedZone !== undefined && scope.selectedZone !== null){
                    scope.$parent.filter.zone = scope.selectedZone.id;
                    scope.$parent.filter.zoneName = scope.selectedZone.name.replace(/%20/g, '+');
                    scope.notifyFilterChanged('zone-filter-changed');
                  }
                });

                scope.registerRequired('zone', attr);
                if(scope.filter !== undefined && scope.filter !== null && scope.filter.zone !== undefined){
                  scope.selectedZone = {
                      id: scope.filter.zone,
                    name: scope.filter.zoneName.replace(/\+/g, ' ')
                  };
                }

                if (attr.districtOnly) {
                    scope.showDistrictOnly = true;
                }
                categoriseZoneBySupervisoryNode(scope);

                var onParamsChanged = function () {
                    if (!scope.showDistrictOnly) {
                        categoriseZoneBySupervisoryNode(scope);
                    }
                    onCascadedVarsChanged(scope, attr);
                };

                //check if the directive does not depend on any other property to load data
                if (attr.standAlone) {
                    onParamsChanged();
                } else {
                    scope.subscribeOnChanged('zone', 'program', onParamsChanged, true);
                }
            },
            templateUrl: 'filter-zone-template'
        };
    }
]);

app.directive('periodFilter', ['ReportPeriods', 'ReportPeriodsByScheduleAndYear', '$routeParams',
    function (ReportPeriods, ReportPeriodsByScheduleAndYear, $routeParams) {

        var onCascadedVarsChanged = function ($scope) {
            // don't call the server if you don't have all that it takes.
            if (isUndefined($scope.filter) || isUndefined($scope.filter.year) || isUndefined($scope.filter.schedule))
                return;

            if ($scope.filter.year !== undefined && $scope.filter.schedule !== undefined) {
                ReportPeriodsByScheduleAndYear.get({
                    scheduleId: $scope.filter.schedule,
                    year: $scope.filter.year
                }, function (data) {
                    $scope.periods = $scope.unshift(data.periods, 'report.filter.select.period');
                });
            }
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('period', attr);
                if (!$routeParams.schedule) {
                    scope.periods = scope.unshift([], 'report.filter.select.period');
                }

                function onParentChanged() {
                    onCascadedVarsChanged(scope);
                }

                scope.subscribeOnChanged('period', 'program', onParentChanged, false);
                scope.subscribeOnChanged('period', 'year', onParentChanged, false);
                scope.subscribeOnChanged('period', 'schedule', onParentChanged, true);
            },
            templateUrl: 'filter-period-template'
        };
    }
]);

app.directive('requisitionGroupFilter', ['RequisitionGroupsByProgram',
    function (RequisitionGroupsByProgram) {

        var onRgCascadedVarsChanged = function ($scope) {

            if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0)
                return;
            RequisitionGroupsByProgram.get({
                program: $scope.filter.program
            }, function (data) {
                $scope.requisitionGroups = $scope.unshift(data.requisitionGroupList, 'report.filter.all.requisition.groups');
            });
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('requisitionGroup', attr);
                var onParentChanged = function () {
                    onRgCascadedVarsChanged(scope);
                };
                scope.subscribeOnChanged('requisitionGroup', 'program', onParentChanged, true);
            },
            templateUrl: 'filter-requisition-group-template'
        };
    }
]);

app.directive('adjustmentTypeFilter', ['AdjustmentTypes', 'messageService', function (AdjustmentTypes, messageService) {
    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {
            scope.registerRequired('adjustmentType', attr);
            AdjustmentTypes.get(function (data) {
                scope.adjustmentTypes = data.adjustmentTypeList;
                scope.adjustmentTypes.unshift({
                    name: '',
                    description: messageService.get('report.filter.all.adjustment.types')
                });
            });
        },
        templateUrl: 'filter-adjustment-type-template'
    };
}]);

app.directive('productCategoryFilter', ['ProductCategoriesByProgram', '$routeParams',
    function (ProductCategoriesByProgram, $routeParams) {

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('productCategory', attr);
                if (!$routeParams.productCategory) {
                    scope.productCategories = scope.unshift([], 'report.filter.all.product.categories');
                }

                var onProgramChanged = function () {
                    ProductCategoriesByProgram.get({
                        programId: scope.filter.program
                    }, function (data) {
                        scope.productCategories = scope.unshift(data.productCategoryList, 'report.filter.all.product.categories');
                    });
                };
                scope.subscribeOnChanged('productCategory', 'program', onProgramChanged, true);
            },
            templateUrl: 'filter-product-category-template'
        };
    }
]);

app.directive('facilityFilter', ['FacilitiesByProgramParams', '$routeParams',
    function (FacilitiesByProgramParams, $routeParams) {

        var onPgCascadedVarsChanged = function ($scope, required) {

            var filter_caption = '';

            if(!isUndefined(required) && angular.equals(required, 'true') ){
                filter_caption =  'report.filter.select.facility';
            }
            else {
                filter_caption = 'report.filter.all.facilities';
            }

            if (!$routeParams.program) {
                if(isUndefined(required))
                    $scope.facilities = $scope.unshift([], filter_caption);
                else
                    $scope.facilities = $scope.unshift([], filter_caption);
            }

            if (isUndefined($scope.filter.program) || $scope.filter.program === 0) {
                return;
            }

            var program = (!utils.isEmpty($scope.filter.program)) ? $scope.filter.program : 0;
            var schedule = (!utils.isEmpty($scope.filter.schedule)) ? $scope.filter.schedule : 0;
            var facilityType = (!utils.isEmpty($scope.filter.facilityType)) ? $scope.filter.facilityType : 0;
            var requisitionGroup = (!utils.isEmpty($scope.filter.requisitionGroup)) ? $scope.filter.requisitionGroup : 0;
            var zone = (!utils.isEmpty($scope.filter.zone)) ? $scope.filter.zone : 0;
            var facilityOperator = (!utils.isEmpty($scope.filter.facilityOperator)) ? $scope.filter.facilityOperator : 0;

            // load facilities
            FacilitiesByProgramParams.get({
                program: program,
                schedule: schedule,
                type: facilityType,
                requisitionGroup: requisitionGroup,
                zone: zone,
                facilityOperator: facilityOperator
            }, function (data) {
                    $scope.facilities = $scope.unshift(data.facilities, filter_caption);
            });
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('facility', attr);

                var onChange = function () {
                    onPgCascadedVarsChanged(scope, attr.required);
                };

                scope.subscribeOnChanged('facility', 'requisition-group', onChange, false);
                scope.subscribeOnChanged('facility', 'zone', onChange, false);
                scope.subscribeOnChanged('facility', 'schedule', onChange, false);
                scope.subscribeOnChanged('facility', 'facility-type', onChange, false);
                scope.subscribeOnChanged('facility', 'program', onChange, true);
                scope.subscribeOnChanged('facility', 'facility-operator', onChange, true);
            },
            templateUrl: 'filter-facility-template'
        };
    }
]);

app.directive('geoFacilityFilter', ['FacilitiesByGeographicZone', '$routeParams', 'messageService',
    function (FacilitiesByGeographicZone, $routeParams, messageService) {

        var onPgCascadedVarsChanged = function ($scope) {

            if (!$routeParams.facility) {
                $scope.facilities = [{
                    name: messageService.get('report.filter.select.facility'), id: 0
                }];
            }

            var zone = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.zone)) ? $scope.filter.zone : 0;
            // load facilities
            FacilitiesByGeographicZone.get({
                geoId: zone
            }, function (data) {
                $scope.facilities = data.facilities;
                if (isUndefined($scope.facilities)) {
                    $scope.facilities = [];
                }
                $scope.facilities.unshift({
                    name: messageService.get('report.filter.all.facilities'), id: 0
                });
            });
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                scope.facilities = [];
                scope.facilities.push({
                    name: messageService.get('report.filter.all.facilities')
                });

                scope.filter.facility = (isUndefined($routeParams.facility) || $routeParams.facility === '') ? 0 : $routeParams.facility;

                if (attr.required) {
                    scope.requiredFilters.facility = 'facility';
                }

                scope.$on('zone-changed', function () {
                    onPgCascadedVarsChanged(scope);
                });
            },
            templateUrl: 'filter-facility-template'
        };
    }
]);

app.directive('productFilter', ['ReportProductsByProgram', 'messageService', '$routeParams',
    function (ReportProductsByProgram, messageService, $routeParams) {

        var onPgCascadedVarsChanged = function ($scope, attr) {
            if (isUndefined($scope.filter.program) || $scope.filter.program === 0)
                return;

            var program = (angular.isDefined($scope.filter.program)) ? $scope.filter.program : 0;

            ReportProductsByProgram.get({
                programId: program
            }, function (data) {
                $scope.products = data.productList;
                if (!attr.required) {
                    $scope.products.unshift({
                        'name': messageService.get('report.filter.select.indicator.product'),
                        id: -1
                    });
                    $scope.products.unshift({
                        'name': messageService.get('report.filter.all.products'),
                        id: 0
                    });
                }

            });

        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('product', attr);
                if (!$routeParams.product && !attr.required) {
                    scope.products = [{
                        'name': messageService.get('report.filter.all.products'),
                        id: 0
                    }];
                }

                // this is what filters products based on product categories selected.
                scope.productCFilter = function (option) {
                    var show = (
                        _.isEmpty(scope.filter.productCategory) ||
                        _.isUndefined(scope.filter.productCategory) ||
                        parseInt(scope.filter.productCategory, 10) === 0 ||
                        option.categoryId == scope.filter.productCategory ||
                        option.id === -1 ||
                        option.id === 0
                    );
                    return show;
                };

                var onFiltersChanged = function () {
                    onPgCascadedVarsChanged(scope, attr);
                };
                scope.subscribeOnChanged('product', 'product-category', onFiltersChanged, false);
                scope.subscribeOnChanged('product', 'program', onFiltersChanged, true);
            },
            templateUrl: 'filter-product-template'
        };

    }
]);

app.directive('rmnchProductPeriodFilter', ['RmnchProducts', 'GetYearSchedulePeriodTree', '$routeParams',
    function (RmnchProducts, GetYearSchedulePeriodTree, $routeParams) {
        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                if (attr.required) {
                    scope.requiredFilters.program = 'program';
                }

                scope.filter.product = (isUndefined($routeParams.product) || $routeParams.product === '') ? 0 : $routeParams.product;
                scope.filter.period = (isUndefined($routeParams.period) || $routeParams.period === '') ? 0 : $routeParams.period;

                scope.$evalAsync(function () {
                    RmnchProducts.get({}, function (data) {
                        scope.products = data.productList;
                    });

                    //Load period tree
                    GetYearSchedulePeriodTree.get({}, function (data) {
                        scope.periods = data.yearSchedulePeriod;
                    });
                });

            },
            templateUrl: 'filter-rmnch-product-period'
        };
    }
]);

app.directive('periodTreeFilter', ['GetYearSchedulePeriodTree', '$routeParams', 'messageService',
    function (GetYearSchedulePeriodTree, $routeParams, messageService) {
        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                if (attr.required) {
                    scope.requiredFilters.period = 'period';
                }

                scope.filter.period = (isUndefined($routeParams.period) || $routeParams.period === '') ? 0 : $routeParams.period;

                scope.$evalAsync(function () {
                    //Load period tree
                    GetYearSchedulePeriodTree.get({}, function (data) {
                        scope.periods = data.yearSchedulePeriod;
                    });

                    scope.period_placeholder = messageService.get('label.select.period');
                });

            },
            templateUrl: 'filter-period-tree-template'
        };
    }
]);


app.directive('vaccinePeriodTreeFilter', ['GetVaccineReportPeriodFlat', '$routeParams', 'messageService', 'SettingsByKey',
    function (GetVaccineReportPeriodFlat, $routeParams, messageService, SettingsByKey) {
        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                if (attr.required) {
                    scope.requiredFilters.period = 'period';
                }

                scope.filter.period = (isUndefined($routeParams.period) || $routeParams.period === '') ? 0 : $routeParams.period;
                SettingsByKey.get({key: 'VACCINE_LATE_REPORTING_DAYS'}, function (data, er) {
                    scope.cutoffdate = data.settings.value;
                });
                scope.$evalAsync(function () {
                    //Load period tree
                    GetVaccineReportPeriodFlat.get({}, function (data) {
                        scope.periods = data.vaccinePeriods.periods;

                        var defaultPeriodId = utils.getVaccineMonthlyDefaultPeriod(scope.periods, scope.cutoffdate);
                        scope.filter.defaultPeriodId = defaultPeriodId;
                        scope.period_placeholder = messageService.get('label.select.period');
                        if (!angular.isUndefined(scope.periods)) {
                            if (scope.periods.length === 0)
                                scope.period_placeholder = messageService.get('report.filter.period.no.vaccine.record');
                        }
                        scope.filter.period = defaultPeriodId;
                    });
                });
                scope.$watch('filter.period', function () {
                    scope.$parent.OnFilterChanged();
                });
            },
            templateUrl: 'filter-period-tree-template'
        };
    }
]);

app.directive('productMultiFilter', ['ReportProductsByProgram',
    function (ReportProductsByProgram) {

        var onPgCascadedVarsChanged = function ($scope) {

            if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0)
                return;

            var program = (angular.isDefined($scope.filter) && angular.isDefined($scope.filter.program)) ? $scope.filter.program : 0;
            ReportProductsByProgram.get({
                programId: program
            }, function (data) {
                $scope.products = data.productList;
                $scope.products.unshift({
                    'name': '-- Indicator Products --',
                    id: -1
                });
                $scope.products.unshift({
                    'name': '-- All Products --',
                    id: 0
                });
            });

        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('products', attr);

                // register the function that filters products by cascading product categories
                scope.productCFilter = function (option) {

                    return (
                        // show all products if the product category filter is not on screen at all
                        !angular.isDefined(scope.filter.productCategory) ||
                            // show all products if product category is on screen but no selection is made
                        scope.filter.productCategory === '' ||
                        parseInt(scope.filter.productCategory, 10) === 0 ||
                            // show products that are in product category selected
                        option.categoryId == scope.filter.productCategory ||
                            // always show "all products and indicator products filters"
                        (option.id === 0) ||
                        (option.id === -1)
                    );
                };

                var onFiltersChanged = function () {
                    onPgCascadedVarsChanged(scope);
                };

                scope.subscribeOnChanged('product', 'product-category', onFiltersChanged, false);
                scope.subscribeOnChanged('product', 'program', onFiltersChanged, true);


            },
            templateUrl: 'filter-product-multi-template'
        };
    }
]);


app.directive('regimenCategoryFilter', ['ReportRegimenCategories', function (ReportRegimenCategories) {

    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {
            scope.registerRequired('regimenCategory', attr);
            ReportRegimenCategories.get(function (data) {
                scope.regimenCategories = scope.unshift(data.regimenCategories, 'report.filter.all.regimen.category');
            });
        },
        templateUrl: 'filter-regimen-category-template'
    };
}]);

app.directive('regimenFilter', ['ReportRegimensByCategory',
    function (ReportRegimensByCategory) {

        var onPgCascadedVarsChanged = function ($scope) {

            if (isUndefined($scope.filter) || isUndefined($scope.filter.regimenCategory) || $scope.filter.regimenCategory === 0)
                return;

            ReportRegimensByCategory.get({
                regimenCategoryId: $scope.filter.regimenCategory
            }, function (data) {
                $scope.regimens = $scope.unshift(data.regimens, 'report.filter.all.regimens');
            });
        };
        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('regimen', attr);
                scope.subscribeOnChanged('regimen', 'regimen-category', function () {
                    onPgCascadedVarsChanged(scope);
                }, true);
            },
            templateUrl: 'filter-regimen-template'
        };

    }
]);

app.directive('topRightTableSummary', [function(){
    return {
      restrict: 'EA',
      templateUrl: '/public/pages/reports/shared/top-right-pagination-summary.html'
    };
}]);

app.directive('clientSideSortPagination', ['$filter', 'ngTableParams',
    function ($filter, ngTableParams) {

        return {
            restrict: 'A',
            link: function (scope) {

                // the grid options
                scope.tableParams = new ngTableParams({
                    page: 1,
                    total: 0,
                    count: 100
                });

                scope.paramsChanged = function (params) {

                    // slice array data on pages
                    if (scope.data === undefined) {
                        scope.datarows = [];
                        params.total = 0;
                        params.page = 1;
                    } else {
                        if(((params.page - 1) * params.count) > scope.data.length){
                            // reset the page number to 1.
                            params.page = 1;
                        }
                        var data = scope.data;
                        var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
                        orderedData = params.sorting ? $filter('orderBy')(orderedData, params.orderBy()) : data;

                        params.total = orderedData.length;
                        scope.datarows = orderedData.slice((params.page - 1) * params.count, params.page * params.count);
                        var i = 0;
                        var baseIndex = params.count * (params.page - 1) + 1;
                        while (i < scope.datarows.length) {
                            scope.datarows[i].no = baseIndex + i;
                            i++;
                        }
                    }
                };
                // watch for changes of parameters
                scope.$watch('tableParams', scope.paramsChanged, true);
            }
        };
    }
]);

app.directive('equipmentTypeFilter', ['ReportEquipmentTypes', function (ReportEquipmentTypes) {
    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {
            scope.registerRequired('equipmentType', attr);
            ReportEquipmentTypes.get(function (data) {
                scope.equipmentTypes = scope.unshift(data.equipmentTypes, 'report.filter.all.equipment.types');
            });
        },
        templateUrl: 'filter-equipment-type'
    };
}]);

app.directive('programProductPeriodFilter', ['ReportUserPrograms', 'GetProductCategoryProductByProgramTree', 'GetYearSchedulePeriodTree', 'SettingsByKey',
    function (ReportUserPrograms, GetProductCategoryProductByProgramTree, GetYearSchedulePeriodTree, SettingsByKey) {

        // When a program filter changes
        var onProgramChanged = function ($scope) {

            if (isUndefined($scope.filter) || isUndefined($scope.filter.program) || $scope.filter.program === 0) {
                $scope.products = {};
                return;
            }
            GetProductCategoryProductByProgramTree.get({
                programId: $scope.filter.program
            }, function (data) {
                $scope.products = data.productCategoryTree;
            });
        };

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                ReportUserPrograms.get(function (data) {
                    scope.programs = scope.unshift(data.programs, 'report.filter.select.program');
                });

                GetYearSchedulePeriodTree.get({}, function (data) {
                    scope.periods = data.yearSchedulePeriod;
                });

                var onParentChanged = function () {
                    onProgramChanged(scope);
                };
                scope.subscribeOnChanged('programProductPeriod', 'program', onParentChanged, true);
                scope.$watch('filter.program', function (value) {
                    onProgramChanged(scope);
                });
            },
            templateUrl: 'filter-program-product-period'
        };
    }
]);

app.directive('equipmentFilter', ['ReportEquipments', function (ReportEquipments) {
    var onEquipmentTypeChanged = function ($scope) {
        if (isUndefined($scope.filter) || isUndefined($scope.filter.equipmentType) || $scope.filter.equipmentType === 0) {
            $scope.equipments = [];
            return;
        }
        ReportEquipments.get({
            equipmentType: $scope.filter.equipmentType
        }, function (data) {
            $scope.equipments = scope.unshift(data.equipments, 'report.filter.all.equipments');
        });
    };

    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope) {
            var cascaseOnEquipmentTypeChanged = function () {
                onEquipmentTypeChanged(scope, value);
            };
            scope.subscribeOnChanged('equipment', 'equipmentType', cascaseOnEquipmentTypeChanged, true);
        },
        templateUrl: 'filter-equipment'
    };
}]);

app.directive('serviceContractFilter', [function () {

    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope, elm, attr) {
            scope.serviceContract = [{
                'key': 0,
                'value': '--All service status--'
            }, {
                'key': 1,
                'value': 'Yes'
            }, {
                'key': 2,
                'value': 'No'
            }];

        },
        templateUrl: 'filter-service-contract'
    };
}]);

app.directive('donorFilter', ['GetDonors', function (GetDonors) {

    return {
        restrict: 'E',
        require: '^filterContainer',
        link: function (scope) {
            GetDonors.get({}, function (data) {
                scope.donors = scope.unshift(data.donors, 'report.filter.all.donors');
            });
        },
        templateUrl: 'filter-donors'
    };
}]);

app.directive('vaccineFacilityLevelFilter', ['FacilitiesByLevel', 'VaccineInventoryPrograms', '$routeParams',
        function (FacilitiesByLevel, VaccineInventoryPrograms, $routeParams) {

            var getVaccineEquipmentProgram = function ($scope) {

                VaccineInventoryPrograms.get({}, function (data) {
                    if (data.programs.length > 0) {
                        $scope.filter.program = data.programs[0].id;
                    }

                });
            };


            var onCascadedVarsChanged = function ($scope) {

                if (!angular.isUndefined($scope.filter) && !angular.isUndefined($scope.filter.program)) {
                    $scope.$parent.facilityLevels = $scope.$parent.homeFacility = [];
                    FacilitiesByLevel.get({program: $scope.filter.program}, function (data) {
                        if (data.facilityLevels !== undefined && data.facilityLevels.length !== 0) {
                            $scope.$parent.homeFacility = _.pluck(data.facilityLevels, 'homeFacilityName');
                            $scope.homeFacilityId = _.pluck(data.facilityLevels, 'facilityId');

                            $scope.$parent.facilityLevels = _.uniq(data.facilityLevels, 'superVisedFacilityId');

                            $scope.greaterThan = function (prop, val) {
                                return function (item) {
                                    return item[prop] > val;
                                };
                            };

                        }
                    });
                    $scope.filter.facilityId = (isUndefined($routeParams.facilityId) || $routeParams.facilityId === '') ? 0 : $routeParams.facilityId;

                }
            };

            return {
                restrict: 'E',
                require: '^filterContainer',
                link: function (scope, elm, attr) {
                    scope.filter.facilityId = $routeParams.facilityId;

                    scope.filter.facilityId = (isUndefined($routeParams.facilityId) || $routeParams.facilityId === '') ? 0 : $routeParams.facilityId;

                    if (attr.required) {
                        scope.requiredFilters.facilityId = 'facilityId';
                    }

                    scope.$watch('filter.program', function (value) {
                        getVaccineEquipmentProgram(scope);
                        onCascadedVarsChanged(scope, value);
                    });
                },
                templateUrl: 'filter-vaccine-facility-level-template'
            };

        }]
);

app.directive('vaccineZoneFilter', ['FacilitiesByGeographicZone', 'TreeGeographicZoneList', 'TreeGeographicTreeByProgramNoZones', 'GetUserUnassignedSupervisoryNode', 'messageService', 'VimsVaccineSupervisedIvdPrograms',
    function (FacilitiesByGeographicZone, TreeGeographicZoneList, TreeGeographicTreeByProgramNoZones, GetUserUnassignedSupervisoryNode, messageService, VimsVaccineSupervisedIvdPrograms) {
        var onPgCascadedVarsChanged1 = function ($scope) {
            var zone = ( angular.isDefined($scope.filterZone)) ? $scope.filterZone : 0;
            FacilitiesByGeographicZone.get({
                geoId: zone
            }, function (data) {
                $scope.facilities = data.facilities;
                if (isUndefined($scope.facilities)) {
                    $scope.facilities = [];
                }
                $scope.facilities.unshift({
                    name: messageService.get('report.filter.all.facilities')
                });
            });
        };

        var onCascadedVarsChanged = function ($scope) {


            VimsVaccineSupervisedIvdPrograms.get({}, function (data) {

                $scope.program = data.programs[0].id;
                TreeGeographicTreeByProgramNoZones.get({
                    program: $scope.program
                }, function (data) {
                    $scope.zones = [data.zone];
                    if($scope.selectedZone === undefined || $scope.selectedZone === null){
                      $scope.selectedZone = data.zone;
                        $scope.filterZone =  $scope.zones [0];

                    }

                });

            });


        };

        var categoriseZoneBySupervisoryNode = function ($scope) {


            GetUserUnassignedSupervisoryNode.get({
                program: $scope.program
            }, function (data) {
                $scope.user_geo_level = messageService.get('vaccine.report.filter.all.geographic.zones');
                if (!angular.isUndefined(data.supervisory_nodes)) {
                    if (data.supervisory_nodes === 0)
                        $scope.user_geo_level = messageService.get('label.vaccine.geographic.zone');
                }
            });
        };

        return {
            restrict: 'E',
            scope: {
                filterZone: '=val',
                zones: '=zones',
                program: '=pro',
                faccilitySelection: '=faccility',
                facilityId: '=facilityid'


            },
            controller: function ($scope, $routeParams, $location) {

                $scope.filterZone = 0;
                $scope.program = 0;
                $scope.user_geo_level = messageService.get('vaccine.report.filter.all.geographic.zones');
                if (!angular.isUndefined($scope.faccilitySelection) && $scope.faccilitySelection === true) {
                    $scope.faccilityShow = true;
                }
                //categoriseZoneBySupervisoryNode($scope);
                var onParamsChanged = function () {

                    onCascadedVarsChanged($scope);
                };

                onParamsChanged();
                $scope.$watch('filterZone', function (newValues, oldValues) {

                    onPgCascadedVarsChanged1($scope);
                    $scope.$parent.OnFilterChanged();
                });
                $scope.$watch('facilityId', function (newValues, oldValues) {


                    $scope.$parent.OnFilterChanged();
                });
            },

            //require: '^filterContainer',
            link: function (scope, elm, attr) {

            },

            templateUrl: 'filter-vaccine-zone-template'
        };
    }
]);
app.directive('vaccineDropoutFilter', ['DropoutProducts', 'messageService',
    function (DropoutProducts, messageService) {


        return {
            restrict: 'E',
            scope: {
                filterProduct: '=filterproduct',
                defaultProduct: '=defaultselction',
                onChange: '&'
            },
            controller: function ($scope, $routeParams, $location) {
                DropoutProducts.get({}, function (data) {
                    $scope.dropoutProductsList = data.dropoutProductsList;
                    if (!isUndefined($scope.dropoutProductsList)) {
                        $scope.defualt = $scope.dropoutProductsList[0];
                    }
                    if (!isUndefined($scope.default)) {
                        $scope.filterProduct = $scope.defualt.id;
                    }

                });
                if (!isUndefined($scope.defaultProduct)) {
                    $scope.filterProduct = $scope.defaultProduct;
                } else {
                    $scope.filterProduct = 0;
                }
                $scope.$watch('filterProduct', function (newValues, oldValues) {


                    $scope.$parent.OnFilterChanged();
                    $scope.onChange();
                });
            },
            link: function (scope, elm, attr) {


            },
            templateUrl: 'filter-vaccine-dropout-template'
        };
    }
]);

app.directive('vaccineProductFilter', ['VaccineProducts', 'VaccineSupervisedIvdPrograms',
    function (VaccineProducts, VaccineSupervisedIvdPrograms) {


        return {
            restrict: 'E',
            scope: {
                filterProduct: '=cmModel',
                default: '=default',
                onChange: '&'
            },
            controller: function ($scope) {
                VaccineProducts.get({}, function (data) {
                    $scope.products = data.products;

                });
                if (!isUndefined($scope.default)) {
                    $scope.filterProduct = $scope.default;
                } else {

                    $scope.filterProduct = 0;
                }
                $scope.$watch('filterProduct', function (newValues, oldValues) {

                    $scope.$parent.OnFilterChanged();
                    //if(!isUndefined($scope.onChange)){
                    $scope.onChange();
                    // }
                });

            },
            templateUrl: 'filter-vaccine-product-template'
        };
    }
]);

app.directive('vaccineMonthlyPeriodTreeFilter', ['GetVaccineReportPeriodFlat', 'messageService', 'SettingsByKey', 'VaccineCurrentPeriod',
    function (GetVaccineReportPeriodFlat, messageService, SettingsByKey, VaccineCurrentPeriod) {
        return {
            restrict: 'E',
            scope: {
                period: '=cmModel',
                default: '=default',
                onChange: '&'
            },
            controller: function ($scope) {
                $scope.$evalAsync(function () {
                $scope.period_placeholder = messageService.get('label.select.period');
                SettingsByKey.get({key: 'VACCINE_LATE_REPORTING_DAYS'}, function (data, er) {
                    $scope.cutoffdate = data.settings.value;
                });
                VaccineCurrentPeriod.get({}, function (data) {
                    if (!utils.isNullOrUndefined(data) && !utils.isNullOrUndefined(data).vaccineCurrentPeriod) {
                        var defaultPeriodId = data.vaccineCurrentPeriod.current_period;
                        $scope.filter = {defaultPeriodId: defaultPeriodId};
                        GetVaccineReportPeriodFlat.get({}, function (data) {
                            $scope.periods = data.vaccinePeriods.periods;
                            if (!angular.isUndefined($scope.periods)) {
                                if ($scope.periods.length === 0)
                                    $scope.period_placeholder = messageService.get('report.filter.period.no.vaccine.record');
                            }

                            $scope.period = $scope.filter.defaultPeriodId;

                        });

                    }

                });

                    //Load period tree

                });
                //if(!isUndefined($scope.default)){
                //    $scope.period = $scope.default;
                //}else{
                //
                //    $scope.period = 0;
                //}
                $scope.$watch('period', function (newVal, oldVal) {

                    $scope.onChange();
                    $scope.$parent.OnFilterChanged();
                });
            },
            link: function ($scope, elm, attr) {


            },
            templateUrl: 'filter-vaccine-monthly-period-tree-template'
        };
    }
]);

app.directive('staticPeriodFilter', [function () {

    return {
        restrict: 'E',
        scope: {
            periodRange: '=range',
            formateddate: '=formateddate',
            periodStartdate: '=startdate',
            periodEnddate: '=enddate',
            periodRangeMax: '=rangemax',
            perioderror: '=error',
            default: '=default',
            onChange: '&'
        },

        controller: function ($scope, SettingsByKey, $timeout) {
            $scope.periodRange = 0;
            $scope.periodStartdate = $scope.periodEnddate = "";

            SettingsByKey.get({key: 'VACCINE_LATE_REPORTING_DAYS'}, function (data, er) {
                $scope.cutoffdate = data.settings.value;
            });
            if (!isUndefined($scope.default)) {
                $scope.periodRange = $scope.default;
            }

            $scope.$watch('periodRange', function (newValues, oldValues) {
                if ($scope.periodRange < 5 && $scope.periodRange > 0) {
                    periods = utils.getVaccineCustomDateRange($scope.periodRange, $scope.periodStartdate, $scope.periodEnddate, $scope.cutoffdate);

                    $scope.periodStartdate = periods.startdate;
                    $scope.periodEnddate = periods.enddate;

                    $scope.$evalAsync(function () {
                        $scope.onChange();
                        $scope.$parent.OnFilterChanged();
                    });

                    $scope.showCustomeDateInputs = false;
                }

                else if ($scope.periodRange == 5)
                    $scope.showCustomeDateInputs = true;

                $scope.perioderror = "";
            });

            $scope.$watchCollection('[periodStartdate, periodEnddate]', function (newValues, oldValues) {

                if (utils.isEmpty($scope.periodStartdate) || utils.isEmpty($scope.periodEnddate))
                    return;

                else if (!((angular.isUndefined(newValues[0]) || newValues[0] === null) ||
                    (angular.isUndefined(newValues[1]) || newValues[1] === null)) && $scope.periodRange == 5 && $scope.periodRange > 0) {

                    var datediff = differenceInDays();

                    if (!angular.isUndefined($scope.periodRangeMax) && datediff < 0)
                        $scope.perioderror = 'Period start date must be before or equal to end date';

                    else if (!angular.isUndefined($scope.periodRangeMax) && datediff > $scope.periodRangeMax)
                        $scope.perioderror = 'Period start and end date selection are out of range';

                    else {
                        $scope.perioderror = "";
                        $scope.$parent.OnFilterChanged();
                    }
                }
            });

            var differenceInDays = function () {

                var one = new Date($scope.periodStartdate),
                    two = new Date($scope.periodEnddate);

                var millisecondsPerDay = 1000 * 60 * 60 * 24;
                var millisBetween = two.getTime() - one.getTime();
                var days = millisBetween / millisecondsPerDay;

                return Math.floor(days);
            };
        },

        link: function (scope, elm, attr) {
            scope.periods = [
                {key: 1, value: 'Current Period'},
                {key: 2, value: 'Last 3 Months'},
                {key: 3, value: 'Last 6 Months'},
                {key: 4, value: 'Last 1 Year'},
                {key: 5, value: 'Custom Range'}
            ];
        },
        templateUrl: 'filter-static-period'
    };
}]);

app.directive('vaccineFacilityBySupervisoryNodeFilter', ['UserFacilityWithViewStockLedgerReport', '$routeParams',

        function (UserFacilityWithViewStockLedgerReport, $routeParams) {

            var onCascadedPVarsChanged = function ($scope, attr) {

                if (!$routeParams.program) {
                    $scope.facilities = $scope.unshift([], 'report.filter.all.facilities');
                }

                if (isUndefined($scope.filter.program) || $scope.filter.program === 0) {
                    return;
                }

                UserFacilityWithViewStockLedgerReport.get(function (data) {
                    $scope.facilities = (attr.required) ? $scope.unshift(data.facilities, 'report.filter.select.facility') : $scope.unshift(data.facilities, 'report.filter.all.facilities');
                });

            };

            return {
                restrict: 'E',
                link: function (scope, elm, attr) {
                    scope.registerRequired('facility', attr);

                    var onParentChanged = function () {
                        onCascadedPVarsChanged(scope, attr);
                    };
                    scope.subscribeOnChanged('facility', 'program', onParentChanged, true);
                },
                templateUrl: 'filter-vaccine-facility-by-level-template'
            };


        }]
);

app.directive('rangePagination', ['SettingsByKey', function (SettingsByKey) {

    var getRange = function (total, range) {

        if (range <= 0)
            return null;
        var pages = [];
        var offset;
        var extremeVal;
        var loops = Math.floor(total / range) + 1;

        if (total == 1) {

            pages.push({
                offset: offset,
                range: range,
                value: '1'
            });

        } else {
            for (var i = 1; i <= loops; i++) {
                offset = (i - 1) * range;
                extremeVal = total > i * range ? i * range : total;
                if (offset >= total) break;
                pages.push({
                    offset: offset,
                    range: range,
                    value: (offset + 1).toString().concat('-').concat(extremeVal.toString())
                });
            }

        }

        return pages;

    };

    return {
        restrict: 'E',
        scope: {
            range: '=?range',
            labelAll: '@labelAll',
            fieldLabel: '@fieldLabel',
            total: '=total',
            offset: '=?offset',
            iszone: '=iszone',
            onChange: '&'
        },

        controller: function ($scope) {

            //$scope.fieldLabel = 'label.facility';

            if (!isUndefined($scope.iszone) && $scope.iszone === true) {
                SettingsByKey.get({key: 'VACCINE_DASHBOARD_DISTRICT_MAX_X_POINTS_FOR_CHART'}, function (data, er) {
                    $scope.Max_X_Points = !isUndefined(data.settings) ? data.settings.value : null;
                });
            } else {
                SettingsByKey.get({key: 'VACCINE_DASHBOARD_MAX_X_POINTS_FOR_CHART'}, function (data, er) {
                    $scope.Max_X_Points = !isUndefined(data.settings) ? data.settings.value : null;

                });
            }
            var computePages = function () {
                var range = 0;
                if (!isUndefined($scope.range)) {
                    range = $scope.range;
                }
                if (!isUndefined($scope.Max_X_Points)) {
                    range = $scope.Max_X_Points;

                }
                if (range <= 0)
                    return;

                if (range > 12)
                    range = 12; //12 is the maximum allowed range

                $scope.range = range;

                $scope.pages = getRange($scope.total, $scope.range);


                $scope.offset = 0;

            };

            $scope.$watch('offset', function () {
                $scope.onChange(); // custom callback function to be called for the different instances of this directive in a single page
                $scope.$parent.OnFilterChanged();// Used only when this directive is used once in a page
            });

            $scope.$watch('total', function () {
                computePages();
            });

            // computePages();

        },


        link: function (scope, elm, attr) {


        },
        templateUrl: 'filter-range-pagination'
    };


}]);


app.directive('productWithoutDescriptionFilter', ['ReportProductsByProgramWithoutDescriptions', 'messageService', '$routeParams',
    function (ReportProductsByProgramWithoutDescriptions, messageService, $routeParams) {

        var onPgCascadedVarsChanged = function ($scope, attr) {
            if (isUndefined($scope.filter.program) || $scope.filter.program === 0)
                return;

            var program = (angular.isDefined($scope.filter.program)) ? $scope.filter.program : 0;

            ReportProductsByProgramWithoutDescriptions.get({
                programId: program
            }, function (data) {
                $scope.products = data.productList;
                if (!attr.required) {
                    $scope.products.unshift({
                        'name': messageService.get('report.filter.select.indicator.product'),
                        id: -1
                    });
                    $scope.products.unshift({
                        'name': messageService.get('report.filter.all.products'),
                        id: 0
                    });
                }

            });

        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('product', attr);
                if (!$routeParams.product && !attr.required) {
                    scope.products = [{
                        'name': messageService.get('report.filter.all.products'),
                        id: 0
                    }];
                }

                // this is what filters products based on product categories selected.
                scope.productCFilter = function (option) {
                    var show = (
                        _.isEmpty(scope.filter.productCategory) ||
                        _.isUndefined(scope.filter.productCategory) ||
                        parseInt(scope.filter.productCategory, 10) === 0 ||
                        option.categoryId == scope.filter.productCategory ||
                        option.id === -1 ||
                        option.id === 0
                    );
                    return show;
                };

                var onFiltersChanged = function () {
                    onPgCascadedVarsChanged(scope, attr);
                };
                scope.subscribeOnChanged('product', 'product-category', onFiltersChanged, false);
                scope.subscribeOnChanged('product', 'program', onFiltersChanged, true);
            },
            templateUrl: 'filter-product-without-description-template'
        };

    }
]);


app.directive('staticYearFilter', ['StaticYears', 'SettingsByKey', function (StaticYears, SettingsByKey) {

    return {
        restrict: 'E',
        scope: {
            staticYear: '=year',
            periodStartdate: '=startdate',
            periodEnddate: '=enddate',
            perioderror: '=perioderror',
            onChange: '&'
        },

        controller: function ($scope, $timeout) {

            $scope.periodStartdate = $scope.periodEnddate = "";
            $scope.years = [];
            SettingsByKey.get({key: 'VACCINE_LATE_REPORTING_DAYS'}, function (data, er) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    $scope.cutoffdate = data.settings.value;
                } else {
                    $scope.cutoffdate = 0;
                }
            });
            StaticYears.get({}, function (data) {

                data.years.forEach(function (value) {
                    $scope.years.push(value);
                });
                $scope.staticYear = $scope.years[0].id;

            });


            $scope.$watch('staticYear', function (newValues, oldValues) {
                if (!utils.isNullOrUndefined($scope.staticYear) && $scope.staticYear >= 0) {
                    periods = utils.getYearStartAndEnd($scope.staticYear, $scope.periodStartdate, $scope.periodEnddate, $scope.cutoffdate,0);


                    $scope.periodStartdate = periods.startdate;
                    $scope.periodEnddate = periods.enddate;

                    $timeout(function () {
                        $scope.onChange();
                        $scope.$parent.OnFilterChanged();
                    }, 10);
                }
            });

            $scope.$watchCollection('[periodStartdate,periodEnddate]', function (newValues, oldValues) {
                if (utils.isEmpty($scope.periodStartdate) || utils.isEmpty($scope.periodEnddate))
                    return;
                var datediff = differenceInDays();
                var yearDif = yearDifference();

                if (datediff < 0) {
                    $scope.perioderror = 'Period start date must be before or equal to end date';
                }
                else if (yearDif !== 0) {
                    $scope.perioderror = 'Start and End date should be in same year';
                }
                else
                    $scope.perioderror="";
                    $scope.$parent.OnFilterChanged();

            });
            var differenceInDays = function () {

                var one = new Date($scope.periodStartdate),
                    two = new Date($scope.periodEnddate);

                var millisecondsPerDay = 1000 * 60 * 60 * 24;
                var millisBetween = two.getTime() - one.getTime();
                var days = millisBetween / millisecondsPerDay;

                return Math.floor(days);
            };
            var yearDifference = function () {
                var one = new Date($scope.periodStartdate),
                    two = new Date($scope.periodEnddate);
                var startyear = one.getFullYear(),
                    endYear = two.getFullYear();
                var dif = startyear - endYear;
                return Math.floor(dif);

            };
        },
        templateUrl: 'filter-static-year'
    };
}]);

app.directive('staticYearMonthFilter', ['StaticYears', 'SettingsByKey', function (StaticYears, SettingsByKey) {

    return {
        restrict: 'E',
        scope: {
            staticYear: '=year',
            periodStartdate: '=startdate',
            periodEnddate: '=enddate',
            perioderror: '=perioderror',
            onChange: '&'
        },

        controller: function ($scope, $timeout) {

            $scope.periodStartdate = $scope.periodEnddate = "";
            $scope.years = [];
            $scope.monthNames=[{id:0,value:"Jan"},
                {id:1,value:"Feb"},
                {id:2,value:"Mar"},
                {id:3,value:"Apr"},
                {id:4,value:"May"},
                {id:5,value:"Jun"},
                {id:6,value:"Jul"},
                {id:7,value:"Aug"},
                {id:8,value:"Sep"},
                {id:9,value:"Oct"},
                {id:10,value:"Nov"},
                {id:11,value:"Dec"}];
            $scope.startMonth=0;
            $scope.endMonth=11;
            SettingsByKey.get({key: 'VACCINE_LATE_REPORTING_DAYS'}, function (data, er) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    $scope.cutoffdate = data.settings.value;
                } else {
                    $scope.cutoffdate = 0;
                }
            });
            StaticYears.get({}, function (data) {

                data.years.forEach(function (value) {
                    $scope.years.push(value);
                });
                $scope.staticYear = $scope.years[0].id;

            });


            $scope.$watch('staticYear', function (newValues, oldValues) {
                if (!utils.isNullOrUndefined($scope.staticYear) && $scope.staticYear >= 0) {
                    periods = utils.getYearStartAndEnd($scope.staticYear, $scope.startMonth, $scope.endMonth, $scope.cutoffdate,1);
                    $scope.startMonth=0;
                    $scope.endMonth=11;
                    $scope.periodStartdate = periods.startdate;
                    $scope.periodEnddate = periods.enddate;

                    $timeout(function () {
                        $scope.onChange();
                        $scope.$parent.OnFilterChanged();
                    }, 10);
                }
            });

            $scope.$watchCollection('[startMonth,endMonth]', function (newValues, oldValues) {
                if (utils.isEmpty($scope.startMonth) || utils.isEmpty($scope.endMonth))
                    return;
               var end=parseInt($scope.endMonth,10)+1;
                periods = utils.getYearStartAndEnd($scope.staticYear, $scope.startMonth, end, $scope.cutoffdate,1);
                $scope.periodStartdate = periods.startdate;
                $scope.periodEnddate = periods.enddate;
                $scope.$apply();
                var datediff = differenceInDays();
                var yearDif = yearDifference();

                if (datediff < 0) {
                    $scope.perioderror = 'Period start date must be before or equal to end date';
                }
                else if (yearDif !== 0) {
                    $scope.perioderror = 'Start and End date should be in same year';
                }
                else
                    $scope.perioderror="";
                $scope.$parent.OnFilterChanged();

            });
            var differenceInDays = function () {

                var one = new Date($scope.periodStartdate),
                    two = new Date($scope.periodEnddate);

                var millisecondsPerDay = 1000 * 60 * 60 * 24;
                var millisBetween = two.getTime() - one.getTime();
                var days = millisBetween / millisecondsPerDay;

                return Math.floor(days);
            };
            var yearDifference = function () {
                var one = new Date($scope.periodStartdate),
                    two = new Date($scope.periodEnddate);
                var startyear = one.getFullYear(),
                    endYear = two.getFullYear();
                var dif = startyear - endYear;
                return Math.floor(dif);

            };
        },
        templateUrl: 'filter-static-year-month'
    };
}]);

app.directive('vaccineProductDosesFilter', ['VaccineProductDoseList', 'messageService', 'VimsVaccineSupervisedIvdPrograms',
    function (VaccineProductDoseList, messageService, VimsVaccineSupervisedIvdPrograms) {

        return {
            restrict: 'E',
            scope: {
                product: '=product'

            },
            controller: function ($scope) {

                var selectAllDoses = messageService.get('filter.vaccine.doses.select.all');

                $scope.$watch('product', function (newValues, oldValues) {

                    VimsVaccineSupervisedIvdPrograms.get({}, function (data) {

                        VaccineProductDoseList.get(
                            {
                                programId: data.programs[0].id,
                                productId: $scope.product
                            },

                            function (result) {
                                $scope.doses = !utils.isNullOrUndefined(result.doses) ? result.doses : null;
                                !utils.isNullOrUndefined(result.doses) ? $scope.doses.unshift({displayName: selectAllDoses}) :
                                    $scope.doses.push({displayName: selectAllDoses});
                            });
                    });
                });

                $scope.filterChanged = function () {
                    $scope.$parent.filter.dose = $scope.filter.dose;
                    $scope.$parent.OnFilterChanged();
                };

            },
            templateUrl: 'filter-vaccine-product-doses-template'
        };

    }
]);


app.directive('vaccineStockDateFilter', [
    function () {


        return {
            restrict: 'E',
            scope: {
                filterToDate: '=cmModel',
                default: '=default',
                onChange: '&'
            },
            controller: function ($scope) {

                if (!isUndefined($scope.default)) {
                    $scope.filterToDate = $scope.default;
                } else {
                    var d = new Date();
                    month = '' + (d.getMonth() + 1);
                    day = '' + d.getDate();
                    year = d.getFullYear();

                    if (month.length < 2) month = '0' + month;
                    if (day.length < 2) day = '0' + day;

                    $scope.filterToDate = [year, month, day].join('-');
                }

                $scope.$watch('filterToDate', function (newValues, oldValues) {
                    $scope.$parent.OnFilterChanged();
                    $scope.onChange();
                });

            },
            templateUrl: 'filter-vaccine-stock-date-template'
        };
    }
]);

app.directive('vaccineStockFacilityLevelFilter', ['ReportFacilityLevels', 'VaccineSupervisedIvdPrograms',
    function (ReportFacilityLevels, VaccineSupervisedIvdPrograms) {


        return {
            restrict: 'E',
            scope: {
                filterFacilityLevel: '=cmModel',
                default: '=default',
                onChange: '&'
            },
            controller: function ($scope) {

                VaccineSupervisedIvdPrograms.get({}, function (data) {
                    ReportFacilityLevels.get({program: data.programs[0].id}, function (data2) {
                        var facilityLevels = [];
                        var hasRVS = _.where(data2.facilityLevels, {code: 'rvs'});
                        var hasCVS = _.where(data2.facilityLevels, {code: 'cvs'});
                        var isUpperThanDvs = (hasRVS.length > 0) ? true : false;
                        var isUpperThanRVS = (hasCVS.length > 0) ? true : false;

                        data2.facilityLevels.forEach(function (level) {
                            if (level.code === 'rvs' && isUpperThanRVS) {
                                facilityLevels.push(level);
                            }
                            if (level.code === 'dvs' && isUpperThanDvs) {
                                facilityLevels.push(level);
                            }

                        });
                        $scope.facilityLevels = facilityLevels.sort(function (a, b) {
                            return (a.displayOrder > b.displayOrder) ? 1 : ((b.displayOrder > a.displayOrder) ? -1 : 0);
                        });
                        $scope.filterFacilityLevel = $scope.facilityLevels[0].code;
                    });

                });

                if (!isUndefined($scope.default)) {
                    $scope.filterFacilityLevel = $scope.default;
                } else {

                }

                $scope.$watch('filterFacilityLevel', function (newValues, oldValues) {
                    $scope.$parent.OnFilterChanged();
                    $scope.onChange();
                });

            },
            templateUrl: 'filter-vaccine-facility-levels-template'
        };
    }
]);

app.directive('customLegend', [
    function () {
        return {
            restrict: 'E',
            scope: {
                data: '=data'
            },

            controller: function ($scope) {

                $scope.$watch('data', function (newValues, oldValues) {

                });
            },
            templateUrl: 'filter-custom-legend-template'
        };
    }
]);

app.directive('datepickerPopup', function ($filter){
    return {
        restrict: 'EAC',
        require: 'ngModel',
        link: function(scope, elem, attrs, ngModel) {
            ngModel.$parsers.push(function toModel(date) {
                return $filter('date')(date, "yyyy-MM-dd");
            });
        }
    };
});

app.directive('facilityOperatorFilter', ['GetAllFacilityOperators', '$routeParams', 'messageService',
    function (GetAllFacilityOperators, $routeParams, messageService) {

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                scope.registerRequired('facilityOperator', attr);
                GetAllFacilityOperators.get({},
                    function (data) {
                       scope.facilityOperators = data.facilityOperators;
                        scope.facilityOperators.unshift({
                            'text': messageService.get('report.filter.all.facility.operators')
                        });
                    });
            },
            templateUrl: 'filter-facility-operator-template'
        };
    }
]);





app.directive('vaccineFacilityByDistrictFilter', ['UserFacilityWithViewStockLedgerReport', '$routeParams',

        function (UserFacilityWithViewStockLedgerReport, $routeParams) {

            var onCascadedPVarsChanged = function ($scope, attr) {

                if (!$routeParams.program) {
                    $scope.facilities = $scope.unshift([], 'report.filter.all.facilities');
                }

              /*  if (isUndefined($scope.filter.program) || $scope.filter.program === 0) {
                    return;
                }*/

                UserFacilityWithViewStockLedgerReport.get(function (data) {
                    $scope.facilities = (attr.required) ? $scope.unshift(data.facilities, 'report.filter.select.facility') : $scope.unshift(data.facilities, 'report.filter.all.facilities');
                });

            };

            return {
                restrict: 'E',
                link: function (scope, elm, attr) {
                   scope.registerRequired('facility', attr);

                    var onParentChanged = function () {
                        onCascadedPVarsChanged(scope, attr);
                    };
                    scope.subscribeOnChanged('facility','program', onParentChanged, true);
                },
                templateUrl: 'filter-vaccine-facility-by-level-template'
            };


        }]
);



app.directive('vaccineFacilitByDistrictFilter', ['UserFacilityWithViewStockLedgerReport', '$routeParams', 'messageService',
    function (UserFacilityWithViewStockLedgerReport, $routeParams, messageService) {

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                scope.registerRequired('facility', attr);

                UserFacilityWithViewStockLedgerReport.get(function (data) {
                    scope.facilities = (attr.required) ? scope.unshift(data.facilities, 'report.filter.select.facility') : scope.unshift(data.facilities, 'report.filter.all.facilities');
                });

            },
            templateUrl: 'filter-vaccine-facility-by-level-template'
        };
    }
]);



app.directive('facilityLevelWithoutProgramFilter', ['ReportFacilityLevelWithoutProgram',
    function (ReportFacilityLevelWithoutProgram) {

        var onCascadedPVarsChanged = function ($scope) {
                ReportFacilityLevelWithoutProgram.get({
                }, function (data) {
                    $scope.facilityLevels = [];
                    if (data.facility_levels_without_programs.length > 0) {
                        $scope.facilityLevels.unshift({
                            'id': 'hf',
                            'name': 'Health Facilities (HF)'
                        });
                        _.each(data.facility_levels_without_programs, function (item) {
                            if (item.code === 'cvs' ||
                                item.code === 'rvs' ||
                                item.code === 'dvs') {
                                $scope.facilityLevels.unshift({
                                    'id': item.code,
                                    'name': item.name + ' (' + item.code.toUpperCase() + ')',
                                    'display_order': item.displayOrder
                                });
                            }
                        });
                        $scope.facilityLevels.unshift({
                            'id': '',
                            'name': '-- Select Facility Level --',
                            'display_order': 0
                        });
                    }
                });

        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('facilityLevel', attr);
                onCascadedPVarsChanged(scope);
            },
            templateUrl: 'filter-facility-level-without-program-template'
        };
    }
]);

app.directive('productCategoryWithoutProgramFilter', ['ProductCategoriesWithoutProgram', '$routeParams',
    function (ProductCategoriesWithoutProgram, $routeParams) {

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {
                scope.registerRequired('productCategory', attr);
                if (!$routeParams.productCategory) {
                    scope.productCategories = scope.unshift([], 'report.filter.all.product.categories');
                }

                    ProductCategoriesWithoutProgram.get({
                    }, function (data) {
                        scope.productCategories = scope.unshift(data.productCategoryList, 'report.filter.all.product.categories');
                    });
                //scope.subscribeOnChanged('productCategory',  onProgramChanged, false);
            },
            templateUrl: 'filter-product-category-without-program-template'
        };
    }
]);



app.directive('productWithoutDescriptionAndProgramFilter', ['ReportProductsByProgramWithoutDescriptionsAndProgram', 'messageService', '$routeParams',
    function (ReportProductsByProgramWithoutDescriptionsAndProgram, messageService, $routeParams) {

        var onPgCascadedVarsChanged = function ($scope, attr) {

            ReportProductsByProgramWithoutDescriptionsAndProgram.get({
            }, function (data) {
                $scope.products = data.productList;
                if (!attr.required) {
                    $scope.products.unshift({
                        'name': messageService.get('report.filter.select.indicator.product'),
                        id: -1
                    });
                    $scope.products.unshift({
                        'name': messageService.get('report.filter.all.products'),
                        id: 0
                    });
                }

            });

        };

        return {
            restrict: 'E',
            link: function (scope, elm, attr) {
                scope.registerRequired('product', attr);
                if (!$routeParams.product && !attr.required) {
                    scope.products = [{
                        'name': messageService.get('report.filter.all.products'),
                        id: 0
                    }];
                }

                // this is what filters products based on product categories selected.
                scope.productCFilter = function (option) {
                    var show = (
                        _.isEmpty(scope.filter.productCategory) ||
                        _.isUndefined(scope.filter.productCategory) ||
                        parseInt(scope.filter.productCategory, 10) === 0 ||
                        option.categoryId == scope.filter.productCategory ||
                        option.id === -1 ||
                        option.id === 0
                    );
                    return show;
                };

                var onFiltersChanged = function () {
                    onPgCascadedVarsChanged(scope, attr);
                };
                scope.subscribeOnChanged('product', 'product-category', onFiltersChanged, true);
               // scope.subscribeOnChanged('product', 'program', onFiltersChanged, true);
            },
            templateUrl: 'filter-product-without-description-and-program-template'
        };

    }
]);

app.directive('productWithoutDescriptionAndWithoutProgramFilter', ['ReportProductsWithoutDescriptionsAndWithoutProgram', '$routeParams',
    function (ReportProductsWithoutDescriptionsAndWithoutProgram, $routeParams) {

        return {
            restrict: 'E',
            require: '^filterContainer',
            link: function (scope, elm, attr) {

                if (!$routeParams.product) {
                    scope.products = scope.unshift([], 'report.filter.all.products');
                }

                ReportProductsWithoutDescriptionsAndWithoutProgram.get({
                }, function (data) {
                    scope.products = scope.unshift(data.productList, 'report.filter.all.products');
                });
            },
            templateUrl: 'filter-product-without-description-and-without-program-template'
        };
    }
]);





app.directive('vaccineFacilityBySupervisoryNodeWithoutProgramFilter', ['UserFacilityWithViewStockLedgerReport', '$routeParams',

        function (UserFacilityWithViewStockLedgerReport, $routeParams) {

            var onCascadedPVarsChanged = function ($scope, attr) {

                UserFacilityWithViewStockLedgerReport.get(function (data) {
                    $scope.facilities = (attr.required) ? $scope.unshift(data.facilities, 'report.filter.select.facility') : $scope.unshift(data.facilities, 'report.filter.all.facilities');
                });

            };

            return {
                restrict: 'E',
                link: function (scope, elm, attr) {
                    scope.registerRequired('facility', attr);
                        onCascadedPVarsChanged(scope, attr);
                },
                templateUrl: 'filter-vaccine-facility-by-level-template'
            };


        }]
);