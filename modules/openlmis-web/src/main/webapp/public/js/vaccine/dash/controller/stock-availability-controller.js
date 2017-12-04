function StockAvailabilityControllerFunc1($scope,$state, AvailableStockDashboard,FullStockAvailableForDashboard,AggregateFacilityPerformance) {
    $scope.periodName = [];
    $scope.filter = {};
    $scope.mans = [{'k':181}];

    $scope.filter.product=2412;
    var date = new Date();
    var year = date.getFullYear();

    var params = {
        productId:$scope.filter.product,
        periodId:119,
        year:year
    };

    AggregateFacilityPerformance.get(params,function(data){
        if(data !== undefined)
        $scope.facilityPerformance = data.performance;
    });

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
                text: 'Health Facilities with Full Stock Availability at the end of the Month'
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
/*
                    rotation: 0,
*/
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


    //More Drill Down Data for the Chart 2
    function getStockAvailabilityDataView(chart2Data) {
       if(chart2Data.y !== null){

           var indicator = (chart2Data.color === 'lightgray')?'availableStock':(chart2Data.color === 'blue')?'CCE':'coverage';

           var d = { 'indicator':indicator, 'total':chart2Data.y, 'period':chart2Data.category };
           console.log(d);
           $state.go('toMoreStockAvailabilityView', { 'indicator':indicator, 'total':chart2Data.y, 'period':chart2Data.category });

       }

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

        function getLastData(data){
            return data;
        }
        var dataToRender;
        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: 'column'

            },
            title: {
                text: 'CCE vs Stock availability vs Coverage'
            },
            credits: {enabled: false},
            legend: {},
            tooltip: {
                shared: true
            },
           /* plotOptions: {
                series: {
                    shadow: false,
                    borderWidth: 0,
                    pointPadding: 0
                }
            },*/

       /*     plotOptions: {
                column: {
                    shadow: false,
                    borderWidth: 0,
                    pointPadding: 0,
                    cursor: 'pointer',
                    stacking: 'normal',
                    keys: ['x', 'y', 'name'],
                    point: {
                        events: {
                            click: function() {
                                alert(this.name)
                            }
                        }
                    }
                }
            },
*/

            plotOptions: {
                series: {
                    shadow: false,
                    borderWidth: 0,
                    pointPadding: 0,
                    cursor: 'pointer',
                    point: {
                        events: {

                            mouseOver: function () {
                                var chart = this.series.chart;
                                dataToRender = this;

                                console.log(this);
                                if (!chart.lbl) {
                                    chart.lbl = chart.renderer.label('')
                                        .attr({
                                            padding: 10,
                                            r: 10,
                                            fill: Highcharts.getOptions().colors[1]
                                        })
                                        .css({
                                            color: '#FFFFFF'
                                        })
                                        .add();
                                }
                                chart.lbl
                                    .hide()
                                    .attr({
                                        text: 'x: ' + this.x + ', y: ' + this.y
                                    });
                            },
                            click: function() {
                                console.log(dataToRender);
                                //  this.update({ color: '#fe5800' }, true, false);
                                getStockAvailabilityDataView(dataToRender);

                                // alert ('Category: '+ this.category +', value: '+ this.y);
                            }

                        }
                    }
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
/*
                    rotation: 80,
*/
                    margin: 10,
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
                '<span style="font-size: 12px !important;">DTP3-HepB-Hib-3 Coverage Nov, 2017</span>'
                ,align: 'center',
                verticalAlign: 'middle'
            }
            ,

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
                size: '80%',
                depth: 145,
                shadow:false,
                showInLegend: false,
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
                    return '<div style="width:200px"><span style="float:left">' + this.name + '</span><span style="float:right; margin-right:1%">' + Highcharts.numberFormat(this.y, 0) + '</span></div>';
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




    $(function () {

        // First, let's make the colors transparent
        Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function (color) {
            return Highcharts.Color(color)
                .setOpacity(0.5)
                .get('rgba');
        });

        $('#container5').highcharts({
            chart: {
                marginBottom: 100
            },
          /*  chart: {
                type: 'column'
            },*/
            title: {
                text: 'Report Rate VS Timeliness VS Approved VS Distribution Completeness'
            },
            credits: {enabled: false},
            /*subtitle: {
                text: 'Source: WorldClimate.com'
            },*/
            xAxis: {
                categories: ['January',
                'February',
                'March',
            'April',
                'May',

            'June',

            'July',
            'August',
                'September',
                'October','November']
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Percentage'
                },
                gridLineColor: ''

/*
                max: 100
*/

            },
            legend: {
                align: 'center',
                margin: 10,
                verticalAlign: 'bottom',

/*
                y: 50,
*/
               // symbolHeight: 20,

                layout: 'horizontal',
                backgroundColor: '#FFFFFF',
             /*   x: 100,
                y: 20,*/
/*
                floating: true,
*/
                shadow: true
            },
            tooltip: {
                shared: true,
                valueSuffix: ' %'
            },
            plotOptions: {
                column: {
                    grouping: false,
                    shadow: false
                }
            },
            series: [{
                name: '% Distribution completeness',
                type: 'column',
                data: [49.9, 71.5, 100.4,80,81,75,78,90,80,91,77],
                pointPadding: 0

            }, {
                name: '% Completeness',
                type: 'column',
                data: [83.6, 78.8, 98.5,98,80,76,90,91,85,90,50],
                pointPadding: 0.1

            },
                {
                    name: '% Timeliness',
                    type: 'spline',
                    data: [50, 60, 100,70,80,18,80,90,40,60,80],
                    tooltip: {
                        valueSuffix: ' %'
                    }
                }
                ,
                {
                    name: '% Approved',
                    dashStyle: 'longdash',
                    data: [55, 30, 100,60,50,90,40,70,90,89,90],
                    tooltip: {
                        valueSuffix: ' %'
                    }
                }

            ]
        });
    });




    $(function () {
        $('#container7').highcharts({
            chart: {
                type: 'bar'
            },
            title: {
                text: 'DPT-3 Coverage'
            },
            credits: {enabled: false},
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: ['Mwanza','Arusha','Tabora','Mtwara','Njombe' ,'Lindi','Dar'],
                title: {
                    text: null
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: '',
                    align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            },
            tooltip: {
                valueSuffix: ' %'
            },
            plotOptions: {
                bar: {
                    dataLabels: {
                        enabled: true
                    }
                }
            },

            series: [{
                name: 'DTP-3 Coverage',
                data: [
                    {y: 68, color: 'green'}, {y: 67, color: 'green'}, {y: 62, color: 'green'}, {y: 60, color: 'green'},
                    {y: 58, color: 'red'}, {y: 55, color: 'red'},{y: 43, color: 'red'}
                    ],
                valueSuffix: ' %'

            }]
        });
    });




}
StockAvailabilityControllerFunc1.resolve = {};