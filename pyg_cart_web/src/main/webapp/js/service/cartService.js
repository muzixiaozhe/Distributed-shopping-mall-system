//服务层
app.service('cartService',function($http){
	    	

	//获取购物车信息
	this.findCartList=function () {
		return $http.get('../cart/findCartList.do?');
    }
    //获取用户名
    this.findName=function () {
        return $http.get('../cart/findName.do?');
    }
    //添加商品到购物车
	this.addGoodsToCartList=function (itemId,num) {
        return $http.get('../cart/addGoodsToCartList.do?itemId='+itemId+"&num="+num);
    }
});
