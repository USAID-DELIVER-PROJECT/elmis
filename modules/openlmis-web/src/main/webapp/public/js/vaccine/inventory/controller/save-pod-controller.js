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
                     var lineItem = {};

                     lineItem.productId = product.productId;
                     lineItem.quantity=parseInt(product.quantity,10);
                     lineItem.id=product.lineItemId;

                     if(product.podLots !==undefined && product.podLots.length >0){
                         lineItem.lots = [];
                         product.podLots.forEach(function(l)
                         {
                              if(parseInt(l.quantity,10) !==null && parseInt(l.quantity,10) >0)
                              {
                                  if(parseInt(l.quantity,10) !== l.originalIssueQuantity)
                                  {
                                       var event={};
                                       event.productCode =product.productCode;
                                       event.facilityId=$scope.facilityPOD.id;
                                       event.customProps={};
                                       event.customProps.occurred=$scope.facilityPOD.issueDate;
                                       event.occurred=$scope.facilityPOD.issueDate;
                                       event.lotId=l.lotId;
                                       if(parseInt(l.quantity,10) > l.originalIssueQuantity)  {
                                         event.type='ISSUE';
                                         event.quantity=parseInt(l.quantity,10)-l.originalIssueQuantity;
                                         event.customProps.issuedto=$scope.facilityPOD.name;
                                         if((l.quantity -l.originalIssueQuantity) <= l.quantityOnHand)
                                         {
                                             events.push(event);
                                         }
                                       }
                                       else if(parseInt(l.quantity,10) < l.originalIssueQuantity){
                                         event.type='RECEIPT';
                                         event.quantity=l.originalIssueQuantity-parseInt(l.quantity,10);
                                         event.customProps.receivedfrom="Proof of Delivery";
                                         events.push(event);
                                       }

                                  }
                                  var lot = {};
                                  lot.lotId = l.lotId;
                                  lot.id=l.lineItemLotId;
                                  lot.vvmStatus=l.vvmStatus;
                                  lot.quantity = parseInt(l.quantity,10);
                                  lineItem.lots.push(lot);
                              }

                         });
                     }
                     else if(product.podLots ===undefined){
                         if(parseInt(product.quantity,10) !== product.originalIssueQuantity){
                              var event={};
                              event.productCode =product.productCode;
                              event.facilityId=$scope.facilityPOD.id;
                              event.customProps={};
                              event.customProps.occurred=$scope.facilityPOD.issueDate;
                              event.occurred=$scope.facilityPOD.issueDate;
                              if(parseInt(product.quantity,10) > product.originalIssueQuantity)  {
                                  event.type='ISSUE';
                                  event.quantity=product.quantity-product.originalIssueQuantity;
                                  event.customProps.issuedto=$scope.facilityPOD.name;
                                  if((parseInt(product.quantity,10) -product.originalIssueQuantity) <= product.totalQuantityOnHand)
                                  {
                                       events.push(event);
                                  }
                              }
                              else if(product.quantity < product.originalIssueQuantity){
                                  event.type='RECEIPT';
                                  event.quantity=product.originalIssueQuantity-parseInt(product.quantity,10);
                                  event.customProps.receivedfrom="Proof of Delivery";
                                  events.push(event);
                              }
                         }
                     }
                     distribution.lineItems.push(lineItem);
                 }
                 });
              });
              SaveDistribution.save(distribution,function(data){
                 if(events.length >0)
                 {
                    StockEvent.save({facilityId:$scope.homeFacility.id},events, function (data) {

                    });
                 }
                 $scope.closePODModal();
                 $scope.showPODMessages();
              });

          };
}