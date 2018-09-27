package com.pyg.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeixinPayService {
    /**
     * 生成微信支付二维码
     * out_trade_no 支付订单号
     *total_fee 支付金额
     */
    public Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询订单效果
     * @param out_trade_no
     * @return
     */
    Map queryPayStatus(String out_trade_no);
    /**
     * 关闭支付
     */
    Map closePay(String out_trade_no);
}
