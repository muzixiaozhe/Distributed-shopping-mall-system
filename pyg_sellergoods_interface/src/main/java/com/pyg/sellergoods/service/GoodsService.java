package com.pyg.sellergoods.service;
import java.util.List;
import com.pyg.pojo.TbGoods;

import com.pyg.pojo.TbItem;
import entity.Goods;
import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {
	/**
	 * 根据商品id和状态查询item表信息
	 * @param ids
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids,String status);
	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 批量修改状态
	 * @param ids
	 */
	public void updateStatus(Long[] ids,String status);

	/**
	 * 修改上下架状态
	 * @param id
	 * @param marketable
	 */
	public void updateMarketable(Long id,String marketable);
}
