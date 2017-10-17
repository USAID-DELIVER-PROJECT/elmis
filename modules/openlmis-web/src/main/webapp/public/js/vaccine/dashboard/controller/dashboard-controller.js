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

function VaccineDashboardController($scope, $q, $timeout, VaccineDashboardSummary, $filter, VaccineDashboardMonthlyCoverage,
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
                                    InvestigatingDetails,
                                    investigatingDetailList,
                                    ContactList, isDistrictUser,
                                    VaccineDashboardFacilityStock,
                                    settingValues, $log,
                                    VaccineDashboardMonthlyStockStatus,
                                    VaccineDashboardDistrictStockStatus,
                                    VaccineDashboardFacilityStockStatus,
                                    VaccineDashboardFacilityStockStatusDetails, colors,
                                    userPreferences,
                                    VaccineDashboardFacilityInventoryStockStatus, homeFacility,
                                    VaccineDashboardSupervisedFacilityInventoryStockStatus,
                                    EquipmentNonFunctional,
                                    VaccinePendingRequisitions,
                                    daysNotReceive,
                                    batchToExpireNotification,
                                    VaccineInventorySummaryData,
                                    VaccineInventorySummaryDetails,
                                    receiveNotification,receiveDistributionDetailList,
                                    minimumStockNotification
) {
    $scope.actionBar = {openPanel: true};
    $scope.performance = {openPanel: false};
    $scope.coverage = {openPanel: false};
    $scope.dropout = {openPanel: false};
    $scope.wastage = {openPanel: false};
    $scope.stockStatus = {openPanel: false};
    $scope.stockFacilityStatus = {openPanel: false};
    $scope.vaccineInventory = {openPanel: false};
    $scope.homeFacility = homeFacility;
    $scope.sessions = {
        openPanel: true
    };
    $scope.coverage = {loadData: false};
    $scope.dropout = {loadData: false};
    $scope.wastage = {loadData: false};
    $scope.stockStatus = {loadData: false};
    $scope.stockFacilityStatus = {loadData: false};
    $scope.sessions = {
        loadData: false
    };
    $scope.vaccineInventory = {loadData:false};

    $scope.expandAllTabs = function (value) {
        $scope.actionBar = {openPanel: value};
        $scope.performance = {openPanel: value};
        $scope.coverage = {openPanel: value};
        $scope.dropout = {openPanel: value};
        $scope.wastage = {openPanel: value};
        $scope.stockStatus = {openPanel: value};
        $scope.stockFacilityStatus = {openPanel: value};
        $scope.vaccineInventory = {openPanel: value};

        $scope.sessions = {
            openPanel: value
        };
        $scope.stockFacilityStatusTabCliced();
        $scope.wastageTabCliced();
        $scope.dropoutTabCliced();
        $scope.coverageTabCliced();
        $scope.sessionsTabCliced();
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
    $scope.daysForUnreceivedNotification = daysNotReceive;
    $scope.receiveNotification = receiveNotification;
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
    $scope.coverageTabCliced = function () {
        $scope.coverage.loadData = !$scope.coverage.loadData;
        $scope.coverageCallBack();
    };
    $scope.coverageCallBack = function () {

        if ($scope.coverage.loadData === true) {

            $scope.monthlyCoverageCallback();
            $scope.districtCoverageCallback();
            $scope.facilityCoverageCallback();
        }

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
                    $scope.districtCoveragePagination();
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
        else if($scope.filter.totalFacilityCoverage > 0)
            $scope.districtCoverage.dataPoints = $scope.districtCoverage.data;
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
                var facilities = _.uniq(_.pluck(data.facilityCoverageDetails, 'key_val'));
                $scope.addPlaceHolderFornonExistingPeriodData($scope.coverageDetails, $scope.periodsList, facilities);

                $scope.facilityDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.coverageDetails, {key_val: facility}).district_name;
                    var facilityName = _.findWhere($scope.coverageDetails, {key_val: facility}).facility_name;
                    $scope.facilityDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'target',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'target', $scope.coverageDetails)
                    });
                    $scope.facilityDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'actual',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'actual', $scope.coverageDetails)
                    });
                    $scope.facilityDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'coverage',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'coverage', $scope.coverageDetails)
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
    $scope.dropoutTabCliced = function () {
        $scope.dropout.loadData = !$scope.dropout.loadData;
        $scope.dropoutCallback();
    };
    $scope.dropoutCallback = function () {

        if ($scope.dropout.loadData === true) {
            $scope.monthlyDropoutCallback();
            $scope.districtDropoutCallback();
            $scope.facilityDropoutCallback();
        }
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
                var facilities = _.uniq(_.pluck($scope.dropoutDetails, 'key_val'));
                $scope.addPlaceHolderFornonExistingPeriodData($scope.dropoutDetails, $scope.dropoutPeriodsList, facilities);
                $scope.facilityDropoutDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.dropoutDetails, {key_val: facility}).district_name;
                    var facilityName = _.findWhere($scope.dropoutDetails, {key_val: facility}).facility_name;
                    $scope.facilityDropoutDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: messageService.get('label.bcg.mr.dropout'),
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'bcg_mr_dropout', $scope.dropoutDetails)
                    });
                    $scope.facilityDropoutDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: messageService.get('label.dtp.dropout'),
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'dtp1_dtp3_dropout', $scope.dropoutDetails)
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
    $scope.wastageTabCliced = function () {
        $scope.wastage.loadData = !$scope.wastage.loadData;
        $scope.wastageCallback();
    };
    $scope.wastageCallback = function () {

        if ($scope.wastage.loadData === true) {
            $scope.monthlyWastageCallback();
            $scope.districtWastageCallback();
            $scope.facilityWastageCallback();
        }
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
                var facilities = _.uniq(_.pluck($scope.wastageDetails, 'key_val'));
                $scope.addPlaceHolderFornonExistingPeriodData($scope.wastageDetails, $scope.wastagePeriodsList, facilities);
                $scope.facilityWastageDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.wastageDetails, {key_val: facility}).district_name;
                    var facilityName = _.findWhere($scope.wastageDetails, {key_val: facility}).facility_name;
                    $scope.facilityWastageDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'Wastage Rate',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'wastage_rate', $scope.wastageDetails)
                    });
                    $scope.facilityWastageDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'Usage Rate',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'usage_rate', $scope.wastageDetails)
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
    $scope.sessionsTabCliced = function () {
        $scope.sessions.loadData = !$scope.sessions.loadData;
        $scope.sessionsCallback();
    };
    $scope.sessionsCallback = function () {

        if ($scope.sessions.loadData === true) {
            $scope.monthlySessionsCallback();
            $scope.districtSessionsCallback();
            $scope.facilitySessionsCallback();
        }
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
                var facilities = _.uniq(_.pluck($scope.sessionsDetails, 'key_val'));
                $scope.addPlaceHolderFornonExistingPeriodData($scope.sessionsDetails, $scope.sessionsPeriodsList, facilities);
                $scope.facilitySessionsDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.sessionsDetails, {key_val: facility}).district_name;
                    var facilityName = _.findWhere($scope.sessionsDetails, {key_val: facility}).facility_name;
                    $scope.facilitySessionsDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'Fixed Sessions',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'fixed_sessions', $scope.sessionsDetails)
                    });
                    $scope.facilitySessionsDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'Outreach Sessions',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'outreach_sessions', $scope.sessionsDetails)
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

    //////////////
    //My Stock
    //////////////

    $scope.myStockVaccine = {
        dataPoints: [],
        dataColumns: [
            {"id": "mos", "name": messageService.get('label.mos'), "type": "bar"}
        ],
        dataX: {"id": "product"},
        productCategory: "Vaccines",
        legend: [{"label": "insufficient stock", "color": colors.insufficient_color},
            {"label": "re-order level", "color": colors.reorder_color},
            {"label": "sufficient stock", "color": colors.sufficient_color},
            {"label": "over stock", "color": colors.overstock_color}]
    };

    $scope.myStockSupplies = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"
        }
        ],
        dataX: {"id": "product"},
        productCategory: "Supplies",
        legend: [{"label": "insufficient stock", "color": colors.insufficient_color},
            {"label": "re-order level", "color": colors.reorder_color},
            {"label": "sufficient stock", "color": colors.sufficient_color},
            {"label": "over stock", "color": colors.overstock_color}]
    };

    $scope.mySupervisedFacilityStock = {
        dataPoints: [],
        dataColumns: [{
            "id": "mos", "name": messageService.get('label.mos'), "type": "bar"
        }
        ],
        dataX: {"id": "facility_name"},
        productCategory: messageService.get('label.facilities'),
        legend: [{"label": "insufficient stock", "color": colors.insufficient_color},
            {"label": "re-order level", "color": colors.reorder_color},
            {"label": "sufficient stock", "color": colors.sufficient_color},
            {"label": "over stock", "color": colors.overstock_color}]
    };

    $scope.supplyingPendingOrdersDetailCallback = function () {
        VaccinePendingRequisitions.get({facilityId: parseInt(homeFacility.id, 10)}, function (data) {
            $scope.supplyingPendingReceive = {};
            $scope.supplyingAllPendingOrders = data.pendingRequest;
            $scope.supplyingPendingReceive.supplyingPendingToReceive = data.pendingToReceive;
            $scope.supplyingPendingReceive.supplyingPendingToReceiveLowerLevel = data.pendingToReceiveLowerLevel;
            $scope.supplyingPendingReceive.daysForUnreceivedNotification= daysNotReceive;

            if (data.pendingRequest !== undefined)
                $scope.supplying.orders = data.pendingRequest.length;
            else {
                $scope.supplying.orders = 0;
            }
        });
    };
    $scope.equipmentActionBarCallback = function () {
        EquipmentNonFunctional.get({}, function (data) {
            $scope.nonFunctionalEquipments = data.Alerts;
            if (!isUndefined(data.Alerts)) {
                $scope.totalNonFunctionalEquipments = $scope.nonFunctionalEquipments.length;

                var byStatus = _.groupBy($scope.nonFunctionalEquipments, function (e) {
                    return e.status;
                });
                $scope.allNonFunctionalEquipmentsByStatus = $.map(byStatus, function (value, index) {
                    return [{"status": index, "data": value}];
                });
            }
        });
    };


    $scope.batchToExpireNotificationCallBack = function () {

        $scope.batchToExpire = batchToExpireNotification;
        $scope.totalBatchToExpire = $scope.batchToExpire.length;
    };


    $scope.supplyingPendingOrdersDetailCallback();
    $scope.equipmentActionBarCallback();
    $scope.batchToExpireNotificationCallBack();

    $scope.stockStatusTabClicked=function(){
       $scope.stockStatus.loadData = !$scope.stockStatus.loadData;
       $scope.stockCallbacks();
    };

    $scope.stockCallbacks=function(){
        if($scope.stockStatus.loadData){
           $scope.facilityInventoryStockStatusCallback($scope.myStockFilter);
           $scope.mySupervisedFacilitiesCallback($scope.mySupervisedFilter);
        }
    };
    $scope.facilityInventoryStockStatusCallback = function (myStock) {
         $scope.myStockFilter=myStock;

        if (!isUndefined(homeFacility.id) && !isUndefined(myStock.toDate) && $scope.stockStatus.loadData) {
            VaccineDashboardFacilityInventoryStockStatus.get({
                facilityId: parseInt(homeFacility.id, 10),
                date: myStock.toDate
            }, function (data) {
                if (data.facilityStockStatus !== null) {
                    var allProducts = data.facilityStockStatus;
                    var byCategory = _.groupBy(allProducts, function (p) {
                        return p.product_category;
                    });
                    $scope.allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                        return [{"productCategory": index, "dataPoints": value}];
                    });
                    $scope.myStockVaccine.dataPoints = $scope.allStockDataPointsByCategory[0].dataPoints;
                    $scope.myStockVaccine.productCategory = $scope.allStockDataPointsByCategory[0].productCategory;

                    $scope.myStockSupplies.dataPoints = $scope.allStockDataPointsByCategory[1].dataPoints;
                    $scope.myStockSupplies.productCategory = $scope.allStockDataPointsByCategory[1].productCategory;


                }
            });
        }
    };

    $scope.mySupervisedFacilityFilterSize = 5;
    $scope.mySupervisedFacilitiesCallback = function (filter) {
        $scope.mySupervisedFilter=filter;
        if ($scope.stockStatus.loadData && !isUndefined(filter.product) && filter.product !== "0" && !isUndefined(filter.date) && !isUndefined(filter.level) && filter.level !== "0") {
            VaccineDashboardSupervisedFacilityInventoryStockStatus.get({
                    productId: filter.product,
                    date: filter.date,
                    level: filter.level
                },
                function (data) {
                    if (!isUndefined(data.facilityStockStatus)) {
                        $scope.mySupervisedFilterTotal = data.facilityStockStatus.length;
                        $scope.mySupervisedFacilityStock.data = data.facilityStockStatus;
                        $scope.mySupervisedFacilityStock.productCategory = data.facilityStockStatus[0].product;
                        $scope.mySupervisedFacilitiesPagination(0);
                    }
                    else {
                        $scope.mySupervisedFacilityStock.data = [];
                        $scope.mySupervisedFilterTotal = 0;
                    }
                });
        }
    };
    $scope.mySupervisedFacilitiesPagination = function (offset) {
        var s = parseInt($scope.mySupervisedFacilityFilterSize, 10) + parseInt(offset, 10);
        if (!isUndefined(offset)) {
            $scope.mySupervisedFacilityStock.dataPoints = $scope.mySupervisedFacilityStock.data.slice(parseInt(offset, 10), s);
        }
    };


    $scope.getSOHVaccine = function (value, ratio, id, index) {
        var toolTipValue = value + " | " + $filter('number')($scope.myStockVaccine.dataPoints[index].soh) + " " + $scope.myStockVaccine.dataPoints[index].unity_of_measure;
        return toolTipValue;
    };
    $scope.getColorVaccine = function (color, d) {
        if (typeof d === 'object') {
            return $scope.myStockVaccine.dataPoints[d.index].color;
        }
    };

    $scope.getSOHSupplies = function (value, ratio, id, index) {
        var toolTipValue = value + " | " + $filter('number')($scope.myStockSupplies.dataPoints[index].soh) + " " + $scope.myStockSupplies.dataPoints[index].unity_of_measure;
        return toolTipValue;
    };
    $scope.getColorSupplies = function (color, d) {
        if (typeof d === 'object') {
            return $scope.myStockSupplies.dataPoints[d.index].color;
        }
    };

    $scope.getSupervisedSOHTooltip = function (value, ratio, id, index) {
        var toolTipValue = value + " | " + $filter('number')($scope.mySupervisedFacilityStock.dataPoints[index].soh) + " " + $scope.mySupervisedFacilityStock.dataPoints[index].unity_of_measure;
        return toolTipValue;
    };
    $scope.getColorSupervised = function (color, d) {
        if (typeof d === 'object') {
            return $scope.mySupervisedFacilityStock.dataPoints[d.index].color;
        }
    };

    $scope.openMyStockHelp = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-mystock-help-content.html',
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
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'issued', $scope.stockDetail)
                    });

                    $scope.facilityStockDetails.push({
                        district: district,
                        facilityName: facility,
                        indicator: 'Closing Balance',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'cb', $scope.stockDetail)
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

    $scope.getIndicatorValues = function (district, facility, indicator, data) {

        var facilityDetail = _.where(data, {key_val: facility});
        var values = _.pluck(facilityDetail, indicator);
        var tot = _.reduce(values, function (res, num) {
            res = utils.isNullOrUndefined(res) ? 0 : res;
            num = utils.isNullOrUndefined(num) ? 0 : num;
            return res + num;
        }, 0);
        values.push(tot);
        return values;
    };
    $scope.addPlaceHolderFornonExistingPeriodData = function (data, periods, facilities) {
        var i;
        var j;

        for (i = 0; i < periods.length; i++) {
            var period = periods[i];
            for (j = 0; j < facilities.length; j++) {
                var facility = facilities[j];
                var facilityObj = _.findWhere(data, {key_val: facility});
                var facilityDetail = _.findWhere(data, {key_val: facility, period_name: period});

                if (utils.isNullOrUndefined(facilityDetail)) {
                    data.push({
                        facility_name: facilityObj.facility_name,
                        district_name: facilityObj.district_name,
                        key_val: facility,
                        period_name: period
                    });
                }


            }
        }
        data = _.sortBy(data, 'period_start_date');
    };
    $scope.getDetail = function (facility, period) {
        return _.findWhere($scope.stockDetail, {facility_name: facility, period_name: period});
    };


//////////////////
//  Stock Status
///////////////////
    $scope.stockFacilityStatusTabCliced = function () {
        $scope.stockFacilityStatus.loadData = !$scope.stockFacilityStatus.loadData;
        $scope.stockStatusCallBack();
    };
    $scope.stockStatusCallBack = function () {
        if ($scope.stockFacilityStatus.loadData === true) {
            $scope.monthlyStockStatusCallback();
            $scope.districtStockStatusCallback();
            $scope.facilityStockStatusCallback();
        }

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
                var facilities = _.uniq(_.pluck($scope.stockstatusDetails, 'key_val'));
                $scope.addPlaceHolderFornonExistingPeriodData($scope.stockstatusDetails, $scope.stockstatusPeriodsList, facilities);
                $scope.facilityStockStatusDetails = [];
                angular.forEach(facilities, function (facility) {
                    var district = _.findWhere($scope.stockstatusDetails, {key_val: facility}).district_name;
                    var facilityName = _.findWhere($scope.stockstatusDetails, {key_val: facility}).facility_name;
                    $scope.facilityStockStatusDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'Max MOS',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'max', $scope.stockstatusDetails)
                    });
                    $scope.facilityStockStatusDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'MIN MOS',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'min', $scope.stockstatusDetails)
                    });
                    $scope.facilityStockStatusDetails.push({
                        district: district,
                        facilityName: facilityName,
                        indicator: 'MOS',
                        indicatorValues: $scope.getIndicatorValues(district, facility, 'mos', $scope.stockstatusDetails)
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
    //Vaccine Inventory


    var vaccineInventoryData = VaccineInventorySummaryData;
 console.log(vaccineInventoryData);
    $scope.vaccineInventorySummaryData = {

        dataPoints:vaccineInventoryData,
        dataColumns: [
            {"id": "overstock", "name":"overstock", "type": "donut"},
            {"id": 'sufficient', "name":"sufficient", "type": "donut"},
            {"id": "minimum", "name":"Understock", "type": "donut"},
            {"id": "zero", "name":"Zero stock", "type": "donut"}
        ]
    };



    $scope.clicked = {};
    $scope.showClick = function (data) {
        console.log(data);
        $scope.clicked = data;
        dataV();
        VaccineInventorySummaryDetails.get({status:$scope.clicked.id}, function(data){
            console.log(data);
            $scope.name = data.name;
            $scope.vaccineInventoryStock = data.vaccineInventoryStockDetails;
console.log(data.vaccineInventoryStockDetails);
            var z = [{"product":"BCG","zero":10,name :"zero"}];
          /*  $scope.inventory = {

                dataPoints:z,
                dataColumns:[{"id":"zero", name:"zero", type:"bar"}],
                dataX:{
                    "id":"product"
                }


            };*/




        });

        //console.log(data);
    };
function dataV(){
    console.log(vaccineInventoryData);
    $scope.vaccineInventorySummaryData2 = {

        dataPoints:vaccineInventoryData,
        dataColumns: [
            {"id": "overstock", "name":"overstock", "type": "bar"},
            {"id": 'sufficient', "name":"sufficient", "type": "bar"},
            {"id": "minimum", "name":"Understock", "type": "bar"},
            {"id": "zero", "name":"Zero stock", "type": "bar"}
        ],
        dataX:{"id":"overstock"}
    };}

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
    $scope.openStockInventoryStatusDetails = function () {
        var modalInstance = $modal.open({
            templateUrl: 'partials/slide-stock-inventory-status-details-content.html',
            controller: 'DashboardStockInventoryStatusModalInstanceCtrl',
            resolve: {
                items: function () {

                    return $scope.allStockDataPointsByCategory;
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


                    var deferred = $q.defer();
                    $timeout(function () {

                        reportingDetail.get(function (data) {
                            var list = data.reportingDetails;
                            for (i = 0; i < list.length; i++) {
                                list[i].checked = false;
                            }
                            deferred.resolve(list);


                        });

                    }, 100);

                    return deferred.promise;


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
    $scope.openSupplyingOrdersDetailDialog = function (size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'supplying.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {

                    return $scope.supplyingAllPendingOrders;
                }
            }

        });
    };

    $scope.openSupplyingPendingReceiveDetailDialog = function (size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'supplying-pending-receive.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {
                    return $scope.supplyingPendingReceive;
                }
            }
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

                    //return reparingDetailList.repairingDetails;
                    return $scope.allNonFunctionalEquipmentsByStatus;
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

    $scope.openBatchToExpireDialog = function (size) {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'batch-expiry.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {

                    return batchToExpireNotification;
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


//For Receiving Notification
      $scope.totalReceive = receiveDistributionDetailList.receiveNotification.length;

    $scope.toggleSlider = function () {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'receiveDistributionNotification.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {

                    return receiveDistributionDetailList.receiveNotification;
                }
            }
        });
        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });


    };



    //For MinimumStock Notification

   $scope.totalMinimumStock= minimumStockNotification.minimumStock.length;

    $scope.toggleSlider2 = function () {

        var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'minimumStockNotification.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            windowClass: 'my-modal-popup',
            resolve: {
                items: function () {

                    return minimumStockNotification.minimumStock;
                }
            }
        });
        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });


    };

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
                    user_preferences.startdate = "";
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
            SettingsByKey.get({key: 'STOCK_LESS_THAN_BUFFER_COLOR'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.insufficient_color = data.settings.value;
                } else {
                    color_values.insufficient_color = 'red';
                }

            });
            SettingsByKey.get({key: 'STOCK_GREATER_THAN_BUFFER_COLOR'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.reorder_color = data.settings.value;
                } else {
                    color_values.reorder_color = 'yellow';
                }

            });
            SettingsByKey.get({key: 'STOCK_GREATER_THAN_REORDER_LEVEL_COLOR'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.sufficient_color = data.settings.value;
                } else {
                    color_values.sufficient_color = 'green';
                }

            });
            SettingsByKey.get({key: 'STOCK_GREATER_THAN_MAXIMUM_COLOR'}, function (data) {
                if (!utils.isNullOrUndefined(data.settings.value)) {
                    color_values.overstock_color = data.settings.value;
                } else {
                    color_values.overstock_color = 'blue';
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
            HelpContentByKey.get({content_key: 'Coverage Help'}, function (data) {


                if (!isUndefined(data.siteContent)) {

                    helps.coverageHelp = data.siteContent;

                } else {

                    helps.coverageHelp = {htmlContent: messageService.get('content.help.default')};

                }

            });
            HelpContentByKey.get({content_key: 'Wastage Help'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.wastageHelp = data.siteContent;

                } else {

                    helps.wastageHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'Sessions Help'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.sessionsHelp = data.siteContent;

                } else {

                    helps.sessionsHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'Dropout Help'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.dropoutHelp = data.siteContent;

                } else {

                    helps.dropoutHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'My Stock Help'}, function (data) {

                if (!isUndefined(data.siteContent)) {

                    helps.stockHelp = data.siteContent;

                } else {

                    helps.stockHelp = {htmlContent: messageService.get('content.help.default')};

                }
            });
            HelpContentByKey.get({content_key: 'Facility Stock Help'}, function (data) {

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

    receiveDistributionDetailList: function ($q, $timeout, ReceiveDistributionAlert) {
        var deferred = $q.defer();
        $timeout(function () {

            ReceiveDistributionAlert.get(function (data) {
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

    },
    homeFacility: function ($q, $timeout, UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility = {};

        $timeout(function () {
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    },
    daysNotReceive: function ($q, $timeout, SettingsByKey) {
        var deferred = $q.defer();
        $timeout(function () {
            SettingsByKey.get({key: 'NUMBER_OF_DAYS_PANDING_TO_RECEIVE_CONSIGNMENT'}, function (data) {
                deferred.resolve(data.settings.value);
            });
        }, 100);

        return deferred.promise;
    },
    batchToExpireNotification: function ($q, $timeout, BatchExpiryNotification) {
        var deferred = $q.defer();
        $timeout(function () {
            BatchExpiryNotification.get({}, function (data) {
                var expires = [];
                if (!isUndefined(data.expiries)) {
                    expires = data.expiries;

                }
                deferred.resolve(expires);


            });

        }, 100);

        return deferred.promise;

    },
    VaccineInventorySummaryData: function ($q, $timeout, VaccineInventorySummary) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineInventorySummary.get({}, function (data) {
                var summary = [];
                if (!isUndefined(data.stockOverView)) {
                    summary = data.stockOverView;

                }
                console.log(summary);

                deferred.resolve(summary);


            });

        }, 100);

        return deferred.promise;

    },
    receiveNotification: function ($q, $timeout, ReceiveNotification) {
        var deferred = $q.defer();
        $timeout(function () {
            ReceiveNotification.get({}, function (data) {
                var summary = [];
                if (!isUndefined(data)) {
                    summary = data;

                }
                console.log(summary);

                deferred.resolve(summary);

            });

        }, 100);

        return deferred.promise;

    },
    minimumStockNotification: function ($q, $timeout, MinimumStockNotification) {
        var deferred = $q.defer();
        $timeout(function () {
            MinimumStockNotification.get({}, function (data) {
                var summary = [];
                if (!isUndefined(data)) {
                    summary = data;

                }
                deferred.resolve(summary);

            });

        }, 100);

        return deferred.promise;

    }


};
