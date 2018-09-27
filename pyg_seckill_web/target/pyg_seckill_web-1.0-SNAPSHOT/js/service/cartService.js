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
    //查询用户录入的地址信息
    this.findAddressByUserId=function () {
        return $http.get('../address/findAddressByUserId.do?');
    }
    //保存订单
    this.submitOrder=function (order) {
        return $http.post('../order/add.do',order);
    }
    //添加地址
    this.add=function (address) {
        return $http.post('../address/add.do',address);
    }
    //删除地址
    this.dele=function (id) {
        return $http.get('../address/dele.do?id='+id);
    }
    //修改
    this.update=function (address) {
        return $http.post('../address/update.do',address);
    }
    //查询单个
    this.findOne=function (id) {
        return $http.get('../address/findOne.do?id='+id);
    }
});
