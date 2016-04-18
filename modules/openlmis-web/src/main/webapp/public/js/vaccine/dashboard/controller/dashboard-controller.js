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

function VaccineDashboardController($scope, VaccineDashboardSummary, $filter, VaccineDashboardMonthlyCoverage,
                                    VaccineDashboardDistrictCoverage,
                                    VaccineDashboardMonthlyDropout,
                                    VaccineDashboardDistrictDropout,
                                    VaccineDashboardDistrictSessions,
                                    VaccineDashboardSessions,
                                    VaccineDashboardMonthlyStock,
                                    VaccineDashboardDistrictStock,
                                    VaccineDashboardMonthlyWastage,
                                    VaccineDashboardDistrictWastage,
                                    VaccineDashboardFacilityTrend,
                                    VaccineDashboardBundle,
                                    dashboardSlidesHelp,
                                    VaccineDashboardFacilityCoverageDetails,
                                    VaccineDashboardFacilityCoverage,
                                    defaultProduct,
                                    defaultPeriodTrend,
                                    defaultMonthlyPeriod,
                                    SettingsByKey, messageService, $modal,
                                    repairingDetail,
                                    reparingDetailList,
                                    reportingDetail,
                                    reportingDetailList,
                                    InvestigatingDetails,
                                    investigatingDetailList,
                                    ContactList, isDistrictUser,
                                    VaccineDashboardFacilityStock,
                                    settingValues, $log,
                                    VaccineDashboardMonthlyStockStatus,
                                    VaccineDashboardDistrictStockStatus,
                                    VaccineDashboardFacilityStockStatus,
                                    VaccineDashboardFacilityStockStatusDetails, colors,
                                    userPreferences) {
    $scope.actionBar = {openPanel: true};
    $scope.performance = {openPanel: true};
    $scope.stockStatus = {openPanel: true};
    $scope.sessions = {
        openPanel: true
    };


    var monthlyDashletPeriods = utils.getCustomizedStartAndEndDate(settingValues.monthsRange, settingValues.cuttoff);

    $scope.startDate = monthlyDashletPeriods.startdate;
    $scope.endDate = monthlyDashletPeriods.enddate;

    var bcgVaccinated = " BCG Vaccinated";
    var mrVaccinated = " MR Vaccinated";
    var bcgDropout = " BCG Droput";
    $scope.district_user_level = isDistrictUser.district_user;
    $scope.dashboardHelps = dashboardSlidesHelp;
    $scope.defaultPeriodTrend = parseInt(defaultPeriodTrend, 10);
    $scope.defaultProduct = defaultProduct;
    $scope.defaultMonthlyPeriod = defaultMonthlyPeriod;
    $scope.label = {zone: messageService.get('label.zone'), period: messageService.get('label.period')};
    $scope.userPreferences = userPreferences;

    /////////////////////////////////////////////////////////////////////////
    // coverage - Monthly, District, Facility
    //////////////////////////////////////////////////////////////////////////

    $scope.monthlyCoverage = {
        dataPoints: [],
        dataColumns: [{
            "id": "coverage", "name": messageService.get('label.coverage'), "type": "line"
        },

            {"id": "target", "name": messageService.get('label.target'), "type": "bar"},
            {"id": "actual", "name": messageService.get('label.actual'), "type": "bar"}
        ],
        dataX: {"id": "period_name"}
    };


    $scope.districtCoverage = {
        dataPoints: [],
        dataColumns: [{
            "id": "coverage", "name": messageService.get('label.coverage'), "type": "scatter"
        },
            {"id": "actual", "name": messageService.get('label.actual'), "type": "bar"},
            {"id": "target", "name": messageService.get('label.target'), "type": "bar"}
        ],
        dataX: {"id": "geographic_zone_name"}
    };


    $scope.facilityCoverage = {
        dataPoints: [],
        dataColumns: [{
            "id": "coverage", "name": messageService.get('label.coverage'), "type": "scatter"
        },
            {"id": "actual", "name": messageService.get('label.actual'), "type": "bar"},
            {"id": "target", "name": messageService.get('label.target'), "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };


    /////////////////////////////////////////////////////////////////////////
    // Dropout - Monthly, District, Facility
    //////////////////////////////////////////////////////////////////////////

    $scope.monthlyDropout = {
        dataPoints: [],
        dataColumns: [{
            "id": "bcg_mr_dropout", "name": bcgDropout, "type": "line"
        },
            {"id": "bcg_vaccinated", "name": bcgVaccinated, "type": "area"},
            {"id": "mr_vaccinated", "name": mrVaccinated, "type": "area"}
        ],
        dataX: {"id": "period_name"}
    };

    $scope.districtDropout = {
        dataPoints: [],
        dataColumns: [{
            "id": "bcg_mr_dropout", "name": bcgDropout, "type": "scatter"
        },
            {"id": "bcg_vaccinated", "name": bcgVaccinated, "type": "bar"},
            {"id": "mr_vaccinated", "name": mrVaccinated, "type": "bar"}
        ],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.facilityDropout = {
        dataPoints: [],
        dataColumns: [{
            "id": "bcg_mr_dropout", "name": bcgDropout, "type": "scatter"
        },
            {"id": "bcg_vaccinated", "name": bcgVaccinated, "type": "bar"},
            {"id": "mr_vaccinated", "name": mrVaccinated, "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };

    /////////////////////////////////////////////////////////////////////////
    // wastage - Monthly, District, Facility
    //////////////////////////////////////////////////////////////////////////
    $scope.monthlyWastage = {
        dataPoints: [],
        dataColumns: [
            {"id": "vaccinated", "name": messageService.get('label.wastage.vaccinated'), "type": "area"},
            {"id": "usage_denominator", "name": messageService.get('label.wastage.denominator'), "type": "area"},
            {
                "id": "wastage_rate", "name": messageService.get('label.wastage.rate'), "type": "line"
            }
        ],
        dataX: {"id": "period_name"}
    };

    $scope.districtWastage = {
        dataPoints: [],
        dataColumns: [
            {"id": "vaccinated", "name": messageService.get('label.wastage.vaccinated'), "type": "bar"},
            {"id": "usage_denominator", "name": messageService.get('label.wastage.denominator'), "type": "bar"},
            {
                "id": "wastage_rate", "name": messageService.get('label.wastage.rate'), "type": "scatter"
            }
        ],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.facilityWastage = {
        dataPoints: [],
        dataColumns: [{},
            {"id": "vaccinated", "name": messageService.get('label.wastage.vaccinated'), "type": "bar"},
            {"id": "usage_denominator", "name": messageService.get('label.wastage.denominator'), "type": "bar"},
            {"id": "wastage_rate", "name": messageService.get('label.actual'), "type": "scatter"}
        ],
        dataX: {"id": "facility_name"}
    };

    /////////////////////////////////////////////////////////////////////////
    // Sessions - Monthly, District, Facility
    //////////////////////////////////////////////////////////////////////////
    $scope.monthlySessions = {
        dataPoints: [],
        dataColumns: [{
            "id": "outreach_sessions",
            "name": messageService.get('label.outreach.sessions'),
            "type": "area"
        },
            {"id": "fixed_sessions", "name": messageService.get('label.fixed.sessions'), "type": "area"}],
        dataX: {"id": "period_name"}
    };

    $scope.districtSessions = {
        dataPoints: [],
        dataColumns: [{
            "id": "outreach_sessions",
            "name": messageService.get('label.outreach.sessions'),
            "type": "bar"
        },
            {"id": "fixed_sessions", "name": messageService.get('label.fixed.sessions'), "type": "bar"}],
        dataX: {"id": "geographic_zone_name"}

    };

    $scope.facilitySessions = {
        dataPoints: [],
        dataColumns: [{
            "id": "fixed_sessions", "name": messageService.get('label.fixed.sessions'), "type": "bar"
        },
            {"id": "outreach_sessions", "name": messageService.get('label.outreach.sessions'), "type": "bar"}
        ],
        dataX: {"id": "facility_name"}
    };


// stock
    $scope.monthlyStock = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"
        }
        ],
        dataX: {"id": "period_name"},
        grid: {
            min: 3,
            max: 6
        }
    };
    $scope.districtStock = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"
        }
        ],
        dataX: {"id": "geographic_zone_name"}
    };

    $scope.facilityStock = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"
        }
        ],
        dataX: {"id": "facility_name"},
        grid: {
            min: 3,
            max: 6
        }
    };


