package com.cxj.service;


import com.cxj.dao.OrderDao;
import com.cxj.domain.Order;
import com.cxj.utils.JDBCUtils;

import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private OrderDao orderDao = new OrderDao();
    //添加订单
    public void addOrder(Order order){
        try{
            //开启事务
            JDBCUtils.beginTranscation();
            orderDao.addOrder(order);//添加订单
            orderDao.addOrderItem(order.getOrderItemList());//添加订单所属订单条目列表
            //提交事务
            JDBCUtils.commitTranscation();
        }catch (Exception e){
            //出现异常，回滚事务
            try {
                JDBCUtils.rollbackTranscation();
            } catch (SQLException ex) {
                throw new RuntimeException();
            }
        }
    }

    public List<Order> findAll(int vipid) {
        return orderDao.findAll(vipid);
    }

    public Order findById(String orderid, int vipid) {
        return orderDao.findById(orderid,vipid);
    }
}
