package com.cxj.domain;

//订单项实体类
public class OrderItem {
    private int id;//订单条目编号
    private int buycount;//所购买此商品的数量
    private float total;//所购买此商品总价
    private Product product;//所购买商品
    private Order order;//所属订单

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", buycount=" + buycount +
                ", total=" + total +
                ", product=" + product +
                ", orderid=" + order.getOrderid() +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuycount() {
        return buycount;
    }

    public void setBuycount(int buycount) {
        this.buycount = buycount;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
