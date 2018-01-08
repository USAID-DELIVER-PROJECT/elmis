function RejectedControllerFunction($scope, GetRejectedRnRReport,$state) {
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

        var array1 = key, array3 = totalValues, result = [], i = -1;

        while (array1[++i]) {
            if (array3[i] === maximumValue)
                result.push({
                    name: array1[i], y: array3[i], sliced: true,
                    selected: true,color:'red'
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


    var displayEventData = function (event) {
        var params = {'zone':event.point.name,'value':event.point.y};
        $state.go('rejectionByZoneView',params);

    };
    var functionalData = function (data) {

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: 'rejected',
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 90
                },
                style: {
                    fontFamily: 'helvetica'
                }
            },
            credits: {enabled: false},
            title: {
                text: 'RnR Rejection by Zone'
            },
            plotOptions: {
                pie: {
                    shadow: false,
                    cursor: 'pointer',
                    slicedOffset: 20
                }
            },
            tooltip: {
                formatter: function () {
                    return '<b>' + this.point.name + '</b>: ' + this.y;
                }
            },
            series: [{
                name: 'zones',
                data: data,
                size: '60%',
                innerSize: '70%',
                showInLegend: false,
                dataLabels: {
                    enabled: true
                }, animation: true,
                point:{
                    events:{
                        click: function (event) {
                            displayEventData(event);

                        }
                    }
                }
            }]
        });
    };


}

RejectedControllerFunction.resolve = {};