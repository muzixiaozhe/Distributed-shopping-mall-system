package com.pyg.page.service;

import java.io.IOException;

/**
 * 商品详细接口
 */
public interface ItemPageService {
    /**
     * 生成商品详情页
     */
    public void genHtml(Long goodsId) throws IOException, Exception;
    /**
     * 删除静态页
     */
    public void deleteHtml(Long goodsId) throws  Exception;
}
