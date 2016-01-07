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

        if (periodRange !== 5) {

            var currentDate = new Date();
            var endDate;
            var startDate;
            var months = 0;
            var monthBack = 0;
            var currentDays = currentDate.getDate();

            if (periodRange === "0")
                return {startdate: null, enddate: null};

            else if (periodRange !== 0 && utils.isEmpty(_cutoffDate)) {
                console.log("Vaccine period Late reporting date is not defined");
                return {startdate: null, enddate: null};
            }

            else if (currentDays <= _cutoffDate) {
                monthBack = 1;
            }

            endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);
            startDate = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 1);

            switch (periodRange) {
                case '1':
                    months = startDate.getMonth() - 1;
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
            startDate.setMonth(months);
            return {
                startdate: startDate.toISOString().substring(0, 10),
                enddate: endDate.toISOString().substring(0, 10)
            };
        }
        else
            return {startdate: _startDate, enddate: _endDate};

    },


    getVaccineMonthlyDefaultPeriod: function (periods) {
        var monthBack = 0;
        var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        var currentDate = new Date();
        var currentDays = currentDate.getDate();
        var endDate;

        if (currentDays <= 6) {
            monthBack = 1;
        }
        endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - monthBack, 0);

        var formattedDate = months[endDate.getMonth()] + " " + endDate.getFullYear();

        for (yearIndex = 0; yearIndex < periods.length; yearIndex++) {

            for (i = 0; i < periods[yearIndex].children.length; i++) {
                for(j=0;j< periods[yearIndex].children[i].children.length;j++) {
                    if (periods[yearIndex].children[i].children[j].periodname == formattedDate) {
                        return periods[yearIndex].children[i].children[j].periodid;
                    }
                }
            }
        }
            return 0;
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
