package entity;

import com.pyg.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车实体类
 */
public class Cart implements Serializable{
    private String sellerId;//商家id
    private String sellerName;//商家名称
    private List<TbOrderItem> orderItem;//购物车明细


    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<TbOrderItem> orderItem) {
        this.orderItem = orderItem;
    }
}