// bundling
    $scope.bundling = {
        dataPoints: [],
        dataColumns: [{
            "id": "minlimit", "name": messageService.get('label.min.limit'), "type": "line"
        },
            {"id": "maxlimit", "name": messageService.get('label.max.limit'), "type": "line"},
            {"id": "bund_issued", "name": messageService.get('label.bundle.issued'), "type": "bar"},
            {"id": "bund_received", "name": messageService.get('label.bundle.received'), "type": "bar"}
        ],
        dataX: {"id": "period_name"}
    };

    $scope.districtStockStatus = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos_g1", "name": messageService.get('label.value.min.mos'), "type": "bar", "color": colors.red_color
        },
            {
                "id": "mos_g2",
                "name": messageService.get('label.value.between.mos'),
                "type": "bar",
                "color": colors.green_color
            },
            {
                "id": "mos_g3",
                "name": messageService.get('label.value.above.mos'),
                "type": "bar",
                "color": colors.blue_color
            },
            {"id": "minmonthsofstock", "name": messageService.get('label.min.mos'), "type": "line", "color": "black"},
            {"id": "maxmonthsofstock", "name": messageService.get('label.max.mos'), "type": "line", "color": "black"}
        ],
        dataX: {"id": "district_name"}
    };

    $scope.monthlyStockstatus = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos_g1", "name": messageService.get('label.value.min.mos'), "type": "bar", "color": colors.red_color
        },
            {
                "id": "mos_g2",
                "name": messageService.get('label.value.between.mos'),
                "type": "bar",
                "color": colors.green_color
            },
            {
                "id": "mos_g3",
                "name": messageService.get('label.value.above.mos'),
                "type": "bar",
                "color": colors.blue_color
            },
            {"id": "min", "name": messageService.get('label.min.mos'), "type": "line", "color": "black"},
            {"id": "max", "name": messageService.get('label.max.mos'), "type": "line", "color": "black"}
        ],
        dataX: {"id": "period_name"}
    };
    $scope.facilityStockstatus = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos_g1", "name": messageService.get('label.value.min.mos'), "type": "bar", "color": colors.red_color
        },
            {
                "id": "mos_g2",
                "name": messageService.get('label.value.between.mos'),
                "type": "bar",
                "color": colors.green_color
            },
            {
                "id": "mos_g3",
                "name": messageService.get('label.value.above.mos'),
                "type": "bar",
                "color": colors.blue_color
            },
            {"id": "minmonthsofstock", "name": messageService.get('label.min.mos'), "type": "line", "color": "black"},
            {"id": "maxmonthsofstock", "name": messageService.get('label.max.mos'), "type": "line", "color": "black"}
        ],
        dataX: {"id": "facility_name"}
    };
