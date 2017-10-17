function InventoryStatusSummary($scope,VaccineInventorySummaryData,VaccineInventorySummaryDetails,GetVaccineInventoryFacilityDetails,InventoryEvent, ProductCategory, $q, $log, VaccineInventorySummary, GetVaccineInventoryDetails, $modal, $timeout) {

    //Start Inventory Event


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
    events = InventoryEvent;
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


    $scope.inventoryEvent = InventoryEvent;

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


        function asyncGreet(param) {
            var deferred = $q.defer();

            setTimeout(function () {
                VaccineInventorySummary.get(param, function (data) {
                    summary = data;
                    deferred.resolve(summary);
                });
            }, 100);

            return deferred.promise;
        }
        var summary = $scope.inventorySummary = [];
        if ($scope.filter === null || $scope.filter===undefined ) {
            param = {"category": null, "level": null};
        } else {

            param = {
                "category": null/* $scope.filter.category*/,
                "level": null
            };
        }
        $scope.promise = asyncGreet(param);
    $scope.promise.then(function (data) {
            console.log(data);
            $scope.myChart(data.stockOverView);
        }, function (reason) {
            console.log(reason);

            // alert('Failed: ' + reason);
        }, function (update) {
            console.log(update);

            //alert('Got notification: ' + update);
        });




/*$scope.inventoryData = function() {
    var summary = [];
    if ($scope.filter == null) {
        console.log($scope.filter);
        param = {"category": null, "level": null};
    } else {

        param = {
            "category": $scope.filter.category,
            "level": $scope.filter.facilityLevel
        };
    }

        {
            var deferred = $q.defer();
            $timeout(function () {

                VaccineInventorySummary.get(param, function (data) {
                    summary = data;
                    console.log(data.stockOverView);
                    deferred.resolve(summary);
                })
            }, 100);

            return deferred.promise;
        }


};

    console.log($scope.inventoryData());*/




    $scope.openDetailDialog = function (size) {

        var param = {
            "category": $scope.filter.category,
            "level": $scope.filter.facilityLevel,
            "product": size.category,
            "color": size.color
        };
        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
            controller: 'ModalInstanceCtrl',
            size: 'sm',
            animation: true,
            resolve: {
                items: function ($q, $timeout) {
                    var deferred = $q.defer();
                    $timeout(function () {
                        var facilities = [];
                        GetVaccineInventoryFacilityDetails.get(param, function (data) {
                            facilities = data;
                            var x = {'facilities':facilities,'color':size.color,'product':size.category};
                            deferred.resolve(x);
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


    var code = null;
    var param = {};
    $scope.OnFilterChanged = function () {
        if (code !== null) {
            $scope.filter.category = code;
        } else {
            $scope.filter.category = null;
        }

        param = {"category": $scope.filter.category, "level": $scope.filter.facilityLevel};

        VaccineInventorySummary.get(param, function (data) {
            summary = data;
            console.log(summary.stockOverView);
            $scope.myChart(data.stockOverView);
        });



    };
    $scope.updateFilterChanged = function (data) {
        $scope.OnFilterChanged();
    };


    $scope.productCat = ProductCategory;

    if (!isUndefined(ProductCategory))
        var categoryId = ProductCategory[0].id;

    var vaccineInventoryData = $scope.dat = [];

    $scope.callBack = function (data) {

        VaccineInventorySummary.get({category: data}, function (data) {
            if (!isUndefined(data.stockOverView)) {
                vaccineInventoryData = data.stockOverView;
            }
            $scope.myChart(vaccineInventoryData);
            $scope.vaccineInventorySummaryData.dataPoints = vaccineInventoryData;

        });
    };

    $scope.productFormChange = function (data) {
        code = data.id;
        $scope.filter.category = data.id;
        $scope.updateFilterChanged(parseInt(data.id,10));
        $scope.callBack(parseInt(data.id, 10));
        $scope.vaccineInventoryStock = [];
        $scope.reference(data);

    };


    $scope.showClick = function (data) {
        $scope.OnFilterChanged();
        $scope.reference(data);
    };

    $scope.vaccineInventorySummaryData = {
        dataPoints: VaccineInventorySummaryData,
        dataColumns: [
            {"id": "overstock", "name": "overstock", "type": "donut", "color": "blue"},
            {"id": 'sufficient', "name": "sufficient", "type": "donut", "color": "green"},
            {"id": "minimum", "name": "Understock", "type": "donut", "color": "yellow"},
            {"id": "zero", "name": "Zero stock", "type": "donut", "color": "red"}
        ]
    };


    $scope.getSliceData = function (click) {
        console.log(click);
        var status = {"red": "red", "#FFDB00": "yellow", "#00B2EE": "blue", "#006600": "green"};
        console.log(status[click.color]);
        $scope.reference(status[click.color]);


    };
    $scope.myChart = function(data){

         var status = [{"zero": "red", "minimum": "yellow", "overstock": "blue", "sufficient": "green"}];
         var zero = _.pluck(data,'zero');
         var minimum = _.pluck(data,'minimum');
         var overstock = _.pluck(data,'overstock');
         var sufficient = _.pluck(data,'sufficient');
          var allData =
              [ {"name":"Below Minimum","y":minimum[0],'color':"#FFDB00"},
                  {"name":"Sufficient","y":sufficient[0],'color':"#006600"},
                  {"name":"Over Stock","y":overstock[0],'color':"#00B2EE"},
              {"name":"Zero Stock","y":zero[0],'color':"red", sliced: true, selected: true}
              ];


       new Highcharts.chart('summary', {
            credits:{
                enabled:false
            },
            chart: {

                plotBackgroundColor: null,
                plotBorderWidth: 0,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: 'Inventory By Product',
                color:'green'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    size:200,
                    center: ['50%', '50%','50%','50%'],
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        useHTML: true,
                        formatter: function () {
                            return '<span style="color:' + this.point.color + '"><b>' + this.point.name + '<b>'+' '+this.point.percentage.toFixed(1) +'%</span>';


                        }
                    },
                    showInLegend: true,

                    point: {
                        events: {
                            click: function() {
                                $scope.getSliceData(this);
                            }
                        }

                    }
                }
            },
            series: [{
                name: 'stock level',
                colorByPoint: true,
                innerSize: '60%',
                    data:allData
                    /* [{
            name: 'Microsoft',
            y: 56.33,'color':'red'
        }, {
            name: 'Chrome',
            y: 24.03,
            sliced: true,
            selected: true
        }, {
            name: 'Firefox',
            y: 10.38
        }]*/
    }]
});


 };

    $scope.myChart(VaccineInventorySummaryData);


    $scope.chart3 = c3.generate({
        bindto: '#dash2',
        data: {
            x: 'data1',
            columns: [
                ['data1', 30, 200, 100, 400, 150, 250],
                ['data2', 50, 20, 10, 40, 15, 25]
            ],
            type: 'bar'
            /* types: {
             data1: 'bar',
             }*/
        },
        axis: {
            rotated: true
        }
    });

    $scope.reference = function (data2) {

        GetVaccineInventoryDetails.get(param, function (data) {
            if(data.vaccineInventoryStockDetails.length >0){
            $scope.name = data.name;
            $scope.vaccineInventoryStock = data.vaccineInventoryStockDetails;


            var status = [{"zero": "red", "minimum": "yellow", "overstock": "blue", "sufficient": "green"}];


            $scope.plun = _.pluck($scope.vaccineInventoryStock, 'product');
            $scope.val = _.pluck($scope.vaccineInventoryStock, data2);


            var preparePercentage = _.sortBy(_.pluck($scope.vaccineInventoryStock, data2), function (data) {
                return data;
            });
            var sortedDescAndFiltered = _.chain($scope.vaccineInventoryStock).sortBy(data).reverse().filter(_.property('product')).value();

            console.log(sortedDescAndFiltered);
            var dataSum = 0;
            for (var i = 0; i < preparePercentage.length; i++) {
                dataSum += preparePercentage[i];
            }
            var colorStatus = data2;

            $(function () {

                var colors = [{'blue': '#00B2EE', 'green': '#006600', 'yellow': '#FFDB00', 'red': 'red'}];

                var colorToDisplay = _.pluck(colors, colorStatus);

                $('#container').highcharts({
                    credits: {
                        enabled: false
                    },

                    chart: {
                        type: 'bar'
                    },
                    legend: {
                        enabled: false,
                        layout: 'vertical',
                        align: 'right',
                        verticalAlign: 'middle'
                        /* labelFormatter: function() {
                         return this.name + " - <span class='total'>"+this+"</span>"
                         }*/
                    },
                    title: {
                        text: 'Inventory By Location'
                    },
                    xAxis: {

                        title: {
                            text: 'Products'
                        },
                        categories: _.pluck(sortedDescAndFiltered, 'product'),
                        allowDecimals: false

                    },
                    yAxis: {
                        min: 0,
                        allowDecimals: false,
                        stackLabels: {
                            enabled: true,
                            style: {
                                fontWeight: 'bold',
                                color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                            }
                        },
                        title: {
                            text: 'Facilities Percentage'
                        }
                    },
                    plotOptions: {
                        series: {
                            events: {
                                legendItemClick: function (x) {
                                    var i = this.index - 1;
                                    var series = this.chart.series[0];
                                    var point = series.points[i];


                                    if (point.oldY === undefined)
                                        point.oldY = point.data.id;
                                    point.update({y: point.y !== null ? null : point.oldY});
                                }
                            },
                            cursor: 'pointer',
                            point: {
                                events: {
                                    click: function () {

                                        $scope.openDetailDialog(this);
                                        // getListOfFacilities(this);

                                        // alert('Category: ' + this.category + ', value: ' + this.y);
                                    }
                                }
                            }
                        },
                        bar: {
                            dataLabels: {
                                enabled: true,
                                /*   formatter:function() {
                                 var pcnt = (this.y / dataSum) * 100;
                                 return Highcharts.numberFormat(pcnt) + '%';
                                 }*/

                                formatter: function () {
                                    return Highcharts.numberFormat(this.y, 0) + '%';

                                }
                            },
                            enableMouseTracking: true
                        }
                    },
                    /*  legend: {
                     labelFormatter: function(){
                     return $scope.vaccineInventoryStock[this.index].product;
                     }
                     },*/
                    series: [
                        {
                            pointWidth: 14,
                            pointHeight: 220,
                            color: colorToDisplay[0],
                            showInLegend: true,
                            name: 'Facilities',
                            data: preparePercentage.reverse()
                        }

                    ]

                });

                /*  setTimeout(function () {
                 chart.internal.expandArc($scope.vaccineInventoryStock[0].y)
                 }, 0);*/

            });
        }

        });


    };


}

InventoryStatusSummary.resolve = {

    VaccineInventorySummaryData: function ($q, $timeout, VaccineInventorySummary) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineInventorySummary.get({category: 100}, function (data) {
                var summary = [];
                if (!isUndefined(data.stockOverView)) {
                    summary = data.stockOverView;

                }
                //  console.log(summary);

                deferred.resolve(summary);


            });

        }, 100);

        return deferred.promise;

    } ,

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

    },
    ProductCategory: function ($q, $timeout, ProductCategoriesByProgram) {
        var deferred = $q.defer();
        $timeout(function () {
            ProductCategoriesByProgram.get({programId: 82}, function (data) {
                var summary = [];
                if (!isUndefined(data.productCategoryList)) {
                    summary = data.productCategoryList;

                }
                //console.log(summary);

                deferred.resolve(summary);


            });

        }, 100);

        return deferred.promise;

    }


};