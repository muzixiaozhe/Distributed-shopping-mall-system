app.controller('brandController',function ($scope,$controller,brandService) {
    //继承
    $controller('baseController',{$scope:$scope});
    //获取页面列表
    $scope.findAll=function () {
        brandService.findAll().success(
            function (response) {
                $scope.list=response;
            });
    };

    //分页查询 1page,rows 不再用了
    $scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function (response) {//pageResult={rows:[],total:28}
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//总记录数 要给angularJS
            }
        );
    }
    //保存
    $scope.save=function () {
        var methodName=null;
        if ($scope.entity.id!=null){
            methodName=brandService.update($scope.entity);
        }else{
            methodName=brandService.add($scope.entity);
        }
        methodName.success(
            function (response) {
                if(response.success){
                    //重新查询
                    $scope.reloadList();
                }else {
                    alert(response.message)
                }
            }
        );
    }
    //查询实体
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }

    //批量删除
    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if(response.success){
                    //重新查询
                    $scope.reloadList();
                    //重置
                    $scope.selectIds = [];
                }else {
                    alert(response.message);
                }
            }
        );
    }
    //定义搜索对象
    $scope.searchEntity={};
    $scope.search=function(page,rows){
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {//pageResult={rows:[],total:28}
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//总记录数 要给angularJS
            }
        );
    }
});
