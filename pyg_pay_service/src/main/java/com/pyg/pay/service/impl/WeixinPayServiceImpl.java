package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.util.HttpClient;
import com.pyg.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Autowired
    private IdWorker idWorker;
    @Value("${appid}")
    private String appid;//公众账号id
    @Value("${partner}")
    private String partner;//商户号
    @Value("${partnerkey}")
    private String partnerkey;//密钥
    @Value("${notifyurl}")
    private String notifyurl;//回调地址
    /**
     * 生成微信二维码
     * @param out_trade_no 订单号
     * @param total_fee 总金额
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        System.out.println(out_trade_no);
        System.out.println(total_fee);
        Map<String,String> param=new HashMap<>();
        param.put("appid",appid);//公众账号id
        param.put("mch_id",partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body","品优购");//商品描述
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("total_fee",total_fee);//标价金额
        param.put("spbill_create_ip","127.0.0.1");//终端ip
        param.put("notify_url",notifyurl);      //回调地址
        param.put("trade_type","NATIVE");//交易类型
        try {
            //生成要发送的xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //返回结果
            String result = httpClient.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map=new HashMap<>();
            System.out.println(xmlToMap.get("code_url"));
            map.put("code_url",xmlToMap.get("code_url"));//支付地址
            map.put("out_trade_no",out_trade_no);//订单号
            map.put("total_fee",total_fee);//总金额
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map<String,String> param=new HashMap<>();
        param.put("appid",appid);//公众账号id
        param.put("mch_id",partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("out_trade_no",out_trade_no);//商户订单号
        try {
            //生成要发送的xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //返回结果
            String result = httpClient.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(result);
            return xmlToMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *关闭支付
     * @param out_trade_no
     * @return
     */
    @Override
    public Map closePay(String out_trade_no) {
        Map<String,String> param=new HashMap<>();
        param.put("appid",appid);//公众账号id
        param.put("mch_id",partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("out_trade_no",out_trade_no);//商户订单号
        try {
            //生成要发送的xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //返回结果
            String result = httpClient.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(result);
            return xmlToMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            ReentrantLock
        }
    }
}
