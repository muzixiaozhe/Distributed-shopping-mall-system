//服务层
app.service('payService',function($http){

    //获取验证码
    this.createNative=function () {
        return $http.get('../createNative.do');
    }
    //查询支付状态
    this.queryPayStatus=function (out_trade_no) {
        return $http.get('../queryPayStatus.do?out_trade_no='+out_trade_no);
    }
});
