/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */
function StockAdjustmentController($scope, $timeout,$window,$routeParams,$dialog,StockCardsByCategory,configurations,StockEvent,localStorageService,homeFacility,VaccineAdjustmentReasons,UserFacilityList) {

    //Get Home Facility
    $scope.currentStockLot = undefined;
    $scope.adjustmentReasonsDialogModal = false;
    $scope.userPrograms=configurations.programs;
    $scope.adjustmentReason={};
    $scope.vvmStatuses=[{"value":"1","name":" 1 "},{"value":"2","name":" 2 "}];
    $scope.productsConfiguration=configurations.productsConfiguration;
    var AdjustmentReasons=[];

    var loadStockCards=function(programId, facilityId){
            StockCardsByCategory.get(programId,facilityId).then(function(data){
                $scope.stockCardsToDisplay=data;
                VaccineAdjustmentReasons.get({programId:programId},function(data){
                       $scope.adjustmentTypes=data.adjustmentReasons;
                });
            });
        };
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
              return $.inArray(adjustmentTypeObject.name, adjustmentReasonsForLot) == -1 && adjustmentTypeObject.additive === additive;
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
StockAdjustmentController.resolve = {

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