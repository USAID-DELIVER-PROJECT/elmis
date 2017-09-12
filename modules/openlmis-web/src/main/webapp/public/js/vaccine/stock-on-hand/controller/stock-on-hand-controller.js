function StockOnHandControllerFunc($scope,$compile,homeFacilityId,GetStockCards,UserGeographicZoneTree,FacilityGeoTree,GeoDistrictTree){
    console.log(homeFacilityId);
$scope.homeFacility = homeFacilityId;

$scope.stockCards = [];

if(GetStockCards !== undefined)
$scope.stockCards = GetStockCards.stockCards;

  //  console.log(JSON.stringify($scope.stockCards));
    var tree = [
        {
            text: "Parent 1",
            nodes: [
                {
                    text: "Child 1",
                    nodes: [
                        {
                            text: "Grandchild 1"
                        },
                        {
                            text: "Grandchild 2"
                        }
                    ]
                },
                {
                    text: "Child 2"
                }
            ]
        },
        {
            text: "Parent 2"
        },
        {
            text: "Parent 3"
        },
        {
            text: "Parent 4"
        },
        {
            text: "Parent 5"
        }

    ];

    $('#tree').treeview({data: tree/*,

        text: "Node 1",
        icon: "glyphicon glyphicon-stop",
        selectedIcon: "glyphicon glyphicon-stop",
        color: "#000000",
        backColor: "#Red",
        href: "#node-1",
        selectable: true,
        state: {
            checked: false,
            disabled: false,
            expanded: true,
            selected: true
        }*/
    });





    GeoDistrictTree.get({}, function(data){
        var data2=data.regionFacilityTree;
        $('#tree').treeview({data: data2,
            levels: 1,
            color: "red",
            onhoverColor:'red',
            onNodeSelected: function(event, data) {
        //console.log(data);
            }
            /*,

            text: "Node 1",
            icon: "glyphicon glyphicon-stop",
            selectedIcon: "glyphicon glyphicon-stop",
            color: "red",
            backColor: "red",
            href: "#node-1",
            selectable: true,
            state: {
                checked: false,
                disabled: false,
                expanded: false,
                selected: false
            }
*/


        });

    });




    $scope.mainGridOptions = {

        dataSource: {
            type: "odata",
            transport: {
                read: "//demos.telerik.com/kendo-ui/service/Northwind.svc/Employees"
            },
            pageSize: 5,
            serverPaging: true,
            serverSorting: true
        },
        sortable: true,
        pageable: true,
        dataBound: function() {
            this.expandRow(this.tbody.find("tr.k-master-row").first());
        },
        columns: [{
            field: "FirstName",
            title: "First Name",
            width: "120px"
        },{
            field: "LastName",
            title: "Last Name",
            width: "120px"
        },{ command: { text: "Add Tab", click: insertContent }, title: " ", width: "180px" }]
    };




    function insertContent(e) {
        e.preventDefault();
        var dataItem = this.dataItem($(e.currentTarget).closest("tr"));
        $scope.tabstrip.insertAfter(
            { text: dataItem.FirstName + ' ' + dataItem.LastName + ' <button ng-click="removeTab($event)" class="k-button-icon"><span class="k-icon k-i-close"></span></button>',
                encoded: false,
                content: dataItem.Notes
            },
            $scope.tabstrip.tabGroup.children("li:last")
        );
        $compile($scope.tabstrip.tabGroup.children("li:last"))($scope);
    }
    $scope.removeTab = function(event){
        var item = $(event.currentTarget).closest(".k-item");
        $scope.tabstrip.remove(item.index());
    };
}


StockOnHandControllerFunc.resolve= {

    homeFacilityId: function ($q, $timeout, UserHomeFacility) {
        var deferred = $q.defer();

        $timeout(function () {

            UserHomeFacility.get({}, function (data) {

                deferred.resolve(data.homeFacility.id);
            });

        }, 100);

        return deferred.promise;
    },

    GetStockCards: function ($q, $timeout, UserHomeFacility,StockCards) {
        var deferred = $q.defer();

        $timeout(function () {

            UserHomeFacility.get({}, function (data) {

                StockCards.get({facilityId: parseInt(data.homeFacility.id, 10)},
                    function (data) {
                        deferred.resolve(data);
                    });
            });

        }, 100);

        return deferred.promise;
    }

};