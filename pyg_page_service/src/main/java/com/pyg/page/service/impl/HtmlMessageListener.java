package com.pyg.page.service.impl;

import com.pyg.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
@Component
public class HtmlMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage= (ObjectMessage) message;
            Long goodsId = (Long) objectMessage.getObject();
            itemPageService.genHtml(goodsId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
