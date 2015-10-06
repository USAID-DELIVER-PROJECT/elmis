var VaccineOrderRequisition = function (orderRequisition) {
    $.extend(this, orderRequisition);

    VaccineOrderRequisition.prototype.init = function () {

        function getLineItems(collection) {
            var lineItems = [];
            angular.forEach(collection, function (stockCards, r) {
                lineItems.push(new OrderRequisitionLineItem(stockCards, r));
            });
            return lineItems;
        }

        this.lineItems = getLineItems(this.lineItems, this);

        this.LineItemViews = _.groupBy(this.lineItems, 'productCategory');


    };

    this.init();
};