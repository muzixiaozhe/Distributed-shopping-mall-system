 //控制层 
app.controller('payController' ,function($scope, $location, payService){

	//获取验证码
	$scope.createNative=function () {
        payService.createNative().success(
			function (response) {
               $scope.total_fee= (response.total_fee/100).toFixed(2);//总金额
               $scope.out_trade_no=response.out_trade_no;//订单号
                //二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
            }	
		);
    }
    //查询支付状态
    $scope.queryPayStatus=function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if (response.success){
                    location.href="paysuccess.html#?monet="+$scope.total_fee;
                }else{
                    if(response.message=="TIME_OUT"){
                        $scope.createNative();
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        );
    }
    //获取支付金额
    $scope.getMoney=function () {
       return $location.search()['money'];
    }
    
});	
