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

function SaveIssueController($scope,$location, $window,$timeout,StockEvent,SaveDistribution) {

     $scope.distribute=function(){
        $scope.allProductsZero=true;
        $scope.clearErrorMessages();
        var printWindow;
        $scope.facilityToIssue.productsToIssueByCategory.forEach(function(category){
            category.productsToIssue.forEach(function(product){
                if(product.quantity > 0)
                {
                  $scope.allProductsZero=false;
                }
            });

        });
        if($scope.issueForm.$invalid)
        {
            console.log(JSON.stringify($scope.issueForm));
            $scope.showFormError();
            return;
        }
        if($scope.allProductsZero){
            $scope.showNoProductError();
            return;
        }


         var distribution = {};
         var events = [];

         distribution.fromFacilityId = $scope.homeFacility.id;
         distribution.toFacilityId= $scope.facilityToIssue.id;
         distribution.programId=$scope.selectedProgram.id;
         distribution.distributionDate = $scope.facilityToIssue.issueDate;
         distribution.lineItems=[];
         distribution.distributionType=$scope.facilityToIssue.type;
         distribution.status="PENDING";
         $scope.facilityToIssue.productsToIssueByCategory.forEach(function(category){

            category.productsToIssue.forEach(function(product){
             if(product.quantity >0)
             {
                 var list = {};
                 list.productId = product.productId;
                 list.quantity=product.quantity;
                 if(product.lots !==undefined && product.lots.length >0)
                 {
                     list.lots = [];
                     product.lots.forEach(function(l)
                     {
                         if(l.quantity !==null && l.quantity >0)
                         {
                             var lot = {};
                             var event ={};
                             event.type="ISSUE";
                             event.productCode =product.productCode;
                             event.facilityId=$scope.facilityToIssue.id;
                             event.occurred=$scope.facilityToIssue.issueDate;
                             event.customProps={};
                             event.customProps.occurred=$scope.facilityToIssue.issueDate;
                             event.customProps.issuedto=$scope.facilityToIssue.name;
                             event.lotId=l.lotId;
                             event.quantity=l.quantity;

                             lot.lotId = l.lotId;
                             lot.vvmStatus=l.vvmStatus;
                             lot.quantity = l.quantity;
                             list.lots.push(lot);
                             events.push(event);
                         }

                     });
                 }
             else{
                 var event ={};
                 event.type="ISSUE";
                 event.productCode =product.productCode;
                 event.facilityId=$scope.facilityToIssue.id;
                 event.occurred=$scope.facilityToIssue.issueDate;
                 event.customProps={};
                 event.customProps.occurred=$scope.facilityToIssue.issueDate;
                 event.customProps.issuedto=$scope.facilityToIssue.name;
                 event.quantity=product.quantity;
                 events.push(event);
             }
             distribution.lineItems.push(list);
            }
            });
         });


         StockEvent.save({facilityId:$scope.homeFacility.id},events, function (data) {
             if(data.success)
             {
                 SaveDistribution.save(distribution,function(distribution){
                      $scope.showMessages();
                      $scope.closeIssueModal();
                      $scope.distributionId=distribution.distributionId;
                      var url = '/vaccine/orderRequisition/issue/print/'+$scope.distributionId;
                      printWindow.location.href=url;
                 });
             }
         });
         printWindow= $window.open('about:blank','_blank');

     };

}