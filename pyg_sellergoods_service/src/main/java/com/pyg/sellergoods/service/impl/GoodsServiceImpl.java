package com.pyg.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.*;
import com.pyg.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.pojo.TbGoodsExample.Criteria;
import com.pyg.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    /**
     * 根据商品id和状态查询item表信息
     * @param ids
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //新增基本信息表数据
        goods.getGoods().setAuditStatus("0");//设置未审核状态
        goodsMapper.insert(goods.getGoods());
        //新增扩展信息表数据
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置id
        goodsDescMapper.insert(goods.getGoodsDesc());
        //新增商品详情表数据
        saveItemList(goods);

    }
    private void  saveItemList(Goods goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem tbItem : goods.getItemList()) {
                //标题
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec());
                for (String s : map.keySet()) {
                    title += " " + map.get(s);
                }
                tbItem.setTitle(title);
                setItemValues(goods, tbItem);
                itemMapper.insert(tbItem);
            }
        }else {
            TbItem tbItem=new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            tbItem.setPrice( goods.getGoods().getPrice() );//价格
            tbItem.setStatus("1");//状态
            tbItem.setIsDefault("1");//是否默认
            tbItem.setNum(99999);//库存数量
            tbItem.setSpec("{}");
            setItemValues(goods,tbItem);
            itemMapper.insert(tbItem);
        }

    }
    private void setItemValues(Goods goods, TbItem tbItem) {
        //商品SPU编号
        tbItem.setGoodsId(goods.getGoods().getId());
        //商家编号
        tbItem.setSellerId(goods.getGoods().getSellerId());
        //商品分类编号（3级）
        tbItem.setCategoryid(goods.getGoods().getCategory3Id());
        //创建日期 修改日期
        tbItem.setCreateTime(new Date());
        tbItem.setUpdateTime(new Date());
        //品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
        //分类名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        tbItem.setCategory(tbItemCat.getName());
        //商家名称
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSeller(tbSeller.getNickName());
        //图片地址
        List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (maps.size() > 0) {
            tbItem.setImage((String) maps.get(0).get("url"));
        }

    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        goods.getGoods().setAuditStatus("0");//设置未审核状态
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //删除原有的sku列表数据
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的sku列表数据
        saveItemList(goods);

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods=new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //goodsMapper.deleteByPrimaryKey(id);
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }
        criteria.andIsDeleteIsNull();//非删除状态
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量修改状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            tbGoods.setIsMarketable("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 修改上下架状态
     * @param id
     * @param marketable
     */
    @Override
    public void updateMarketable(Long id, String marketable) {

            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            if ("1".equals(marketable)){
                tbGoods.setIsMarketable("0");
            }
            if ("0".equals(marketable)){
                tbGoods.setIsMarketable("1");
            }
            goodsMapper.updateByPrimaryKey(tbGoods);

    }

}
