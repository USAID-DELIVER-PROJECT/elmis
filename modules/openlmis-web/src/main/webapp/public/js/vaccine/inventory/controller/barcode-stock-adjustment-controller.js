/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */
function BarcodeStockAdjustmentController($scope, $timeout,$window,$routeParams,$dialog,$log,$http,StockCardsByCategory,configurations,StockEvent,localStorageService,homeFacility,VaccineAdjustmentReasons,UserFacilityList) {

    //Get Home Facility
    $scope.currentStockLot = undefined;
    $scope.adjustmentReasonsDialogModal = false;
    $scope.userPrograms=configurations.programs;
    $scope.adjustmentReason={};
    $scope.vvmStatuses=[{"value":"1","name":" 1 "},{"value":"2","name":" 2 "}];
    $scope.productsConfiguration=configurations.productsConfiguration;
    var AdjustmentReasons=[];

    //////////////////////////////////////////////////////////////////////////////
    ////////////////These codes have been added by Kelvin for barcode/////////////
    ////////////////////////////////////////////////////////////////////////////
    $scope.data = {};
    $scope.data.allowMultipleScan = true;
    $scope.useBarcode = false;
    $timeout(function(){
        $("#barcode_string").focus();
    });

    //put all values in the local storage
    $scope.addToLocal = function(){
        $log.info('saving gtin lookup locally');
        $http.get('/vaccine/gitn_lookup/all').success(function(data) {
            $scope.gtin_lookups = data.gitn_lookup;
            localStorageService.add('gtin_lookup',JSON.stringify(data.gitn_lookup));
        });
    };
    $scope.addToLocal();

    //pull all gtin information
    //@todo Put these data in local storage and update the method to fetch from local store
    $http.get('/vaccine/gitn_lookup/all').success(function(data) {
        $scope.gtin_lookups = data.gitn_lookup;
    }).
    error(function(data) {
        console.log("Error:" + data);
    });
    $scope.data.loading_item = false;

    //react to scanning of lot number
    $scope.scanLotNumber = function(barcodeString){
        if(barcodeString){
            $scope.barcode ={};
            $scope.barcode.lot_number = "";
            $scope.barcode.gtin = "";
            $scope.barcode.expiry = "";
            //check for the GS1 character
            if(barcodeString.substring(0,3) === "]d2"){
                if(barcodeString.length > 45){
                    var n = barcodeString.lastIndexOf("21");
                    $scope.barcode.expiry = barcodeString.substring(21,27);
                    $scope.barcode.gtin = barcodeString.substring(5,19);
                    $scope.barcode.lot_number = barcodeString.substring(29,n);
                }else if(barcodeString.length >= 29){
                    $scope.barcode.lot_number = barcodeString.substring(29);
                    $scope.barcode.expiry = barcodeString.substring(21,27);
                    $scope.barcode.gtin = barcodeString.substring(5,19);
                }else{
                    $scope.errorOccurred("String does not match expected format");
                }
            }else if(barcodeString.substring(0,2) === "01"){
                if(barcodeString.length > 45){
                    var k = barcodeString.lastIndexOf("21");
                    $scope.barcode.expiry = barcodeString.substring(18,24);
                    $scope.barcode.gtin = barcodeString.substring(2,16);
                    $scope.barcode.lot_number = barcodeString.substring(26,k);
                }else if(barcodeString.length >= 29){
                    $scope.barcode.lot_number = barcodeString.substring(26);
                    $scope.barcode.expiry = barcodeString.substring(18,24);
                    $scope.barcode.gtin = barcodeString.substring(2,16);
                }else{
                    $scope.errorOccurred("String does not match expected format");
                }

            }else{
                $scope.errorOccurred("String does not match expected format");
            }


            $scope.data.loading_item = true;
            var str = $scope.barcode.expiry;
            str = str.slice(str.length -2);
            if(str == '00'){
                $scope.barcode.expiry = $scope.barcode.expiry.slice( 0, $scope.barcode.expiry.length-2 ) + '01';
            }
            $scope.barcode.formatedDate = $scope.formatDate(new Date("20"+$scope.barcode.expiry.replace(/(.{2})/g,"$1-").slice(0, -1)));
            $scope.current_item = $scope.getItemByGTIN($scope.barcode , $scope.productsInList);
            if($scope.current_item.gtinInformation === false){
                $scope.errorOccurred("There is no information about this product");
            }else{
                $scope.data.error_loading_gtin = false;
                $scope.data.error_loading_item = false;
                $scope.data.loading_item = true;
                if($scope.current_item.available === false){
                    $scope.errorOccurred("You do not have this item in store");
                }else{
                    $scope.data.error_loading_item = false;
                    $scope.data.loading_item = false;
                    $scope.data.show_singleItem = true;
                    $scope.data.process_package = false;
                    $("#barcode_string").val('');
                    angular.element(jQuery('#barcode_string')).triggerHandler('input');
                    $("#barcode_string").focus();
                    $scope.show_incorrect_message = false;
                }
            }

        }else{
            $scope.show_incorrect_message = false;
            $scope.data.error_loading_item = false;
            $scope.data.loading_item = false;
        }


    };

    //a function to format date from the GTIN String
    $scope.formatDate = function (date) {
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2){ month = '0' + month;}
        if (day.length < 2) { day = '0' + day; }

        return [year, month, day].join('-');
    };

    //finding an item from the gtin Lookup table
    $scope.getItemByGTIN = function(barcode_object,listItems){
        var item = {available:false,gtinInformation:false};
        angular.forEach($scope.gtin_lookups,function(packagingInformation){
            if(barcode_object.gtin == packagingInformation.gtin){
                item.gtinInformation = true;
                angular.forEach(listItems,function(product){
                    if(packagingInformation.productid == product.product.id){
                        //append packaging information
                        product.packaging = packagingInformation;
                        item.product = product;
                        //construct a lot
                        var lots = angular.copy(product.lotsOnHand);
                        angular.forEach(lots,function(productLot){
                            //if(productLot.lot.lotCode == barcode_object.lot_number && barcode_object.formatedDate == productLot.lot.expirationDate){
                            if(productLot.lot.lotCode == barcode_object.lot_number){
                                item.available = true;
                                //adding products to list of items to be displayed
                                if(!$scope.stockCardsToDisplay[$scope.vaccineIndex].productCategory) {
                                    $scope.stockCardsToDisplay[$scope.vaccineIndex].productCategory = "Vaccine";
                                    $scope.stockCardsToDisplay[$scope.vaccineIndex].stockCards = [];
                                }
                                if($scope.checkProductInList($scope.stockCardsToDisplay[$scope.vaccineIndex].stockCards, product.product.id)){
                                    angular.forEach($scope.stockCardsToDisplay[$scope.vaccineIndex].stockCards, function(item){
                                        if(item.product.id == packagingInformation.productid){
                                            if($scope.checkLOtInList(item.lotsOnHand,barcode_object.lot_number)){
                                                angular.forEach(item.lotsOnHand,function(singleLot){
                                                    if(singleLot.lot.lotCode == barcode_object.lot_number){
                                                        if($scope.data.allowMultipleScan){
                                                            singleLot.boxes++;
                                                            console.log(JSON.stringify(singleLot));
                                                            $scope.updateCurrentTotal1(item,singleLot);
                                                        }
                                                    }

                                                });
                                            }else{
                                                if($scope.data.allowMultipleScan){
                                                    productLot.boxes = 1;
                                                    productLot.vials = 0;
                                                    item.lotsOnHand.push(productLot);
                                                    var indexToUse = item.lotsOnHand.length -1;
                                                    $scope.updateCurrentTotal1(item,item.lotsOnHand[indexToUse]);
                                                }else{
                                                    productLot.boxes = 0;
                                                    productLot.vials = 0;
                                                    item.lotsOnHand.push(productLot);
                                                }

                                            }
                                        }
                                    });
                                    //if it is a new item completely.
                                }else{
                                    var productToPush = angular.copy(product);
                                    productToPush.lotsOnHand = [];
                                    if($scope.data.allowMultipleScan){
                                        productToPush.boxes = 1;
                                        productToPush.vials = 0;
                                        productLot.boxes = 1;
                                        productLot.vials = 0;
                                        productToPush.lotsOnHand.push(productLot);
                                        $scope.updateCurrentTotal1(productToPush,productLot);
                                        $scope.stockCardsToDisplay[$scope.vaccineIndex].stockCards.push(productToPush);

                                    }else{
                                        productToPush.boxes = 0;
                                        productToPush.vials = 0;
                                        productLot.boxes = 0;
                                        productLot.vials = 0;
                                        productToPush.lotsOnHand.push(productLot);
                                        $scope.stockCardsToDisplay[$scope.vaccineIndex].stockCards.push(productToPush);
                                    }

                                }
                            }
                        });
                    }
                });
            }
        });
        $("#barcode_string").val('');
        angular.element(jQuery('#barcode_string')).triggerHandler('input');
        return item;
    };

    //update the stock adjustment reason for each update
    ///////////////////////////////////////////////////////
    $scope.defaultAdjustmentAmount = function(lot){
        //procedure to show the modal
        $scope.oldAdjustmentReason = angular.copy(lot.AdjustmentReasons);
        $scope.currentStockLot = lot;
        $scope.currentStockLot.adjustmentReasons=((lot.adjustmentReasons === undefined)?[]:lot.adjustmentReasons);
        //Remove reason already exist from drop down
        reEvaluateTotalAdjustmentReasons();
        updateAdjustmentReasonForLot(lot.adjustmentReasons);

        //calling the save adjustment reason
        var adjustmentReason={};
        adjustmentReason.type = $scope.adjustmentReasonsToDisplay[0];
        adjustmentReason.name = $scope.adjustmentReasonsToDisplay[0].name;
        adjustmentReason.quantity= Math.abs(lot.quantity - lot.quantityOnHand);
        $scope.currentStockLot.adjustmentReasons = [];
        $scope.currentStockLot.adjustmentReasons.push(adjustmentReason);
        updateAdjustmentReasonForLot($scope.currentStockLot.adjustmentReasons);
        reEvaluateTotalAdjustmentReasons();

    };

    $scope.checkProductInList = function(list,productID){
        var data = false;
        angular.forEach(list,function(product){
            if(productID == product.product.id){
                data = true;
            }
        });
        return data;
    };

    $scope.checkLOtInList = function(list,lotcode){
        var data = false;
        angular.forEach(list,function(product){
            if(lotcode == product.lot.lotCode){
                data = true;
            }
        });
        return data;
    };

    //get Maximum number of boxes one can have per product based on amount on store
    $scope.getMaximumBoxes = function(product,quantityOnHand){
        return parseInt(quantityOnHand / (product.packaging.dosespervial * product.packaging.vialsperbox), 10);
    };
    //get Maximum number of lose vials one can have
    $scope.getMaximumLoseVials = function(product,quantityOnHand,boxes){
        var dosesOnBoxes = boxes*product.packaging.dosespervial * product.packaging.vialsperbox;
        var remainingDoses = quantityOnHand - dosesOnBoxes;
        return parseInt(remainingDoses / (product.packaging.dosespervial), 10);
    };

    //update the number of doses when there is a change in boxes and lose vials
    $scope.updateCurrentTotal1  = function(product,lot){
        var vials_per_box = product.packaging.vialsperbox;
        var doses_per_vials = product.packaging.dosespervial;
        if(lot){
            var boxes = (lot.boxes === '')?0:lot.boxes;
            var vials = (lot.vials === '')?0:lot.vials;
            var num = 0;
            if(boxes !== 0){
                num += boxes*vials_per_box*doses_per_vials;
            }if(vials !== 0){
                if(vials >= vials_per_box){
                    lot.boxes = lot.boxes + Math.floor(vials / vials_per_box);
                    vials = vials % vials_per_box;
                    lot.vials = vials % vials_per_box;
                }
                num += doses_per_vials*vials;
            }
            lot.quantity = num;
            var totalCurrentLots = 0;
            if(product.lots !== undefined)
            {
                $(product.lots).each(function (index, lotObject) {
                    if(lotObject.quantity !== undefined && lotObject.quantity !== null){
                        totalCurrentLots = totalCurrentLots + parseInt(lotObject.quantity,10);
                    }
                });
                product.quantity=totalCurrentLots;
            }
            else{
                product.quantity=product.quantity;
            }
            $scope.defaultAdjustmentAmount(lot);
        }else{
            var boxes1 = (product.boxes === '')?0:product.boxes;
            var vials1 = (product.vials === '')?0:product.vials;
            var num1 = 0;
            if(boxes1 !== 0){
                num1 += boxes1*vials_per_box*doses_per_vials;
            }if(vials1 !== 0){
                num1 += doses_per_vials*vials1;
            }
            product.quantity = num1;
        }


    };

    //update the total doses for the product and the boxes and vials for a updated value
    $scope.updateCurrentTotal=function(product,lot){
        var vials_per_box = product.packaging.vialsperbox;
        var doses_per_vials = product.packaging.dosespervial;
        var dosesInBox = vials_per_box*doses_per_vials;

        //update boxes and vials as doses change
        if(lot){
            lot.boxes = parseInt(lot.quantity / dosesInBox, 10);
            lot.vials = lot.quantity % dosesInBox;
            $scope.defaultAdjustmentAmount(lot);
        }else{
            product.boxes = parseInt(product.quantity / dosesInBox, 10);
            product.vials = product.quantity % dosesInBox;
        }
    };

    //method to control switching between barcode and normal
    $scope.switchBarcodeToNormal = function(){
        if($scope.userPrograms.length > 1)
        {
            $scope.showPrograms=true;
            //TODO: load stock cards on program change
            $scope.selectedProgramId=$scope.userPrograms[0].id;
            loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10),$scope.useBarcode);

        }
        else if($scope.userPrograms.length === 1){
            $scope.showPrograms=false;
            $scope.selectedProgramId=$scope.userPrograms[0].id;
            loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10),$scope.useBarcode);

        }
    };

    //loading stock cards...added some functionality to allow barcode
    var loadStockCards=function(programId, facilityId ){
        //@todo Put these data in local storage and update the method to fetch from local store
        StockCardsByCategory.get(programId,facilityId).then(function(data){
            $scope.stockCardsToDisplay=data;
            if($scope.useBarcode){
                //These codes have been added by Kelvin
                if($scope.stockCardsToDisplay.length !== 0){
                    angular.forEach($scope.stockCardsToDisplay,function(lineItem,index){
                        if(lineItem.productCategory === "Vaccine"){
                            $scope.productsInList = angular.copy(lineItem.stockCards);
                            $scope.stockCardsToDisplay[index] = {};
                            $scope.vaccineIndex = index;
                        }
                    });

                }else{
                    $scope.productsInList = [];
                }
            }else{

            }

            VaccineAdjustmentReasons.get({programId:programId},function(data){
                $scope.adjustmentTypes=data.adjustmentReasons;
            });
        });
    };

    $scope.errorOccurred = function (error_message) {
        var snd = new Audio('data:audio/wav;base64,UklGRl9vT19XQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YU'+Array(1e3).join(123));
        snd.play();
        $scope.incorrect_message = error_message;
        $scope.show_incorrect_message = true;
        $timeout(function(){
            $scope.show_incorrect_message = false;
        },4000);
        $("#barcode_string").val('');
        angular.element(jQuery('#barcode_string')).triggerHandler('input');
        $("#barcode_string").focus();
    };
    ///////////////////////////////////////////////////////////////////////////
    /////////////////End of codes added by kelvin ////////////////////////////
    //////////////////////////////////////////////////////////////////////////


    // var loadStockCards=function(programId, facilityId){
    //     StockCardsByCategory.get(programId,facilityId).then(function(data){
    //         $scope.stockCardsToDisplay=data;
    //         VaccineAdjustmentReasons.get({programId:programId},function(data){
    //             $scope.adjustmentTypes=data.adjustmentReasons;
    //         });
    //     });
    // };
    if(homeFacility){
        $scope.homeFacility = homeFacility;
        $scope.homeFacilityId=homeFacility.id;
        $scope.selectedFacilityId=homeFacility.id;
        $scope.facilityDisplayName=homeFacility.name;
    }
    if($scope.userPrograms.length > 1)
    {
        $scope.showPrograms=true;
        //TODO: load stock cards on program change
        $scope.selectedProgramId=$scope.userPrograms[0].id;
        loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
    }
    else if($scope.userPrograms.length === 1){
        $scope.showPrograms=false;
        $scope.selectedProgramId=$scope.userPrograms[0].id;
        loadStockCards(parseInt($scope.selectedProgramId,10),parseInt($scope.selectedFacilityId,10));
    }

    $scope.date = new Date();
    $scope.apply=function(){
        $scope.$apply();
    };

    $scope.showAdjustmentReason=function(lot)
    {

        $scope.oldAdjustmentReason = angular.copy(lot.AdjustmentReasons);
        $scope.currentStockLot = lot;
        $scope.currentStockLot.adjustmentReasons=((lot.adjustmentReasons === undefined)?[]:lot.adjustmentReasons);
        //Remove reason already exist from drop down
        reEvaluateTotalAdjustmentReasons();
        updateAdjustmentReasonForLot(lot.adjustmentReasons);
        $scope.adjustmentReasonsDialogModal = true;

    };
    $scope.removeAdjustmentReason=function(adjustment)
    {
        $scope.currentStockLot.adjustmentReasons = $.grep($scope.currentStockLot.adjustmentReasons, function (reasonObj) {
            return (adjustment !== reasonObj);
        });
        updateAdjustmentReasonForLot($scope.currentStockLot.adjustmentReasons);
        reEvaluateTotalAdjustmentReasons();
    };

    $scope.closeModal=function(){
        $scope.currentStockLot.adjustmentReasons = $scope.oldAdjustmentReason;
        reEvaluateTotalAdjustmentReasons();
        $scope.adjustmentReasonsDialogModal=false;
    };
    //Save Adjustment
    $scope.saveAdjustmentReasons = function () {
        $scope.modalError = '';
        $scope.clearAndCloseAdjustmentModal();
    };
    $scope.clearAndCloseAdjustmentModal = function () {
        reEvaluateTotalAdjustmentReasons();
        $scope.adjustmentReason = undefined;
        $scope.adjustmentReasonsDialogModal=false;

    };

    $scope.addAdjustmentReason=function(newAdjustmentReason)
    {
        var adjustmentReason={};
        adjustmentReason.type = newAdjustmentReason.type;
        adjustmentReason.name = newAdjustmentReason.type.name;
        adjustmentReason.quantity= newAdjustmentReason.quantity;

        $scope.currentStockLot.adjustmentReasons.push(adjustmentReason);
        updateAdjustmentReasonForLot($scope.currentStockLot.adjustmentReasons);
        reEvaluateTotalAdjustmentReasons();
        newAdjustmentReason.type = undefined;
        newAdjustmentReason.quantity = undefined;

    };
    $scope.updateStock=function(){

        if($scope.adjustmentForm.$invalid)
        {
            console.log(JSON.stringify($scope.adjustmentForm));
            $scope.showFormError=true;
            return;
        }

        var callBack=function(result){
            if(result){
                var events=[];
                $scope.stockCardsToDisplay.forEach(function(st){
                    st.stockCards.forEach(function(s){
                        if(s.lotsOnHand !==undefined && s.lotsOnHand.length>0){
                            s.lotsOnHand.forEach(function(l){
                                if(l.quantity !== undefined && (l.quantity - l.quantityOnHand) !== 0)
                                {
                                    l.adjustmentReasons.forEach(function(reason){
                                        var event={};
                                        event.type= "ADJUSTMENT";
                                        event.productCode=s.product.code;
                                        event.quantity=reason.quantity;
                                        event.lotId=l.lot.id;
                                        event.reasonName=reason.name;
                                        if(l.customProps !==null && l.customProps.vvmstatus !==undefined)
                                        {
                                            event.customProps={"vvmStatus":l.customProps.vvmstatus};
                                        }
                                        events.push(event);
                                    });
                                }
                            });
                        }
                        else{
                            if(s.quantity !==undefined)
                            {
                                s.adjustmentReasons.forEach(function(reason){
                                    var event={};
                                    event.type= "ADJUSTMENT";
                                    event.productCode=s.product.code;
                                    event.quantity=reason.quantity;
                                    event.reasonName=reason.name;
                                    events.push(event);
                                });
                            }
                        }
                    });
                });
                StockEvent.save({facilityId:homeFacility.id},events, function (data) {
                    if(data.success !==null)
                    {
                        $scope.message=data.success;
                        $timeout(function(){
                            $window.location='/public/pages/vaccine/dashboard/index.html#/dashboard';
                        },900);
                    }
                });
            }
        };

        var options = {
            id: "confirmDialog",
            header: "label.confirm.adjust.stock.action",
            body: "msg.question.adjust.stock.confirmation"
        };
        OpenLmisDialog.newDialog(options, callBack, $dialog);
    };
    $scope.cancel=function(){
        $window.location='/public/pages/vaccine/dashboard/index.html#/dashboard';
    };

    $scope.reasonChange=function(){
        var reasonName=$scope.adjustmentReason.type.name;
        var lotExpirationTime=new Date($scope.currentStockLot.lot.expirationDate).getTime();
        var toDayTime=new Date().getTime();
        if(reasonName === "EXPIRED_VIMS" && (lotExpirationTime - toDayTime) >0 )
        {
            $scope.expirationInvalid=true;
        }
        else{
            $scope.expirationInvalid=false;
        }
    };


    function reEvaluateTotalAdjustmentReasons()
    {
        var totalAdjustments = 0;
        $($scope.currentStockLot.adjustmentReasons).each(function (index, adjustmentObject) {
            if(adjustmentObject.type.additive)
            {
                totalAdjustments = totalAdjustments + parseInt(adjustmentObject.quantity,10);
            }else{
                totalAdjustments = totalAdjustments - parseInt(adjustmentObject.quantity,10);
            }

        });
        $scope.currentStockLot.totalAdjustments=totalAdjustments;
    }
    $scope.reEvaluateTotalAdjustmentReasons= function() {reEvaluateTotalAdjustmentReasons();};

    function updateAdjustmentReasonForLot(adjustmentReasons)
    {

        var additive;
        if($scope.currentStockLot.lot !==undefined){
            additive=($scope.currentStockLot.quantity - $scope.currentStockLot.quantityOnHand >=0)?true:false;
        }
        else  if($scope.currentStockLot.lot ===undefined)
        {
            additive=($scope.currentStockLot.quantity - $scope.currentStockLot.totalQuantityOnHand >=0)?true:false;
        }
        var adjustmentReasonsForLot = _.pluck(_.pluck(adjustmentReasons, 'type'), 'name');
        $scope.adjustmentReasonsToDisplay = $.grep($scope.adjustmentTypes, function (adjustmentTypeObject) {
            return $.inArray(adjustmentTypeObject.name, adjustmentReasonsForLot) === -1 && adjustmentTypeObject.additive === additive;
        });
    }

    //Load Right to check if user level can Send Requisition ond do stock adjustment
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
    $scope.vvmTracked=function(c)
    {
        var config=_.filter(configurations.productsConfiguration, function(obj) {
            return obj.product.id===c.product.id;
        });

        if(config.length >0)
        {
            return config[0].vvmTracked;
        }
        else{
            return false;
        }
    };

}
BarcodeStockAdjustmentController.resolve = {

    homeFacility: function ($q, $timeout,UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility={};

        $timeout(function () {
            //Home Facility
            UserFacilityList.get({}, function (data) {
                homeFacility = data.facilityList[0];
                deferred.resolve(homeFacility);
            });

        }, 100);
        return deferred.promise;
    },
    configurations:function($q, $timeout, AllVaccineInventoryConfigurations) {
        var deferred = $q.defer();
        var configurations={};
        $timeout(function () {
            AllVaccineInventoryConfigurations.get(function(data)
            {
                configurations=data;
                deferred.resolve(configurations);
            });
        }, 100);
        return deferred.promise;
    }
};