function DashboardControllerFunction($scope) {
    $scope.data = "mamama";


    var dataValues = [
        ['zm-lp', 0],
        ['zm-no', 1],
        ['zm-ce', 2],
        ['zm-ea', 3],
        ['zm-ls', 4],
        ['zm-co', 5],
        ['zm-nw', 6],
        ['zm-so', 7],
        ['zm-we', 8],
        ['zm-mu', 9]
    ];

    $scope.loadStockStatusByLocation = function (params) {

        $.getJSON('/gis/stock-status-products.json', params, function (data) {
            $scope.products = data.products;
        });

        Highcharts.mapChart('stock_status_map', {
            chart: {
                map: 'countries/zm/zm-all'
            }, credits: {enabled: false},

            title: {
                text: '<span style="font-size: 15px !important;color: #0c9083;text-align: center">Stock Status By Location</span>'
            },

            subtitle: {
                text: '',
                floating: true,
                align: 'right',
                y: 50,
                style: {
                    fontSize: '16px'
                }
            },

            legend: {},

            /*   colorAxis: {
                   min: 0,
                   minColor: '#FF0000',
                   maxColor: '#52C552'
               },*/
            colorAxis: {
                dataClasses: [{
                    from: 0,
                    to: 80,
                    color: '#ff0d00',
                    name: ''
                }, {
                    from: 80,
                    to: 90,
                    color: '#ffdb00',
                    name: ''
                }, {
                    from: 90,
                    color: '#006600',
                    name: ' '

                }]
            },

            mapNavigation: {
                enabled: true,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            },

            plotOptions: {
                map: {
                    states: {
                        /* hover: {
                             color: '#EEDD66'
                         }*/
                    }
                }
            },

            series: [{
                data: dataValues,
                // joinBy: ['hc-key', 'code'],
                name: 'Coverage',
                dataLabels: {
                    enabled: true,
                    format: '{point.properties.postal-code}'
                }, shadow: false
            }]
        });


    };

    var defaultParam = {
        year: parseInt(2017, 10),
        schedule: parseInt(1, 10),
        period: parseInt(115, 10),
        program: parseInt(3, 10)
    };
    $scope.loadStockStatusByLocation(defaultParam);

    $scope.loadCoverageMap = function (params) {

        GetCoverageMapInfo.get(params).then(function (data) {
            var dataValues = [];
            Highcharts.each(data, function (code, i) {
                var colorV;
                if (parseInt(code.value, 10) <= 0 || isNaN(code.value))
                    colorV = 'black';

                dataValues.push({
                    code: code.code,
                    value: parseInt(code.value, 10),
                    color: colorV
                    // color:interpolateCoverage(code.cumulative_vaccinated,code.monthly_district_target,code.coverageclassification.toLowerCase())

                });
            });
            var small = $('#coverage_map').width() < 400;
            var separators = Highcharts.geojson(Highcharts.maps['countries/tz/tz-all'], 'mapline');


            Highcharts.mapChart('coverage_map', {
                chart: {
                    map: 'countries/tz/tz-all'
                }, credits: {enabled: false},

                title: {
                    text: '<span style="font-size: 15px !important;color: #0c9083;text-align: center">' + params.productName + '-' + params.dose + ' Coverage By Region, ' + params.year + '</span>'
                },

                subtitle: {
                    text: '',
                    floating: true,
                    align: 'right',
                    y: 50,
                    style: {
                        fontSize: '16px'
                    }
                },

                legend: {},

                /*   colorAxis: {
                       min: 0,
                       minColor: '#FF0000',
                       maxColor: '#52C552'
                   },*/
                colorAxis: {
                    dataClasses: [{
                        from: 0,
                        to: 80,
                        color: '#ff0d00',
                        name: 'Below 80%'
                    }, {
                        from: 80,
                        to: 90,
                        color: '#ffdb00',
                        name: '80% to 89%'
                    }, {
                        from: 90,
                        color: '#006600',
                        name: '90%+'

                    }]
                },

                mapNavigation: {
                    enabled: true,
                    buttonOptions: {
                        verticalAlign: 'bottom'
                    }
                },

                plotOptions: {
                    map: {
                        states: {
                            /* hover: {
                                 color: '#EEDD66'
                             }*/
                        }
                    }
                },

                series: [{
                    data: dataValues,
                    joinBy: ['hc-key', 'code'],
                    name: 'Coverage',
                    dataLabels: {
                        enabled: true,
                        format: '{point.properties.postal-code}'
                    }, shadow: false
                }/*, {
                    type: 'mapline',
                    data: separators,
                    color: 'silver',
                    enableMouseTracking: false,
                    animation: {
                        duration: 500
                    }
                }*/]/*,

                drilldown: {
                    activeDataLabelStyle: {
                        color: '#FFFFFF',
                        textDecoration: 'none',
                        textOutline: '1px #000000'
                    },
                    drillUpButton: {
                        relativeTo: 'spacingBox',
                        position: {
                            x: 0,
                            y: 60
                        }
                    }
                }*/
            });


            /*console.log(dataValues);
                        var coverage = _.pluck(data,'value');
                        var mapCode = _.pluck(data,'code');
                        var mapData =_.zip(mapCode,coverage);
            mapInfo(mapData,'coverage_map');
                        console.log(mapData);*/


            /*
                        var data1 = Highcharts.geojson(Highcharts.maps['countries/tz/tz-all']),
                            separators = Highcharts.geojson(Highcharts.maps['countries/tz/tz-all'], 'mapline'),
                            // Some responsiveness
                            small = $('#coverage_map').width() < 400;


                        // Set drilldown pointers
                        $.each(data1, function (i) {

                            // this.drilldown = this.properties['hc-key'];

                            this.value = i;


                            // Non-random bogus data
                        });
            */


            // Initiate the chart
            /*
                        $('#coverage_map').highcharts('Map', {

                            chart: {
                                /!*    events: {
                                        drilldown: function (e) {
                                            if (!e.seriesOptions) {
                                                var chart = this,
                                                    mapKey = 'countries/us/' + e.point.drilldown + '-all',
                                                    // Handle error, the timeout is cleared on success
                                                    fail = setTimeout(function () {
                                                        if (!Highcharts.maps[mapKey]) {
                                                            chart.showLoading('<i class="icon-frown"></i> Failed loading ' + e.point.name);
                                                            fail = setTimeout(function () {
                                                                chart.hideLoading();
                                                            }, 1000);
                                                        }
                                                    }, 3000);

                                                // Show the spinner
                                                chart.showLoading('<i class="icon-spinner icon-spin icon-3x"></i>'); // Font Awesome spinner

                                                // Load the drilldown map
                                                $.getScript('https://code.highcharts.com/mapdata/' + mapKey + '.js', function () {

                                                    data = Highcharts.geojson(Highcharts.maps[mapKey]);

                                                    // Set a non-random bogus value
                                                    $.each(data, function (i) {
                                                        this.value = i;
                                                    });

                                                    // Hide loading and add series
                                                    chart.hideLoading();
                                                    clearTimeout(fail);
                                                    chart.addSeriesAsDrilldown(e.point, {
                                                        name: e.point.name,
                                                        data: data,
                                                        dataLabels: {
                                                            enabled: true,
                                                            format: '{point.name}'
                                                        }
                                                    });
                                                });
                                            }

                                            this.setTitle(null, {text: e.point.name});
                                        },
                                        drillup: function () {
                                            this.setTitle(null, {text: ''});
                                        }
                                    }*!/
                            },
                            credits: {
                                enabled: false
                            },
                            title: {
                                text: 'DTP3 Coverage By Region'
                            },

                            subtitle: {
                                text: '',
                                floating: true,
                                align: 'right',
                                y: 50,
                                style: {
                                    fontSize: '16px'
                                }
                            },

                            legend: small ? {} : {
                                layout: 'vertical',
                                align: 'right',
                                verticalAlign: 'middle'
                            },

                            colorAxis: {
                                min: 0,
                                minColor: '#E6E7E8',
                                maxColor: '#005645'
                            },

                            mapNavigation: {
                                enabled: true,
                                buttonOptions: {
                                    verticalAlign: 'bottom'
                                }
                            },

                            plotOptions: {
                                map: {
                                    states: {
                                        hover: {
                                            color: '#EEDD66'
                                        }
                                    }
                                }
                            },

                            series: [{
                                data: mapData,
                                mapData: data1,
                                joinBy: ['hc-key', 'mapCode'],
                                name: 'Region',
                                dataLabels: {
                                    enabled: true,
                                    format: '{point.properties.value}'
                                }
                            }, {
                                type: 'mapline',
                                data: separators,
                                color: 'silver',
                                enableMouseTracking: false,
                                animation: {
                                    duration: 500
                                }
                            }],

                            drilldown: {
                                activeDataLabelStyle: {
                                    color: '#FFFFFF',
                                    textDecoration: 'none',
                                    textOutline: '1px #000000'
                                },
                                drillUpButton: {
                                    relativeTo: 'spacingBox',
                                    position: {
                                        x: 0,
                                        y: 60
                                    }
                                }
                            }
                        });
            */


        });

    };


}

DashboardControllerFunction.resolve = {};