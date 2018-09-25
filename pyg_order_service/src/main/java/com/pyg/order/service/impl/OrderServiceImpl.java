package com.pyg.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.pyg.mapper.TbOrderItemMapper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojo.TbPayLog;
import com.pyg.util.IdWorker;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderExample;
import com.pyg.pojo.TbOrderExample.Criteria;
import com.pyg.order.service.OrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbPayLogMapper payLogMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//得到购物车数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		long orderId=idWorker.nextId();
		double total_fee=0.00;//父订单价格
		List<String> idList=new ArrayList();//订单列表
		for (Cart cart : cartList) {
			TbOrder tbOrder=new TbOrder();//创建订单
			tbOrder.setUserId(order.getUserId());//用户id
			tbOrder.setOrderId(orderId);//订单id
			tbOrder.setSellerId(order.getSellerId());//商家id
			tbOrder.setStatus("1");//状态
			tbOrder.setCreateTime(new Date());//订单创建时间
			tbOrder.setUpdateTime(new Date());//订单更新时间
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
			tbOrder.setReceiver(order.getReceiver());//收货人
			tbOrder.setReceiverMobile(order.getReceiverMobile());//手机
			tbOrder.setPaymentType(order.getPaymentType());//支付类型
			idList.add(orderId+"");
			double money=0;
			for (TbOrderItem orderItem : cart.getOrderItem()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);//订单id
				orderItem.setSellerId(order.getSellerId());
				money+=orderItem.getTotalFee().doubleValue();//计算总金额
				orderItemMapper.insert(orderItem);
				total_fee+=money;
			}
			tbOrder.setPayment(new BigDecimal(money));//实付金额
			orderMapper.insert(tbOrder);
		}
		//如果是微信支付
		if ("1".equals(order.getPaymentType())){
			TbPayLog payLog=new TbPayLog();
			payLog.setOutTradeNo(idWorker.nextId()+"");//订单号
			payLog.setCreateTime(new Date());//创建日期
			payLog.setTotalFee((long)(total_fee*100));//支付金额
			payLog.setUserId(order.getUserId());//用户id
			payLog.setTradeState("0");//交易状态
			payLog.setPayType("1");//支付类型
			String ids=idList.toString().replace("[","").replace("]","").replace(" ","");
			payLog.setOrderList(ids);//子订单列表
			payLogMapper.insert(payLog);//添加到数据库
			//放到redis缓存中
			redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
		}
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据用户查询payLog
	 * @param username
	 * @return
	 */
	@Override
	public TbPayLog searchPayLogFromRedis(String username) {
		Object payLog = redisTemplate.boundHashOps("payLog").get(username);
		System.out.println(payLog==null);
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(username);
	}

	/**
	 * 修改订单状态
	 * @param out_trade_no 订单号
	 * @param transaction_id 微信返回的流水号
	 */
	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setPayTime(new Date());//订单完成时间
		payLog.setTradeState("1");//已支付
		payLog.setTransactionId(transaction_id);//交易号码
		payLogMapper.updateByPrimaryKey(payLog);
		String[] split = payLog.getOrderList().split(",");
		for (String orderId : split) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			if (tbOrder!=null){
				tbOrder.setStatus("2");//已支付
				orderMapper.updateByPrimaryKey(tbOrder);
			}

		}
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());//删除redis中的数据
	}

}