//////////////////
//  Coverage
///////////////////
    $scope.coverageCallBack = function () {
        $scope.monthlyCoverageCallback();
        $scope.districtCoverageCallback();
        $scope.facilityCoverageCallback();

    };
    $scope.monthlyCoverageCallback = function () {

        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.coverage.product) && $scope.filter.coverage.product !== 0) {

            VaccineDashboardMonthlyCoverage.get({
                startDate: $scope.startDate, endDate: $scope.endDate,
                product: $scope.filter.coverage.product
            }, function (data) {
                $scope.monthlyCoverage.dataPoints = data.monthlyCoverage;
            });

        }
    };

    $scope.districtCoverageCallback = function () {
        if (!isUndefined($scope.filter.coverage.period) && !isUndefined($scope.filter.coverage.product) && $scope.filter.coverage.product !== 0) {
            VaccineDashboardDistrictCoverage.get({
                period: $scope.filter.coverage.period,
                product: $scope.filter.coverage.product
            }, function (data) {
                $scope.districtCoverage.data = data.districtCoverage;
                if (!isUndefined($scope.districtCoverage.data)) {
                    $scope.filter.totalDistrictCoverage = $scope.districtCoverage.data.length;
                } else {
                    $scope.filter.totalDistrictCoverage = 0;
                }
            });
        }
    };

    $scope.facilityCoverageCallback = function () {
        if (!isUndefined($scope.filter.coverage.period) && !isUndefined($scope.filter.coverage.product) && $scope.filter.coverage.product !== 0) {
            //VaccineDashboardFacilityCoverage.get({period: $scope.filter.facilityCoverage.period,
            VaccineDashboardFacilityTrend.coverage({
                period: $scope.filter.coverage.period,
                product: $scope.filter.coverage.product
            }, function (data) {
                $scope.facilityCoverage.data = data.facilityCoverage;
                if (!isUndefined($scope.facilityCoverage.data)) {
                    $scope.filter.totalFacilityCoverage = $scope.facilityCoverage.data.length;
                } else {
                    $scope.filter.totalFacilityCoverage = 0;
                }
            });


        }
    };

    $scope.facilityCoveragePagination = function () {
        var s = parseInt($scope.filter.facilityCoverageOffset, 10) + parseInt($scope.filter.facilityCoverageRange, 10);
        if (!isUndefined($scope.filter.facilityCoverageOffset)) {
            $scope.facilityCoverage.dataPoints = $scope.facilityCoverage.data.slice(parseInt($scope.filter.facilityCoverageOffset, 10), s);
        }
    };
    $scope.districtCoveragePagination = function () {
        var s = parseInt($scope.filter.districtCoverageOffset, 10) + parseInt($scope.filter.districtRange, 10);
        if (!isUndefined($scope.filter.districtCoverageOffset)) {
            $scope.districtCoverage.dataPoints = $scope.districtCoverage.data.slice(parseInt($scope.filter.districtCoverageOffset, 10), s);
        }
    };
    $scope.coverageDetailCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.coverage.product) && $scope.filter.coverage.product !== 0) {
            // VaccineDashboardFacilityCoverageDetails.get({startDate: $scope.filter.detailCoverage.startDate, endDate: $scope.filter.detailCoverage.endDate,
            VaccineDashboardFacilityTrend.coverageDetails({
                startDate: $scope.startDate, endDate: $scope.endDate,
                product: $scope.filter.coverage.product
            }, function (data) {

                $scope.coverageDetails = data.facilityCoverageDetails;
                $scope.periodsList = _.uniq(_.pluck(data.facilityCoverageDetails, 'period_name'));
                var facilities = _.uniq(_.pluck(data.facilityCoverageDetails, 'facility_name'));

                $scope.facilityDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.coverageDetails, {facility_name: facility}).district_name;

                    $scope.facilityDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'target',
                        indicatorValues: $scope.getIndicatorValues(facility, 'target', $scope.coverageDetails)
                    });
                    $scope.facilityDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'actual',
                        indicatorValues: $scope.getIndicatorValues(facility, 'actual', $scope.coverageDetails)
                    });
                    $scope.facilityDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'coverage',
                        indicatorValues: $scope.getIndicatorValues(facility, 'coverage', $scope.coverageDetails)
                    });

                });
                var modalInstance = $modal.open({
                    templateUrl: 'partials/slide-coverage-detail-trend.html',
                    controller: 'DashboardModalInstanceCtrl',
                    resolve: {
                        items: function () {

                            return {facilityDetails: $scope.facilityDetails, periodsList: $scope.periodsList};
                        }
                    }
                });
            });

        }
    };
    $scope.openCoverageHelp = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-coverage-help-content.html',
            controller: 'DashboardHelpModalInstanceCtrl',
            resolve: {
                items: function () {

                    return {dashboardHelps: $scope.dashboardHelps};
                }
            }
        });
    };

//////////////////
//  Dropout
///////////////////
    $scope.dropoutCallback = function () {
        $scope.monthlyDropoutCallback();
        $scope.districtDropoutCallback();
        $scope.facilityDropoutCallback();
    };
    $scope.monthlyDropoutCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.dropout.product) && $scope.filter.dropout.product !== 0) {
            VaccineDashboardMonthlyDropout.get({
                startDate: $scope.startDate,
                endDate: $scope.endDate,
                product: $scope.filter.dropout.product
            }, function (data) {
                $scope.monthlyDropout.dataPoints = dropoutSelector(data.monthlyDropout, $scope.filter.dropout.product);

            });
        }
    };

    $scope.districtDropoutCallback = function () {
        if (!isUndefined($scope.filter.dropout.period) && !isUndefined($scope.filter.dropout.product) && $scope.filter.dropout.product !== 0) {
            VaccineDashboardDistrictDropout.get({
                period: $scope.filter.dropout.period,
                product: $scope.filter.dropout.product
            }, function (data) {
                $scope.districtDropout.data = dropoutSelector(data.districtDropout, $scope.filter.dropout.product);
                if (!isUndefined($scope.districtDropout.data)) {
                    $scope.filter.totalDistrictDropout = $scope.districtDropout.data.length;
                } else {
                    $scope.filter.totalfacilityDropout = 0;
                }

            });
        }
    };

    $scope.facilityDropoutCallback = function () {

        if (!isUndefined($scope.filter.dropout.period) && !isUndefined($scope.filter.dropout.product) && $scope.filter.dropout.product !== 0) {

            VaccineDashboardFacilityTrend.dropout({
                period: $scope.filter.dropout.period,
                product: $scope.filter.dropout.product
            }, function (data) {
                $scope.facilityDropout.data = dropoutSelector(data.facilityDropout, $scope.filter.dropout.product);

                $scope.facilityDropoutPagination();
                if (!isUndefined($scope.facilityDropout.data)) {
                    $scope.filter.totalfacilityDropout = $scope.facilityDropout.data.length;
                } else {
                    $scope.filter.totalfacilityDropout = 0;
                }
            });


        }
    };

    $scope.facilityDropoutPagination = function () {
        var s = parseInt($scope.filter.facilityDropoutOffset, 10) + parseInt($scope.filter.facilityDropoutRange, 10);
        if (!isUndefined($scope.filter.facilityDropoutOffset)) {
            $scope.facilityDropout.dataPoints = $scope.facilityDropout.data.slice(parseInt($scope.filter.facilityDropoutOffset, 10), s);
        }
    };
    $scope.districtDropoutPagination = function () {
        var s = parseInt($scope.filter.districtDropoutOffset, 10) + parseInt($scope.filter.districtRange, 10);
        if (!isUndefined($scope.filter.districtDropoutOffset)) {
            $scope.districtDropout.dataPoints = $scope.districtDropout.data.slice(parseInt($scope.filter.districtDropoutOffset, 10), s);
        }
    };
    $scope.dropoutDetailCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.dropout.product) && $scope.filter.dropout.product !== 0) {

            VaccineDashboardFacilityTrend.dropoutDetails({
                startDate: $scope.startDate,
                endDate: $scope.endDate,
                product: $scope.filter.dropout.product
            }, function (data) {

                $scope.dropoutDetails = dropoutSelector(data.facilityDropoutDetails, $scope.filter.dropout.product);

                $scope.dropoutPeriodsList = _.uniq(_.pluck($scope.dropoutDetails, 'period_name'));
                var facilities = _.uniq(_.pluck($scope.dropoutDetails, 'facility_name'));

                $scope.facilityDropoutDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.dropoutDetails, {facility_name: facility}).district_name;

                    $scope.facilityDropoutDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: messageService.get('label.bcg.mr.dropout'),
                        indicatorValues: $scope.getIndicatorValues(facility, 'bcg_mr_dropout', $scope.dropoutDetails)
                    });
                    $scope.facilityDropoutDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: messageService.get('label.dtp.dropout'),
                        indicatorValues: $scope.getIndicatorValues(facility, 'dtp1_dtp3_dropout', $scope.dropoutDetails)
                    });

                });
                var modalInstance = $modal.open({
                    templateUrl: 'partials/slide-dropout-detail-trend.html',
                    controller: 'DashboardDropoutModalInstanceCtrl',
                    resolve: {
                        items: function () {

                            return {
                                facilityDetails: $scope.facilityDropoutDetails,
                                periodsList: $scope.dropoutPeriodsList
                            };
                        }
                    }
                });
            });

        }
    };
    $scope.openDropoutHelp = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-dropout-help-content.html',
            controller: 'DashboardHelpModalInstanceCtrl',
            resolve: {
                items: function () {

                    return {dashboardHelps: $scope.dashboardHelps};
                }
            }
        });
    };
