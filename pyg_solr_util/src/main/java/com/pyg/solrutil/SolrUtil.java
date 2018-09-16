package com.pyg.solrutil;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;
    /**
     * 导入商品数据
     */
    public void importItemData(){
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);//将scop的JSON字段转换成map
            tbItem.setSpecMap(map);//给带注解的字段赋值
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }
    public void delete(){
        SolrDataQuery query=new SimpleQuery("*:*");

        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) classPathXmlApplicationContext.getBean("solrUtil");
        /*solrUtil.importItemData();*/
        solrUtil.delete();

    }
}
