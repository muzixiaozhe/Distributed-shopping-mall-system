package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {


        Map<String, Object> resultMap=new HashMap<>();
       /* //添加查询条件
        Query query=new SimpleFacetQuery();
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        resultMap.put("rows",page.getContent());*/
        //根据关键字搜索列表

            String keywords = ((String) searchMap.get("keywords")).replace(" ","");
        if (keywords.length()>0){
            searchMap.put("keywords",keywords);
            resultMap.putAll(searchList(searchMap));
            List categoryList = searchCategoryList(searchMap);
            //根据关键字查询商品分类
            resultMap.put("categoryList",categoryList);
            //查询规格和品牌
            String categoryName = (String) searchMap.get("category");
            if (!"".equals(categoryName)){//如果有分类名称
                resultMap.putAll(searchBrandAndSpecList(categoryName));
            }else{
                if (categoryList.size()>0){
                    Map map = searchBrandAndSpecList((String) categoryList.get(0));
                    resultMap.putAll(map);
                }
            }
        }
        return resultMap;
    }
    /**
     * 向solr中添加数据
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }
    /**
     * 删除solr中的数据
     */
    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
    }

    /**
     * 查询规格和品牌
     * @param category
     * @return
     */
    public Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        //获取模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId!=null){
            //通过模板id查询品牌
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            //通过模板id查询规格
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }
    /**
     * 查询分类列表
     * @param searchMap
     * @return
     */
    public List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList<>();
        Query query=new SimpleQuery();
        //使用关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupoptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupoptions);
        //得到分组页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //通过列得到分组结果集
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            //将分组结果的名称封装到返回值里
            String groupValue = tbItemGroupEntry.getGroupValue();
            list.add(groupValue);
        }
        return list;
    }
    /**
     * 根据关键字搜索列表
     * @param searchMap
     * @return
     */
    public Map searchList(Map searchMap){
        Map map=new HashMap();
        HighlightQuery query=new SimpleHighlightQuery();
        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//设置前缀
        highlightOptions.setSimplePostfix("</em>");//后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //按分类筛选
        if (!"".equals(searchMap.get("category"))){
            Criteria criteria3=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(criteria3);
            query.addFilterQuery(filterQuery);
        }
        //按品牌筛选
        if (!"".equals(searchMap.get("brand"))){
            Criteria criteria2=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(criteria2);
            query.addFilterQuery(filterQuery);
        }
        //按规格筛选
        if (!"".equals(searchMap.get("spec"))){
            Map<String,String> spec = (Map) searchMap.get("spec");
            for (String key : spec.keySet()) {
                Criteria criteria4=new Criteria("item_spec_"+key).is(spec.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(criteria4);
                query.addFilterQuery(filterQuery);
            }
        }
        //按价格筛选
        if (!"".equals(searchMap.get("price"))){
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if (split.length>1){
                if (!"0".equals(split[0])){
                    Criteria criteria5=new Criteria("item_price").greaterThanEqual(split[0]);
                    FilterQuery filterQuery=new SimpleFilterQuery(criteria5);
                    query.addFilterQuery(filterQuery);
                }
                if (!"*".equals(split[1])){
                    Criteria criteria6=new Criteria("item_price").lessThanEqual(split[1]);
                    FilterQuery filterQuery=new SimpleFilterQuery(criteria6);
                    query.addFilterQuery(filterQuery);
                }
            }
        }
        //排序
        String sort = (String) searchMap.get("sort");//排序方式
        String sortField = (String) searchMap.get("sortField");//排序字段
       if (sort!=null&&"".equals(sort)){
           if ("ASC".equals(sort)){
               Sort solr=new Sort(Sort.Direction.ASC,"item_"+sortField);
               query.addSort(solr);
           }
           if ("DESC".equals(sort)){
               Sort solr=new Sort(Sort.Direction.DESC,"item_"+sortField);
               query.addSort(solr);
           }
       }
        //获取当前页码和每页显示个数
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        query.setOffset((pageNo-1)*pageSize);//设置查询的起始值
        query.setRows(pageSize);//设置每页显示个数
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> h : highlighted) {//循环高亮入口集合
            TbItem entity = h.getEntity();
            if (h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0){
                entity.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }

        }
        map.put("rows",page.getContent());//返回查询结果
        map.put("totalPages",page.getTotalPages());//返回总页数
        map.put("total",page.getTotalElements());//返回总记录数

        return map;
    }

}
