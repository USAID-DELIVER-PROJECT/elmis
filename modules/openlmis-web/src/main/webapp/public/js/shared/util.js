/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

var utils = {
    getFormattedDate: function (date) {
        return ('0' + date.getDate()).slice(-2) + '/' + ('0' + (date.getMonth() + 1)).slice(-2) +
            '/' + date.getFullYear();
    },

    isNullOrUndefined: function (obj) {
        return obj === undefined || obj === null;
    },

    isNumber: function (numberValue) {
        if (this.isNullOrUndefined(numberValue)) return false;
        var number = numberValue.toString();

        if (number.trim() === '') return false;
        return !isNaN(number);
    },

    parseIntWithBaseTen: function (number) {
        return parseInt(number, 10);
    },

    getValueFor: function (number, defaultValue) {
        if (!utils.isNumber(number)) return defaultValue ? defaultValue : null;
        return utils.parseIntWithBaseTen(number);
    },

    isValidPage: function (pageNumber, totalPages) {
        pageNumber = parseInt(pageNumber, 10);
        return !!pageNumber && pageNumber > 0 && pageNumber <= totalPages;
    },

    isEmpty: function (value) {
        return (value === null || value === undefined || value.toString().trim().length === 0);
    },

    sum: function () {
        var values = Array.prototype.slice.call(arguments), sum = 0;

        values.forEach(function (value) {
            if (!isUndefined(value)) {
                sum += utils.parseIntWithBaseTen(value);
            }
        });
        return sum;
    },

    getVaccineCustomDateRange: function (periodRange, _startDate, _endDate, _cutoffDate) {
        var er = 0;
        var endDate;
        var startDate;
        if (periodRange !== 5) {
            var currentDate = new Date();
            var months = 0;
            var monthBack = 0;
            var currentDays = currentDate.getDate();

            if (periodRange === "0")
                return {startdate: null, enddate: null};

            else if (periodRange !== 0 && utils.isEmpty(_cutoffDate)) {

                return {startdate: null, enddate: null};
            }

            else if (currentDays <= _cutoffDate) {
                monthBack = 1;
            }

            endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack + 1, 0);
            startDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 1);


            /*            switch (periodRange) {
             case '1':
             months = startDate.getMonth();
             break;
             case '2':
             months = startDate.getMonth() - 3;
             break;
             case '3':
             months = startDate.getMonth() - 6;
             break;
             case '4':
             months = startDate.getMonth() - 12;
             break;
             default :
             months = 0;
             }
             //startDate.setMonth(months);
             */
            return {
                startdate: utils.formatDate(startDate),
                enddate: utils.formatDate(endDate)
            };
        }
        else
            return {startdate: _startDate, enddate: _endDate};


    },

    getYearStartAndEnd: function (year, _startDate, _endDate, _cuttofdate, type) {

        var periodValues = [];
        var endDate;
        var startDate;
        if (year !== '-1') {
            if (year === '0') {
                periodValues = utils.getVaccineCustomDateRange(1, null, null, _cuttofdate);

            } else {
                if(type===0) {
                    periodValues = {
                        enddate: utils.formatDate(new Date(year, 12, 0)),
                        startdate: utils.formatDate(new Date(year, 0, 1))
                    };
                }
                else{
                    periodValues = {
                        enddate: utils.formatDate(new Date(year, _endDate, 0)),
                        startdate: utils.formatDate(new Date(year, _startDate, 1))
                    };
                }
            }
        } else {
            if (type === 0) {
                periodValues = {startdate: _startDate, enddate: _endDate};
            }else if(type===1){
                periodValues = {
                    enddate: utils.formatDate(new Date(year, _endDate+1, 0)),
                    startdate: utils.formatDate(new Date(year, _startDate, 1))
                };
            }
            return periodValues;
        }
        return periodValues;

    },

    getVaccineMonthlyDefaultPeriod: function (periods, cuttoffDate) {
        var monthBack = 0;
        var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        var currentDate = new Date();
        var currentDays = currentDate.getDate();
        var endDate;
        if (cuttoffDate === undefined || cuttoffDate === null) {
            cuttoffDate = 10;
        }
        if (currentDays <= cuttoffDate) {
            monthBack = 1;
        }
        endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);

        var formattedDate = months[endDate.getMonth()] + " " + endDate.getFullYear();

        for (yearIndex = 0; yearIndex < periods.length; yearIndex++) {


            if (periods[yearIndex].periodname == formattedDate) {
                return periods[yearIndex].periodid;
            }


        }
        return 0;
    },
    generatePeriodNamesForVaccineYear: function (year) {
        var periodList = [];
        var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        for (var i = 0; i < 12; i++) {
            periodList[i] = months[i] + " " + year;
        }
        return periodList;

    },
    generatePeriodNamesWithDashForVaccineYear: function (year) {
        var periodList = [];
        var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        for (var i = 0; i < 12; i++) {
            periodList[i] = months[i] + "-" + year;
        }
        return periodList;

    },
    formatDate: function (dateValue) {
        var day = dateValue.getDate();
        var monthIndex = dateValue.getMonth() + 1;
        var year = dateValue.getFullYear();
        var formatedDate = ("0000" + year).slice(-4) + "-" + ("00" + monthIndex).slice(-2) + "-" + ("00" + day).slice(-2);
        return formatedDate;
    },
    getCustomizedStartAndEndDate: function (periodRange, _cutoffDate) {
        var er = 0;
        var endDate;
        var startDate;

        var currentDate = new Date();
        var months = 0;
        var monthBack = 0;
        var currentDays = currentDate.getDate();
        if (currentDays <= _cutoffDate) {
            monthBack = 1;
        }

        endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);
        startDate = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 1);

        months = startDate.getMonth() - periodRange;

        startDate.setMonth(months);

        return {
            startdate: utils.formatDate(startDate),
            enddate: utils.formatDate(endDate)
        };

    },

    getDistrictBasedReportDataWithSubAndGrandTotal: function (reportData, districtNameKey,
                                                              columnKeys, includeGrandTotal) {
        if (reportData.length === 0)
            return;

        var uniqueDistrictName = _.uniq(_.pluck(reportData, districtNameKey));
        var reportDataWithAggregates = [];
        var grandTotal = {};
        var districtFilter = {};

        _.each(uniqueDistrictName, function (districtName) {

            districtFilter[districtNameKey] = districtName;

            var district_total = {};
            var districtData = _.where(reportData, districtFilter);

            reportDataWithAggregates.push({data: districtData});

            if (districtData.length > 1) {

                _.each(columnKeys, function (columnKey) {
                    district_total[columnKey] = utils.getColumnSubTotal(reportData, districtName, columnKey);
                });

                reportDataWithAggregates.push({subtotal: district_total});
            }

        });

        // Calculate grand Total
        if (includeGrandTotal) {

            _.each(columnKeys, function (columnKey) {
                grandTotal[columnKey] = utils.getGrandTotal(reportData, columnKey);
            });

            reportDataWithAggregates.push({grandtotal: grandTotal});
        }

        return reportDataWithAggregates;
    },
    getReportDataWithSubAndGrandTotal: function (reportData, nameKey,
                                                 columnKeys, includeGrandTotal, type) {
        if (utils.isNullOrUndefined(reportData) || utils.isEmpty(reportData) || reportData.length === 0)
            return;

        var uniqueName = _.uniq(_.pluck(reportData, nameKey));

        var reportDataWithAggregates = [];
        var grandTotal = {};

        _.each(uniqueName, function (districtName) {

            var district_total = {};
            var districtData;
            if (type === 1) {
                districtData = _.where(reportData, {facility_name: districtName});
            } else if (type === 2) {
                districtData = _.where(reportData, {district_name: districtName});
            } else {
                districtData = _.where(reportData, {region_name: districtName});
            }

            reportDataWithAggregates.push({data: districtData});

            if (districtData.length > 1) {

                _.each(columnKeys, function (columnKey) {
                    district_total[columnKey] = utils.getReportColumnSubTotalVS(reportData, districtName, columnKey, type);
                });

                reportDataWithAggregates.push({subtotal: district_total});
            }

        });

        // Calculate grand Total
        if (includeGrandTotal) {

            _.each(columnKeys, function (columnKey) {
                grandTotal[columnKey] = utils.getGrandTotal(reportData, columnKey);
            });

            reportDataWithAggregates.push({grandtotal: grandTotal});
        }

        return reportDataWithAggregates;
    },

    getColumnSubTotal: function (reportData, districtName, columnToBeAgregated) {
        return _.chain(reportData).where({districtName: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
            return memo + num;
        }, 0).value();
    },
    getReportColumnSubTotal: function (reportData, districtName, columnToBeAgregated, type) {

        if (type === 1) {
            return _.chain(reportData).where({facilityname: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
                return memo + num;
            }, 0).value();
        } else if (type === 3) {
            return _.chain(reportData).where({regionname: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
                return memo + num;
            }, 0).value();
        }
        return _.chain(reportData).where({districtname: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
            return memo + num;
        }, 0).value();
    },
    getReportColumnSubTotalVS: function (reportData, districtName, columnToBeAgregated, type) {

        if (type === 1) {
            return _.chain(reportData).where({facility_name: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
                return memo + num;
            }, 0).value();
        } else if (type === 3) {
            return _.chain(reportData).where({region_name: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
                return memo + num;
            }, 0).value();
        }
        return _.chain(reportData).where({district_name: districtName}).pluck(columnToBeAgregated).reduce(function (memo, num) {
            return memo + num;
        }, 0).value();
    },
    getGrandTotal: function (reportData, columnToBeAgregated) {
        return _.chain(reportData).pluck(columnToBeAgregated).reduce(function (memo, num) {
            return memo + num;
        }, 0).value();
    }

};

String.prototype.format = function () {
    var formatted = this;
    for (var i = 0; i < arguments.length; i++) {
        var regexp = new RegExp('\\{' + i + '\\}', 'gi');
        formatted = formatted.replace(regexp, arguments[i]);
    }
    return formatted;
};

String.prototype.endsWith = function (searchString) {
    var position = this.length - searchString.length;
    if (position >= 0 && position < length)
        return false;
    return this.indexOf(searchString, position) !== -1;
};
