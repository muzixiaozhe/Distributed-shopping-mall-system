package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.pojo.TbPayLog;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PayController {
    @Reference
    private SeckillOrderService seckillOrderService;
    @Reference
    private WeixinPayService weixinPayService;

    /**
     * 获取验证码
     *
     * @return
     */
    @RequestMapping("createNative")
    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户从缓存中查询订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchSeckillOrderFromRedis(name);
        if (seckillOrder==null){
            return new HashMap();
        }else{
            return weixinPayService.createNative(seckillOrder.getId()+"",(long)(seckillOrder.getMoney().doubleValue()*100)+"");
        }

    }

    /**
     * 查询支付状态
     */
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(Long out_trade_no) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int num=0;
        while (true) {
            //查询结果
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no+"");
            if (map == null) {
                result = new Result(false, "订单不存在");
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))) {
                result = new Result(true, "支付成功");
                seckillOrderService.saveOrderFromRedisToDb(username,out_trade_no,map.get("transaction_id"));//修改订单状态
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            num++;
            if (num>=10){//超时
                //调用关闭订单接口
                Map payresult = weixinPayService.closePay(out_trade_no + "");
                if( !"SUCCESS".equals(payresult.get("result_code")) ) {//如果返回结果是正常关闭
                    if ("ORDERPAID".equals(payresult.get("err_code"))) {
                        result = new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(username, out_trade_no, map.get("transaction_id"));
                    }
                    if(result.getSuccess()==false){
                        System.out.println("TIME_OUT");
                        //2.调用删除
                        seckillOrderService.deleteOrderFromRedis(out_trade_no,username);
                    }
                }
                break;
            }
        }
        return result;
    }

}
