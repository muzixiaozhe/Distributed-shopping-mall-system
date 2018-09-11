app.service('searchService', function ($http) {
    //根据广告类型Id查询列表
    this.search = function (searchMap) {
        return $http.post('../itemsearch/search.do',searchMap);
    }
})