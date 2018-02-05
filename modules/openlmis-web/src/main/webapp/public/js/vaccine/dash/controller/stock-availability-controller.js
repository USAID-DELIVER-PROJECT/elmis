function StockAvailabilityControllerFunc1($scope, homeFacility, FacilityInventoryStockStatusData, AvailableStockData, GetPeriodForDashboard, YearFilteredData, ProductFilteredData, $routeParams, leafletData, ProductService, GetFullStockAvailability, $state, VaccineProductDoseList, ReportPeriodsByYear, VimsVaccineSupervisedIvdPrograms, ReportingTarget, NationalVaccineCoverageData, AvailableStockDashboard, FullStockAvailableForDashboard, GetAggregateFacilityPerformanceData, Categorization, VaccineCoverageByProductData) {
    $scope.homeFacility = homeFacility;
    $scope.homePageDate = new Date();

     //Lower Level Charts
    function getVaccineStockStatusChartForLowerLevel(data) {

        var vaccineData = data[0].dataPoints;
        var product = _.pluck(data[0].dataPoints, 'product');

        var  dataV = [];
        vaccineData.forEach(function (data) {
            dataV.push({y:data.mos,color:data.color,soh:data.soh});
        });

        Highcharts.chart('myStockVaccine', {
            chart: {
                type: 'column'
            },
            credits:{
                enabled:false
            },
            title: {
                text: 'Current Stock Status of '  + homeFacility.facilityname
            },
            subtitle: {
                text: 'Vaccine Stock'
            },
            xAxis: {
                categories:product,
                crosshair: false
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'MOS'
                },
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickWidth: 1,
                tickLength: 3,
                gridLineColor: ''
            },
            tooltip: {
                formatter: function() {
                    var tooltip;
                        tooltip =  '<span style="color:' + this.series.color + '">' + this.series.name + '</span>: <b>' + this.y+ '</b><br/>';

                    return tooltip;
                }
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0

                }
            },
            series: [{
                name: 'Vaccine',
                data: dataV

            }]
        });


    }

    FacilityInventoryStockStatusData.get({
        facilityId: parseInt(homeFacility.facilityid, 10),
        date: '2017-01-31'
    }).then(function (data) {
        if(!isUndefined(data)){
            var byCategory = _.groupBy(data, function (p) {
                return p.product_category;
            });

            var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                return [{"productCategory": index, "dataPoints": value}];
            });
            getVaccineStockStatusChartForLowerLevel(allStockDataPointsByCategory);
        }
    });

