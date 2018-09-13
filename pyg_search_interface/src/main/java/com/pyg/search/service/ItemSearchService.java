package com.pyg.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 向solr中添加数据
     * @param list
     */
    public void importList(List list);
    /**
     * 删除solr中的数据
     */
    public void deleteByGoodsIds(List list);
}