//////////////
// wastage
//////////////
    $scope.wastageCallback = function () {
        $scope.monthlyWastageCallback();
        $scope.districtWastageCallback();
        $scope.facilityWastageCallback();
    };
    $scope.monthlyWastageCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.wastage.product) && $scope.filter.wastage.product !== 0) {
            VaccineDashboardMonthlyWastage.get({
                startDate: $scope.startDate, endDate: $scope.endDate,
                product: $scope.filter.wastage.product
            }, function (data) {
                $scope.monthlyWastage.dataPoints = data.wastageMonthly;
            });
        }
    };

    $scope.districtWastageCallback = function () {
        if (!isUndefined($scope.filter.wastage.period) && !isUndefined($scope.filter.wastage.product) && $scope.filter.wastage.product !== 0) {
            VaccineDashboardDistrictWastage.get({
                period: $scope.filter.wastage.period,
                product: $scope.filter.wastage.product
            }, function (data) {
                $scope.districtWastage.data = data.districtWastage;
                if (!isUndefined($scope.facilityWastage.data)) {

                    $scope.filter.totalDistrictWastage = $scope.districtWastage.data.length;
                    // $scope.facilityWastagePagination();
                } else {
                    $scope.filter.totalDistrictWastage = 0;
                }
            });
        }
    };

    $scope.facilityWastageCallback = function () {
        if (!isUndefined($scope.filter.wastage.period) && !isUndefined($scope.filter.wastage.product) && $scope.filter.wastage.product !== 0) {
            VaccineDashboardFacilityTrend.wastage({
                period: $scope.filter.wastage.period,
                product: $scope.filter.wastage.product
            }, function (data) {
                $scope.facilityWastage.data = data.facilityWastage;
                if (!isUndefined($scope.facilityWastage.data)) {

                    $scope.filter.totalfacilityWastage = $scope.facilityWastage.data.length;
                    // $scope.facilityWastagePagination();
                } else {
                    $scope.filter.totalfacilityWastage = 0;
                }
            });
        }
    };

    $scope.facilityWastagePagination = function () {
        var s = parseInt($scope.filter.facilityWastageOffset, 10) + parseInt($scope.filter.facilityWastageRange, 10);
        if (!isUndefined($scope.filter.facilityWastageOffset)) {
            $scope.facilityWastage.dataPoints = $scope.facilityWastage.data.slice(parseInt($scope.filter.facilityWastageOffset, 10), s);
        }
    };
    $scope.districtWastagePagination = function () {
        var s = parseInt($scope.filter.districtWastageOffset, 10) + parseInt($scope.filter.districtRange, 10);
        if (!isUndefined($scope.filter.districtWastageOffset)) {
            $scope.districtWastage.dataPoints = $scope.districtWastage.data.slice(parseInt($scope.filter.districtWastageOffset, 10), s);
        }
    };
    $scope.wastageDetailCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.wastage.product) && $scope.filter.wastage.product !== 0) {

            VaccineDashboardFacilityTrend.wastageDetails({
                startDate: $scope.startDate, endDate: $scope.endDate,
                product: $scope.filter.wastage.product
            }, function (data) {

                $scope.wastageDetails = data.facilityWastageDetails;
                $scope.wastagePeriodsList = _.uniq(_.pluck($scope.wastageDetails, 'period_name'));
                var facilities = _.uniq(_.pluck($scope.wastageDetails, 'facility_name'));
                $scope.facilityWastageDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.wastageDetails, {facility_name: facility}).district_name;

                    $scope.facilityWastageDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Wastage Rate',
                        indicatorValues: $scope.getIndicatorValues(facility, 'wastage_rate', $scope.wastageDetails)
                    });
                    $scope.facilityWastageDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Usage Rate',
                        indicatorValues: $scope.getIndicatorValues(facility, 'usage_rate', $scope.wastageDetails)
                    });

                });
                var modalInstance = $modal.open({
                    templateUrl: 'partials/slide-wastage-detail-trend.html',
                    controller: 'DashboardWastageModalInstanceCtrl',
                    resolve: {
                        items: function () {

                            return {
                                facilityDetails: $scope.facilityWastageDetails,
                                periodsList: $scope.wastagePeriodsList
                            };
                        }
                    }
                });
            });

        }
    };
    $scope.openWastageHelp = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-wastage-help-content.html',
            controller: 'DashboardHelpModalInstanceCtrl',
            resolve: {
                items: function () {

                    return {dashboardHelps: $scope.dashboardHelps};
                }
            }
        });
    };

