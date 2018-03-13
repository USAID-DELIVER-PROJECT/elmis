function StockAvailabilityControllerFunc1($scope,GetCoverageByRegionSummary,GetPerformanceMonitoringData, GetDistributionOfDistrictPerPerformanceData, GetClassificationDistrictDrillDownData, GetDistrictClassificationSummaryData, GetCategorizationByDistrictDrillDownData, GetCategorizationByDistrictData, GetCoverageByDistrictData, GetInventoryByMaterialFacilityListData, VaccineDashboardFacilityInventoryStockStatus, GetCoverageMapInfo, GetInventorySummaryByLocationData, GetInventorySummaryByMaterialData, StockCardsByCategory, GetDistrictInventorySummaryData, GetRegionInventorySummaryData, homeFacility, FacilityInventoryStockStatusData, GetPeriodForDashboard, YearFilteredData, ProductFilteredData, $routeParams, leafletData, ProductService, $state, VaccineProductDoseList, ReportPeriodsByYear, VimsVaccineSupervisedIvdPrograms, AvailableStockDashboard, FullStockAvailableForDashboard, GetAggregateFacilityPerformanceData, VaccineCoverageByProductData, GetCoverageByProductAndDoseData) {
    $scope.region=true;
    $scope.showDistrict = function (d) {
    if(d==='district') {
        $scope.region = false;
        $scope.district = true;
    }else{$scope.region=true;
        $scope.district = false;
    }

};
      $('ul.tabs').tabs({
        swipeable : true,
        responsiveThreshold : 1920
    });
    $scope.showModal = function (data) {
        var colors = {'#ffdb00': 'yellow', '#ff0d00': 'red', '#00B2EE': 'blue', '#006600': 'green'};

        $scope.modal12 = true;
        $scope.modal = true;

        $scope.productName = data.category;
        $scope.level = data.level;

        var params = {level: data.level, color: colors[data.color], product: data.category};
        GetInventoryByMaterialFacilityListData.get(params).then(function (data) {
            $scope.facilityList = data;
        });
        $('#modal12').modal().modal('open');

    };
    $scope.closeStockModal = function () {

        $scope.modal12 = false;
        $('#modal12').modal().modal('close');
    };


    $scope.homeFacility = homeFacility;
    $scope.homePageDate = new Date();

    var dataV = [];
    var chartIds = ['myStockVaccine', 'myStockSyringe'];
    var title = ['Vaccines', 'Syringes'];
    var name = ['Vaccines', 'Syringes'];

    function populateTheChart(vaccineDataT, product, chartName, title, name) {
        dataV = [];
        vaccineDataT.forEach(function (data) {
            dataV.push({y: Math.abs(data.mos), color: data.color, soh: data.soh, uom: data.unity_of_measure});
        });
        new Highcharts.chart(chartName, {
            chart: {
                type: 'column'
            },
            credits: {
                enabled: false
            },

            legend: {
                align: 'right',
                shadow: false
            },
            title: {
                text: ''
            },
            subtitle: {
                text: title
            },
            xAxis: {
                categories: product,
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

                formatter: function () {
                    var tooltip;
                    tooltip = '<span style="color:' + this.series.color + '">' + this.point.category + '<hr/><br/> <span>MOS </span>' + this.y + '</span>: <b>' + this.point.soh + ' ' + this.point.uom + ' </b><br/>';

                    return tooltip;
                }
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0

                }, showLegend: false
            },
            series: [{
                name: name,
                data: dataV

            }]
        });


    }

    //Lower Level Charts
    function getVaccineStockStatusChartForLowerLevel(dataV) {
        var vaccineDataT = [];
        var productT = [];
        for (var i = 0; i <= 1; i++) {
            vaccineDataT = dataV[i].dataPoints;
            productT = _.pluck(vaccineDataT, 'product');
            populateTheChart(vaccineDataT, productT, chartIds[i], title[i], name[i]);

        }


        /*    var vaccineData = data[0].dataPoints;
            var product = _.pluck(data[0].dataPoints, 'product');

            vaccineData.forEach(function (data) {
                dataV.push({y:data.mos,color:data.color,soh:data.soh});
            });*/

    }

    function inventorySummaryChart(chartId, title, subTitle, data) {
        Highcharts.chart(chartId, {
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'

            },
            credits: {
                enabled: false
            },
            title: {
                text: title
            },
            subtitle: {
                text: subTitle
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    innerSize: '70%',
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>  {point.percentage:.0f} %',

                        /*
                                                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        */
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black',
                            fontFamily: '\'Lato\', sans-serif', lineHeight: '18px', fontSize: '17px'
                        }
                    },
                    showInLegend: true
                }
            },
            series: data
        });


    }

    function getInventorySummarySortedData(data, color) {

        var indicatorData = _.filter(data, function (data) {

            var indicator = (color === 'red') ? data.red : (color === 'blue') ? data.blue : (color === 'yellow') ? data.yellow : data.green;
            return indicator > 0;
        });

        return _.sortBy(indicatorData, function (data) {
            return -parseInt((color === 'red') ? data.red : (color === 'blue') ? data.blue : (color === 'yellow') ? data.yellow : data.green, 10);
        });
    }

    function getByInventoryByLocation(values) {

        var colors = {'#ffdb00': 'yellow', '#ff0d00': 'red', '#00B2EE': 'blue', '#006600': 'green'};
        var customizeStatus = {
            'overstock': 'Over Stocked',
            'zero stock': 'Stocked Out',
            'sufficient stock': 'Adequately Stocked',
            'low stock': 'Under Stocked'
        };

        GetInventorySummaryByLocationData.get({
            level: values.level,
            status: colors[values.color]
        }).then(function (data) {
            var sortedData = getInventorySummarySortedData(data, (colors[values.color]));

            var byLocationPercentage = _.pluck(sortedData, (colors[values.color]));
            var byLocationFacility = _.pluck(sortedData, 'name');

            var totalProduct = _.pluck(sortedData, 'totalproduct');

            var percentageItems = [];
            var i = -1;
            angular.forEach(byLocationPercentage, function (data) {
                percentageItems.push({color: values.color, y: data, total: totalProduct[++i]});

            });
            var type = 'bar';
            var chartTitle = 'Stock Status By Location';
            var verticalTitle = 'Products';
            var chartNameId = 'byLocation';
            var name = 'By Location';
            var subTitle = '(Shows ' + (values.level).toUpperCase() + ' ' + customizeStatus[values.name] + ' with one or more Antigens)';
            var toolTip = 'Antigens';
            $scope.byLocation = true;
            loadDynamicChart2(chartNameId, type, chartTitle, subTitle, verticalTitle, toolTip, name, byLocationFacility, percentageItems);

        });

        GetInventorySummaryByMaterialData.get({
            level: values.level,
            status: colors[values.color]
        }).then(function (data) {
            var sortedData = getInventorySummarySortedData(data, (colors[values.color]));

            var byProductPercentage = _.pluck(sortedData, (colors[values.color]));
            var byProductFacility = _.pluck(sortedData, 'primaryname');

            var totalProduct = _.pluck(sortedData, 'totalproduct');

            var percentageItems = [];
            var i = -1;
            angular.forEach(byProductPercentage, function (data) {

                percentageItems.push({color: values.color, y: data, total: totalProduct[++i], level: values.level});

            });
            var type = 'bar';
            var chartTitle = 'Stock Status By Antigen';
            var verticalTitle = 'Stores';
            var chartNameId = 'byProduct';
            var name = 'By Antigen';
            var subTitle = '(Shows Antigens  ' + customizeStatus[values.name] + ' across all ' + (values.level).toUpperCase() + ')';
            var toolTip = 'Stores';
            $scope.byLocation = true;
            loadDynamicChart2(chartNameId, type, chartTitle, subTitle, verticalTitle, toolTip, name, byProductFacility, percentageItems);

        });


    }

    function summaryChartValues(blue, green, yellow, red, level) {
        return [{
            name: 'products', colorByPoint: true,
            data: [{


                name: 'overstock',
                y: blue, color: '#00B2EE', level: level
            }, {
                name: 'sufficient stock',
                y: green, color: '#006600', level: level

            }, {
                name: 'low stock',
                y: yellow, color: '#ffdb00', level: level
            }, {
                name: 'zero stock',
                y: red, color: '#ff0d00', level: level,
                sliced: true,
                selected: true
            }],
            point: {
                events: {
                    click: function (event) {
                        getByInventoryByLocation(this);
                    }
                }
            }
        }];

    }

    $scope.getVaccineInventorySummary = function () {

        GetDistrictInventorySummaryData.get().then(function (data) {

            if (!isUndefined(data)) {
                $scope.showDistrictSummary = true;
                var chartTitle = 'District Vaccine Stock Summary';
                var subTitle = '(Shows availability of all antigens across all DVS in the country)';
                var values = summaryChartValues(_.pluck(data, 'blue_total')[0], _.pluck(data, 'green_total')[0], _.pluck(data, 'yellow_total')[0], _.pluck(data, 'red_total')[0], 'dvs');
                inventorySummaryChart('districtSummary', chartTitle, subTitle, values);
            }

        });

        GetRegionInventorySummaryData.get().then(function (data) {

            if (!isUndefined(data)) {
                $scope.showDistrictSummary = true;
                var chartTitle = 'Regional Vaccine Stock Summary';
                var subTitle = '(Shows availability of all antigens across all RVS in the country)';
                var values = summaryChartValues(_.pluck(data, 'blue_total')[0], _.pluck(data, 'green_total')[0], _.pluck(data, 'yellow_total')[0], _.pluck(data, 'red_total')[0], 'rvs');
                inventorySummaryChart('regionSummary', chartTitle, subTitle, values);
            }

        });


    };

    function loadDynamicPieChart(data, title, chartId,legend,periodName,productName,dose) {

        var char2 = new Highcharts.Chart({

            chart: {
                renderTo: chartId,
                type: 'pie'


            },

            credits: {enabled: false},

            plotOptions: {
                pie: {
                    // innerSize: '80%',
                    size:'60%',
                    showInLegend: true,
                    dataLabels: {
                        enabled: true,
                        format: '<span style="  text-decoration: underline !important;">{point.y}  ' +legend+ ' </span>'
                        /*
                                                format: '{point.name}: {point.y:.1f}%'
                        */
                    }
                }
            },
            title: {
                text: '<span style="font-size: 15px!important;color: #0c9083">'+title+'</span>'
            },
            subtitle: {
                text: '( '+productName+', '+periodName+')'
            },
            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'


            },

            /* title: {
                 verticalAlign: 'middle',
                 floating: true,
                 text:''
 /!*
                 text: '<span style="font-size: 50px;">%</span>'
 *!/
                 /!*
                                 '<div class="clearfix"></div><span style="font-size: 12px !important;"></span>',align:'center',verticalAlign: 'middle'
                 *!/
             },*/

            series: [{
                colorByPoint: true,
                name: 'national Coverage',
                data: data
            }]


        });

    }

    $scope.loadDistrictCoverageFunc = function (para) {

        GetCoverageByRegionSummary.get(para).then(function (data) {

            var badValue  = _.where(data,{coverageclassification:'BAD'});
            var goodValue  = _.where(data,{coverageclassification:'GOOD'});
            var normalValue  = _.where(data,{coverageclassification:'NORMAL'});
            var summary1 =(badValue.length >0)?badValue.length:0;
            var summary3 =(goodValue.length >0)?goodValue.length:0;
            var summary2 =(normalValue.length >0)?normalValue.length:0;

            var title = 'Region Coverage Sumary', chartId = 'regionCoverageChart',legend='Region(s)';
            var values =[];
            values.push({name: 'Below 80%', y: summary1, color: 'red', sliced: true},
                {
                    name: '80% to 89%',
                    y:summary2,
                    color: 'yellow'
                },
                {name: '90% +', y: summary3, color: 'green'}
                );
            loadDynamicPieChart(values, title, chartId,legend,para.periodName,para.productName,para.doseId);


        });

        GetCoverageByDistrictData.get(para).then(function (data) {

            var title = 'District Coverage Sumary', chartId = 'districtCoverageChart',legend='District(s)';
            var values = [];
            data.forEach(function (data) {
                values.push({name: 'Below 80%', y: data.red, color: 'red', sliced: true}, {
                        name: '80% to 89%',
                        y: data.yellow,
                        color: 'yellow'
                    },
                    {name: '90% +', y: data.green, color: 'green'});
            });
            loadDynamicPieChart(values, title, chartId,legend,para.periodName,para.productName,para.dose);
        });


    };
    $scope.successModal = false;

    function showCategorizationPopup(events, data) {
        $scope.parameters = {period: data.point.category, category: events.name};

        GetCategorizationByDistrictDrillDownData.get($scope.parameters).then(function (data) {

            if (!isUndefined(data)) {
                $scope.classificationData = data;
                console.log(data);

            }

        });
        $('#exampleModalCenter').modal().modal('open');

        $scope.successModal = true;

        // $('#categorizationModal').modal().modal('open');

    }

    function showClassificationPopup(events, event, product, year) {
        $scope.parameters = {
            period: event.point.category,
            indicator: events.name,
            product: parseInt(product, 10),
            year: parseInt(year, 10)
        };
        $scope.classificationByDistrict = [];
        GetClassificationDistrictDrillDownData.get($scope.parameters).then(function (data) {
            $scope.classificationByDistrict = data;

            $('#classificationModal').modal().modal('open');


        });


    }

    function getDynamicStackedChart(data, chartId, title, Category, chartCategory, product, year, horizontalTitle) {

        Highcharts.chart(chartId, {
            chart: {
                type: 'column'
            },
            credits: {enabled: false},
            title: {
                text: title
            },
            xAxis: {
                categories: Category
            },
            yAxis: {
                min: 0,
                title: {
                    text: horizontalTitle
                },
                stackLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                    }
                },
                gridLineColor: ''
            },

            legend: {

                backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
                borderColor: '#CCC',
                borderWidth: 1,
                shadow: false
            },
            tooltip: {
                headerFormat: '<b>{point.x}</b><br/>',
                pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
            },
            plotOptions: {
                column: {
                    stacking: 'normal',
                    dataLabels: {
                        enabled: true,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    },
                    events: {
                        click: function (event) {
                            if (chartCategory === 'classification')
                                showClassificationPopup(this, event, product, year);
                            else
                                showCategorizationPopup(this, event);

                        }
                    },
                    cursor: 'pointer'

                }
            },
            series: data
        });


    }

    $scope.districtCategorizationFunct = function (params) {

        var colors = {'Cat_3': '#ffdb00', 'Cat_4': '#ff0d00', 'Cat_2': '#ABC9AA', 'Cat_1': '#006600'};
        GetCategorizationByDistrictData.get(params).then(function (data) {

            var category = _.uniq(_.pluck(data, 'period_name'));
            var groupByPeriod = _.groupBy(data, function (period) {
                return period.catagorization;
            });

            var mappedData = _.map(groupByPeriod, function (value, index) {
                return {data: value, index: index};
            });
            var categorization = [];
            for (var i = 0; i < mappedData.length; i++) {
                categorization.push({
                    name: mappedData[i].index,
                    data: _.pluck(mappedData[i].data, 'total'),
                    color: colors[mappedData[i].index]
                });

            }
            var title = '<span style="color:#509fc5; font-size: 15px ">Categorization by Districts based on Coverage and Dropout ' + params.year + '</span>';
            var chartId = 'categorizationByDistrict';
            getDynamicStackedChart(categorization, chartId, title, category, null, null, null, 'Districts');

        });

    };

    $scope.districtClassificationFunc = function (filter) {
        var colors = {'Class C': '#ffdb00', 'Class D': '#ff0d00', 'Class B': '#ABC9AA', 'Class A': '#006600'};
        var parameter = {product: parseInt(filter.product, 10), year: parseInt(filter.year, 10)};
        GetDistrictClassificationSummaryData.get(parameter).then(function (data) {
            var category = _.uniq(_.pluck(data, 'period'));

            var groupByClassification = _.groupBy(data, function (period) {
                return period.classification;
            });

            var mappedData = _.map(groupByClassification, function (value, index) {
                return {data: value, index: index};
            });
            var classification = [];
            for (var i = 0; i < mappedData.length; i++) {
                classification.push({
                    name: mappedData[i].index,
                    data: _.pluck(mappedData[i].data, 'count'),
                    color: colors[mappedData[i].index]
                });

            }
            var title = '<span style="color:#509fc5; font-size: 15px">Classification of Districts based On Coverage and Utilization ' + filter.year + '</span>';
            var chartId = 'classificationByDistrict';

            var chartCategory = ' ';
            chartCategory = 'classification';

            getDynamicStackedChart(classification, chartId, title, category, chartCategory, filter.product, filter.year, 'Districts');
        });
    };

    function returnProductRange(indicator, product) {
        return (indicator === 'BAD') ? product + ' < 50%' : (indicator === 'WARN') ? '50%<=' + product + '<80%' : (indicator === 'NORMAL') ? '80%<=' + product + '<90%' : product + ' >=90%';
    }

    $scope.districtPerformanceFunc = function (filter) {

        var colors = {'WARN': '#ffdb00', 'BAD': '#ff0d00', 'NORMAL': '#ABC9AA', 'GOOD': '#006600'};
        var params = {
            product: parseInt(filter.product, 10),
            year: parseInt(filter.year, 10),
            doseId: parseInt(filter.dose, 0)
        };
        GetDistributionOfDistrictPerPerformanceData.get(params).then(function (data) {
            var category = _.uniq(_.pluck(data, 'periodname'));

            var groupByClassification = _.groupBy(data, function (period) {
                return period.coverageclassification;
            });
            var mappedData = _.map(groupByClassification, function (value, index) {
                return {data: value, index: index};
            });
            var classification = [];
            for (var i = 0; i < mappedData.length; i++) {
                classification.push({
                    name: returnProductRange(mappedData[i].index, _.pluck(mappedData[i].data, 'product')[0]),
                    data: _.pluck(mappedData[i].data, 'total'),
                    color: colors[mappedData[i].index]
                });

            }
            var title = '<span style="color:#509fc5; font-size: 15px">Distribution of Districts per Performance  ' + filter.year + '</span>';
            var chartId = 'districtDistribution';

            var chartCategory = '';
            chartCategory = 'Districts';
            getDynamicStackedChart(classification, chartId, title, category, chartCategory, filter.product, filter.year, 'Districts');


        });


    };

    function populatePerformanceMonitoringChart(chartdata,estimate, monthlyVaccinated,cumulativeVaccinated,chartId,title,year) {
       var chartValues =[];

       new Highcharts.chart(chartId, {
            chart: {
                zoomType: 'xy'
            },
            title: {
                text: title
            },
            credits:{
                enabled:false
            },
            subtitle: {
                text: ' '
            },
            xAxis: [{
                categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                crosshair: true
            }],

            yAxis: [{ // Primary yAxis
                labels: {
                    format: '{value}',
                    style: {
                        color: Highcharts.getOptions().colors[2]
                    }
                },
                title: {
                    text: '',
                    style: {
                        color: Highcharts.getOptions().colors[2]
                    }
                },
                gridLineWidth: 1,
                opposite: false,
                gridLineColor: '#197F07',
                lineWidth:1

            }, { // Secondary yAxis
                gridLineWidth: 1,
                title: {
                    text: 'Target Vs Vaccinated',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                labels: {
                    format: '{value}',
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                }

            }, { // Tertiary yAxis
                gridLineWidth: 1,
                title: {
                    text: '',
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

            },
            series:chartdata/* [{
                name: 'estimate',
                type: 'line',
                yAxis: 1,
                zIndex: 2,
                data:estimate,
                tooltip: {
                    valueSuffix: ' Target'
                }

            },*/


               /* {
                name: 'Cumulative',
                type: 'line',
                yAxis: 1,

                zIndex: 1,
                data:cumulativeVaccinated,
                tooltip: {
                    valueSuffix: ' '
                }
            }*///]
        });





    }

    $scope.performanceMonitoring = function (filter) {
        var param = {product:parseInt(filter.product,10), year:parseInt(filter.year,10)};
        GetPerformanceMonitoringData.get(param).then(function (data) {

            var byCategory = _.groupBy(data, function (p) {
                return p.doseid;
            });


            var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                return [{"byDose": index, "dataPoints": value}];
            });
            var performanceData =[];
            var period =[];
            var estimate =[];
            var productValue =[];
            var monthlyVaccinated =[];
            var cumulativeVaccinated =[];
            chartIds = 'performanceMonitoring';
            title = '<span style="color: #0c9083">Performance Monitoring, '+filter.year+'</span>';

            period = _.pluck(allStockDataPointsByCategory[0].dataPoints, 'period');

           // productValue.push( _.zip(period,estimate));

              console.log(allStockDataPointsByCategory);
                estimate = _.pluck(allStockDataPointsByCategory[0].dataPoints, 'estimate');

            for (var i = 0; i <= allStockDataPointsByCategory.length-1; i++) {
                monthlyVaccinated = _.pluck(allStockDataPointsByCategory[i].dataPoints, 'monthlyvaccinated');
                cumulativeVaccinated.push({
                    name: filter.productName+' '+allStockDataPointsByCategory[i].byDose,
                    type: 'line',
                    yAxis: 1,
                    zIndex: 1,
                    data:_.pluck(allStockDataPointsByCategory[i].dataPoints, 'vaccinated_cumulative'),
                    tooltip: {
                        valueSuffix: ' '}});
            }

            var chartData = [];
            var estimateValue = [];
          estimateValue =[{
                name: 'estimate',
                    type: 'line',
                yAxis: 1,
                zIndex: 2,
                data:estimate,
                tooltip: {
                valueSuffix: ' Target'
            }}];
            chartData = cumulativeVaccinated.concat(estimateValue);

                populatePerformanceMonitoringChart(chartData,estimate, monthlyVaccinated,cumulativeVaccinated, chartIds, title,filter.year);

        });

    };

    if ($scope.homeFacility.facilitytypecode !== 'cvs') {
        console.log($scope.homeFacility.facilityid);

        var para = {
            facilityId: parseInt($scope.homeFacility.facilityid, 10),
            date: null
        };
        FacilityInventoryStockStatusData.get(para).then(function (data) {
            if (data !== null) {
                var byCategory = _.groupBy(data, function (p) {
                    return p.product_category;
                });
                var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                    return [{"productCategory": index, "dataPoints": value}];
                });
                getVaccineStockStatusChartForLowerLevel(allStockDataPointsByCategory);


                /*   var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                       return [{"productCategory": index, "dataPoints": value}];
                   });
                   if (!isUndefined(mos)) {
                       getVaccineStockStatusChartForLowerLevel(allStockDataPointsByCategory);

                   } else
                       return 'No Chart data';*/

            }


        });


        /*        VaccineDashboardFacilityInventoryStockStatus.get({
                facilityId: parseInt($scope.homeFacility.facilityid, 10),
                date: '2017-09-31'
            }, function (data) {*/

        /*
                if (data.facilityStockStatus !== null) {
                    var allProducts = data.facilityStockStatus;
                    var byCategory = _.groupBy(allProducts, function (p) {
                        return p.product_category;
                    });

                    var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                        return [{"productCategory": index, "dataPoints": value}];
                    });

                    var mos = _.pluck($scope.allStockDataPointsByCategory[0].dataPoints, 'mos');

                    if (!isUndefined(mos)) {
                      getVaccineStockStatusChartForLowerLevel(allStockDataPointsByCategory);

                    } else
                        return 'No Chart data';

                }
        */


        /* StockCardsByCategory.get(parseInt(82, 10), parseInt($scope.homeFacility.facilityid, 10)).then(function (data) {

             console.log(data);

         });*/


        // })
        /*   FacilityInventoryStockStatusData.get({
               facilityId: parseInt(homeFacility.facilityid, 10),
               date: new Date()
           }).then(function (data) {
               if (!isUndefined(data)) {
                   console.log(data);
                   var byCategory = _.groupBy(data, function (p) {
                       return p.product_category;
                   });

                   var allStockDataPointsByCategory = $.map(byCategory, function (value, index) {
                       return [{"productCategory": index, "dataPoints": value}];
                   });
                   //  console.log(allStockDataPointsByCategory);
                   getVaccineStockStatusChartForLowerLevel(allStockDataPointsByCategory);
               }
           });*/

    } else {


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
            var para = angular.extend(par, currentDate, {
                periodName: data.name,
                productName: $scope.productToDisplay.name
            });
            $scope.loadCoverageMap(para);
            $scope.districtCategorizationFunct(para);
            $scope.districtClassificationFunc(para);
            $scope.districtPerformanceFunc(para);
            $scope.performanceMonitoring(para);
            // $scope.loadMap(par);
            $scope.loadDistrictCoverageFunc(para);
            $scope.getVaccineInventorySummary();
            $scope.vaccineCoverageByRegionAndProductFunc(para);
            $scope.vaccineCoverageByProductAndDoseFunc(para);
            $scope.getAggregatePerformanceFunc(para);


        });

        $scope.loadDashboardData = function (filter) {
            $scope.productToDisplay = _.findWhere($scope.products, {id: parseInt(filter.product, 10)});
            $scope.periodToDisplay = _.findWhere($scope.periods, {id: parseInt(filter.period, 10)});
            $scope.doseToDisplay = filter.dose;
            var prepareParams = angular.extend(filter, {
                productName: $scope.productToDisplay.name,
                periodName: $scope.periodToDisplay.name
            });

            $scope.loadCoverageMap(filter);
            $scope.districtCategorizationFunct(filter);
            $scope.districtClassificationFunc(filter);
            $scope.loadDistrictCoverageFunc(filter);
            $scope.districtPerformanceFunc(filter);
            $scope.performanceMonitoring(filter);
            $scope.vaccineCoverageByRegionAndProductFunc(prepareParams);
            $scope.vaccineCoverageByProductAndDoseFunc(prepareParams);
            $scope.getAggregatePerformanceFunc(filter);
            $scope.showfilter = false;
        };

        $scope.changeYear = function () {

            $scope.periods = [];
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


        $scope.vaccineCoverageByRegionAndProductFunc = function (params) {

            VaccineCoverageByProductData.get(params).then(function (coverage) {
                if (!isUndefined(coverage))
                    coverageByRegion(coverage, params);
            });
        };

        $scope.vaccineCoverageByProductAndDoseFunc = function (params) {
            GetCoverageByProductAndDoseData.get(params).then(function (coverage) {
                if (!isUndefined(coverage))
                    coverageByProductAndDose(coverage, params);
            });
        };


        // allData();


      /*  var product = 2413,
            period = 121;
        // var params = {product:2413,doseId:1,period:121,year:2017};

        var loadOnStart = function () {
            var params = {product: 2413, doseId: 1, period: 121, year: 2017};
            $scope.fullStockAvailability(params);

        };
        //loadOnStart();

        $scope.OnFilterChanged = function () {
/!*
            if ($scope.filter === null || $scope.filter === undefined) {
                return;
            } else {
                $scope.fullStockAvailability($scope.filter);

                getDoseFilter($scope.filter);
            }*!/

        };*/


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


        //Coverage

        var d = new Date();
        var pastYear = d.getFullYear() - 2;
        var periodSorter = function (value) {
            return parseInt(value.id, 10);
        };


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
        // $('#modal1').modal('open');

        $scope.year_slider = {
            value: parseInt(pastYear, 10),
            options: {
                floor: parseInt(pastYear, 10),
                ceil: parseInt(d.getFullYear(), 10),

                translate: function (value, sliderId, label) {
                    return value;
                }, onChange: function (sliderId, modelValue, highValue, pointerType) {
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


        };
        $scope.icons = [
            /*   {name: 'table', image: 'table.jpg', action: ''},
               {name: 'column', image: 'bar.png', action: ''},
               {name: 'line', image: 'line.png', action: ''},
               {name: 'combined', image: 'combined.jpg', action: ''},
               {name: 'column', image: 'column.png', action: ''},
               {name: 'area', image: 'area.jpg', action: ''},*/
/*
            {name: 'pie', image: 'search.png', action: ''},
*/
            {name: 'filter', image: 'si-glyph-apron.svg', action: ''}
        ];
        $scope.hideIcons = function (card) {

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


        $scope.showProduct = false;
        var getProduct = function () {
            ProductService.get(parseInt($scope.filter.product, 10)).then(function (data) {
                $scope.product = data;
                $scope.showProduct = true;
            });
        };
        getProduct();


        $scope.geojson = {};

        $scope.default_indicator = "ever_over_total";

        $scope.expectedFilter = function (item) {
            return item.monthlyEstimate > 0;
        };


        $scope.$watch('period', function (newVal, oldVal) {
            // $scope.onChange();
            // $scope.$parent.OnFilterChanged();
        });
    }


    function round(value, precision) {
        var multiplier = Math.pow(10, precision || 0);
        return Math.round(value * multiplier) / multiplier;
    }


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


    function showDoseSlider(dose) {

        var displayName = _.pluck(dose, 'displayName');

        $scope.dose_slider = {
            value: 1,
            options: {
                floor: 1,
                ceil: parseInt(displayName.length, 10),
                translate: function (value, sliderId, label) {
                    return displayName[value - 1];
                },
                onChange: function (sliderId, modelValue, highValue, pointerType) {
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

    //More Drill Down Data for the Chart 2
    function getStockAvailabilityDataView(chart2Data) {
        if (chart2Data.y !== null) {

            var indicator = (chart2Data.color === 'lightgray') ? 'availableStock' : (chart2Data.color === 'blue') ? 'CCE' : 'coverage';

            var d = {'indicator': indicator, 'total': chart2Data.y, 'period': chart2Data.category};
            $state.go('toMoreStockAvailabilityView', {
                'indicator': indicator,
                'total': chart2Data.y,
                'period': chart2Data.category
            });

        }

    }


    function getPeriodSlider(data) {

        var sortedValues = _.sortBy(data, periodSorter);
        var period_name = _.pluck(sortedValues, 'name');


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

    function coverageByProductAndDose(coverage, params) {
        var colors = {'WARN': '#ffdb00', 'BAD': '#ff0d00', 'NORMAL': '#ABC9AA', 'GOOD': '#006600'};

            var dataValues =[];
            coverage.forEach(function (data) {
                dataValues.push({name:'byChart',color:colors[data.coverageclassification],y:data.coverage});
            });
        console.log(dataValues);

        var cov = _.pluck(coverage, 'coverage');
        var product = _.pluck(coverage, 'product');

        var chartNameId = 'productByDoseChart';
        var type = 'column';
        var chartTitle = '<span style="font-size: 15px !important;color: #0c9083;text-align: center">National Coverage</span>';
        var name = 'Coverage';
        var verticalTitle = 'Percentage';

        loadDynamicChart(chartNameId, type, chartTitle, verticalTitle, name, product, dataValues,params.year,params.periodName);

    }

    function dynamicChart(chartId, title) {

        new Highcharts.chart(chartId, {

            chart: {
                type: chartId,
                height: 200,
                spacingBottom: 15,
                spacingTop: 20,
                spacingLeft: 5,
                spacingRight: 15,
                borderWidth: 1,
                borderColor: '#ddd'
            },

            title: {text: title},
            legend: {padding: 0, margin: 5},
            credits: {enabled: true},
            tooltip: {enabled: false},
            plotOptions: {column: {dataLabels: {enabled: true}}},
            colors: ['#4572A7', '#AA4643', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'],
            loading: {labelStyle: {top: '35%', fontSize: "2em"}},
            xAxis: {categories: ["7/12", "7/13", "7/14", "7/15", "7/16", "7/17", "7/18"]},
            series: [
                {
                    "name": "Odometer",
                    "data": [{"y": 94.98}, {"y": 182.96}, {"y": 160.97}, {"y": 18.00}, {"y": 117.97}, {"y": 6.00}, {"y": 127.97}]
                }
            ]


        });

    }


    function loadDynamicChart2(chartNameId, type, chartTitle, chartSubtitle, verticalTitle, toolTip, name, category, dataValue) {

        new Highcharts.chart(chartNameId, {
            chart: {
                type: type
            },
            credits: {
                enabled: false
            },
            title: {
                text: chartTitle
            },
            subtitle: {
                text: chartSubtitle
            },
            xAxis: {
                categories: category,
                crosshair: false
            },
            yAxis: {
                min: 0,
                title: {
                    text: verticalTitle
                },
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickWidth: 1,
                tickLength: 5,
                gridLineColor: '',
                tickInterval: 1
            },
            tooltip: {
                formatter: function () {
                    var tooltip;
                    tooltip = '<span style="color:' + this.color + '">' + this.y + ' Of ' + this.total + ' ' + toolTip + ' </span>';

                    return tooltip;
                }
            },
            plotOptions: {
                bar: {
                    pointPadding: 0.2,
                    borderWidth: 0,
                    cursor: 'pointer',
                    point: {
                        events: {
                            click: function () {
                                $scope.showModal(this);
                            }
                        }
                    }

                }
            },

            series: [{
                name: name,
                data: dataValue

            }]
        });


    }


    function loadDynamicChart(chartNameId, type, chartTitle, verticalTitle, name, category, dataValue,year,period) {

        new Highcharts.chart(chartNameId, {
            chart: {
                type: type
            },
            legend: {
                enabled:false,
                useHTML: true,
                symbolHeight: 14,
                symbolWidth: 14,
                symbolRadius: 3,
                style: {
                    fontSize: '9px',
                    whiteSpace: 'normal'
                },
                layout: 'vertical',
                itemMarginTop: 5,
                itemMarginBottom: 5,
                padding: 0,
                itemStyle: {
                    fontSize: '12px',
                    color: '#666',
                    fontWeight: 'bold'
                }

            },
            credits: {
                enabled: false
            },
            title: {
                text: chartTitle
            },
            subtitle: {
                text: '<span style="font-size: 11px !important; color: #0c9083">( Coverage Compararison between two Antigens '+', '+period+'</span>)'
            },
            xAxis: {
                categories: category,
                crosshair: false
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Coverage %'
                },
                lineColor: '#999',
                lineWidth: 1,
                tickColor: '#666',
                tickWidth: 1,
                tickLength: 3,
                gridLineColor: ''
            },
            tooltip: {
                formatter: function () {
                    var tooltip;
                    tooltip = '<span style="color:' + this.series.color + '">' + this.series.name + '</span>: <b>' + this.y + '</b><br/>';

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
                name: name,
                data: dataValue

            }

            ]
        });


    }


    function getPeriodByYear(modelValue) {

        ReportPeriodsByYear.get({
            year: parseInt(modelValue, 10)
        }, function (data) {
            getPeriodSlider(data.periods);
        });
    }


    $scope.loadCoverageMap = function (params) {

        GetCoverageMapInfo.get(params).then(function (data) {
            var dataValues = [];
            Highcharts.each(data, function (code, i) {
                var colorV;
                if (parseInt(code.value,10) <= 0 || isNaN(code.value))
                    colorV = 'black';

                dataValues.push({
                    code: code.code,
                    value: parseInt(code.value,10),
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
                    text: '<span style="font-size: 15px !important;color: #0c9083;text-align: center">'+params.productName +'-'+params.dose+' Coverage By Region, '+params.year+'</span>'
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
                        name: 'Below 80%'
                    }, {
                        from: 80,
                        to: 90,
                        color: '#ffdb00',
                        name:'80% to 89%'
                    }, {
                        from: 90,
                        color: '#006600',
                        name:'90%+'

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


    function mapInfo(data, MapId) {

// Create the chart
        Highcharts.mapChart(MapId, {
            chart: {
                map: 'countries/tz/tz-all'
            },

            title: {
                text: 'Highmaps basic demo'
            },

            /* subtitle: {
                 text: 'Source map: <a href="http://code.highcharts.com/mapdata/countries/tz/tz-all.js">United Republic of Tanzania</a>'
             },*/

            mapNavigation: {
                enabled: true,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            },

            colorAxis: {
                min: 0
            },

            series: [{
                data: data,
                name: 'Random data',
                states: {
                    hover: {
                        color: '#BADA55'
                    }
                },
                dataLabels: {
                    enabled: true,
                    format: '{point.value}'
                }
            }]
        });
    }

}

StockAvailabilityControllerFunc1.resolve = {

    ProductFilteredData: function ($q, $timeout, ReportProductsWithoutDescriptionsAndWithoutProgram) {
        var deferred = $q.defer();
        $timeout(function () {
            ReportProductsWithoutDescriptionsAndWithoutProgram.get({}, function (data) {
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