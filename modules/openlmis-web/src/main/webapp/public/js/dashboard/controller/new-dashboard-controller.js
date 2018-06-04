function DashboardControllerFunction($scope) {

 function calculatePercentage(data){
     var total = 0;
     angular.forEach(data,function (da,index) {
         total += da.current;
     });
     return parseInt(total/parseInt(data.length,10),10);
 }

$scope.orderFillRateByZone ={
    "zones": [
        {
            "name": "North East",
            "prev": 89,
            "current": 90,
            "status": "good"
        },
        {
            "name": "Western",
            "prev": 89,
            "current": 89,
            "status": "normal"
        },
        {
            "name": "Southern",
            "prev": 50,
            "current": 69,
            "status": "bad"
        },{
            "name": "North Western",
            "prev": 70,
            "current": 20,
            "status": "bad"
        },{
            "name": "Northern",
            "prev": 70,
            "current": 60,
            "status": "bad"
        },{
            "name": "Muchinga",
            "prev": 70,
            "current": 90,
            "status": "bad"
        },{
            "name": "Luapula",
            "prev": 70,
            "current": 90,
            "status": "bad"
        },{
            "name": "Copperbelt",
            "prev": 70,
            "current": 20,
            "status": "bad"
        },{
            "name": "Central",
            "prev": 70,
            "current": 80,
            "status": "bad"
        },{
            "name": "Lusaka Province",
            "prev": 70,
            "current": 85,
            "status": "bad"
        }
    ]
};

$scope.stockAvailability ={
    "zones": [
        {
            "name": "North East",
            "prev": 75,
            "current": 85,
            "status": "good"
        },
        {
            "name": "Western",
            "prev": 80,
            "current": 81,
            "status": "normal"
        },
        {
            "name": "Southern",
            "prev": 61,
            "current": 71,
            "status": "bad"
        },{
            "name": "North Western",
            "prev": 70,
            "current": 75,
            "status": "bad"
        },{
            "name": "Northern",
            "prev": 50,
            "current": 55,
            "status": "bad"
        },{
            "name": "Muchinga",
            "prev": 30,
            "current": 79,
            "status": "bad"
        },{
            "name": "Luapula",
            "prev": 40,
            "current": 79,
            "status": "bad"
        },{
            "name": "Copperbelt",
            "prev": 90,
            "current": 85,
            "status": "bad"
        },{
            "name": "Central",
            "prev": 75,
            "current": 86,
            "status": "bad"
        },{
            "name": "Lusaka Province",
            "prev": 89,
            "current": 90,
            "status": "bad"
        }
    ]
};
$scope.reportingRate ={
    "zones": [
        {
            "name": "North East",
            "prev": 60,
            "current": 80,
            "status": "good"
        },
        {
            "name": "Western",
            "prev": 89,
            "current": 81,
            "status": "normal"
        },
        {
            "name": "Southern",
            "prev": 81,
            "current": 89,
            "status": "bad"
        },{
            "name": "North Western",
            "prev": 81,
            "current": 90,
            "status": "bad"
        },{
            "name": "Northern",
            "prev": 76,
            "current": 83,
            "status": "bad"
        },{
            "name": "Muchinga",
            "prev": 84,
            "current": 98,
            "status": "bad"
        },{
            "name": "Luapula",
            "prev": 70,
            "current": 80,
            "status": "bad"
        },{
            "name": "Copperbelt",
            "prev": 50,
            "current": 50,
            "status": "bad"
        },{
            "name": "Central",
            "prev": 60,
            "current": 79,
            "status": "bad"
        },{
            "name": "Lusaka Province",
            "prev": 75,
            "current": 80,
            "status": "bad"
        }
    ]
};

function borderColor(data){
    return (data >= 80)?'green':(data<80 && data>70)?'orange':'red';

}

$scope.dynamicPerformanceChart = function(data,chartId,name,result)

{

        var gaugeOptions = {

            chart: {
                type: 'solidgauge',
                margin: [0, 0, 0, 0],
                backgroundColor: 'transparent'
            },
            title: null,
            yAxis: {
                min: 0,
                max: 100,
                minColor: borderColor(result),
                maxColor: borderColor(result),
                lineWidth: 0,
                tickWidth: 0,
                minorTickLength: 0,
                // minTickInterval: 500,
                labels: {
                    enabled: false
                }
            },
            pane: {
                size: '100%',
                center: ['50%', '50%'],
                startAngle: 0,
                endAngle: 360,
                background: {
                    borderWidth: 20,
                    backgroundColor: '#DBDBDB',
                    shape: 'arch',
                    borderColor: '#DBDBDB',
                    outerRadius: '80%',
                    innerRadius: '80%'
                }
            },
            tooltip: {
                enabled: true
            },
            plotOptions: {
                solidgauge: {
                    borderColor: borderColor(result),
                    borderWidth: 18,
                    radius: 75,
                    innerRadius: '80%',
                    dataLabels: {
                        borderWidth: 0,
                        useHTML: true,
                        enable: true
                    }
                }
            },
            series: [{
                name: name,
                data: [result],
                dataLabels: {
                    format: '<div style="Width: 30px;text-align:center"><span style="font-size:20px;color:"'+borderColor(data.ofr)+'"><br>{y}%</span></div>'
                }

            }],

            credits: {
                enabled: false
            }
        };
        $(chartId).highcharts(gaugeOptions);
    };
    $scope.dynamicPerformanceChart($scope.orderFillRateByZone,'#container-order-fill-rate','OrderFillRate',calculatePercentage($scope.orderFillRateByZone.zones));
    $scope.dynamicPerformanceChart($scope.stockAvailability,'#stock-availability','StockAvailability',calculatePercentage($scope.stockAvailability.zones));
    $scope.dynamicPerformanceChart($scope.reportingRate,'#reporting-rate','ReportingRate',calculatePercentage($scope.reportingRate.zones));

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
            console.log($scope.products);
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

}

DashboardControllerFunction.resolve = {};