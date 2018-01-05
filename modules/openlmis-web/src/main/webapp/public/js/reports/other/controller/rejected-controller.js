function RejectedControllerFunction($scope, GetRejectedRnRReport) {
    "use strict";

    function getRejectionRate(rows) {

        var groupByZone = _.groupBy(rows, 'zoneName');

        var data = _.map(groupByZone, function (value, key) {

            var total = 0;
            for (var i = 0; i < value.length; i++) {
                var rejectedCount = value[i].rejectedCount;
                total += (rejectedCount);
            }
            return {'key': key, 'total': total};
        });
        var totalValues = _.pluck(data, 'total');
        var key = _.pluck(data, 'key');

        var maximumValue = Math.max.apply(null, totalValues);

        var array1 = key, array3 = maximumValue, result = [], i = -1;

        while (array1[++i]) {
            if (array3[i] === totalValues)
                result.push({
                    name: array1[i], y: array3[i], sliced: true,
                    selected: true
                });
            else
                result.push([array1[i], array3[i]]);
        }
        functionalData(result);
    }

    GetRejectedRnRReport.get({}, function (data) {
        $scope.rejectedRnRLis = data.pages.rows;
        getRejectionRate(data.pages.rows);
    });


    var functionalData = function (data) {
        // Create the chart
        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'rejected',
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 45
                },
                style: {
                    fontFamily: 'helvetica'
                }
            },
            credits: {enabled: false},
            title: {
                text: 'RnR Rejection Rate by MSD Zone'
            },
            yAxis: {
                title: {
                    text: 'Total percent market share'
                }
            },
            plotOptions: {
                pie: {
                    shadow: false,
                    cursor: 'pointer',
                    slicedOffset: 30,
                }
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.point.name + '</b>: ' + this.y + ' %';
                }
            },
            series: [{
                name: 'zones',
                data: data,
                /* data: [["Firefox",6],["MSIE",4],["Chrome",7],{
                     name: 'May',
                     y: 9,
                     sliced: true,
                     selected: true
                 }],*/
                size: '60%',
                innerSize: '60%',
                showInLegend: true,
                dataLabels: {
                    enabled: true
                }
            }]
        });
    };


}

RejectedControllerFunction.resolve = {};