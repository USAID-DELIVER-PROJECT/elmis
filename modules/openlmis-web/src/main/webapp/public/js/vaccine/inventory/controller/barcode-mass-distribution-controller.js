/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */


function BarcodeMassDistributionController($scope,$location, $document,$window, $http, configurations,$timeout,homeFacility,OneLevelSupervisedFacilities,FacilityWithProducts,DistributionsByDate,StockCards,StockCardsByCategory,StockEvent,SaveDistribution,localStorageService,$anchorScroll) {

    $scope.userPrograms=configurations.programs;
    $scope.period=configurations.period;
    $scope.homeFacility=homeFacility;
    $scope.facilityDisplayName=homeFacility.name;
    $scope.toIssue=[];
    $scope.distributionType='ROUTINE';
    $scope.UnScheduledFacility=undefined;
    $scope.toDay=new Date();
    $scope.data = {};
    $scope.data.allowMultipleScan = true;
    $scope.data.showReport = false;
    $scope.maxModalBodyHeight='max-height:'+parseInt($document.height() * 0.35,10)+'px !important;height:'+parseInt($document.height() * 0.35,10)+'px !important';
    $scope.loadSupervisedFacilities=function(programId){
        OneLevelSupervisedFacilities.get({programId:programId},function(data){
            $scope.supervisedFacilities=data.facilities;
        });
    };


    $scope.loadFacilityDistributionData=function(){
        $scope.routineFacility=undefined;
        $scope.message=false;
        $scope.podMessage=false;
        if($scope.selectedRoutineFacility !== null)
            FacilityWithProducts.get($scope.selectedProgram,$scope.selectedRoutineFacility,$scope.homeFacility.id).then(function(data){
                $scope.routineFacility=data;
            });
    };

    $scope.loadStockCards=function(){
        StockCards.get({facilityId:$scope.homeFacility.id},function(data){
            $scope.stockCards=data.stockCards;
        });
    };
    $scope.loadDistributionsByDate=function(searchDate){
        $scope.distributionsByDate=[];
        DistributionsByDate.get({facilityId:$scope.homeFacility.id,date:searchDate},function(data){
            $scope.distributionsByDate=data.distributions;
        });
    };

    $scope.searchDistributions=function(searchDate){
        $scope.dateChange=true;
        $scope.loadDistributionsByDate(searchDate);
    };

    $scope.getMinDate=function(period){
        var max=new Date(period.startDate);
        max.setDate(max.getDate() -30);
        return max;
    };

    $scope.enableSearchChange=function(){

        if($scope.enableSearch ===false)
        {
            $scope.searchDate=$scope.toDay;
            $scope.loadDistributionsByDate($scope.toDay);
        }
    };

    $scope.printAll=function(distributions){
        var ids='';
        distributions.forEach(function(d){
            if(d.isSelected)
                ids=ids + d.id + ',';
        });
        ids = ids.slice(0, -1);
        if(ids !=='')
        {
            var url='/vaccine/inventory/distribution/summary/print/'+ids+'.json';
            $window.open(url,'_blank');
        }
        else{alert('No facility selected');}
    };
    $scope.getQuantityDistributionForProduct=function(distribution,stockCard){
        var product= _.findWhere(distribution.lineItems,{productId:stockCard.product.id});
        var quantity=(product !== undefined)?product.quantity:null;
        return quantity;
    };

    $scope.getTotalDistributionForProduct=function(stockCard){
        var total=0;
        $scope.distributionsByDate.forEach(function(distribution){
            if(distribution.isSelected)
            {
                var product= _.findWhere(distribution.lineItems,{productId:stockCard.product.id});
                var quantity=(product !== undefined)?product.quantity:0;
                total=total+quantity;
            }
        });
        return total;
    };

    $scope.closeModal=function(){
        $scope.currentProduct.lots=$scope.oldProductLots;
        evaluateTotal($scope.currentProduct);
        evaluateSOH($scope.currentProduct);
        $scope.currentFacility=undefined;
        $scope.lotsModal=false;
    };
    $scope.saveCurrent=function(){
        evaluateTotal($scope.currentProduct);
        evaluateSOH($scope.currentProduct);
        $scope.currentFacility=undefined;
        $scope.lotsModal=false;
    };
    $scope.updateCurrentTotal=function(product){
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

    };
    $scope.updateCurrentPOD=function(product){
        var totalCurrentLots = 0;
        product.podLots.forEach(function (lot) {
            if(lot.quantity !== undefined){
                totalCurrentLots = totalCurrentLots + parseInt(lot.quantity,10);
            }
        });
        product.quantity=totalCurrentLots;
    };
    function evaluateTotal(product){
        var totalLots = 0;
        if(product.lots !== undefined)
        {
            $(product.lots).each(function (index, lotObject) {
                if(lotObject.quantity !== undefined){
                    totalLots = totalLots + parseInt(lotObject.quantity,10);
                }

            });
            $scope.currentProduct.quantity=totalLots;
        }
        else{
            //$scope.currentProduct.quantity=totalLots;
        }

    }
    function getLotSum(_product,_lot){
        var total=0;
        ($scope.allScheduledFacilities).forEach(function(facility){
            var product=_.find(facility.productsToIssue,function(p){
                return p.productId ===_product.productId;
            });
            if(product.lots !== undefined){
                var lot=_.find(product.lots,function(l){
                    return l.lotId === _lot.lotId;
                });
                if(lot && lot.quantity !==undefined)
                {
                    total=total+parseInt(lot.quantity,10);
                }
            }
            else{
                if(product.quantity !== undefined){
                    total=total+parseInt(product.quantity,10);
                }

            }

        });
        return total;
    }
    function evaluateSOH(_product)
    {
        ($scope.allScheduledFacilities).forEach(function(facility){
            facility.productsToIssue.forEach(function(product){
                if(product.lots !== undefined)
                {
                    product.lots.forEach(function(lot){
                        lot.quantityOnHand=lot.quantityOnHand2-getLotSum(product,lot);
                    });
                }
                else{
                    product.totalQuantityOnHand=product.totalQuantityOnHand2-getLotSum(product,undefined);
                }

            });

        });
    }
    if($scope.userPrograms.length > 1)
    {
        $scope.showPrograms=true;
        //TODO: load stock cards on program change
        $scope.selectedProgram=$scope.userPrograms[0];
        $scope.loadSupervisedFacilities($scope.userPrograms[0]);
        $scope.loadStockCards();
        $scope.searchDate=$scope.toDay;
        $scope.loadDistributionsByDate($scope.toDay);
    }
    else if($scope.userPrograms.length === 1){
        $scope.showPrograms=false;
        $scope.selectedProgram=$scope.userPrograms[0];
        $scope.loadSupervisedFacilities($scope.userPrograms[0].id);
        $scope.loadStockCards();
        $scope.searchDate=$scope.toDay;
        $scope.loadDistributionsByDate($scope.toDay);
    }

    ////////////////////////////////////////////////////////////////////////
    ///////////////////Barcode codes added by kelvin///////////////////////
    //////////////////////////////////////////////////////////////////////
    //pull all gtin information
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
                    $scope.errorOccurred("Incorrect barcode string format");
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
                    $scope.errorOccurred("Incorrect barcode string format");
                }

            }else{
                $scope.errorOccurred("Incorrect barcode string format");
            }

            $scope.data.loading_item = true;
            var str = $scope.barcode.expiry;
            str = str.slice(str.length -2);
            if(str == '00'){
                $scope.barcode.expiry = $scope.barcode.expiry.slice( 0, $scope.barcode.expiry.length-2 ) + '01';
            }
            $scope.barcode.formatedDate = $scope.formatDate(new Date("20"+$scope.barcode.expiry.replace(/(.{2})/g,"$1-").slice(0, -1)));

            var todays_time = new Date();
            var snd;
            var barcode_date = new Date($scope.barcode.formatedDate);
            if(todays_time.getTime() > barcode_date.getTime()){
                $scope.errorOccurred("Item scanned is expired, expiry date is "+$scope.barcode.formatedDate);
            }else{
                $scope.current_item = $scope.getItemByGTIN($scope.barcode , $scope.facilityToIssue.productsToIssueByCategory[$scope.vaccineIndex].productsToIssue);
                if($scope.current_item.gtinInformation === false){
                    $scope.data.error_loading_gtin = true;
                    $scope.data.error_loading_item = false;
                    $scope.data.loading_item = false;
                    $("#barcode_string").val('');
                    angular.element(jQuery('#barcode_string')).triggerHandler('input');
                    $("#barcode_string").focus();
                    snd = new Audio('data:audio/wav;base64,UklGRl9vT19XQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YU'+Array(1e3).join(123));
                    snd.play();
                }
                else{
                    $scope.data.error_loading_gtin = false;
                    $scope.data.error_loading_item = false;
                    $scope.data.loading_item = true;
                    if($scope.current_item.available === false){
                        $scope.errorOccurred("You do not have this Item in your stock");

                    }else{
                        $scope.data.error_loading_item = false;
                        $scope.data.loading_item = false;
                        $scope.data.show_singleItem = true;
                        $scope.show_incorrect_message = false;
                        $("#barcode_string").val('');
                        angular.element(jQuery('#barcode_string')).triggerHandler('input');
                        $("#barcode_string").focus();
                        snd = new Audio("data:audio/wav;base64,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qJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=");
                        snd.play();
                    }
                }
            }



        }else{
            $scope.data.error_loading_gtin = false;
            $scope.data.error_loading_item = false;
            $scope.data.loading_item = false;
            // var snd = new Audio("data:audio/wav;base64,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qJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=");
            // snd.play();
        }


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

    //producing pdf for issuing report
    $scope.produceIssuingPDF = function(){
        html2canvas(document.getElementById('exportthis'), {
            onrendered: function (canvas) {
                var data = canvas.toDataURL();
                var docDefinition = {
                    content: [{
                        image: data,
                        width: 1100
                    }]
                };
                pdfMake.createPdf(docDefinition).download("Score_Details.pdf");
            }
        });
    };

    //close pdf view
    $scope.cancelPDF = function(){
        $scope.data.showReport = false;
    };

    //check if there is another batch in the system for that product that expires earlier
    $scope.expireSonner = function(barcode_object,lots){
        var return_object = {'available':false,item:{}};
        if(lots.length <= 1){

        }else{
            angular.forEach(lots,function(lot,key){
                var todays_time = new Date();
                var lots_date = new Date(lot.expirationDate);
                if(!lot.quantityOnHand || lot.quantityOnHand <= 0 || lot.quantityOnHand === "" || todays_time.getTime() > lots_date.getTime()){
                    lots.splice(key, 1);
                }
            });
            var sorted = lots.sort($scope.sort_by('expirationDate', false, function(a){return new Date(a).getTime();}));
            if(sorted[0].lotCode === barcode_object.lot_number){
                return_object.available = false;
            }else{
                return_object.available = true;
                return_object.item = sorted[0];
            }
        }
        return return_object;
    };

    $scope.sort_by = function(field, reverse, primer){

        var key = primer ?
            function(x) {return primer(x[field]);} :
            function(x) {return x[field];};

        reverse = !reverse ? 1 : -1;

        return function (a, b) {
            return a = key(a), b = key(b), reverse * ((a > b) - (b > a));
        };
    };

    $scope.removeFromLineItems = function(product){
        angular.forEach($scope.facilityToIssue.productsToIssueByCategory[$scope.vaccineIndex].productsToIssue,function(product1,key){
            if(product.productId === product1.productId){
                $scope.facilityToIssue.productsToIssueByCategory[$scope.vaccineIndex].productsToIssue.splice(key,1);
            }
        });
    };

    //finding an item from the gtin Lookup table
    $scope.getItemByGTIN = function(barcode_object,listItems){
        var item = {available:false, gtinInformation:false};
        angular.forEach($scope.gtin_lookups,function(packagingInformation){
            if(barcode_object.gtin === packagingInformation.gtin){
                item.gtinInformation = true;
                angular.forEach( listItems,function( product ){
                    if( packagingInformation.productid === product.productId ){
                        item.product = product;
                        //first check if there is another batch that expires sooner
                        var ExpireSooner = $scope.expireSonner(barcode_object,product.lots);
                        var progress = true;
                        if(ExpireSooner.available){
                            var confirm_box = confirm("There are "+ExpireSooner.item.quantityOnHand+" Doses of a batch ("+ExpireSooner.item.lotCode+") That expires ("+ExpireSooner.item.expirationDate+") Which is sooner than the selected item, Do you still want to distribute same Item?");
                            if(confirm_box){
                                progress = true;
                            }else{
                                progress = false;
                                item.available = true;
                            }
                        }else{
                            progress = true;
                        }

                        //append packaging information
                        product.packaging = packagingInformation;
                        if(progress){
                            //construct a lot
                            var lots = angular.copy(product.lots);
                            angular.forEach(lots,function(productLot){
                                // if(productLot.lotCode === barcode_object.lot_number && barcode_object.formatedDate === productLot.expirationDate){
                                if(productLot.lotCode === barcode_object.lot_number){
                                    item.available = true;
                                    if($scope.checkProductInList($scope.facilityToIssue.productsToIssueByCategory[$scope.vaccineIndex].productsToIssue,product.productId)){
                                        angular.forEach($scope.facilityToIssue.productsToIssueByCategory[$scope.vaccineIndex].productsToIssue, function(item){
                                            // if(item.productId === packagingInformation.productid){
                                            if($scope.checkLOtInList(item.lots,barcode_object.lot_number)){
                                                angular.forEach(item.lots,function(singleLot){
                                                    if(singleLot.lotCode === barcode_object.lot_number){
                                                        if($scope.data.allowMultipleScan){
                                                            if(singleLot.boxes){
                                                                singleLot.boxes++;
                                                            }else{
                                                                singleLot.boxes = 1;
                                                            }
                                                            $scope.updateCurrentTotal1(item,singleLot);
                                                        }
                                                    }

                                                });
                                            }
                                        });
                                        //if it is a new item completely.
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        return item;
    };

    $scope.switchBarcode = function (value) {
        if(value){
            $timeout(function(){
                $("#barcode_string").focus();
            });
            if($scope.facilityToIssue.productsToIssueByCategory.length !== 0){
                angular.forEach($scope.facilityToIssue.productsToIssueByCategory,function(lineItem,index){
                    if(lineItem.productCategory === "Vaccine"){
                        angular.forEach($scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue,function(item, lineIndex){
                            angular.forEach($scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots, function (lotItem, lotIndex) {
                                $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots[lotIndex].showthis = true;

                            });
                        });
                        // $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue = [];
                        $scope.vaccineIndex = index;
                    }
                });

            }
        }else{
            if($scope.facilityToIssue.productsToIssueByCategory.length !== 0){
                angular.forEach($scope.facilityToIssue.productsToIssueByCategory,function(lineItem,index){
                    if(lineItem.productCategory === "Vaccine"){
                        angular.forEach($scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue,function(item, lineIndex){
                            angular.forEach($scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots, function (lotItem, lotIndex) {
                                $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots[lotIndex].showthis = false;

                            });
                        });
                        // $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue = [];
                        $scope.vaccineIndex = index;
                    }
                });

            }
        }
    };

    $scope.checkProductInList = function(list,productID){
        var data = false;
        angular.forEach(list,function(product){
            if(productID === product.productId){
                data = true;
            }
        });
        return data;
    };

    $scope.checkLOtInList = function(list,lotcode){
        var data = false;
        angular.forEach(list,function(product){
            if(lotcode === product.lotCode){
                data = true;
            }
        });
        return data;
    };

    //get Maximum number of boxes one can have per product based on amount on store
    $scope.getMaximumBoxes = function(product,quantityOnHand){
        if(product.packaging) {
            return parseInt(quantityOnHand / (product.packaging.dosespervial * product.packaging.vialsperbox), 10);
        }else{
            return 0;
        }
    };
    //get Maximum number of lose vials one can have
    $scope.getMaximumLoseVials = function(product,quantityOnHand,boxes){
        if(product.packaging){
            var dosesOnBoxes = boxes*product.packaging.dosespervial * product.packaging.vialsperbox;
            var remainingDoses = quantityOnHand - dosesOnBoxes;
            return parseInt(remainingDoses / (product.packaging.dosespervial), 10);
        }else{
            return 0;
        }

    };
    //display the model for issuing
    $scope.useBarcode = false;

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

    //update the total doses for the product and the boxes and vials for a updated value
    $scope.updateCurrentTotal=function(product,lot){
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

        //update boxes and vials as doses change
        if(lot){
            if(product.packaging){
                var vials_per_box = product.packaging.vialsperbox;
                var doses_per_vials = product.packaging.dosespervial;
                var dosesInBox = vials_per_box*doses_per_vials;
                lot.boxes = parseInt(lot.quantity / dosesInBox, 10);
                lot.vials = lot.quantity % dosesInBox;
            }
        }else{

        }

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

    ////////////////////////////////////////////////////////////////////////
    ///////////////////End of Barcode codes added by kelvin///////////////////////
    //////////////////////////////////////////////////////////////////////
    //switch between using barcode and normal

    //I have added some codes here to take care of barcode scanning option..
    $scope.showIssueModal=function(facility, type){
        $scope.hidePDF();
        if($scope.useBarcode){
            $scope.facilityToIssue=angular.copy(facility);
            $scope.facilityToIssue.type=type;
            var rightNow = new Date();
            $scope.facilityToIssue.displayIssueDate = (rightNow.getMonth() + 1) + '/' + rightNow.getDate() + '/' +  rightNow.getFullYear();
            $scope.facilityToIssue.issueDate = $scope.formatDate(new Date());

            $timeout(function(){
                $("#barcode_string").focus();
            });
            if($scope.facilityToIssue.productsToIssueByCategory.length !== 0){
                angular.forEach($scope.facilityToIssue.productsToIssueByCategory,function(lineItem,index){
                    if(lineItem.productCategory === "Vaccine"){
                        angular.forEach($scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue,function(item, lineIndex){
                            $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].show_now = true;
                            angular.forEach($scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots, function (lotItem, lotIndex) {
                                $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots[lotIndex].boxes = null;
                                $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue[lineIndex].lots[lotIndex].vials = null;
                            });
                        });
                        // $scope.facilityToIssue.productsToIssueByCategory[index].productsToIssue = [];
                        $scope.vaccineIndex = index;
                    }
                });

            }else{
                $scope.productsInList = [];
            }
            $scope.colspanTouse = 3;
        }else{
            $scope.facilityToIssue=angular.copy(facility);
            $scope.facilityToIssue.type=type;

            $scope.colspanTouse = 1;
        }
        $scope.issueModal=true;

    };

    $scope.closeIssueModal=function(){
        $scope.facilityToIssue=undefined;
        $scope.clearErrorMessages();
        $scope.issueModal=false;
    };
    $scope.resetUnscheduledFacility=function(){
        $scope.UnScheduledFacilityId=undefined;
        $scope.UnScheduledFacility=undefined;
    };
    $scope.showNoProductError=function(){
        $scope.showNoProductErrorMessage=true;
    };
    $scope.showFormError=function(){
        $scope.showFormErrorMessage=true;
    };
    $scope.clearErrorMessages=function(){
        $scope.showFormErrorMessage = false;
        $scope.showNoProductErrorMessage=false;
    };

    $scope.showPODModal=function(facility){
        $scope.podModal=true;
        $scope.facilityPOD=facility;
    };

    $scope.closePODModal=function(){
        $scope.podModal=false;
        $scope.facilityPOD=undefined;
    };

    $scope.distribution_found = false;
    $scope.print = function(distributionId){
        $scope.distribution_found = false;
        angular.forEach($scope.distributionsByDate,function (data) {
            if( data.id === distributionId ){
              $scope.data.finalDistribution = data;
                $scope.distribution_found = true;
                $timeout(function () {
                    window.print();
                    html2canvas(document.getElementById('exportthis'), {
                        onrendered: function (canvas) {
                            var data = canvas.toDataURL();
                            var docDefinition = {
                                content: [{
                                    image: data,
                                    width: 1100
                                }]
                            };
                           pdfMake.createPdf(docDefinition).download("Score_Details.pdf");
                        }
                    });
                });

            }
        });
        // var url = '/vaccine/orderRequisition/issue/print/'+distributionId;
        // $window.open(url, '_blank');
    };

    $scope.hidePDF = function () {
        $scope.distribution_found = false;
    };

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

    $scope.cancel=function(){
        $window.location='/public/pages/vaccine/dashboard/index.html#/dashboard';
    };

    $scope.setSelectedFacility=function(facility)
    {
        if(facility)
        {
            $scope.selectedFacilityId=facility.id;
        }
        else
        {
            $scope.selectedFacilityId=null;
        }

    };

    $scope.getSelectedFacilityColor = function (facility) {
        if(facility !== undefined)
        {
            if (!$scope.selectedFacilityId) {
                return 'none';
            }

            if ($scope.selectedFacilityId=== facility.id) {
                return "background-color :#dff0d8; color: white !important";
            }
            else {
                return 'none';
            }
        }

    };
    $scope.loadUnScheduledFacilities=function(){
        $scope.UnScheduledFacilities=_.where($scope.allUnScheduledFacilities,{name:$scope.facilityQuery});
    };
    $scope.hasProductToIssue=function(facility)
    {
        var hasAtLeastOne=false;
        var hasError=false;

        if(facility !==undefined && facility.productsToIssue !== undefined)
        {
            facility.productsToIssue.forEach(function(p)
            {
                if(p.quantity >0 )
                {
                    hasAtLeastOne=true;
                }
                if(p.quantity >0 && p.quantityOnHand < p.quantity)
                {
                    hasError=true;
                }
            });
            return (hasAtLeastOne && !hasError);
        }

    };
    $scope.updateCurrentScheduledFacilities = function () {
        $scope.allScheduledFacilities = [];
        $scope.query = $scope.query.trim();

        if (!$scope.query.length) {
            $scope.allScheduledFacilities = $scope.allScheduledFacilitiesCopy;
            //$scope.page();
            return;
        }

        $($scope.allScheduledFacilitiesCopy).each(function (index, facility) {
            var searchString = $scope.query.toLowerCase();
            if (facility.name.toLowerCase().indexOf(searchString) >= 0) {
                $scope.allScheduledFacilities.push(facility);
            }
        });
        //  $scope.page();
    };
    $scope.saveAll=function(){
        $scope.allScheduledFacilities.forEach(function(facility){
            if($scope.hasProductToIssue(facility) && facility.status !== "PENDING"){
                $scope.showIssueModal(facility,"SCHEDULED");
            }
        });
    };
    $scope.showMessages=function(){
        $scope.message=true;
        $scope.selectedRoutineFacility = null;
        $scope.routineFacility=false;
    };
    $scope.showPODMessages=function(){
        $scope.podMessage=true;
        $scope.selectedRoutineFacility = null;
        $scope.routineFacility=false;
    };

}
BarcodeMassDistributionController.resolve = {

    homeFacility: function ($q, $timeout,UserFacilityList) {
        var deferred = $q.defer();
        var homeFacility={};

        $timeout(function () {
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