/*    VaccineDashboardFacilityInventoryStockStatus.get({
        facilityId: parseInt(homeFacility.facilityid, 10),
        date: '2017-01-31'
    }, function (data) {

        if (data.facilityStockStatus !== null) {
            var allProducts = data.facilityStockStatus;
            var byCategory = _.groupBy(allProducts, function (p) {
                return p.product_category;
            });

            var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                return [{"productCategory": index, "dataPoints": value}];
            });


            var vaccineData = $scope.allStockDataPointsByCategory[0].dataPoints;
            var mos = _.pluck($scope.allStockDataPointsByCategory[0].dataPoints, 'mos');
            var color = _.pluck($scope.allStockDataPointsByCategory[0].dataPoints, 'color');
            var product = _.pluck($scope.allStockDataPointsByCategory[0].dataPoints, 'product');

            console.log(mos);
            if (!isUndefined(mos)) {
              getVaccineStockStatusChartForLowerLevel(allStockDataPointsByCategory);

            } else
                return 'No Chart data';
/!*
              $scope.myStockVaccine.productCategory = $scope.allStockDataPointsByCategory[0].productCategory;

            $scope.myStockSupplies.dataPoints = $scope.allStockDataPointsByCategory[1].dataPoints;*!/
            // $scope.myStockSupplies.productCategory = $scope.allStockDataPointsByCategory[1].productCategory;


        }*/
   // });


    /*$scope.facilityInventoryStockStatusCallback = function (myStock) {
        $scope.myStockFilter=myStock;

        if (!isUndefined(homeFacility.id) && !isUndefined(myStock.toDate) && $scope.stockStatus.loadData) {

        }
    };
*/


    var currentDate = new Date().getFullYear() - 1;
    GetPeriodForDashboard.get(currentDate).then(function (data) {

        $scope.filter.product = 2421;
        $scope.findProductToDisplay = _.where(ProductFilteredData, {'id': 2421});
        $scope.years = YearFilteredData.sort(function (a, b) {
            return b - a;
        });
        $scope.filter.year = currentDate;

        $scope.products = $scope.findProductToDisplay;

        $scope.filter.period = data.id;
        var par = {year: currentDate, product: 2421, period: parseInt(data.id, 10), dose: 3};
        $scope.productToDisplay = _.findWhere($scope.products, {id: parseInt(2421, 10)});
        console.log(par);

        var para = angular.extend(par, currentDate, {periodName: data.name, productName: $scope.productToDisplay.name});
        $scope.nationalVaccineCoverageFunc(para);
        $scope.loadMap(par);
        $scope.vaccineCoverageByRegionAndProductFunc(para);
        $scope.getAggregatePerformanceFunc(para);
        $scope.categorizationFunct(para);
        $scope.fullStockAvailabilityFunc(para);
        $scope.availableStockFunc(para);


    });

    $scope.loadDashboardData = function (filter) {
        $scope.productToDisplay = _.findWhere($scope.products, {id: parseInt(filter.product, 10)});
        $scope.periodToDisplay = _.findWhere($scope.periods, {id: parseInt(filter.period, 10)});
        $scope.doseToDisplay = filter.dose;
        var prepareParams = angular.extend(filter, {
            productName: $scope.productToDisplay.name,
            periodName: $scope.periodToDisplay.name
        });
        console.log(filter);


        $scope.categorizationFunct(filter);
        $scope.fullStockAvailabilityFunc(filter);
        $scope.availableStockFunc(filter);
        $scope.vaccineCoverageByRegionAndProductFunc(prepareParams);
        $scope.nationalVaccineCoverageFunc(prepareParams);

        $scope.loadMap(filter);
        $scope.getAggregatePerformanceFunc(filter);
        $scope.showfilter = false;
    };

    $scope.changeYear = function () {

        $scope.periods = [];
        //   $scope.filter.year = currentDate;
        ReportPeriodsByYear.get({
            year: $scope.filter.year
        }, function (data) {
            $scope.periods = data.periods;
            $scope.filter.period = $scope.periods[0].id;

        });

    };

    $scope.periodName = [];
    $scope.filter = {};
    $scope.mans = [{'k': 181}];

    $scope.doseByProduct = function () {
        if ($scope.filter.product !== undefined)
            getDoseFilter($scope.filter.product);

    };
    if ($scope.filter.product === null || $scope.filter.product === undefined) {
        $scope.filter.product = 2412;
    }


    var date = new Date();
    var year = date.getFullYear();
    year = 2017;
    var doseId = 3;
    var params = {
        productId: $scope.filter.product,
        periodId: 121,
        year: year,
        doseId: 1
    };
    ReportingTarget.get({}, function (data) {
        if (data !== null) {
            var dataValue = data.reportingTarget;
            reportingPerformance(dataValue);
        }
    });


    $scope.getAggregatePerformanceFunc = function (params) {

        GetAggregateFacilityPerformanceData.get(params).then(function (data) {
            if (!isUndefined(data) || data.length > 0) {
                $scope.facilityPerformance = data;
                $scope.showNoData = false;
            }
            else
                $scope.showNoData = true;


        });

    };


    function round(value, precision) {
        var multiplier = Math.pow(10, precision || 0);
        return Math.round(value * multiplier) / multiplier;
    }

    function getNationalCoverageChart(data, params) {
        var nationalCoverage = _.pluck(data, 'nationalcoverage');
        var max_value = _.max(nationalCoverage, function (data) {
            return data;
        });
        var color;
        if (max_value < 50)
            color = 'red';
        else
            color = 'green';

        var months = _.pluck(data, 'monthly');
        var lastMonth = months[months.length - 1];
        var coverage_arr = [];
        angular.forEach(_.pluck(data, 'coverage'), function (data) {
            coverage_arr.push(round(data, 0));
        });
        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'NationalChart',
                type: 'column'

            },
            credits: {enabled: false},
            title: {
                text: 'National Coverage(' + params.productName + '-' + params.dose + ',' + params.periodName + ')'
            },
            xAxis: {
                categories: months
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Percentage',
                    align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            },
            tooltip: {
                valueSuffix: ' %'
            },
            series: [{
                type: 'column',
                name: 'Cummulative Coverage Trends',
                data: coverage_arr
            }]
        });

        var char2 = new Highcharts.Chart({
            chart: {
                renderTo: 'NationalChart2',
                type: 'pie'
            },

            credits: {enabled: false},

            plotOptions: {
                pie: {
                    innerSize: '80%',
                    showInLegend: false,
                    dataLabels: {
                        enabled: false
                    }
                }
            },

            title: {
                verticalAlign: 'middle',
                floating: true,
                text: '<span style="font-size: 50px;">' + max_value + '%</span>'
                /*
                                '<div class="clearfix"></div><span style="font-size: 12px !important;"></span>',align:'center',verticalAlign: 'middle'
                */
            },

            series: [{
                data: [
                    {name: 'National Coverage', y: max_value, color: color, selected: true}

                ]
            }]


        });

    }

    $scope.categorizationFunct = function (params) {

        Categorization.get(params).then(function (data) {
            if (data !== undefined || data.length > 0) {
                categoryFunc(data);
            } else
                $scope.categorized = [];
        });
    };


    $scope.fullStockAvailabilityFunc = function (params) {
        console.log(params);
        if (!isUndefined(params.period) && !isUndefined(params.year)) {

            GetFullStockAvailability.get(params).then(function (data) {

                if (data !== undefined) {
                    console.log(data);
                    $scope.fullStocks = data;
                    getFullStockAvailabilityForChart(data);
                } else
                    $scope.fullStocks = [];
            });
        }
    };


    var getCategorization = function () {
        var product = 2413,
            doseId = 1,
            period = 121, year = 2017;

        Categorization.get(product, doseId, period).then(function (data) {
            if (data !== undefined) {
                console.log(data);
                categoryFunc(data);
            }
        });
    };

    function getPerformanceMonitoringChart(data, params) {

        var vaccinated = _.pluck(data, 'cumulative_vaccinated');
        var target = _.pluck(data, 'monthly_district_target');
        var monthly = _.pluck(data, 'monthly');
        var comb = [];
        comb = _.zip(monthly, target);

        Highcharts.chart('performanceMonitoring', {
            chart: {
                zoomType: 'xy'
            },
            title: {
                text: 'Performance Monitoring'
            },
            subtitle: {
                text: ''
            },
            xAxis: monthly,
            crosshair: true,
            yAxis: [{ // Primary yAxis
                labels: {
                    format: '{value}°C',
                    style: {
                        color: Highcharts.getOptions().colors[2]
                    }
                },
                title: {
                    text: 'Temperature',
                    style: {
                        color: Highcharts.getOptions().colors[2]
                    }
                },
                opposite: true

            }, { // Secondary yAxis
                gridLineWidth: 0,
                title: {
                    text: 'Rainfall',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                labels: {
                    format: '{value} mm',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                }

            }, { // Tertiary yAxis
                gridLineWidth: 0,
                title: {
                    text: 'Sea-Level Pressure',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                labels: {
                    format: '{value} mb',
                    style: {
                        color: Highcharts.getOptions().colors[1]
                    }
                },
                opposite: true
            }],
            tooltip: {
                shared: true
            },
            legend: {
                layout: 'vertical',
                align: 'left',
                x: 80,
                verticalAlign: 'top',
                y: 55,
                floating: true,
                backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
            },
            series: [{
                name: 'Rainfall',
                type: 'column',
                yAxis: 1,
                data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4],
                tooltip: {
                    valueSuffix: ' mm'
                }

            }, {
                name: 'Target',
                type: 'spline',
                yAxis: 2,
                data: target,
                marker: {
                    enabled: false
                },
                dashStyle: 'shortdot',
                tooltip: {
                    valueSuffix: ' mb'
                }

            }, {
                name: 'Temperature',
                type: 'spline',
                data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6],
                tooltip: {
                    valueSuffix: ' °C'
                }
            }]
        });


        /*
                new Highcharts.chart('performanceMonitoring', {

                  credits: {enabled: false},

                 /!* xAxis: {
                        min: -0.5,
                        max: 5.5
                    },*!/
                    yAxis: {
                        min: 0
                    },
                    title: {
                        text: 'Performance Monitoring'
                    },
                    series: [{
                        type: 'line',
                        name: 'Target',
                        data: comb,
                        marker: {
                            enabled: true
                        },
                        states: {
                            hover: {
                                lineWidth: 0
                            }
                        },
                        enableMouseTracking: false
                    }, {
                        type: 'spline',
                        name: 'Children Vaccinated',
                        data: vaccinated,
                        dashStyle: 'shortdot',
                        marker: {
                            radius: 4
                        }
                    }]
                });
        */

    }

    $scope.nationalVaccineCoverageFunc = function (params) {
        NationalVaccineCoverageData.get(params).then(function (data) {


            if (data !== undefined) {
                console.log(data);

                getNationalCoverageChart(data, params);
                getPerformanceMonitoringChart(data, params);
            } else
                $scope.showData = false;
        });
    };


    function coverageByRegion(coverage, params) {

        var cov = _.pluck(coverage, 'coverage');
        var region = _.pluck(coverage, 'region');

        var result = [], i = -1,
            color = {Cat_1: '#52C552', Cat_2: '#509fc5', Cat_3: '#E4E44A', Cat_4: '#FF0000'};

        while (cov[++i]) {
            if (cov[i] < 50)
                result.push([{color: '#FF0000', y: cov[i]}]);
            else
                result.push([{color: '#009012', y: cov[i]}]);
        }
        var mergedArrays = [].concat.apply([], result);

        $('#container7').highcharts({
            chart: {
                type: 'bar'
            },
            title: {
                text: params.productName + '-' + params.dose + ' Coverage By Region' + ' ,' + params.periodName
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
            legend: {
                shadow: false
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
                name: 'Coverage',
                data: mergedArrays,
                valueSuffix: ' %'

            }]
        });

    }

    $scope.vaccineCoverageByRegionAndProductFunc = function (params) {

        VaccineCoverageByProductData.get(params).then(function (coverage) {
            if (!isUndefined(coverage))
                coverageByRegion(coverage, params);
        });
    };


    function getFullStockAvailabilityForChart(data) {

        var percentageOfFullStock = _.pluck(data, 'percentageoffullstock');
        var periodName = _.pluck(data, 'periodname');

        var dataValues = [
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


        function drawChart(seriesValues, chartType) {

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
                                    onclick: function () {
                                        drawChart(seriesValues, 'bar');
                                    }
                                },
                                {
                                    text: 'Line',
                                    onclick: function () {
                                        drawChart(seriesValues, 'line');
                                    }
                                },
                                {
                                    text: 'Pie',
                                    onclick: function () {
                                        drawChart(seriesValues, 'pie');
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
                series: seriesValues


            });
        }


        drawChart(dataValues, 'column');
        //end


    }


    // allData();

    function showDoseSlider(dose) {

        var displayName = _.pluck(dose, 'displayName');
        console.log(displayName);

        $scope.dose_slider = {
            value: 1,
            options: {
                floor: 1,
                ceil: parseInt(displayName.length, 10),
                translate: function (value, sliderId, label) {
                    return displayName[value - 1];
                },
                onChange: function (sliderId, modelValue, highValue, pointerType) {
                    console.log(modelValue);
                    return sliderId;
                },
                interval: 1,
                showTicksValues: true,
                showTicks: true
            }
            /* value: 1,
             options: {
                 floor: 1,
                 ceil: 12,
                 showTicksValues: true,
                 translate: function(value) {
                     return value;
                 },
                 ticksValuesTooltip: function(v) {
                     return v;
                 },
                 showTicks: true

             }*/
        };

    }

    function getDoseFilter(product) {

        if (!isUndefined(product)) {
            VimsVaccineSupervisedIvdPrograms.get({}, function (data) {
                VaccineProductDoseList.get(
                    {
                        programId: parseInt(data.programs[0].id, 10),
                        productId: parseInt(product, 10)
                    },
                    function (result) {
                        $scope.doses = result.doses;
                        $scope.filter.dose = 'Dose 3';
                    });
            });
        }

    }


    var product = 2413,
        period = 121;
    // var params = {product:2413,doseId:1,period:121,year:2017};

    var loadOnStart = function () {
        var params = {product: 2413, doseId: 1, period: 121, year: 2017};
        $scope.fullStockAvailability(params);

    };
    //loadOnStart();

    $scope.OnFilterChanged = function () {

        console.log($scope.filter);
        allData();
        if ($scope.filter === null || $scope.filter === undefined) {
            return;
        } else {
            console.log($scope.filter);
            $scope.fullStockAvailability($scope.filter);

            getDoseFilter($scope.filter);
            $scope.categorizationFunct($scope.filter);
        }

    };

    $scope.availableStockFunc = function (params) {

        AvailableStockData.get(params).then(function (data) {
            console.log(data);
            if (!isUndefined(data) || data.length > 0) {
                $scope.stocks = data;
                getData(data);

            } else
                $scope.stocks = [];
        });


    };


    //More Drill Down Data for the Chart 2
    function getStockAvailabilityDataView(chart2Data) {
        if (chart2Data.y !== null) {

            var indicator = (chart2Data.color === 'lightgray') ? 'availableStock' : (chart2Data.color === 'blue') ? 'CCE' : 'coverage';

            var d = {'indicator': indicator, 'total': chart2Data.y, 'period': chart2Data.category};
            console.log(d);
            $state.go('toMoreStockAvailabilityView', {
                'indicator': indicator,
                'total': chart2Data.y,
                'period': chart2Data.category
            });

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

        function getLastData(data) {
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
                            click: function () {
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
                    name: '% of Facilities With Functional CCE',
                    data: equipmentPercentage
                },
                {
                    name: '% of Facilities With Available Stock',
                    data: stockPercentage
                }, {
                    name: 'Monthly Coverage',
                    data: percentageCoverage
                }]
        });


    };


    /* var chart = Highcharts.chart('container2', {
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

             /!* subtitle: {
                  text: '3D donut in Highcharts'
              },*!/
             plotOptions: {
                 pie: {
                     /!*
                                     innerSize:200,
                     *!/
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
                 /!*
                                 itemMarginBottom: 10
                 *!/
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



     );*/

    /*
        var text = chart.renderer.text('<span style="font-size: 50px;text-align: center">89 %</span>' +
                '<br/><br/><Strong> Coverage</Strong>').add(),
            textBBox = text.getBBox(),
            x = chart.plotLeft + (chart.plotWidth *0.4) - (textBBox.width * 0.2),
            y = chart.plotTop + (chart.plotHeight * 0.5) + (textBBox.height * 0.25);
    // Set position and styles
        text.attr({ x: x, y: y }).css({ fontSize: '20px', color: '#666666' }).add();*/


    function reportingPerformance(reportingData) {

        var distribution = _.pluck(reportingData, 'distributed_rate');
        var timeliness = _.pluck(reportingData, 'ontime_rate');
        var completeness = _.pluck(reportingData, 'reported_rate');
        var approved = _.pluck(reportingData, 'approved_rate');
        var period = _.pluck(reportingData, 'period_name');

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
                categories: period
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
                    data: approved,
                    tooltip: {
                        valueSuffix: ' %'
                    }
                }

            ]
        });
    }


    //Coverage


    var categoryFunc = function (category) {

        var cat = _.pluck(category, 'catagorization');
        var total = _.pluck(category, 'total');

        $scope.categorized = cat;

        var result = [], i = -1,
            color = {Cat_1: '#52C552', Cat_2: '#509fc5', Cat_3: '#E4E44A', Cat_4: '#FF0000'};

        while (total[++i]) {
            result.push([{color: color[cat[i]], y: total[i]}]);
        }
        var mergedArrays = [].concat.apply([], result);

        $('#container8').highcharts({
            chart: {
                type: 'bar',
                height: 220

            },
            title: {
                text: ' <span style="color:cadetblue;font-size:15px">Categorization based on DTP-HepB+Hib-1 Coverage and Dropout</span>'
            },
            credits: {enabled: false},
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: ['Category A', 'Category B', 'Category C', 'Category D'],
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


    var d = new Date();
    var pastYear = d.getFullYear() - 2;
    var periodSorter = function (value) {
        return parseInt(value.id, 10);
    };

    function getPeriodSlider(data) {

        var sortedValues = _.sortBy(data, periodSorter);
        var period_name = _.pluck(sortedValues, 'name');

        console.log(period_name);

        $scope.slider = {
            value: 1,
            options: {
                floor: 1,
                ceil: period_name.length,
                translate: function (value, sliderId, label) {
                    return period_name[value - 1];
                },
                interval: 10,
                // logScale:true,
                //ticksArray: [0, 2,3,4,5,6,7,8,9,10,11,12],
                // showSelectionBar: true,
                showTicksValues: true,
                showTicks: true,
                focus: true

            }
            /* value: 1,
             options: {
                 floor: 1,
                 ceil: 12,
                 showTicksValues: true,
                 translate: function(value) {
                     return value;
                 },
                 ticksValuesTooltip: function(v) {
                     return v;
                 },
                 showTicks: true

             }*/
        };


        $(function () {
            $('#sampleInput').popover();
            $('#selectb').select2();
            $('#selectb-popover').popover();
            $('select').material_select();
            $("#e1").select2();

            $("#select2insidemodal").select2({
                dropdownParent: $("#myModal")
            });
            $("#idSelect").select2({
                width: "100%"
            });
        });
    }


    $scope.popover = {
        "title": "Title",
        "content": "Hello Popover<br />This is a multiline message!",
        "saved": true
    };

    $scope.prod = [{id: 2, name: 'name'}, {id: 3, name: '777'}];
    $scope.showfilter = false;

    $scope.myModal = false;
    $scope.showFilter = function () {
        $scope.products = ProductFilteredData;

        $scope.showfilter = true;
        $scope.myModal = true;
    };
    $scope.hideFilter = function () {
        $scope.showfilter = false;
    };
    $(".button-collapse").sideNav();
    $('#modal1').modal('open');

    function getPeriodByYear(modelValue) {

        ReportPeriodsByYear.get({
            year: parseInt(modelValue, 10)
        }, function (data) {
            getPeriodSlider(data.periods);
        });
    }

    $scope.year_slider = {
        value: parseInt(pastYear, 10),
        options: {
            floor: parseInt(pastYear, 10),
            ceil: parseInt(d.getFullYear(), 10),

            translate: function (value, sliderId, label) {
                return value;
            }, onChange: function (sliderId, modelValue, highValue, pointerType) {
                console.log(modelValue);
                getPeriodByYear(modelValue);
                return sliderId;
            },
            interval: 1,
            //ticksArray: [0, 2,3,4,5,6,7,8,9,10,11,12],
            showSelectionBar: false,
            //  showTicksValues: true,
            showTicks: true
        }
        /* value: 1,
         options: {
             floor: 1,
             ceil: 12,
             showTicksValues: true,
             translate: function(value) {
                 return value;
             },
             ticksValuesTooltip: function(v) {
                 return v;
             },
             showTicks: true

         }*/
    };


    $scope.card = {};
    $scope.showIcons = function (card) {

        $scope.showicons = true;

        /*    if(!card.displayTable && !card.displayMap && !card.displayColumn){
                card.showicons = true;
            }else{
                card.showicons = false;
            }*/
    };
    $scope.changeChart = function (data) {
        console.log(data);
    };
    $scope.icons = [
        {name: 'table', image: 'table.jpg', action: ''},
        {name: 'column', image: 'bar.png', action: ''},
        {name: 'line', image: 'line.png', action: ''},
        {name: 'combined', image: 'combined.jpg', action: ''},
        {name: 'column', image: 'column.png', action: ''},
        {name: 'area', image: 'area.jpg', action: ''},
        {name: 'pie', image: 'pie.png', action: ''}
    ];
    $scope.hideIcons = function (card) {
        console.log("cARD");

        $scope.showicons = false;


        //card.showicons = false;
    };

    $(function () {
        $('#container15').highcharts({
            chart: {
                type: 'column'
            },

            plotOptions: {
                pie: {
                    innerSize: '70%'
                }
            },

            title: {
                verticalAlign: 'top',
                floating: true,
                text: 'Home' + '200%'
            },

            series: [{
                data: [
                    ['Firefox', 44.2],
                    ['IE7', 26.6],
                    ['IE6', 20],
                    ['Chrome', 3.1],
                    ['Other', 5.4]
                ]
            }]
        });
    });


    //Map

    function getExportDataFunction(features) {

        var arr = [];
        angular.forEach(features, function (value, key) {
            if (value.monthlyEstimate > 0) {
                var percentage = {'percentage': ((value.period / value.expected) * 100).toFixed(0) + ' %'};
                arr.push(angular.extend(value, percentage));
            }
        });
        $scope.exportData = arr;
    }

    $scope.showProduct = false;
    var getProduct = function () {
        ProductService.get(parseInt($scope.filter.product, 10)).then(function (data) {
            $scope.product = data;
            $scope.showProduct = true;
            console.log(data);
        });
    };
    getProduct();


    $scope.geojson = {};

    $scope.default_indicator = "ever_over_total";

    $scope.expectedFilter = function (item) {
        return item.monthlyEstimate > 0;
    };

    $scope.style = function (feature) {
        if (feature.monthlyEstimate > 0)
            console.log(feature.monthlyEstimate);

        if ($scope.filter !== undefined && $scope.filter.indicator_type !== undefined) {
            $scope.indicator_type = $scope.filter.indicator_type;
        }
        else {
            $scope.indicator_type = $scope.default_indicator;
        }
        var color = ($scope.indicator_type === 'ever_over_total') ? interpolateCoverage(feature.vaccinated, feature.monthlyEstimate, feature.coverageClassification) : ($scope.indicator_type === 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);
        return {
            fillColor: color,
            weight: 1,
            opacity: 1,
            color: 'white',
            dashArray: '1',
            fillOpacity: 0.7
        };
    };

    $scope.drawMap = function (json) {

        angular.extend($scope, {
            geojson: {
                data: json,
                style: $scope.style,
                onEachFeature: onEachFeatureForCoverageMap,
                resetStyleOnMouseout: true
            }
        });
        $scope.$apply();
    };

    $scope.loadMap = function (params) {

        $.getJSON('/gis/vaccine-coverage.json', params, function (data) {
            $scope.features = data.map;
            getExportDataFunction($scope.features);
            angular.forEach($scope.features, function (feature) {
                feature.geometry_text = feature.geometry;
                feature.geometry = JSON.parse(feature.geometry);
                feature.type = "Feature";
                feature.properties = {};
                feature.properties.name = feature.name;
                feature.properties.id = feature.id;
            });
            console.log($scope.features);

            $scope.drawMap({
                "type": "FeatureCollection",
                "features": $scope.features
            });
            zoomAndCenterMap(leafletData, $scope);
        });


    };
    initiateCoverageMap($scope);

    $scope.onDetailClicked = function (feature) {
        console.log(feature);
        $scope.currentFeature = feature;
        $scope.$broadcast('openDialogBox');
    };

    $scope.$watch('period', function (newVal, oldVal) {
        console.log($scope.filter);
        // $scope.onChange();
        // $scope.$parent.OnFilterChanged();
    });

}

StockAvailabilityControllerFunc1.resolve = {

    ProductFilteredData: function ($q, $timeout, ReportProductsWithoutDescriptionsAndWithoutProgram) {
        var deferred = $q.defer();
        $timeout(function () {
            ReportProductsWithoutDescriptionsAndWithoutProgram.get({}, function (data) {
                console.log(data);
                deferred.resolve(data.productList);
            }, {});
        }, 100);
        return deferred.promise;
    },
    YearFilteredData: function ($q, $timeout, OperationYears) {
        var deferred = $q.defer();
        $timeout(function () {
            OperationYears.get({}, function (data) {
                deferred.resolve(data.years);
            }, {});
        }, 100);
        return deferred.promise;
    },

    homeFacility: function ($q, $timeout, HomeFacilityWithType) {
        var deferred = $q.defer();
        var homeFacility = {};

        $timeout(function () {
            HomeFacilityWithType.get({}, function (data) {
                homeFacility = data.homeFacility;
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    }


};