function EquipmentOperationalStatusListController($scope,messageService,EquipmentOperationalStatuses,$dialog,navigateBackService,$location,RemoveOperationalStatus){

    EquipmentOperationalStatuses.get(function (data) {
        $scope.equipmentOperationalStatuses = data.operationalStatuses;
    });

    $scope.listAll = function(){

        EquipmentOperationalStatuses.get(function (data) {
            $scope.equipmentOperationalStatuses = data.operationalStatuses;
        });

    };

    $scope.editOperationalStatus = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };


    $scope.showRemoveEquipmentOperationalStatusesConfirmDialog = function (index) {

        var programEquipment = $scope.equipmentOperationalStatuses[index];
        $scope.selectedEquipmentOperationalStatus = programEquipment;

        var options = {
            id: "removeProgramEquipmentConfirmDialog",
            header: "Confirmation",
            body: "Please confirm that you want to remove the equipment Operational Status: " + programEquipment.name
        };

        OpenLmisDialog.newDialog(options, $scope.removeEquipmentOperationalStatusesConfirm, $dialog, messageService);
    };


    $scope.removeEquipmentOperationalStatusesConfirm = function (result) {

        var successCallBack = function (response) {
            $scope.message = response.success;
            $scope.showMessage = true;
            $scope.listAll();
        };

        var errorCallBack = function (response) {
            $scope.equipmentError = true;
            $scope.equipmentErrorMessage = response.data.error;
        };

        if (result) {
            RemoveOperationalStatus.delete({id: $scope.selectedEquipmentOperationalStatus.id}, successCallBack, errorCallBack);
        }
    };

}
EquipmentOperationalStatusListController.resolve = {



};

