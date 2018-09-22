 //控制层 
app.controller('cartController' ,function($scope,  cartService){

	
	//获取购物车信息
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
                $scope.totalValue=sum($scope.cartList);
			}			
		);
	}
	//获取用户名
    $scope.findName=function () {
        cartService.findName().success(
            function (response) {
                $scope.username=response.success;
                $scope.name=response.message;

            }
        );
    }
	//求合计数
	sum=function (cartList) {
        var totalValue={totalNum:0,totalMoney:0.00};
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cart.orderItem.length;j++){
                var orderItem=cart.orderItem[j];//购物车明细
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+= orderItem.totalFee;
            }
        }
		return totalValue;
    }
	//添加商品到购物车
	$scope.addGoodsToCartList=function (itemId,num) {
		cartService.addGoodsToCartList(itemId,num).success(
			function (response) {
				if (response.success){
                    $scope.findCartList()//重新加载

				}else{
					alert(response.message)
				}
            }	
		);
    }
//添加商品到购物车判断
    $scope.addGTC=function (itemId,numValue,num) {
		if (numValue==1){
			return
		}
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if (response.success){
                    $scope.findCartList()//重新加载

                }else{
                    alert(response.message)
                }
            }
        );
    }
    
});	
