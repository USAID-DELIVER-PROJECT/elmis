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

function SavePODController($scope,$location, $window,$timeout,StockEvent,SaveDistribution) {

     $scope.updatePOD=function(){
              var distribution={};
              distribution.id=$scope.facilityPOD.distributionId;
              distribution.toFacilityId=$scope.facilityPOD.id;
              distribution.programId=$scope.userPrograms[0].id;
              distribution.status="RECEIVED";
              distribution.lineItems=[];
              var events =[];
              $scope.facilityPOD.productsToIssueByCategory.forEach(function(category){

                 category.productsToIssue.forEach(function(product){
                 if(product.quantity >0)
                 {
                     var list = {};

                     list.productId = product.productId;
                     list.quantity=product.quantity;
                     list.id=product.lineItemId;

                     if(product.lots !==undefined && product.lots.length >0)
                     {
                         list.lots = [];
                         product.lots.forEach(function(l)
                         {
                              if(l.quantity !==null && l.quantity >0)
                              {
                                  if(l.quantity !== l.originalIssueQuantity)
                                  {
                                       var event={};
                                       event.productCode =product.productCode;
                                       event.facilityId=$scope.facilityPOD.id;
                                       event.customProps={};
                                       event.customProps.occurred=$scope.facilityPOD.issueDate;
                                       event.lotId=l.lotId;
                                       if(l.quantity > l.originalIssueQuantity)  {
                                         event.type='ISSUE';
                                         event.quantity=l.quantity-l.originalIssueQuantity;
                                         event.customProps.issuedto=$scope.facilityPOD.name;
                                         if((l.quantity -l.originalIssueQuantity) <= l.quantityOnHand)
                                         {
                                             events.push(event);
                                         }
                                       }
                                       else if(l.quantity < l.originalIssueQuantity){
                                         event.type='RECEIPT';
                                         event.quantity=l.originalIssueQuantity-l.quantity;
                                         events.push(event);
                                       }

                                  }
                                  var lot = {};
                                  lot.lotId = l.lotId;
                                  lot.id=l.lineItemLotId;
                                  lot.vvmStatus=l.vvmStatus;
                                  lot.quantity = l.quantity;
                                  list.lots.push(lot);
                              }

                         });
                     }
                     else{

                     }
                     distribution.lineItems.push(list);
                 }
                 });
              });

              SaveDistribution.save(distribution,function(data){
                 if(events.length >0)
                 {
                    StockEvent.save({facilityId:$scope.homeFacility.id},events, function (data) {

                    });
                 }
              });
              $scope.closePODModal();
              $scope.showMessages();
          };
}