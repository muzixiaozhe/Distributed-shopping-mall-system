package com.pyg.sellergoods.service;

import com.pyg.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {

    /**
     * 添加
     */
    public void add(TbBrand tbBrand);
    /**
     * 修改
     */
    public void update(TbBrand tbBrand);
    /**
     * 删除
     */
    public void delete(Long[] ids);
    /**
     * 根据id查询品牌信息
     */
    public TbBrand findOne(Long id);
    /**
     * 搜索分页
     */
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
}
