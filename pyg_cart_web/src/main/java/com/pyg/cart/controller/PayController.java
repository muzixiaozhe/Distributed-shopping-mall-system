package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.OrderService;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.pojo.TbPayLog;
import com.pyg.util.IdWorker;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private OrderService orderService;

    /**
     * 获取验证码
     *
     * @return
     */
    @RequestMapping("createNative")
    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        TbPayLog payLog = orderService.searchPayLogFromRedis(name);
        if (payLog==null){
            return new HashMap();
        }else{
            return weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        }


    }

    /**
     * 查询支付状态
     */
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        System.out.println(out_trade_no);
        int num=0;
        while (true) {
            //查询结果
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "订单不存在");
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))) {
                result = new Result(true, "支付成功");
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));//修改订单状态
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            num++;
            if (num>=300){//超时
                result = new Result(false, "TIME_OUT");
                break;
            }
        }
        return result;
    }
}