///////////////
// Sessions
///////////////
    $scope.sessionsCallback = function () {
        $scope.monthlySessionsCallback();
        $scope.districtSessionsCallback();
        $scope.facilitySessionsCallback();
    };
    $scope.monthlySessionsCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate)) {
            VaccineDashboardSessions.get({
                startDate: $scope.startDate,
                endDate: $scope.endDate
            }, function (data) {

                $scope.monthlySessions.dataPoints = data.monthlySessions;
            });
        }
    };
    $scope.districtSessionsCallback = function () {
        if (!isUndefined($scope.filter.sessions.period)) {
            VaccineDashboardDistrictSessions.get({period: $scope.filter.sessions.period}, function (data) {

                $scope.districtSessions.data = data.districtSessions;
                if (!isUndefined($scope.districtSessions.data)) {
                    $scope.filter.districtSessions = $scope.districtSessions.data.length;
                } else {
                    $scope.filter.districtSessions = 0;
                }
            });
        }
    };

    $scope.facilitySessionsCallback = function () {
        if (!isUndefined($scope.filter.sessions.period)) {
            VaccineDashboardFacilityTrend.sessions({period: $scope.filter.sessions.period}, function (data) {
                $scope.facilitySessions.data = data.facilitySessions;
                if (!isUndefined($scope.facilitySessions.data)) {
                    $scope.filter.totalFacilitySessions = $scope.facilitySessions.data.length;
                } else {
                    $scope.filter.totalFacilitySessions = 0;
                }
            });


        }
    };

    $scope.facilitySessionsPagination = function () {
        var s = parseInt($scope.filter.facilitySessionsOffset, 10) + parseInt($scope.filter.facilitySessionsRange, 10);
        if (!isUndefined($scope.filter.facilitySessionsOffset)) {
            $scope.facilitySessions.dataPoints = $scope.facilitySessions.data.slice(parseInt($scope.filter.facilitySessionsOffset, 10), s);
        }
    };
    $scope.districtSessionsPagination = function () {
        var s = parseInt($scope.filter.districtSessionsOffset, 10) + parseInt($scope.filter.districtRange, 10);
        if (!isUndefined($scope.filter.districtSessionsOffset)) {
            $scope.districtSessions.dataPoints = $scope.districtSessions.data.slice(parseInt($scope.filter.districtSessionsOffset, 10), s);
        }
    };
    $scope.sessionDetailCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate)) {

            VaccineDashboardFacilityTrend.sessionsDetails({
                startDate: $scope.startDate,
                endDate: $scope.endDate
            }, function (data) {

                $scope.sessionsDetails = data.facilitySessionsDetails;
                $scope.sessionsPeriodsList = _.uniq(_.pluck($scope.sessionsDetails, 'period_name'));
                var facilities = _.uniq(_.pluck($scope.sessionsDetails, 'facility_name'));
                $scope.facilitySessionsDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.sessionsDetails, {facility_name: facility}).district_name;

                    $scope.facilitySessionsDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Fixed Sessions',
                        indicatorValues: $scope.getIndicatorValues(facility, 'fixed_sessions', $scope.sessionsDetails)
                    });
                    $scope.facilitySessionsDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Outreach Sessions',
                        indicatorValues: $scope.getIndicatorValues(facility, 'outreach_sessions', $scope.sessionsDetails)
                    });

                });
                var modalInstance = $modal.open({
                    templateUrl: 'partials/slide-sessions-detail-trend.html',
                    controller: 'DashboardSessionModalInstanceCtrl',
                    resolve: {
                        items: function () {

                            return {
                                facilityDetails: $scope.facilitySessionsDetails,
                                periodsList: $scope.sessionsPeriodsList
                            };
                        }
                    }
                });
            });

        }
    };
    $scope.openSessionsHelp = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-sessions-help-content.html',
            controller: 'DashboardHelpModalInstanceCtrl',
            resolve: {
                items: function () {

                    return {dashboardHelps: $scope.dashboardHelps};
                }
            }
        });
    };

