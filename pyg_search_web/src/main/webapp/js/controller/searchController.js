app.controller('searchController',function ($scope,searchService) {

    //搜索对象
    $scope.searchMap={'keywords':'','brand':'','category':'','spec':{}}
    //添加搜索项
    $scope.addSearchIte=function (key,value) {
        if (key=="brand"||key=='category'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();//重新查询
    }
    //移除复合搜索条件
    $scope.removeSearchIte=function (key) {
        if (key=="brand"||key=='category'){
            $scope.searchMap[key]='';
        }else{
           delete $scope.searchMap.spec[key];
        }
        $scope.search();//重新查询
    }
    //搜索
    $scope.search=function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
            }
        );
    }

})