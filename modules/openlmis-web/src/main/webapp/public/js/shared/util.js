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
                startdate: utils.formatDate(startDate),
                enddate: utils.formatDate(endDate)
            };
        }
        else
            return {startdate: _startDate, enddate: _endDate};


    },

    getYearStartAndEnd: function (year,_cuttofdate) {

        var periodValues=[];
        var endDate;
        var startDate;
        if(year==='0'){
            periodValues=utils.getVaccineCustomDateRange(1,null,null,_cuttofdate);

        }else {
            periodValues={  enddate :utils.formatDate(new Date(year, 12, 0)),
                startdate :utils.formatDate(new Date(year, 0, 1))};
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
    formatDate: function (dateValue) {
        var day = dateValue.getDate();
        var monthIndex = dateValue.getMonth() + 1;
        var year = dateValue.getFullYear();
        var formatedDate = ("0000" + year).slice(-4) + "-" + ("00" + monthIndex).slice(-2) + "-" + ("00" + day).slice(-2);
        return formatedDate;
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
