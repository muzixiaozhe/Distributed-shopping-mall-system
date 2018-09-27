//控制层
app.controller('seckillController' ,function($scope, $interval,$location, seckillService){
    //获取验证码
    $scope.createNative=function () {
        seckillService.createNative().success(
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
                $scope.queryPayStatus();
            }
        );
    }
    //查询支付状态
    $scope.queryPayStatus=function () {
        seckillService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if (response.success){
                    location.href="paysuccess.html#?money="+$scope.total_fee;
                }else{
                    if(response.message=="TIME_OUT"){
                        $scope.createNative();
                    }else{
                        location.href="payTimeOut.html";
                    }
                }
            }
        );
    }
    //获取支付金额
    $scope.getMoney=function () {
        return $location.search()['money'];
    }

    //查询所有秒杀商品
    $scope.findList=function () {
        seckillService.findList().success(
            function (response) {
                $scope.list=response;
            }
        );
    }
    //提交订单
    $scope.submitOrder=function () {
        seckillService.submitOrder($scope.entity.id).success(
            function (response) {
                if(response.success){
                    location.href="pay.html"
                }else{
                    alert(response.message)
                }
            }
        );
    }

    //根据id查询实体
    $scope.findOneFormRedis=function () {

        seckillService.findOneFormRedis($location.search()['id']).success(
            function (response) {
                $scope.entity=response;
                allsecond=Math.floor((new Date($scope.entity.endTime).getTime()-(new Date().getTime()))/1000)//总秒数
                var time=$interval(function () {
                    if(allsecond>0){
                        allsecond-=1;
                        $scope.atime=convertTimeString(allsecond);//转换时间字符串
                    }else{
                        $interval.cancel(time);
                        alert("秒杀服务结束")
                    }
                },1000)
            }
        );
    }
    //转换时间字符串
    convertTimeString=function (allsecond) {
        var days= Math.floor( allsecond/(60*60*24));//天数
        var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小时数
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        var timeString="";
        if(days>0){
            timeString=days+"天 ";
        }
        return timeString+hours+":"+minutes+":"+seconds;
    }
})