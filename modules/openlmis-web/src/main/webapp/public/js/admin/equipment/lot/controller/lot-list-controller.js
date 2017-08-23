function LotListController($scope,navigateBackService,LotLists,$location){

    LotLists.get(function (data) {
        $scope.lots = data.lots;
    });


    $scope.editLot = function (id) {
        console.log(id);

        var data = {query: $scope.query};
        navigateBackService.setData(data);

        $location.path('edit/' + id);
    };
}

LotListController.resolve ={

};