function StockAvailabilityControllerFunc1($scope,$state,ReportingTarget, AvailableStockDashboard,FullStockAvailableForDashboard,AggregateFacilityPerformance,GetDistrictCategorization,GetVaccineCoverageByRegionAndProduct) {
    $scope.periodName = [];
    $scope.filter = {};
    $scope.mans = [{'k':181}];

    $scope.filter.product=2412;
    var date = new Date();
    var year = date.getFullYear();
    year = 2017;

    var params = {
        productId:$scope.filter.product,
        periodId:121,
        year:year
    };
    ReportingTarget.get({}, function(data){
        if(data !== null) {
            var dataValue = data.reportingTarget;
            reportingPerformance(dataValue);
        }
    });

    AggregateFacilityPerformance.get(params,function(data){
        if(data !== undefined)
            $scope.facilityPerformance = data.performance;
        console.log( $scope.facilityPerformance);
    });

    GetDistrictCategorization.get(params, function (data) {
        $scope.categorization = data.categories;
        categoryFunc($scope.categorization);
    });



    function coverageByRegion(coverage) {

        var cov = _.pluck(coverage,'coverage');
        var region = _.pluck(coverage,'region');

        var result = [], i = -1,
            color={Cat_1:'#52C552',Cat_2:'#509fc5',Cat_3:'#E4E44A', Cat_4:'#FF0000'};

        while (cov[++i]) {
            if(cov[i]<50)
            result.push([{color:'#FF0000',y:cov[i]}]);
            else
            result.push([{color:'#009012',y:cov[i]}]);
        }
        var mergedArrays = [].concat.apply([], result);

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
                    categories: region,
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
                    data: mergedArrays,
                    valueSuffix: ' %'

                }]
            });

    }

    GetVaccineCoverageByRegionAndProduct.get(params, function (data) {
        var coverage = data.coverage;
        coverageByRegion(coverage);
    });


    function getFullStockAvailabilityForChart(data) {

        var percentageOfFullStock = _.pluck(data,'percentageoffullstock');
        var periodName = _.pluck(data,'periodname');

        var dataValues =  [
            {
                name: '% of Full Stock Availability',
                data: percentageOfFullStock
            }];
        //var chart = new Highcharts.Chart({

        //start
        /*           var options = {
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
                               /!*
                                rotation: 0,
                                *!/
                               margin: 50,
                               style: {
                                   color: '#333'
                               }
                           }
                       },
                       series:dataValues
                   };
                   //end

               options.chart.renderTo = 'container3';
               options.chart.type = 'line';
               var chart1 = new Highcharts.Chart(options);
               $scope.changeChart = function(){

                   options.chart.renderTo = 'container3';
                   options.chart.type = 'bar';
                   var chart1 = new Highcharts.Chart(options);

               };*/











        //});

        //Alternative
        //start


        function drawChart(seriesValues,chartType){

            var chart = new Highcharts.Chart({
                chart: {
                    renderTo: 'container3',
                    type: chartType
                    //zoomType: "xy"

                },
                title: {
                    text: 'Health Facilities with Full Stock Availability at the end of the Month'
                },
                credits: {enabled: false},
                legend: {
                    buttons: {
                        customButton: {
                            text: 'Custom Button',
                            onclick: function () {
                                alert('You pressed the button!');
                            }
                        }
                    }

                },
                tooltip: {
                    shared: true
                },
                plotOptions: {
                    series: {
                        shadow: false,
                        borderWidth: 0,
                        pointPadding: 0,
                        dataLabels: {
                            useHTML: true
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
                         rotation: 0,
                         */
                        margin: 50,
                        style: {
                            color: '#333'
                        }
                    }
                },
                exporting: {
                    buttons: {
                        customButton: {
                            menuItems: [
                                {
                                    text: 'Bar',
                                    onclick: function() {
                                        drawChart(seriesValues,'bar');
                                    }
                                },
                                {
                                    text: 'Line',
                                    onclick: function() {
                                        drawChart(seriesValues,'line');
                                    }
                                },
                                {
                                    text: 'Pie',
                                    onclick: function() {
                                        drawChart(seriesValues,'pie');
                                    }
                                }
                            ],
                            symbol: 'triangle'
                        },
                        printButton: {
                            text: 'Print',
                            onclick: function () {
                                this.print();
                            }
                        },
                        exportButton: {
                            symbol: 'anX'
                        }
                    }
                },
                series:seriesValues


            });
        }


        drawChart(dataValues,'column');
        //end



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
                text: '<span style="font-size: 60px;">80 %</span> <br/> <div class="clearfix"></div><span style="font-size: 12px !important;">DTP3-HepB-Hib-3 Coverage Nov, 2017</span>',align:'center',verticalAlign: 'middle'
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




    function reportingPerformance(reportingData) {

        var distribution = _.pluck(reportingData,'distributed_rate');
        var timeliness = _.pluck(reportingData,'ontime_rate');
        var completeness = _.pluck(reportingData,'reported_rate');
        var approved = _.pluck(reportingData,'approved_rate');
        var period = _.pluck(reportingData,'period_name');

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
                text: 'Reporting VS Timeliness VS Approved VS Distribution Completeness rate'
            },
            credits: {enabled: false},
            /*subtitle: {
                text: 'Source: WorldClimate.com'
            },*/
            xAxis: {
                categories:period
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Percentage'
                },
                gridLineColor: '',

                max: 100


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
                    // stacking: 'percent'
                }
            },
            series: [{
                name: '% Distribution completeness',
                type: 'column',
                data: distribution,
                pointPadding: 0

            }, {
                name: '% Completeness',
                type: 'column',
                data: completeness,
                pointPadding: 0.1

            },
                {
                    name: '% Timeliness',
                    type: 'spline',
                    data: timeliness,
                    tooltip: {
                        valueSuffix: ' %'
                    }
                },
                {
                    name: '% Approved',
                    dashStyle: 'longdash',
                    data:approved,
                    tooltip: {
                        valueSuffix: ' %'
                    }
                }

            ]
        });
    }





    //Coverage


    var categoryFunc =    function (category) {

        var cat = _.pluck(category,'catagorization');
        var total = _.pluck(category,'total');

        var result = [], i = -1,
            color={Cat_1:'#52C552',Cat_2:'#509fc5',Cat_3:'#E4E44A', Cat_4:'#FF0000'};

        while (total[++i]) {
                result.push([{color:color[cat[i]],y:total[i]}]);
        }
        var mergedArrays = [].concat.apply([], result);

        $('#container8').highcharts({
                chart: {
                    type: 'bar',
                    height: 220

                },
                title: {
                    text: 'Performance Indicator for '
                },
                credits: {enabled: false},
                subtitle: {
                    text: ''
                },
                xAxis: {
                    categories: ['Category A','Category B','Category C','Category D'],
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
                    },
                    gridLineColor: '',
                    lineColor: '#999',
                    lineWidth: 1,
                    tickColor: '#666',
                    tickWidth: 1,
                    tickLength: 3

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
                    data: mergedArrays,
                    valueSuffix: ' %'

                }]
            });
        };






}
StockAvailabilityControllerFunc1.resolve = {};