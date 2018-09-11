app.service('contentService',function ($http) {
    //根据广告类型Id查询列表
    this.findByCategoryId=function (categoryId) {
        return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
    }
})