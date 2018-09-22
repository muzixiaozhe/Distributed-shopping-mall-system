package com.pyg.cart.service;

import entity.Cart;

import java.util.List;

public interface CartService {
    /**
     * 添加商品
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 从redis中查询购物车
     * @return
     */
    List<Cart> findCartListFromRedis(String username);
    /**
     * 将购物车保存到redis
     */
    void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
