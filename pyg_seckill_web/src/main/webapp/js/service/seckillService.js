//服务层
app.service('seckillService',function($http){
    //查询所有秒杀商品
    this.findList=function () {
       return $http.get("../seckillGoods/findList.do");
    }
    //根据id查询实体
    this.findOneFormRedis=function (id) {
        return $http.get("../seckillGoods/findOneFormRedis.do?id="+id);
    }
    //提交订单
    this.submitOrder=function (id) {
        return $http.get("../seckillOrder/submitOrder.do?seckillId="+id);
    }
    //获取验证码
    this.createNative=function () {
        return $http.get('../createNative.do');
    }
    //查询支付状态
    this.queryPayStatus=function (out_trade_no) {
        return $http.get('../queryPayStatus.do?out_trade_no='+out_trade_no);
    }
})