/////////////////
// Stock Status
////////////////

    $scope.monthlyStockCallback = function () {
        if (!isUndefined($scope.filter.monthlyStock.startDate) && !isUndefined($scope.filter.monthlyStock.endDate) && !isUndefined($scope.filter.monthlyStock.product) && $scope.filter.monthlyStock.product !== 0) {
            VaccineDashboardMonthlyStock.get({
                startDate: $scope.filter.monthlyStock.startDate, endDate: $scope.filter.monthlyStock.endDate,
                product: $scope.filter.monthlyStock.product
            }, function (data) {
                $scope.monthlyStock.dataPoints = data.monthlyStock;
            });
        }
    };

    $scope.districtStockCallback = function () {
        if (!isUndefined($scope.filter.districtStock.period) && !isUndefined($scope.filter.districtStock.product) && $scope.filter.districtStock.product !== 0) {
            VaccineDashboardDistrictStock.get({
                period: $scope.filter.districtStock.period,
                product: $scope.filter.districtStock.product
            }, function (data) {
                $scope.districtStock.dataPoints = data.districtStock;
            });
        }
    };

    $scope.facilityStockCallback = function () {

        if (!isUndefined($scope.filter.facilityStock.period) && !isUndefined($scope.filter.facilityStock.product) && $scope.filter.facilityStock.product !== 0) {
            VaccineDashboardFacilityStock.get({
                period: $scope.filter.facilityStock.period,
                product: $scope.filter.facilityStock.product
            }, function (data) {
                $scope.facilityStock.data = data.facilityStock;
                if (!isUndefined($scope.facilityStock.data)) {

                    $scope.filter.totalfacilityStock = $scope.facilityStock.data.length;
                    // $scope.facilityWastagePagination();
                } else {
                    $scope.filter.totalfacilityStock = 0;
                }
            });
        }
    };


    $scope.facilityStockDetailCallback = function () {
        if (!isUndefined($scope.filter.detailStock.startDate) && !isUndefined($scope.filter.detailStock.endDate) && !isUndefined($scope.filter.detailStock.product) && $scope.filter.detailStock.product !== 0) {

            VaccineDashboardFacilityTrend.stockDetails({
                startDate: $scope.filter.detailStock.startDate,
                endDate: $scope.filter.detailStock.endDate,
                product: $scope.filter.detailStock.product
            }, function (data) {

                $scope.stockDetail = data.facilityStockDetail;
                $scope.periodsList = _.uniq(_.pluck(data.facilityStockDetail, 'period_name'));
                var facilities = _.uniq(_.pluck(data.facilityStockDetail, 'facility_name'));

                $scope.facilityStockDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.stockDetail, {facility_name: facility}).district_name;

                    $scope.facilityStockDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Issued',
                        indicatorValues: $scope.getIndicatorValues(facility, 'issued', $scope.stockDetail)
                    });

                    $scope.facilityStockDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Closing Balance',
                        indicatorValues: $scope.getIndicatorValues(facility, 'cb', $scope.stockDetail)
                    });

                });


            });

        }
    };

    $scope.facilityStockPagination = function () {
        var s = parseInt($scope.filter.facilityStockOffset, 10) + parseInt($scope.filter.facilityStockRange, 10);
        if (!isUndefined($scope.filter.facilityStockOffset)) {
            $scope.facilityStock.dataPoints = $scope.facilityStock.data.slice(parseInt($scope.filter.facilityStockOffset, 10), s);
        }
    };

    $scope.formatValue = function (value, ratio, id) {
        return $filter('number')(value);
    };

    $scope.getIndicatorValues = function (facility, indicator, data) {
        var facilityDetail = _.where(data, {facility_name: facility});
        var values = _.pluck(facilityDetail, indicator);
        var tot = _.reduce(values, function (res, num) {
            return res + num;
        }, 0);
        values.push(tot);
        return values;
    };

    $scope.getDetail = function (facility, period) {
        return _.findWhere($scope.stockDetail, {facility_name: facility, period_name: period});
    };


    /* $scope.$watchCollection('[filter.monthlyCoverage.startDate, filter.monthlyCoverage.endDate, filter.monthlyCoverage.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.monthlyCoverage.startDate) &&
     !isUndefined( $scope.filter.monthlyCoverage.endDate) &&
     !isUndefined($scope.filter.monthlyCoverage.product)){
     VaccineDashboardMonthlyCoverage.get({startDate: $scope.filter.monthlyCoverage.startDate, endDate: $scope.filter.monthlyCoverage.endDate,
     product: $scope.filter.monthlyCoverage.product}, function(data){
     $scope.monthlyCoverage.dataPoints = data.monthlyCoverage;
     });
     }
     });*/

    /* $scope.$watchCollection('[filter.districtCoverage.period, filter.districtCoverage.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.districtCoverage.period) &&
     !isUndefined($scope.filter.districtCoverage.product)){
     VaccineDashboardDistrictCoverage.get({period: $scope.filter.districtCoverage.period,
     product: $scope.filter.districtCoverage.product}, function(data){
     $scope.districtCoverage.dataPoints = data.districtCoverage;
     });
     }
     });*/

    /*  $scope.$watchCollection('[filter.monthlyWastage.startDate, filter.monthlyWastage.endDate, filter.monthlyWastage.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.monthlyWastage.startDate) &&
     !isUndefined( $scope.filter.monthlyWastage.endDate) &&
     !isUndefined($scope.filter.monthlyWastage.product)){
     VaccineDashboardMonthlyWastage.get({startDate: $scope.filter.monthlyWastage.startDate, endDate: $scope.filter.monthlyWastage.endDate,
     product: $scope.filter.monthlyWastage.product}, function(data){
     $scope.monthlyWastage.dataPoints = data.wastageMonthly;
     });
     }
     });

     $scope.$watchCollection('[filter.districtWastage.period, filter.districtWastage.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.districtWastage.period) &&
     !isUndefined( $scope.filter.districtWastage.product) ){
     VaccineDashboardDistrictWastage.get({period: $scope.filter.districtWastage.period,  product: $scope.filter.districtWastage.product}, function(data){
     $scope.districtWastage.dataPoints = data.districtWastage;
     });
     }
     });
     */
    /*  $scope.$watchCollection('[filter.monthlyDropout.startDate, filter.monthlyDropout.endDate, filter.monthlyDropout.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.monthlyDropout.startDate) &&
     !isUndefined( $scope.filter.monthlyDropout.endDate) &&
     !isUndefined($scope.filter.monthlyDropout.product)){
     VaccineDashboardMonthlyDropout.get({startDate: $scope.filter.monthlyDropout.startDate, endDate: $scope.filter.monthlyDropout.endDate,
     product: $scope.filter.monthlyDropout.product}, function(data){
     $scope.monthlyDropout.dataPoints = data.monthlyDropout;
     });
     }
     });

     $scope.$watchCollection('[filter.districtDropout.period, filter.districtDropout.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.districtDropout.period) &&
     !isUndefined($scope.filter.districtDropout.product)){
     VaccineDashboardDistrictDropout.get({period: $scope.filter.districtDropout.period,
     product: $scope.filter.districtDropout.product}, function(data){
     $scope.districtDropout.dataPoints = data.districtDropout;
     });
     }
     });

     $scope.$watchCollection('[filter.monthlyStock.startDate, filter.monthlyStock.endDate, filter.monthlyStock.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.monthlyStock.startDate) &&
     !isUndefined( $scope.filter.monthlyStock.endDate) &&
     !isUndefined($scope.filter.monthlyStock.product)){
     VaccineDashboardMonthlyStock.get({startDate: $scope.filter.monthlyStock.startDate, endDate: $scope.filter.monthlyStock.endDate,
     product: $scope.filter.monthlyStock.product}, function(data){
     $scope.monthlyStock.dataPoints = data.monthlyStock;
     });
     }
     });

     $scope.$watchCollection('[filter.districtStock.period, filter.districtStock.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.districtStock.period) &&
     !isUndefined( $scope.filter.districtStock.product) ){
     VaccineDashboardDistrictStock.get({period: $scope.filter.districtStock.period,  product: $scope.filter.districtStock.product}, function(data){
     $scope.districtStock.dataPoints = data.districtStock;
     });
     }
     });


     $scope.$watchCollection('[filter.monthlySessions.startDate, filter.monthlySessions.endDate]', function(newValues, oldValues){

     if(!isUndefined( $scope.filter.monthlySessions.startDate) && !isUndefined( $scope.filter.monthlySessions.endDate)){
     VaccineDashboardSessions.get({startDate: $scope.filter.monthlySessions.startDate, endDate: $scope.filter.monthlySessions.endDate}, function(data){

     $scope.monthlySessions.dataPoints =   data.monthlySessions;
     });
     }
     });


     $scope.$watchCollection('[filter.bundling.startDate, filter.bundling.endDate, filter.bundling.product]', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.bundling.startDate) &&
     !isUndefined( $scope.filter.bundling.endDate) &&
     !isUndefined($scope.filter.bundling.product)){
     VaccineDashboardBundle.get({startDate: $scope.filter.bundling.startDate, endDate: $scope.filter.bundling.endDate,
     product: $scope.filter.bundling.product}, function(data){
     $scope.bundling.dataPoints = data.bundling;
     });
     }
     });

     $scope.$watch('filter.districtSessions.period', function(newValues, oldValues){
     if(!isUndefined( $scope.filter.districtSessions.period) ) {
     VaccineDashboardDistrictSessions.get({period: $scope.filter.districtSessions.period}, function(data){

     $scope.districtSessions.dataPoints =   data.districtSessions;
     });
     }

     });

     */
