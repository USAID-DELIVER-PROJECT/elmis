/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//For Vaccine Coverage Map

function interpolateCoverage(value, count, color) {
    var color2 = (color === 'good') ? '#52C552' : (color === 'normal') ? '#509fc5' : (color === 'warn') ? '#E4E44A' : '#FF0000';
    var val = parseFloat(value) / parseFloat(count);
    var interpolator = chroma.interpolate.bezier(['red', color2]);
    return interpolator(val).hex();
}

function initiateCoverageMap(scope) {
    angular.extend(scope, {
        layers: {
            baselayers: {
                googleTerrain: {
                    name: 'Terrain',
                    layerType: 'TERRAIN',
                    type: 'google'
                },
                googleHybrid: {
                    name: 'Hybrid',
                    layerType: 'HYBRID',
                    type: 'google'
                },
                googleRoadmap: {
                    name: 'Streets',
                    layerType: 'ROADMAP',
                    type: 'google'
                }
            }
        },
        legend: {
            position: 'bottomleft',
            colors: ['#FF0000', '#FFFF00', '#509fc5','#52C552', "#000000"],
            labels: ['Coverage <50%', '50% <= Coverage <80%', '80% <= Coverage <90%', 'Coverage > 90%', 'Not Started to Report']
        }
    });

    scope.indicator_types = [
        {
            code: 'ever_over_total',
            name: 'Coverage'
        },
        {
            code: 'ever_over_expected',
            name: 'Ever Reported / Expected Facilities'
        },
        {
            code: 'period_over_expected',
            name: 'Reported during period / Expected Facilities'
        }
    ];


    scope.viewOptins = [
        {id: '0', name: 'Non Reporting Only'},
        {id: '1', name: 'Reporting Only'},
        {id: '2', name: 'All'}
    ];

}

function popupFormatForCoverageMap(feature) {
    return '<table class="table table-bordered" style="width: 500px">' +
        '<tr><th><strong>Region</strong></th>' +
        '<th><strong> '+feature.region+'</strong></th>' +
        '</tr>' +
        '<tr>' +
        '<th><strong>District</strong></th>' +
        '<th>' + feature.properties.name + '</th>' +
        '<th>Previous Period</th>' +
        '<th><b>Current Period</b></th>' +
        '</tr>' +
        '<tr>' +
        '<td>Health Facilities</td>' +
        '<td style="text-align: right">Expected</td>' +
        '<td class="number">' + feature.prevExpected + '</td>' +
        '<td class="number">' + feature.expected + '</td>' +
        '</tr>' +
        '<tr><td> </td>' +
        '<td class="number" style="text-align: right">Reported</td>' +
        '<td class="number">' + feature.prevPeriod + '</td>' +
        '<td class="number">' + feature.period + '</td>' +
        '</tr>' +
        '<tr><td>Stock Status</td>' +
        '<td class="number">DVS Stock On Hand</td>' +
        '<td class="number">' + feature.prevSOH + '</td>' +
        '<td class="number">' + feature.soh + '</td>' +
        '</tr>' +
        '<tr><td class="bold"></b></td><td class="number bold">MOS</td>' +
        '<td class="number bold">' + feature.prevMOS + '</td>' +
        '<td class="number bold">' + feature.mos + '</td>' +
        '</tr>' +
        '<tr><td>Out Reach</td>' +
        '<td class="number">Planned Session</td>' +
        '<td class="number">' + feature.prevPlanned + '</td>' +
        '<td class="number">' + feature.planned + '</td>' +
        '</tr>' +
        '<tr><td class="bold"></b></td><td class="number bold">Conducted Session</td>' +
        '<td class="number bold">' + feature.prevOutReachSession + '</td>' +
        '<td class="number bold">' + feature.outReachSession + '</td>' +
        '</tr>';
        /* +
        '<tr><td>Equipment Status</td>' +
        '<td class="number">Functional</td>' +
        '<td class="number">' + feature.ever + '</td>' +
        '<td class="number">' + feature.ever + '</td>' +
        '</tr>' +
        '<tr><td class="bold"></b></td><td class="number bold">Non Functional</td>' +
        '<td class="number bold">' + feature.total + '</td>' +
        '<td class="number bold">' + feature.total + '</td>' +
        '</tr>';*/
}

function onEachFeatureForCoverageMap(feature, layer) {
    layer.bindPopup(popupFormatForCoverageMap(feature));
}


//End Vaccine Coverage Map presentation

function interpolate(value, count) {
    var val = parseFloat(value) / parseFloat(count);
    var interpolator = chroma.interpolate.bezier(['red', 'yellow', 'green']);
    return interpolator(val).hex();
}

function initiateMap(scope) {
    angular.extend(scope, {
        layers: {
            baselayers: {
                googleTerrain: {
                    name: 'Terrain',
                    layerType: 'TERRAIN',
                    type: 'google'
                },
                googleHybrid: {
                    name: 'Hybrid',
                    layerType: 'HYBRID',
                    type: 'google'
                },
                googleRoadmap: {
                    name: 'Streets',
                    layerType: 'ROADMAP',
                    type: 'google'
                }
            }
        },
        legend: {
            position: 'bottomleft',
            colors: ['#FF0000', '#FFFF00', '#5eb95e', "#000000"],
            labels: ['Non Reporting', 'Partial Reporting ', 'Fully Reporting', 'Not expected to Report']
        }
    });

    scope.indicator_types = [
        {
            code: 'ever_over_total',
            name: 'Ever Reported / Total Facilities'
        },
        {
            code: 'ever_over_expected',
            name: 'Ever Reported / Expected Facilities'
        },
        {
            code: 'period_over_expected',
            name: 'Reported during period / Expected Facilities'
        }
    ];


    scope.viewOptins = [
        {id: '0', name: 'Non Reporting Only'},
        {id: '1', name: 'Reporting Only'},
        {id: '2', name: 'All'}
    ];

}

function popupFormat(feature) {
    return '<table class="table table-bordered" style="width: 250px"><tr><th colspan="2"><b>' + feature.properties.name + '</b></th></tr>' +
        '<tr><td>Expected Facilities</td><td class="number">' + feature.expected + '</td></tr>' +
        '<tr><td>Reported This Period</td><td class="number">' + feature.period + '</td></tr>' +
        '<tr><td>Ever Reported</td><td class="number">' + feature.ever + '</td></tr>' +
        '<tr><td class="bold">Total Facilities</b></td><td class="number bold">' + feature.total + '</td></tr>';
}

function onEachFeature(feature, layer) {
    layer.bindPopup(popupFormat(feature));
}

function zoomAndCenterMap(leafletData, $scope) {
    leafletData.getMap().then(function (map) {
        var latlngs = [];
        for (var c = 0; c < $scope.features.length; c++) {
            if ($scope.features[c].geometry === null || angular.isUndefined($scope.features[c].geometry))
                continue;
            if ($scope.features[c].geometry.coordinates === null || angular.isUndefined($scope.features[c].geometry.coordinates))
                continue;
            for (var i = 0; i < $scope.features[c].geometry.coordinates.length; i++) {
                var coord = $scope.features[c].geometry.coordinates[i];
                for (var j = 0; j < coord.length; j++) {
                    var points = coord[j];
                    for (var p in points) {
                        var latlng;
                        if (angular.isNumber(points[p])) {
                            latlng = L.GeoJSON.coordsToLatLng(points);
                        } else {
                            latlng = L.GeoJSON.coordsToLatLng(points[p]);
                        }
                        latlngs.push(latlng);
                    }
                }
            }
        }

        map.fitBounds(latlngs);
    });


}
