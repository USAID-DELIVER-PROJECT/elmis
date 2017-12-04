function GeoVaccineCoverageController($scope,leafletData,GetProductById){

    $scope.filter = {};

    $scope.filter.product=2421;
    $scope.filter.period=121;

    $scope.filter.year= (new Date()).getFullYear();


    $scope.geojson = {};

    $scope.default_indicator = "ever_over_total";

    $scope.expectedFilter = function (item) {
        return item.monthlyEstimate > 0;
    };

    $scope.style = function (feature) {
        if ($scope.filter !== undefined && $scope.filter.indicator_type !== undefined) {
            $scope.indicator_type = $scope.filter.indicator_type;
        }
        else {
            $scope.indicator_type = $scope.default_indicator;
        }
        var color = ($scope.indicator_type === 'ever_over_total') ? interpolateCoverage(feature.vaccinated,feature.monthlyEstimate,feature.coverageClassification) : ($scope.indicator_type === 'ever_over_expected') ? interpolate(feature.ever, feature.expected) : interpolate(feature.period, feature.expected);
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

    $scope.OnFilterChanged = function () {

        GetProductById.get({id:parseInt($scope.filter.product,10)}, function (data) {
                $scope.product = data.productDTO.product.primaryName;
            $scope.showProduct = true;
        });
        $.getJSON('/gis/vaccine-coverage.json', $scope.filter, function (data) {
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



}