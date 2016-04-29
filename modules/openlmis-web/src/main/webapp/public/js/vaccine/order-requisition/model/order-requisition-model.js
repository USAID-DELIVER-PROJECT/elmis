/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */

var VaccineOrderRequisition = function (orderRequisition) {
    $.extend(this, orderRequisition);

    VaccineOrderRequisition.prototype.init = function () {

        function getLineItems(collection) {
            var lineItems = [];
            angular.forEach(collection, function (lineItem, r) {
                lineItems.push(new OrderRequisitionLineItem(lineItem, r));
            });
            lineItems=_.sortBy(lineItems,'displayOrder');
            return lineItems;
        }

        this.lineItems = getLineItems(this.lineItems, this);
        this.LineItemViews = _.groupBy(this.lineItems, function(s){
            return s.productCategory.name;
        });


    };

    this.init();
};