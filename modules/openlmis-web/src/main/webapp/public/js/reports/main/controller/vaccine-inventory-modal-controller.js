function ModalInstanceCtrl($scope,$http, $modalInstance,items){
    var colors = {'#00B2EE':'blue', '#006600':'green', '#FFDB00':'yellow', 'red': 'red'};




    $scope.items =  _.filter(items.facilities.facilities, _.property(colors[items.color]), function(n){
        return n > 0;
    });


    console.log(items.facilities.facilities);
    $scope.selectedItem = items.product;



        $scope.getBackGroundColor = function (_index) {
            var bgColor = '';
            var fColor='#a6a6a6';
             if (_index % 2 === 0) {
             bgColor = 'lightblue';
             fColor='white';
             } else {
             bgColor = 'white';
             }

            return {fColor:fColor, bgColor:bgColor};
        };
        $scope.ok = function () {
            $modalInstance.close();
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    $scope.printPdf = function (uuid){
        $http({
            url : 'PDF_URL',
            method : 'GET',
            headers : {
                'Content-type' : 'application/pdf'
            },
            responseType : 'arraybuffer'
        }).success(function(data, status, headers, config) {
            var pdfFile = new Blob([ data ], {
                type : 'application/pdf'
            });
            var pdfUrl = URL.createObjectURL(pdfFile);
            var printwWindow = $window.open(pdfUrl);
            printwWindow.print();
        }).error(function(data, status, headers, config) {
            alert('Sorry, something went wrong');
        });
    };


}
