package com.cxj.domain;

import java.util.Date;
import java.util.List;

//订单实体类
public class Order {
    private String orderid;//订单编号
    private Date ordertime;//下单时间
    private int status;//订单状态
    private float totalprice;//订单总价
    private String content;//备注
    private User user;//下单人
    private Address address;//收货地址
    private List<OrderItem> orderItemList;

    @Override
    public String toString() {
        return "Order{" +
                "orderid='" + orderid + '\'' +
                ", ordertime=" + ordertime +
                ", status=" + status +
                ", totalprice=" + totalprice +
                ", content='" + content + '\'' +
                ", user=" + user +
                ", address=" + address +
                ", orderItemList=" + orderItemList +
                '}';
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Date getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(Date ordertime) {
        this.ordertime = ordertime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(float totalprice) {
        this.totalprice = totalprice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
