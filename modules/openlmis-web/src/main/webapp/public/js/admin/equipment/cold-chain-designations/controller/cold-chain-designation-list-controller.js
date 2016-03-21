function DesignationListController($scope,navigateBackService,ColdChainDesignations,$location){

    ColdChainDesignations.get(function (data) {
        $scope.designations = data.designations;
    });


    $scope.editDesignation = function (id) {
        var data = {query: $scope.query};
        navigateBackService.setData(data);
        $location.path('edit/' + id);
    };
}

DesignationListController.resolve ={

};