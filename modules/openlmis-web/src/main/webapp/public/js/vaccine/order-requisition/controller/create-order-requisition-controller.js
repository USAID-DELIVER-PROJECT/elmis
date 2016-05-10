/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI)/MoHCDGEC Tanzania.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the GNU Affero General Public License for more details.
 */


function CreateVaccineOrderRequisition($scope, $dialog,$routeParams, $window, report,
                                       VaccineOrderRequisitionSubmit, GetFacilitySupervisorsByProgram,SendMessages,SettingsByKey,$filter) {

    $scope.report = new VaccineOrderRequisition(report);

   /* SettingsByKey.get({key: 'VACCINE_ORDER_REQUISITION_SUPERVISOR_NOTIFICATION_EMAIL_TEMPLATE'}, function (data) {
        $scope.email_template_supervisor = data.settings.value;
    });


    var constructMessage = function () {
        // construct the messages here
        var messages = [];

        for (var i = 0; i < $scope.contacts.length; i++) {
            var template =  $scope.email_template_supervisor;
            var contact = $scope.contacts[i];

            template = template.replace('{approver_name}', contact.name);
            template = template.replace('{facility_name}', $scope.homeFacility.name);
            template = template.replace('{period}', $scope.period);
            template = template.replace('{link}',$scope.emailLink);

            messages.push({
                type: 'email',
                facilityId: parseInt($scope.homeFacility.id,10),
                contact: contact.contact,
                message: template
            });
        }
        return messages;
    };

    $scope.showSendEmailSupervisor = function (facility) {


        $scope.selected_facility = facility;
        $scope.homeFacility = facility.facility;
        $scope.period = facility.period.name;
        $scope.emailLink = 'http://localhost:9091/public/pages/vaccine/order-requisition/index.html#/view';
       GetFacilitySupervisorsByProgram.get({
            programId: parseInt(facility.programId, 10),
            facilityId: parseInt(facility.facilityId,10)
        },
            function (data) {
            $scope.contacts = data.supervisors;

                var messages = constructMessage();
                console.log(messages);

                SendMessages.post({messages: messages}, function () {
                    $scope.sent_confirmation = true;
                });


        });

        $scope.show_email_supervisor = !$scope.show_email_supervisor;

    };

    $scope.sendFacilityEmail = function () {

        var messages = constructMessage();

        SendMessages.post({messages: messages}, function () {
            $scope.sent_confirmation = true;
            console.log($scope.sent_confirmation);
        });
    };*/

  /*  $scope.doSend = function(){
        $scope.showSendEmailSupervisor(report);
       // $scope.sendFacilityEmail();
        $scope.show_email_supervisor = false;
    };*/
    $scope.orderModal = false;

    $scope.selectedType = 0;

    $scope.productFormChange = function () {
        $scope.selectedType = 0;
        $scope.calculateVial = false;
        $scope.report = new VaccineOrderRequisition(report);

    };

    $scope.productFormChange1 = function () {
        $scope.selectedType = 1;
        $scope.calculateVial = true;
        $scope.report = new VaccineOrderRequisition2(report);

    };

    $scope.print = function (reportId) {

        VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
            $scope.$parent.print = data.report;
        });
        var url = '/vaccine/orderRequisition/' + reportId + '/print';
        $window.open(url, '_blank');
    };

    $scope.submit = function () {
        var printWindow;
        if ($scope.report.emergency === true) {
            $scope.orderModal = true;
        } else {

            var callBack = function (result) {

                if (result) {

                    VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
                        var url = '/vaccine/orderRequisition/' + data.report.id + '/print';
                        printWindow.location.href=url;
                        $scope.disableButton = true;
                        $window.location = '/public/pages/vaccine/dashboard/index.html#/dashboard';
                    });
                    printWindow= $window.open('about:blank','_blank');
                    $scope.message = "label.form.Submitted.Successfully";

                }
            };
            var options = {
                id: "confirmDialog",
                header: "label.confirm.order.submit.action",
                body: "msg.question.submit.order.confirmation"
            };
            OpenLmisDialog.newDialog(options, callBack, $dialog);

        }

    };

    $scope.closeOrderModal = function () {
        $scope.orderModal = false;
    };

    $scope.submitEmergency = function () {
        var printWindow;
        VaccineOrderRequisitionSubmit.update($scope.report, function (data) {
            var url = '/vaccine/orderRequisition/' + data.report.id + '/print';
            printWindow.location.href=url;
            $scope.message = "label.form.Submitted.Successfully";
            $window.location = '/public/pages/vaccine/dashboard/index.html#/dashboard';
        });
        printWindow= $window.open('about:blank','_blank');


    };

    $scope.cancel = function () {
        $window.location = '/public/pages/vaccine/dashboard/index.html#/dashboard';
    };

    $scope.viewOrderPrint = function () {

        var url = '/vaccine/orderRequisition/' +  parseInt($routeParams.id,10) + '/print';
        $window.open(url, '_blank');
    };



}


CreateVaccineOrderRequisition.resolve = {

    report: function ($q, $timeout, $route, VaccineOrderRequisitionByCategory) {
        var deferred = $q.defer();
        $timeout(function () {
            VaccineOrderRequisitionByCategory.get(parseInt($route.current.params.id, 10), parseInt($route.current.params.programId, 10)).then(function (data) {
                deferred.resolve(data);
            });
        }, 100);
        return deferred.promise;
    }

};