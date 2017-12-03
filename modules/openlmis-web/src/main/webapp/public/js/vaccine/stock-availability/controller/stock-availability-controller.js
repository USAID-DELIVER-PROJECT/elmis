function StockAvailabilityControllerFunc($scope, AvailableStockDashboard) {
    $scope.periodName = [];
    $scope.filter = {};

    $scope.filter.product=2412;

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
                gridLineColor: '#ddd',
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
                    name:'% of Facilities Of Functional CCE',
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


}
StockAvailabilityControllerFunc.resolve = {};