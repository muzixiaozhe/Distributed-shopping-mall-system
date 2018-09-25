package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import entity.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    private TbItemMapper tbItemMapper;

    /**
     * 向购物车中添加数据
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //通过itemid查询商品信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        //通过商品id查看购物车中是否有商家
        Cart cart=searchCartBySellerId(cartList,tbItem.getSellerId());
        //如果购物车列表中不存在该商家的购物车
        if (cart==null){
            //新建商家购物车
            cart=new Cart();
            cart.setSellerId(tbItem.getSellerId());
            cart.setSellerName(tbItem.getSeller());
            //添加商品明细
            TbOrderItem orderItem = createOrderItem(tbItem,num);
            List orderItemList=new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItem(orderItemList);
            //添加到购物车
            cartList.add(cart);
        }else{
            //判断购物车中是否有该商品明细
            TbOrderItem orderItem=searchOrderItemByItemId(cart.getOrderItem(),itemId);
            if (orderItem==null){
                //如果没有 新增购物车明细
                orderItem = createOrderItem(tbItem, num);
                cart.getOrderItem().add(orderItem);
            }else {
                //如果有 在原来的基础上修改数量和小计
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setPrice(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                //如果商品明细数量小于0,则移除
                if(orderItem.getNum()<=0)
                    cart.getOrderItem().remove(orderItem);
                //如果购物车明细为0,则移除cart
                if (cart.getOrderItem().size()==0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }



    /**
     * 判断购物车中是否有该商品明细
     * @param orderItem
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItem, Long itemId) {
        for (TbOrderItem tbOrderItem : orderItem) {
            if (tbOrderItem.getItemId().equals(itemId)){
                return tbOrderItem;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细
     * @param tbItem
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {
        TbOrderItem tbOrderItem=new TbOrderItem();

        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));
        return tbOrderItem;
    }

    /**
     * 通过商品id查看购物车中是否有商家
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
               return cart;
            }
        }
        return null;
    }
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     *从redis中查询购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        Object cartList = redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            return new ArrayList<>();

        }
        return (List<Cart>) cartList;
    }

    /**
     *将购物车保存到redis
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            for (TbOrderItem tbOrderItem : cart.getOrderItem()) {
                addGoodsToCartList(cartList2,tbOrderItem.getItemId(),tbOrderItem.getNum());
            }
        }
        return cartList2;
    }
}
