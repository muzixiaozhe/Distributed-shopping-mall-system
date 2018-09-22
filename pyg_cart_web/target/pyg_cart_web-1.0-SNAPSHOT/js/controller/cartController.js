 //控制层 
app.controller('cartController' ,function($scope,  cartService){

	
	//获取购物车信息
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
			}			
		);
	}
    $scope.totalValue={totalNum:0,totalMoney:0.00}
	//求合计数
	sum=function () {
        for(var i=0;i<$scope.cartList.length;i++){
        	for (var j=0;j<$scope.cartList.orderItem.length;j++){
                $scope.totalValue.totalNum+=$scope.cartList.orderItem[j].num;
                $scope.totalValue.totalMoney+=$scope.cartList.orderItem[j].price;
			}
		}
    }
	//添加商品到购物车
	$scope.addGoodsToCartList=function (itemId,orderNum,num) {
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
    //添加判断
    $scope.addGTC=function (itemId,orderNum,num) {
		if (orderNum==1){
			return;
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
