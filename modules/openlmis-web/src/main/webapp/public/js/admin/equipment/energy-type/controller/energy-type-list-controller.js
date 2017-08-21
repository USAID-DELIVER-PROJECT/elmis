function EnergyListController($scope,navigateBackService,EnergyTypes,$location){

    EnergyTypes.get(function (data) {
        $scope.energyTypes = data.energy_types;
    });


    $scope.editEnergyType = function (id) {
        console.log(id);

        var data = {query: $scope.query};
        navigateBackService.setData(data);

        $location.path('edit/' + id);
    };
}

EnergyListController.resolve ={

};