//////////////////
//  Stock Status
///////////////////
    $scope.stockStatusCallBack = function () {
        $scope.monthlyStockStatusCallback();
        $scope.districtStockStatusCallback();
        $scope.facilityStockStatusCallback();

    };
    $scope.monthlyStockStatusCallback = function () {

        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.stockstatus.product) && $scope.filter.stockstatus.product !== 0) {

            VaccineDashboardMonthlyStockStatus.get({
                startDate: $scope.startDate, endDate: $scope.endDate,
                product: $scope.filter.stockstatus.product
            }, function (data) {
                $scope.monthlyStockstatus.dataPoints = data.monthlyStockStatus;

            });

        }
    };

    $scope.districtStockStatusCallback = function () {
        if (!isUndefined($scope.filter.stockstatus.period) && !isUndefined($scope.filter.stockstatus.product) && $scope.filter.stockstatus.product !== 0) {
            VaccineDashboardDistrictStockStatus.get({
                period: $scope.filter.stockstatus.period,
                product: $scope.filter.stockstatus.product
            }, function (data) {
                $scope.districtStockStatus.data = data.districtStockStatus;

                if (!isUndefined($scope.districtStockStatus.data)) {
                    $scope.filter.totalDistrictStockStatus = $scope.districtStockStatus.data.length;
                } else {
                    $scope.filter.totalDistrictStockStatus = 0;
                }
            });
        }
    };

    $scope.facilityStockStatusCallback = function () {
        if (!isUndefined($scope.filter.stockstatus.period) && !isUndefined($scope.filter.stockstatus.product) && $scope.filter.stockstatus.product !== 0) {
            //VaccineDashboardFacilityCoverage.get({period: $scope.filter.facilityCoverage.period,
            VaccineDashboardFacilityStockStatus.get({
                period: $scope.filter.stockstatus.period,
                product: $scope.filter.stockstatus.product
            }, function (data) {
                $scope.facilityStockstatus.data = data.facilityStockStatus;
                if (!isUndefined($scope.facilityStockstatus.data)) {
                    $scope.filter.totalFacilityStockStatus = $scope.facilityStockstatus.data.length;
                } else {
                    $scope.filter.totalFacilityStockStatus = 0;
                }
            });


        }
    };

    $scope.facilityStockPagination = function () {
        var s = parseInt($scope.filter.facilityStockStatusOffset, 10) + parseInt($scope.filter.facilityStockStatusRange, 10);
        if (!isUndefined($scope.filter.facilityStockStatusOffset)) {
            $scope.facilityStockstatus.dataPoints = $scope.facilityStockstatus.data.slice(parseInt($scope.filter.facilityStockStatusOffset, 10), s);
        }
    };
    $scope.districtStockStatusPagination = function () {
        var s = parseInt($scope.filter.districtStockstatusOffset, 10) + parseInt($scope.filter.districtRange, 10);
        if (!isUndefined($scope.filter.districtStockstatusOffset)) {
            $scope.districtStockStatus.dataPoints = $scope.districtStockStatus.data.slice(parseInt($scope.filter.districtStockstatusOffset, 10), s);

        }
    };
    $scope.stockStatusDetailCallback = function () {
        if (!isUndefined($scope.startDate) && !isUndefined($scope.endDate) && !isUndefined($scope.filter.stockstatus.product) && $scope.filter.stockstatus.product !== 0) {
            // VaccineDashboardFacilityCoverageDetails.get({startDate: $scope.filter.detailCoverage.startDate, endDate: $scope.filter.detailCoverage.endDate,
            VaccineDashboardFacilityStockStatusDetails.get({
                startDate: $scope.startDate, endDate: $scope.endDate,
                product: $scope.filter.stockstatus.product
            }, function (data) {

                $scope.stockstatusDetails = data.facilityStockStatusDetails;
                $scope.stockstatusPeriodsList = _.uniq(_.pluck($scope.stockstatusDetails, 'period_name'));
                var facilities = _.uniq(_.pluck($scope.stockstatusDetails, 'facility_name'));

                $scope.facilityStockStatusDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.stockstatusDetails, {facility_name: facility}).district_name;

                    $scope.facilityStockStatusDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Max MOS',
                        indicatorValues: $scope.getIndicatorValues(facility, 'max', $scope.stockstatusDetails)
                    });
                    $scope.facilityStockStatusDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'MIN MOS',
                        indicatorValues: $scope.getIndicatorValues(facility, 'min', $scope.stockstatusDetails)
                    });
                    $scope.facilityStockStatusDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'MOS',
                        indicatorValues: $scope.getIndicatorValues(facility, 'mos', $scope.stockstatusDetails)
                    });

                });
                var modalInstance = $modal.open({
                    templateUrl: 'partials/slide-stock-status-detail-trend.html',
                    controller: 'DashboardStockStatusModalInstanceCtrl',
                    resolve: {
                        items: function () {

                            return {
                                facilityDetails: $scope.facilityStockStatusDetails,
                                periodsList: $scope.stockstatusPeriodsList
                            };
                        }
                    }
                });
            });

        }
    };
    $scope.openStockStatusHelp = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-stock-status-help-content.html',
            controller: 'DashboardHelpModalInstanceCtrl',
            resolve: {
                items: function () {

                    return {dashboardHelps: $scope.dashboardHelps};
                }
            }
        });
    };


    SettingsByKey.get({key: 'DASHBOARD_SLIDES_TRANSITION_INTERVAL_MILLISECOND'}, function (data) {
        $scope.defaultSlideTransitionInterval = data.settings.value;
        $scope.consumptionSlideInterval = $scope.stockSlideInterval = $scope.lossesSlideInterval = $scope.defaultSlideTransitionInterval;


        var carousel = function (id) {
            return {
                id: id,
                interval: $scope.defaultSlideTransitionInterval,
                isPlaying: function () {
                    return this.interval >= 0;
                },
                play: function () {
                    this.interval = $scope.defaultSlideTransitionInterval;
                    this.isPlaying = true;
                },
                pause: function () {
                    this.interval = -1;
                    this.isPlaying = false;
                }
            };
        };

        $scope.carousels = [carousel('trend'), carousel('district'), carousel('facility')];
    });


    $scope.setInterval = function (carouselId) {
        var cr = _.findWhere($scope.carousels, {id: carouselId});
        if (!isUndefined(cr)) {
            return cr.interval;
        }
        return -1;
    };

    $scope.OnFilterChanged = function () {

        $scope.data = $scope.datarows = [];
        // $scope.filter.max = 10000;
    };


    VaccineDashboardSummary.get({}, function (data) {
        $scope.reportingPerformance = data.summary.reportingSummary;

        $scope.repairing = data.summary.repairing;
        $scope.investigating = data.summary.investigating;
    });


    $scope.reportingPerformance = {};

    $scope.repairing = {};
    $scope.supplying = {};
    $scope.investigating = {};
    $scope.filter = {sessions: {}};

    $scope.datapoints = [];
    $scope.datacolumns = [{
        "id": "outreach_sessions",
        "name": messageService.get('label.outreach.sessions'),
        "type": "bar"
    },
        {"id": "fixed_sessions", "name": messageService.get('label.outreach.sessions'), "type": "bar"}
    ];

    $scope.datax = {"id": "period_name"};
    $scope.items = ['item1', 'item2', 'item3'];

    $scope.animationsEnabled = true;

    $scope.openDetailDialog = function (size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'myModalContent.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {
                    var list = reportingDetailList.reportingDetails;
                    for (i = 0; i < list.length; i++) {
                        list[i].checked = false;
                    }

                    return list;
                }
            }
        });
        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.openMonthlyCoverageDetailDialog = function () {

        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-coverage-detail-trend.html',
            controller: 'DashboardModalInstanceCtrl'
        });
    };
    $scope.openRepairingDetailDialog = function (size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'repairing.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {

                    return reparingDetailList.repairingDetails;
                }
            }
        });
    };
    $scope.openInvestigatingDetailDialog = function (size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'investigating.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {

                    return investigatingDetailList.investigatingDetails;
                }
            }
        });
        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
    $scope.toggleAnimation = function () {
        $scope.animationsEnabled != $scope.animationsEnabled;
    };
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';

        if (_index % 2 === 0) {
            bgColor = 'red';
        } else if (value > 10) {
            bgColor = 'white';
        }

        return bgColor;
    };
    function dropoutSelector(dropoutList, selectedProduct) {
        $scope.bcgMRDropoutId = '2412';
        $scope.dtpDropoutId = '2421';
        var data = [];
        var len = dropoutList.length;


        if (dropoutList, selectedProduct === $scope.dtpDropoutId) {

            bcgDropout = messageService.get('label.dtp.dropout');
            bcgVaccinated = messageService.get('label.dtp1.vaccinated');
            mrVaccinated = messageService.get('label.dtp3.vaccinated');


            for (var i = 0; i < len; i++) {
                dropoutList[i].bcg_vaccinated = dropoutList[i].dtp1_vaccinated;
                dropoutList[i].mr_vaccinated = dropoutList[i].dtp3_vaccinated;
                dropoutList[i].bcg_mr_dropout = dropoutList[i].dtp1_dtp3_dropout;
            }
        } else {
            bcgDropout = messageService.get('label.bcg.mr.dropout');
            bcgVaccinated = messageService.get('label.bcg.vaccinated');
            mrVaccinated = messageService.get('label.mr.vaccinated');
        }


        return dropoutList;
    }

}
VaccineDashboardController.resolve = {
    userPreferences: function ($q, $timeout, VaccineCurrentPeriod, UserGeographicZonePereference) {
        var deferred = $q.defer();
        var user_preferences = {};
        $timeout(function () {
            VaccineCurrentPeriod.get({}, function (data) {
                if (!utils.isNullOrUndefined(data.vaccineCurrentPeriod)) {
                    user_preferences.period_name = data.vaccineCurrentPeriod.name;
                    user_preferences.period_id = data.vaccineCurrentPeriod.current_period;
                    user_preferences.startdate = data.vaccineCurrentPeriod.startdate;
                } else {
                    user_preferences.period_name = "";
                    user_preferences.period_id = "";
                    user_preferences.startdate ="";
                }

            });
            UserGeographicZonePereference.get({}, function (data) {
                if (!utils.isNullOrUndefined(data.UserGeographicZonePreference)) {
                    user_preferences.zone_name = data.UserGeographicZonePreference.zone_name;
                    user_preferences.level_name = data.UserGeographicZonePreference.level_name;
                } else {
                    user_preferences.zone_name = "";
                    user_preferences.level_name = "";
                }

            });

            deferred.resolve(user_preferences);
        }, 100);

        return deferred.promise;
    },
    colors: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        var color_values = {};
        $timeout(function () {
            SettingsByKey.get({key: 'VCP_GREEN'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.green_color = data.settings.value;
                } else {
                    color_values.green_color = 'green';
                }

            });
            SettingsByKey.get({key: 'VCP_BLUE'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.blue_color = data.settings.value;
                } else {
                    color_values.blue_color = 'blue';
                }

            });
            SettingsByKey.get({key: 'VCP_RED'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.red_color = data.settings.value;
                } else {
                    color_values.blue_color = 'red';
                }

            });
            deferred.resolve(color_values);
        }, 100);

        return deferred.promise;
    },

    dashboardSlidesHelp: function ($q, $timeout, HelpContentByKey, messageService) {

        var deferred = $q.defer();
        var helps = {};
        $timeout(function () {
            HelpContentByKey.get({content_key: 'Coverage Dashboard'}, function (data) {


                if (!isUndefined(data.siteContent)) {

                    helps.coverageHelp = data.siteContent;

                } else {

                    helps.coverageHelp = {htmlContent: messageService.get('content.help.default')};

                }

            });
            HelpContentByKey.get({content_key: 'Wastage Dashboard'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.wastageHelp = data.siteContent;

                } else {

                    helps.wastageHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'Sessions Dashboard'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.sessionsHelp = data.siteContent;

                } else {

                    helps.sessionsHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'Dropout Dashboard'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.dropoutHelp = data.siteContent;

                } else {

                    helps.dropoutHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'Stockhelp'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.stockHelp = data.siteContent;

                } else {

                    helps.stockHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'StockStatushelp'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.stockStatusHelp = data.siteContent;

                } else {

                    helps.stockStatusHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });

            deferred.resolve(helps);

        }, 100);
        return deferred.promise;
    },
    settingValues: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        var settings = {};

        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_LATE_REPORTING_DAYS'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    settings.cuttoff = data.settings.value;
                } else {
                    settings.cuttoff = 10;
                }

            });
            SettingsByKey.get({key: 'VCP_DASHBOARD_MONTHS_RANGE'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings) && !utils.isNullOrUndefined(data.settings.value)) {
                    settings.monthsRange = data.settings.value;
                } else {
                    settings.monthsRange = 6;
                }

              });

           deferred.resolve(settings);
        }, 100);
        return deferred.promise;
    },
    defaultProduct: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_DASHBOARD_DEFAULT_PRODUCT'}, function (data) {

                if (isUndefined(data.settings) || isUndefined(data.settings.value)) {

                    deferred.resolve(0);
                } else {

                    deferred.resolve(data.settings.value);
                }

            });

        }, 100);

        return deferred.promise;

    },
    defaultPeriodTrend: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_DASHBOARD_DEFAULT_PERIOD_TREND'}, function (data) {
                if (isUndefined(data.settings) || isUndefined(data.settings.value)) {
                    deferred.resolve(1);
                } else {

                    deferred.resolve(data.settings.value);
                }

            });

        }, 100);

        return deferred.promise;

    },
    defaultMonthlyPeriod: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();

        $timeout(function () {

            SettingsByKey.get({key: 'VACCINE_DASHBOARD_DEFAULT_MONTHLY_PERIOD'}, function (data) {
                if (isUndefined(data.settings) || isUndefined(data.settings.value)) {
                    deferred.resolve(0);
                } else {

                    deferred.resolve(data.settings.value);
                }

            });

        }, 100);

        return deferred.promise;

    },
    reportingDetailList: function ($q, $timeout, reportingDetail) {
        var deferred = $q.defer();
        $timeout(function () {

            reportingDetail.get(function (data) {

                deferred.resolve(data);


            });

        }, 100);

        return deferred.promise;

    },
    reparingDetailList: function ($q, $timeout, repairingDetail) {
        var deferred = $q.defer();
        $timeout(function () {

            repairingDetail.get(function (data) {


                deferred.resolve(data);


            });

        }, 100);

        return deferred.promise;

    },
    investigatingDetailList: function ($q, $timeout, InvestigatingDetails) {
        var deferred = $q.defer();
        $timeout(function () {

            InvestigatingDetails.get(function (data) {

                deferred.resolve(data);


            });

        }, 100);

        return deferred.promise;

    },

    isDistrictUser: function ($q, $timeout, IsDistrictUser) {
        var deferred = $q.defer();
        $timeout(function () {

            IsDistrictUser.get(function (data) {

                deferred.resolve(data);


            });

        }, 100);

        return deferred.promise;

    }

};
