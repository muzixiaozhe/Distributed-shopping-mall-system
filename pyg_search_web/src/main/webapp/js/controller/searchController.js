app.controller('searchController',function ($scope,$location,searchService) {

    //搜索对象
    $scope.searchMap={'keywords':'','brand':'','category':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sort':'','sortField':''}
    //排序
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();//重新查询
    }
    //加载查询字符串
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords= $location.search()['keywords'];
        $scope.search();//重新查询
    }
    //判断关键字是不是品牌
   $scope.keywordsIsBrand=function () {
       for(var i=0;i<$scope.resultMap.brandList.length;i++){
           if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
               return true;
           }
       }
       return false;
   }
    //添加搜索项
    $scope.addSearchIte=function (key,value) {
        if (key=="brand"||key=='category'||key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();//重新查询
    }
    //移除复合搜索条件
    $scope.removeSearchIte=function (key) {
        if (key=="brand"||key=='category'||key=='price'){
            $scope.searchMap[key]='';
        }else{
           delete $scope.searchMap.spec[key];
        }
        $scope.search();//重新查询
    }
    //搜索
    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                buildPageLabel();
            }
        );
    }
    buildPageLabel=function () {
        $scope.pageLabel=[];//新增分页栏属性
        var maxPageNo=$scope.resultMap.totalPages;//最大页数
        var firstPage=1;//开始页数
        var lastPage=maxPageNo;//截止页数
        if ($scope.resultMap.totalPages>5){
            if ($scope.searchMap.pageNo<=3){
                lastPage=5;
            }else if ($scope.searchMap.pageNo>=maxPageNo-2){
                firstPage=maxPageNo-4;
            }else{//显示当前页为中心的5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }

        }
        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }


    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        //页码验证
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }
})