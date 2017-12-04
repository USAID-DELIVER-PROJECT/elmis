function StockAvailabilityControllerFunc($scope, AvailableStockDashboard,FullStockAvailableForDashboard) {
    $scope.periodName = [];
    $scope.filter = {};

    $scope.filter.product=2412;


    function getFullStockAvailabilityForChart(data) {

       var percentageOfFullStock = _.pluck(data,'percentageoffullstock');
        var periodName = _.pluck(data,'periodname');
        console.log(periodName);
        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container3',
                type: 'line'
            },
            title: {
                text: 'Full Stock Availability Percentage'
            },
            credits: {enabled: false},
            legend: {},
            tooltip: {
                shared: true
            },
            plotOptions: {
                series: {
                    shadow: false,
                    borderWidth: 0,
                    pointPadding: 0
                }
            },
            xAxis: {
                categories: periodName,
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickLength: 3,
                title: {
                    text: 'Months',
                    style: {
                        color: '#333'
                    }
                }
            },
            yAxis: {
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickWidth: 1,
                tickLength: 3,
                gridLineColor: '',
                title: {
                    text: '% percentage',
                    rotation: 0,
                    margin: 50,
                    style: {
                        color: '#333'
                    }
                }
            },
            series: [
                {
                    name:'% of Full Stock Availability',
                    data: percentageOfFullStock
                }]
        });
    }

    FullStockAvailableForDashboard.get({}, function(data){
        if(data !== undefined){
            getFullStockAvailabilityForChart(data.fullStocks);
        }
    });
    


    allData();

    $scope.OnFilterChanged = function () {
        allData();


    };
   function allData() {
       AvailableStockDashboard.get({product:$scope.filter.product}, function (data) {

           $scope.available = data.availableStock;

           if (data.availableStock.length > 0) {
               getData($scope.available);
           }
       });
   }
    var getData = function (data) {
        var periodName = _.pluck(data, 'period');
        var percentageCoverage = _.pluck(data, 'percentagecoverage');
        var stockPercentage = _.pluck(data, 'stockpercentage');
        var equipmentPercentage = _.pluck(data, 'equipmentpercentage');
        Highcharts.setOptions({
            colors: [
                'blue',
                'lightgray',
                '#7ac36a',
                '#9e67ab',
                '#f15a60',
                '#ce7058',
                '#d77fb4'
            ]
        });

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: 'column'
            },
            title: {
                text: 'Performance of My Supervised Facilities'
            },
            credits: {enabled: false},
            legend: {},
            tooltip: {
                shared: true
            },
            plotOptions: {
                series: {
                    shadow: false,
                    borderWidth: 0,
                    pointPadding: 0
                }
            },
            xAxis: {
                categories: periodName,
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickLength: 3,
                title: {
                    text: 'Months',
                    style: {
                        color: '#333'
                    }
                }
            },
            yAxis: {
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickWidth: 1,
                tickLength: 3,
                gridLineColor: '',
                title: {
                    text: '% percentage',
                    rotation: 0,
                    margin: 50,
                    style: {
                        color: '#333'
                    }
                }
            },
            series: [
                {
                    name:'% of Facilities With Functional CCE',
                    data: equipmentPercentage
                },
                {
                name: '% of Facilities With Available Stock',
                data: stockPercentage
            },{
                name:'Monthly Coverage',
                data: percentageCoverage
            }]
        });


    };



    var chart = Highcharts.chart('container2', {
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45
            },
            style: {
                fontFamily: 'helvetica'
            }
        },

            title: {
                text: '<span style="font-size: 60px;">80 %</span> <br/> <div class="clearfix"></div>' +
                '<span style="font-size: 12px !important;">DTP3-HepB-Hib-3 Coverage Nov, 2017</span>',align: 'center',verticalAlign: 'middle'
            },

        credits: {enabled: false},

       /* subtitle: {
            text: '3D donut in Highcharts'
        },*/
        plotOptions: {
            pie: {
/*
                innerSize:200,
*/
                innerSize: '80%',
                size: '40%',
                depth: 145,
                shadow:false,
                showInLegend: true,
                dataLabels: {
                    enabled: false
                },
                borderWidth: 0
            }
        },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'left',
                floating: true,
                x: 0,
                y: 30,
                itemMarginTop: 10,
                backgroundColor: '#f3f3f3',
                useHTML: true,
                labelFormatter: function () {
                    return '<div style="width:200px"><span style="float:left">' + this.name + '</span><span style="float:right; margin-right:15%">$' + Highcharts.numberFormat(this.y, 0) + '</span></div>';
                }
/*
                itemMarginBottom: 10
*/
            },

        series: [{
            name: 'Coverage %',
            data: [
                ['January', 8],
                ['February', 3],
                ['March', 1],
                ['April', 6],
                {
                    name: 'May',
                    y: 9,
                    sliced: true,
                    selected: true
                },
                ['June', 4],

                ['July', 4],
                ['August', 1],
                ['September', 1]
            ]
        }]
    }



    );
/*
    var text = chart.renderer.text('<span style="font-size: 50px;text-align: center">89 %</span>' +
            '<br/><br/><Strong> Coverage</Strong>').add(),
        textBBox = text.getBBox(),
        x = chart.plotLeft + (chart.plotWidth *0.4) - (textBBox.width * 0.2),
        y = chart.plotTop + (chart.plotHeight * 0.5) + (textBBox.height * 0.25);
// Set position and styles
    text.attr({ x: x, y: y }).css({ fontSize: '20px', color: '#666666' }).add();*/



}
StockAvailabilityControllerFunc.resolve = {};