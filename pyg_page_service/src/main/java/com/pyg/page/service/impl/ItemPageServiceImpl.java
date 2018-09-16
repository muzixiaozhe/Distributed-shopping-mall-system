package com.pyg.page.service.impl;

import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.page.service.ItemPageService;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService{
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    /**
     * 生成商品详情页
     */
    @Override
    public void genHtml(Long goodsId) throws Exception {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        Map dataMap=new HashMap();
        Template template = configuration.getTemplate("item.ftl");
        FileWriter out = new FileWriter(pagedir+goodsId+".html");
        //加载商品表数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        dataMap.put("goods",tbGoods);
        //加载商品详情表数据
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        dataMap.put("goodsDesc",tbGoodsDesc);
        //面包屑
        String name1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
        String name2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
        String name3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
        dataMap.put("categoryName1",name1);
        dataMap.put("categoryName2",name2);
        dataMap.put("categoryName3",name3);
        //加载item表数据
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andGoodsIdEqualTo(goodsId);
        example.setOrderByClause("is_default desc");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        dataMap.put("itemList",tbItems);

        template.process(dataMap,out);
        out.close();
    }

    /**
     * 删除静态页
     * @param goodsId
     * @throws Exception
     */
    @Override
    public void deleteHtml(Long goodsId) throws Exception {
        String path=pagedir+goodsId+".html";
        new File(path).delete();

    }
}
