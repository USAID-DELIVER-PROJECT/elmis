function DashboardControllerFunction($scope, leafletData,$routeParams,RnRStatusDetail,ngTableParams,$filter) {
    $scope.data = "mamama";
    console.log("Reached Here");
//year=2017&schedule=1&period=114&program=3
$scope.filter={
   period: "114",
    program:"3",
    schedule: 1,
    year: "2017",
    zoneId:18
};
    $scope.geojson = {};

    $scope.default_indicator = "period_over_expected";

    $scope.expectedFilter = function (item) {
        return item.expected > 0;
    };

    $scope.style = function (feature) {
        if ($scope.filter !== undefined && $scope.filter.indicator_type !== undefined) {
            $scope.indicator_type = $scope.filter.indicator_type;
        }
        else {
            $scope.indicator_type = $scope.default_indicator;
        }
        var color = ($scope.indicator_type === 'ever_over_total') ? interpolate(feature.ever, feature.total) : ($scope.indicator_type === 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);

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
                onEachFeature: onEachFeature,
                resetStyleOnMouseout: true
            }
        });
        $scope.$apply();
    };

    function getExportDataFunction(features) {

        var arr = [];
        angular.forEach(features, function (value, key) {
            if (value.expected > 0) {
                var percentage = {'percentage': ((value.period / value.expected) * 100).toFixed(0) + ' %'};
                arr.push(angular.extend(value, percentage));
            }
        });
        $scope.exportData = arr;
    }

    filter();
    function filter() {

        $.getJSON('/gis/reporting-rate.json', $scope.filter, function (data) {
            $scope.features = data.map;
            var dataValues = [];
            var districts = _.pluck( $scope.features, 'name');
            var  expected= _.pluck( $scope.features, 'expected');
            var  reported= _.pluck( $scope.features, 'period');
            var expArray=[{name:'expected',data:expected},
                {name:'reported',data:reported}
            ];
           var districtMap = _.groupBy($scope.features,'name');
            getExportDataFunction($scope.features);
            angular.forEach(districtMap,function () {

            });
            angular.forEach($scope.features, function (feature) {
                feature.geometry_text = feature.geometry;
                feature.geometry = JSON.parse(feature.geometry);
                feature.type = "Feature";
                feature.properties = {};
                feature.properties.name = feature.name;
                feature.properties.id = feature.id;
                dataValues.push({
                    name:feature.name,
                    data:[ parseInt('200',feature.expected),parseInt('200',feature.period)]
                    // period: parseInt('200',feature.period),
                    // value:300,
                    // color: 'green'
                    // // color:interpolateCoverage(code.cumulative_vaccinated,code.monthly_district_target,code.coverageclassification.toLowerCase())

                });
            });
            console.log(JSON.stringify( $scope.features))
            $scope.drawMap({
                "type": "FeatureCollection",
                "features": $scope.features
            });
            zoomAndCenterMap(leafletData, $scope);
            var separators = Highcharts.geojson(Highcharts.maps['countries/zm/zm-all'], 'mapline');
            Highcharts.chart('container', {
                chart: {
                    type: 'bar'
                },
                title: {
                    text: 'Reporting Rate'
                },
                xAxis: {
                    categories: districts
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Reporting Rate'
                    }
                },
                legend: {
                    reversed: true
                },
                plotOptions: {
                    series: {
                        stacking: 'normal'
                    }
                },
                series:  expArray
            });
            Highcharts.mapChart('container1', {
                chart: {
                    map: 'countries/zm/zm-all'
                }, credits: {enabled: false},

                title: {
                    text: '<span style="font-size: 15px !important;color: #0c9083;text-align: center"> Reporting Rate, </span>'
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

                legend: {

                },

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
                        name: 'Non Reporting'
                    }, {
                        from: 80,
                        to: 90,
                        color: '#ffdb00',
                        name:'Partial Reporting'
                    }, {
                        from: 90,
                        color: '#006600',
                        name:'Fully Reporting'

                    },
                        , {
                            from: 90,
                            color: '#000000',
                            name:'Not Expected To'

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
                    data:   dataValues ,
                    keys: ['name', 'value'],
                    joinBy: 'name',
                    name: 'Coverage',
                    dataLabels: {
                        enabled: true,
                        format: '{point.properties.postal-code}'
                    }, shadow: false
                }]
            });

        });


    };

    initiateMap($scope);

    $scope.onDetailClicked = function (feature) {
        $scope.currentFeature = feature;
        $scope.$broadcast('openDialogBox');
    };

// Instantiate the map


// Prepare random data
    var data = [
        ['Schleswig-Holstein', 1728]
    ];

    $.getJSON('https://cdn.rawgit.com/highcharts/highcharts/057b672172ccc6c08fe7dbb27fc17ebca3f5b770/samples/data/germany.geo.json', function (geojson) {

        // Initiate the chart

    });

//rnr status


}
DashboardControllerFunction.resolve={


};