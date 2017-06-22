function StockInventoryEvent($scope,$http,VaccineStockEvent, $filter){

    $scope.exportReport = function (type) {
        console.log( $scope.reportParams);
        var paramString = jQuery.param($scope.reportParams);
        var url = '/reports/download/stock_event/' + type + '?' + paramString;
        window.open(url, "_BLANK");
    };




    $scope.OnFilterChanged = function () {


        //Start Inventory Event

           $scope.reportParams =  {
               product: parseInt($scope.filter.product,10),
               period: parseInt($scope.filter.period,10),
               year:       parseInt($scope.filter.year,10),
               district:    utils.isEmpty($scope.filter.zone.id) ? 0 : parseInt($scope.filter.zone.id, 10)
           };
        $scope.inventoryEvent =[];
        $scope.summary = [];
            VaccineStockEvent.get($scope.reportParams, function (data) {

                if (!isUndefined(data.events)) {
                    $scope.summary = data.events;
                    $scope.inventoryEvent =data.events;

                }
                //console.log($scope.summary);




        console.log($scope.inventoryEvent);



        Date.prototype.addDays = function(days) {
            var dat = new Date(this.valueOf());
            dat.setDate(dat.getDate() + days);
            return dat;
        };

        function getDates(startDate, stopDate) {
            var dateArray = [];
            var currentDate = startDate;
            while (currentDate <= stopDate) {
                /*            dateArray.push(
                 /!* Date.parse(*!/$filter('date')(new Date(currentDate),'yyyy-MM-dd'));*/
                dateArray.push(new Date(currentDate).getDate());
                currentDate = currentDate.addDays(1);
            }
            return dateArray;
        }

        var date = new Date();
        var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
        var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);


        var day=[];
        var dateArray = getDates(new Date('2017-01-01'), (new Date('2017-01-31')));
        for (i = 0; i < dateArray.length; i ++ ) {

            day.push(dateArray[i]);

        }

        function pad (str, max) {
            str = str.toString();
            return str.length < max ? pad("0" + str, max) : str;
        }
        var all=[];
        day.forEach(function(data){
            return all.push(pad(data,2));
        });
        var events = [];
        events = $scope.summary;
        var filterDays = _.pluck(events,'day');
        var filterSOH = _.pluck(events,'quantity');
        var iMax = _.pluck(events,'maximum')[0];
        var iMin = _.pluck(events,'minimum')[0];

        var allMonthDays = all;
        var filteredMonths = [];

        for (var i = 0; i < allMonthDays.length; i++) {

            if (_.contains(allMonthDays, filterDays[i])) {
                filteredMonths.push(filterDays[i]);
            }
        }
        var sohValues = [];
        filterSOH.forEach(function(data){
            sohValues.push((parseInt(data,10) < 0)?parseInt(data,10)* -1:parseInt(data,10));
        });



        //Stock Event Chart
        console.log(iMin);
        /*

         var min = 100;
         var max = 500;
         */

        $(function () {
            $('#event').highcharts({
                credits:{
                    enabled:false
                },
                chart: {
                    type: 'line',
                    /*marginRight: 130,
                     marginBottom: 25,*/
                    marginBottom: 100
                },

                title: {
                    text: 'Stock Adequacy'
                    /* x: -20 //center*/
                },

                xAxis: {
                    categories: filteredMonths,
                    label : {
                        text : 'January'
                    }
                    /*   type: 'datetime',
                     tickInterval: 24 * 3600000,*/
                    /* labels: {
                     formatter: function() {
                     return Highcharts.dateFormat('%b %d', this.value);
                     }
                     }*/
                    /*dateTimeLabelFormats: { // don't display the dummy year
                     month: '%e. %b',
                     year: '%b'
                     }*/
                },
                yAxis: {
                    min: iMin - iMin,
                    max: iMax + iMin,
                    title: {
                        text: 'Stock On Hand (SOH)'
                    },
                    plotLines: [{

                        color: '#FF0000',
                        dashStyle: 'ShortDash',
                        width: 2,
                        value: iMin,
                        zIndex: 0,
                        label : {
                            text : 'Minimum Stock'
                        }
                    }, {
                        color: '#008000',
                        dashStyle: 'ShortDash',
                        width: 2,
                        value: iMax,
                        zIndex: 0,
                        label : {
                            text : 'Maximum Stock'
                        }
                    }]

                },
                tooltip: {
                    valueSuffix: ''
                },
                legend: {
                    layout: 'horizontal',
                    align: 'center',
                    verticalAlign: 'bottom',
                    x: 0,
                    y: 0,
                    borderWidth: 0
                },/*, legend: {
                 align: 'center',
                 verticalAlign: 'bottom',
                 x: 0,
                 y: 0
                 },*/
                series: [{
                    name: 'SOH',
                    data: sohValues
                }]
            });
        });


        //End Inventory Event

            });
    };



}
StockInventoryEvent.resolve ={
/*
    InventoryEvent: function ($q, $timeout, VaccineStockEvent) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineStockEvent.get({}, function (data) {
                var summary = [];
                if (!isUndefined(data.events)) {
                    summary = data.events;

                }
                console.log(summary);

                deferred.resolve(summary);


            });

        }, 100);

        return deferred.promise;

    }*/

};
