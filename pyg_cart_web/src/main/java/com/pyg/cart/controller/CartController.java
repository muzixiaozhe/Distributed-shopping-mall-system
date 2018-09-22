package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService;
import com.pyg.util.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;
    /**
     * 返回用户名
     */
    @RequestMapping("findName")
    public Result findName(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if("anonymousUser".equals(username)){
            return new Result(false,"未登录");
        }else{
            return new Result(true,username);
        }
    }
    /**
     * 查询购物车
     */
    @RequestMapping("findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");//获取cookie中的数据
        if(StringUtils.isEmpty(cartList)){
            cartList="[]";
        }
        List<Cart> carts = JSON.parseArray(cartList, Cart.class);
        if ("anonymousUser".equals(username)){//如果未登录
            return carts;
        }else{//如果已登录 就读取redis中的数据
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
            if (carts.size()>0) {//如果本地存在购物车
                //合并购物车
                cartListFromRedis= cartService.mergeCartList(carts, cartListFromRedis);
                //清除cookie
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username,cartListFromRedis);
            }
            return cartListFromRedis;
        }



    }
    /**
     * 添加商品到购物车
     */
    @RequestMapping("addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Cart> cartList = findCartList();
            //调用service添加商品
            cartList= cartService.addGoodsToCartList(cartList,itemId,num);
            if ("anonymousUser".equals(username)){//如果是未登录 保存到cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
            }else{//如果是已登录 保存到redis
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}
