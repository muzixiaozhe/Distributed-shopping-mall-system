 //控制层 
app.controller('cartController' ,function($scope,  cartService){
    $scope.selectIds = [];
    //勾选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);//添加
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1)//删除
        }
    }
    //新增收货地址
    $scope.add=function () {
        cartService.add($scope.entity).success(
            function (response) {
                if(response.success){
                    //location.href="getOrderInfo.html";//刷新地址信息
                    $scope.findAddressList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //查询 回显
    $scope.findOne=function (id) {
        cartService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }
    //确定
    $scope.save=function () {
        if($scope.entity.id==null){
            $scope.add()
        }else{
            $scope.update();
        }
    }
    //修改
    $scope.update=function () {
        cartService.update($scope.entity).success(
            function (response) {
                if(response.success){
                    //location.href="getOrderInfo.html";//刷新地址信息
                    $scope.findAddressList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //删除
    $scope.dele=function (id) {
        cartService.dele(id).success(
            function (response) {
                if(response.success){
                    //location.href="getOrderInfo.html";//刷新地址信息
                    $scope.findAddressList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //查询用户录入的地址信息
    $scope.findAddressList=function () {
        cartService.findAddressByUserId().success(
            function (response) {
                $scope.addressList=response;
                //将默认的地址选中
                for(var i=0;i< $scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i]
                        return
                    }
                }
            }
        );
    }

    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }
    //获取选中的地址
    $scope.selectAddress=function (address) {
        $scope.address=address
    }
	//判断地址是否选中
    $scope.isSelectAddress=function (address) {
        if ($scope.address==address){
            return true
        }
        return false;
    }
	//获取购物车信息
	$scope.findCartList=function(){
		cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
                $scope.totalValue=sum($scope.cartList);
			}			
		);
	}
	//鼠标悬停选择
    $scope.sA=function (value) {
        $scope.oo=value;
    }
    $scope.iA=function (value) {
        if($scope.oo==value){
            return true
        }else{
            return false
        }
    }

	//保存订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address//地址
        $scope.order.receiverMobile=$scope.address.mobile//手机
        $scope.order.receiver=$scope.address.contact//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success){
                    if ($scope.order.paymentType=='1'){//如果是微信支付就跳转到支付页
                        location.href="pay.html";
                    }else{//如果是货到付款就跳转到成功页
                        location.href="paysuccess.html";

                    }
                }else{
                    alert(response.message)
                }
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
