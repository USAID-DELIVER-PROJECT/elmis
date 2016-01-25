// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $uibModal service used above.

function ModalInstanceCtrl($scope, $modalInstance, items, ContactList,SettingsByKey,SendVaccineMessages) {
    SettingsByKey.get({key: 'LATE_RNR_NOTIFICATION_EMAIL_TEMPLATE'}, function (data) {
        $scope.email_template = data.settings.value;
    });
    $scope.items = items;
    $scope.show_email = false;
    $scope.contacts = [];
    var contactsCallBack=function (aa,i) {
        ContactList.get(aa, function (aa, i) {

            return function (data) {
                var index = aa.facilityId;

                //$scope.contacts = data.contacts;
                angular.forEach(data.contacts, function (contact) {
                    $scope.contacts.push(contact);
                });


            };
        }(aa, i));


    };
    function loadFacilityContacts() {
        for (i = 0; i < items.length; i++) {
            if (items[i].hascontacts && items[i].checked) {
                var aa = {type: 'email', facilityId: items[i].facility_id};
                contactsCallBack(aa,i);
            }
        }
    }

    $scope.itemChecked = function (_index) {
        var value = !$scope.items[_index].checked;
        $scope.items[_index].checked = value;

    };
    $scope.emailForNonReportingFacilities = function () {
        var len = $scope.items.length;
        $scope.show_email = true;
        loadFacilityContacts();

    };
    $scope.getBackGroundColor = function (_index) {
        var bgColor = '';

        if (_index % 2 === 0) {
            bgColor = 'lightGreen';
        } else {
            bgColor = 'white';
        }


        return bgColor;
    };
    $scope.ok = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
    $scope.sendVaccineFacilityEmail = function () {

        var messages = constructMessage();

        SendVaccineMessages.post({messages: messages}, function () {
            $scope.sent_confirmation = true;
        });
    };

    var constructMessage = function () {
        // construct the messages here
        var messages = [];

        for (var i = 0; i < $scope.contacts.length; i++) {
            var template =  $scope.email_template;
            var contact = $scope.contacts[i];

            template = template.replace('{name}', contact.name);
            template = template.replace('{facility_name}', "");
            template = template.replace('{period}', "");
            messages.push({
                type: 'email',
                facilityId: "14254",
                contact: contact.contact,
                message: template
            });
        }
        return messages;
    };
}