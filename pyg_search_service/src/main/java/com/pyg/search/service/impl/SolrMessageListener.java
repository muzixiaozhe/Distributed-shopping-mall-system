package com.pyg.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class SolrMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        try {
            TextMessage textMessage= (TextMessage) message;
            String text = textMessage.getText();
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            //添加solr索引
            for (TbItem tbItem : tbItems) {
                Map map = JSON.parseObject(tbItem.getSpec(), Map.class);//将scop的JSON字段转换成map
                tbItem.setSpecMap(map);//给带注解的字段赋值
            }
            itemSearchService.importList(tbItems);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
