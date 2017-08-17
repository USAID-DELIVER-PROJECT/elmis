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
function OrderFillRateController($scope, $window, OrderFillRateReport, GetPushedProductList) {
    //to minimize and maximize the filter section
    $scope.wideOption = {'multiple': true, dropdownCss: { 'min-width': '500px' }};
    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.pusheditems = $scope.data = $scope.datarows = $scope.summaries = [];
        $scope.filter.max = 10000;
        OrderFillRateReport.get($scope.getSanitizedParameter(), function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.summaries = data.pages.rows[0].keyValueSummary;

                //all orders
                allOrders      =  _.where(data.pages.rows[0].details, {substitutedProductName: null});
                //all substituted orders
                allSubstitutes =  _.difference(data.pages.rows[0].details, allOrders);

                // create primary-substitute product tree relationship
                if(allSubstitutes.length > 0) {
                    _.each(allOrders, function (row) {
                        row.substitutes = _.chain(allSubstitutes).where({productcode: row.productcode}).map(function (row) {
                            return row;
                        }).value();
                        if (row.substitutes.length > 0) {
                            // substitutedProductsReceivedTotal =  _.chain(row.substitutes).pluck('substitutedProductQuantityShipped').reduce(function(memo, amt){ return memo + amt; }, 0).value();
                            row.totalQuantityShipped = row.receipts + _.chain(row.substitutes).pluck('substitutedProductQuantityShipped').reduce(function (memo, amt) {
                                    return memo + amt;
                                }, 0).value();
                            if (row.approved === 0)
                                row.total_item_rate = 0;
                            else
                                row.total_item_rate = (row.totalQuantityShipped / row.approved)*100;
                        }
                    });
                }

                $scope.data = allOrders;

                $scope.paramsChanged($scope.tableParams);
            }
        });


        // GetPushedProductList.get($scope.getSanitizedParameter(),function (data) {
        //         if (data.pages !== undefined && data.pages.rows !== undefined) {
        //             $scope.pusheditems = data.pages.rows;
        //         }
        //     });
    };

    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.getSanitizedParameter());
        var url;
        if (type == "pushed-product-list") {
            url = '/reports/download/pushed_product_list/' + "pdf" + '?' + params;
        } else {
            url = '/reports/download/order_fill_rate/' + type + '?' + params;
        }
        $window.open(url);
    };
}
