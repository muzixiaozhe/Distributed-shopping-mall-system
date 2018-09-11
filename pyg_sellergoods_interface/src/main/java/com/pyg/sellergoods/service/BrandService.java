package com.pyg.sellergoods.service;

import com.pyg.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有品牌
     * @return
     */
    public List<TbBrand> findAll();
    /**
     * 分页
     */
    public PageResult findPage(int pageNum,int pageSize);
    /**
     * 添加
     */
    public void add(TbBrand tbBrand);
    /**
     * 修改
     */
    public void update(TbBrand tbBrand);
    /**
     * 根据id获取单个品牌信息
     */
    public TbBrand findOne(Long id);
    /**
     * 批量删除
     */
    public void delete(Long[] ids);
    /**
     * 查询分页
     */
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    List<Map> selectOptionList();

}
