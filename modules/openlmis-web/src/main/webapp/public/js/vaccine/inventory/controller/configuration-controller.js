/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


function VaccineInventoryConfigurationController($scope,programs,DemographicEstimateCategories,VaccineInventoryConfigurations,VaccineProgramProducts,configurations,SaveVaccineInventoryConfigurations,localStorageService,$location) {

    $scope.userPrograms=programs;
    $scope.configToAdd={};
    $scope.configToAdd.batchTracked=false;
    $scope.configToAdd.vvmTracked=false;
    $scope.configToAdd.survivingInfants = false;

    DemographicEstimateCategories.get({}, function (data) {
        $scope.demographicCategories = data.estimate_categories;
        //$scope.demographicCategories = [{"id":1,"name":"Population","description":null,"isPrimaryEstimate":true,"defaultConversionFactor":100},{"id":2,"name":"Pregnant Women","description":"test1","isPrimaryEstimate":false,"defaultConversionFactor":4.2},{"id":3,"name":"Annual Births","description":null,"isPrimaryEstimate":false,"defaultConversionFactor":4.2},{"id":4,"name":"Surviving Infants","description":"Children 0-1 years","isPrimaryEstimate":false,"defaultConversionFactor":3.9},{"id":5,"name":"Children under 2 Years","description":null,"isPrimaryEstimate":false,"defaultConversionFactor":3.9},{"id":6,"name":"Adolescent girls","description":null,"isPrimaryEstimate":false,"defaultConversionFactor":2},{"id":12,"name":"LiveBirth","description":null,"isPrimaryEstimate":false,"defaultConversionFactor":3.9}];
    });

    $scope.loadProducts=function(programId){
        VaccineProgramProducts.get({programId:programId},function(data){
            $scope.allProducts = data.programProductList;
            VaccineInventoryConfigurations.get(function(configdata)
            {
                if(configdata.Configurations.length === 0){
                    $scope.usedProducts = data.programProductList;
                    var order = 0;
                    var order1 = 0;
                    angular.forEach($scope.allProducts,function(value){
                        value.id = null;
                        if(value.productCategory.code == 'Vaccine'){
                            order = order + 1;
                            value.batchTracked  = true;
                            value.vvmTracked  = true;
                            value.survivingInfants  = true;
                            value.schedule  = value.product.programProductIsa.isa.dosesPerYear;
                            value.presentation  = value.product.dosesPerDispensingUnit;
                            value.coverage  = value.product.programProductIsa.isa.whoRatio;
                            value.denominatorEstimateCategoryId = value.product.programProductIsa.isa.populationSource;
                            value.ordering = order;
                        }else{
                            value.batchTracked  = false;
                            value.vvmTracked  = false;
                            value.survivingInfants  = false;
                            order1 = order1 + 1;
                            value.ordering = order1;
                        }
                        value.editing  = false;
                    });
                }else{
                    $scope.usedProducts = data.programProductList;
                    angular.forEach($scope.usedProducts,function(value){
                        angular.forEach(configdata.Configurations,function(value1){
                            if(value.product.id === value1.product.id){
                                if(value.programProductIsa){
                                    console.log(value.programProductIsa);
                                    value.schedule  = value.programProductIsa.isa.dosesPerYear;
                                    value.coverage  = value.programProductIsa.isa.whoRatio ;
                                    value.denominatorEstimateCategoryId = value.programProductIsa.isa.populationSource;
                                }
                                value.id  = value1.id ;
                                value.type  = value1.type ;
                                value.productId  = value1.productId;
                                //value.schedule  = value1.schedule;

                                //value.coverage  = value1.coverage ;

                                //value.presentation  = value1.presentation;
                                value.presentation  = value.product.dosesPerDispensingUnit;
                                value.packedVolumePerDose  = value1.packedVolumePerDose;
                                value.administrationMode  = value1.administrationMode;
                                value.dilutionSyringe  = value1.dilutionSyringe;
                                value.diluentPackedVolumePerDose  = value1.diluentPackedVolumePerDose;
                                value.batchTracked  = value1.batchTracked;
                                value.vvmTracked  = value1.vvmTracked;
                                value.survivingInfants  = value1.survivingInfants;
                                value.ordering = value1.ordering;
                                //value.denominatorEstimateCategoryId = value1.denominatorEstimateCategoryId;

                            }
                        });
                    });
                }
            });
        });
    };

    //enable editing of a single row
    $scope.enableEditing = function(product){
        product.editing = true;
    };
    $scope.disableEditing = function(product){
        product.editing = false;
        $scope.configurations = [];
        angular.forEach($scope.allProducts,function(value){
            var prodObject = {
                'id':(value.id)?value.id:null,
                'type':'PRODUCT',
                'productId':value.product.id,
                'schedule': (value.schedule)?value.schedule:null,
                'coverage':(value.coverage)?value.schedule:null,
                'presentation':(value.presentation)?value.schedule:null,
                'packedVolumePerDose':(value.packedVolumePerDose)?value.packedVolumePerDose:null,
                'administrationMode':(value.administrationMode)?value.administrationMode:null,
                'dilutionSyringe':(value.dilutionSyringe)?value.dilutionSyringe:null,
                'diluentPackedVolumePerDose':(value.diluentPackedVolumePerDose)?value.diluentPackedVolumePerDose:null,
                'batchTracked':(value.batchTracked)?value.batchTracked:null,
                'vvmTracked':(value.vvmTracked)?value.vvmTracked:null,
                'survivingInfants':(value.survivingInfants)?value.survivingInfants:null,
                'denominatorEstimateCategoryId':(value.denominatorEstimateCategoryId)?value.denominatorEstimateCategoryId:null,
                'ordering':(value.ordering)?value.ordering:null
            };
            $scope.configurations.push(prodObject);
            //console.log(prodObject);
        });

        console.log($scope.configurations);
        $scope.saveConfigurations();
    };

    //changing the order of products
    $scope.changeOrder = function(direction,product){
        var newOrder = 0;
        if(direction === 'up'){
            newOrder = product.ordering - 1;
            angular.forEach($scope.allProducts,function(value){
                if (value.ordering === newOrder){
                    value.ordering = product.ordering;
                }
            });
            product.ordering = newOrder;
        }else if(direction === 'down'){
            if(product.ordering === 0){

            }else{
                newOrder = product.ordering + 1;
                angular.forEach($scope.allProducts,function(value){
                    if (value.ordering === newOrder){
                        value.ordering = product.ordering;
                    }
                });
                product.ordering = newOrder;
            }

        }
    };

    $scope.loadConfigurations=function(){
        VaccineInventoryConfigurations.get(function(data)
        {
            $scope.configurations=data.Configurations;
            updateProductToDisplay($scope.configurations);
        });
    };
    $scope.addConfiguration=function(configToAdd)
    {
        configToAdd.type='PRODUCT';
        console.log(JSON.stringify($scope.configurations[0]));
        console.log(JSON.stringify(configToAdd));

        $scope.configurations.push(configToAdd);
        updateProductToDisplay($scope.configurations);
        $scope.configToAdd={};
        $scope.configToAdd.batchTracked=false;
        $scope.configToAdd.vvmTracked=false;
    };
    $scope.$watch('configurations',function(){
        if($scope.configurations !==undefined)
        {

        }
    });
    $scope.changeTab=function(key){
        $scope.visibleTab=key;
    };

    $scope.saveConfigurations=function()
    {
        SaveVaccineInventoryConfigurations.update($scope.configurations,function(data){
            $scope.configurations=data.Configurations;
            updateProductToDisplay($scope.configurations);
        });
    };

    function updateProductToDisplay(configurationProducts)
    {
        var toExclude = _.pluck(_.pluck(configurationProducts, 'product'), 'primaryName');
        $scope.productsToDisplay = $.grep($scope.allProducts, function (productObject) {
            return $.inArray(productObject.product.primaryName, toExclude) == -1;
        });
    }

    if($scope.userPrograms.length > 1)
    {
        $scope.showPrograms=true;
        //TODO: load stock cards on program change
        $scope.loadProducts($scope.userPrograms[0].id);

    }
    else if($scope.userPrograms.length === 1){
        $scope.showPrograms=false;
        $scope.loadProducts($scope.userPrograms[0].id);
    }



    $scope.loadRights = function () {
        $scope.rights = localStorageService.get(localStorageKeys.RIGHT);
    }();

    $scope.hasPermission = function (permission) {
        if ($scope.rights !== undefined && $scope.rights !== null) {
            var rights = JSON.parse($scope.rights);
            var rightNames = _.pluck(rights, 'name');
            return rightNames.indexOf(permission) > -1;
        }
        return false;
    };

}
VaccineInventoryConfigurationController.resolve = {

    programs:function ($q, $timeout, VaccineInventoryPrograms) {
        var deferred = $q.defer();
        var programs={};

        $timeout(function () {
            VaccineInventoryPrograms.get({},function(data){
                programs=data.programs;
                deferred.resolve(programs);
            });
        }, 100);
        return deferred.promise;
    },

    configurations:function ($q, $timeout, VaccineInventoryConfigurations) {
        var deferred = $q.defer();
        var configurations=[];

        $timeout(function () {
            VaccineInventoryConfigurations.get({},function(data){
                configurations=data;
                deferred.resolve(configurations);
            });
        }, 100);
        return deferred.promise;
    }

};
