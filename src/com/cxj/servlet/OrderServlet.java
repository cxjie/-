package com.cxj.servlet;


import com.cxj.domain.*;
import com.cxj.service.AddressService;
import com.cxj.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet("/orderServlet")
public class OrderServlet extends HttpServlet {
    private AddressService addressService = new AddressService();
    private OrderService orderService=new OrderService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            add(request,response);
        }else if ("findAll".equals(action)) {
            findAll(request,response);
        }else if ("findById".equals(action)) {
            findById(request,response);
        }
    }

    private void findById(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        0.判断用户是否登录，否，跳转到login.jsp
        1.获取订单id
        2.调用Service层的findById方法来查询订单
        3.把读出来的Order对象存入到request中
        4.转向/person/orderinfo.jsp页面
         */
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user==null){
            request.getRequestDispatcher(request.getContextPath()+"/home/login.jsp");
            return;
        }
        String orderid =request.getParameter("id");
        int vipid = user.getVipid();

        Order order = orderService.findById(orderid,vipid);
        request.setAttribute("order",order);
        request.getRequestDispatcher("/person/orderinfo.jsp").forward(request,response);
    }


    private void findAll(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /*
        0.判断用户是否登录，否，跳转到login.jsp
        1.获取用户的vipid
        2.调用Service层的findAll方法来查询订单
        3.把订单列表存入到request中
        4.转向order.jsp页面
         */
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user==null){
            response.sendRedirect(request.getContextPath()+"/home/login.jsp");
            return;
        }
        int vipid = user.getVipid();
        List<Order> orderList = orderService.findAll(vipid);
        request.setAttribute("orderlist",orderList);
        request.getRequestDispatcher("/person/order.jsp").forward(request,response);
    }

    private void add(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        /*
        0.判断用户是否登录，否，跳转到login.jsp
        1.从表里取出收货地址id和备注
        2.创建Order
        3.调用Service层来添加订单
        4.清空购物车
        5.转向paying.jsp页面（刷新重复提交的问题）
         */
        HttpSession session = request.getSession();
        //0.判断用户是否登录，否，跳转到login.jsp
        User user =(User) session.getAttribute("user");
          if(user==null){
              response.sendRedirect(request.getContextPath()+"/home/login.jsp");
              return;
          }
//          1.从表里取出收货地址id和备注
        String addressid = request.getParameter("id");
        String content = request.getParameter("content");


//          2.创建Order
        Order order = new Order();
        String orderid = UUID.randomUUID().toString().replace("-","");
        order.setOrderid(orderid);//设置订单编号
        order.setOrdertime(new Date());//设置下订单时间
        order.setStatus(0);//设置订单状态是未付款
        order.setTotalprice((Float) session.getAttribute("totalprice"));//设置订单总价
        order.setContent(content);//设置订单备注
        order.setUser(user);//设置下单用户


        Address address = addressService.findById(Integer.parseInt(addressid));
        //把省市区合并到addressname中
        StringBuilder addressname = new StringBuilder(address.getProvince());
        String city = address.getCity();
        if("市辖区".equals(city)||"县".equals(city)){
            city = "";
        }
        String area = address.getArea();
        if("市辖区".equals(area)){
            area = "";
        }
        addressname.append(city);
        addressname.append(area);
        address.setAddressname(addressname.toString());
        order.setAddress(address);
        //设置点单条目项
        List<Map<String,Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        List<OrderItem> orderItemList=new ArrayList<OrderItem>();
        //把购物车中每一个商品取出来，生成一个对应的订单条目
        for (int i=0;i<cart.size();i++){
            Map<String,Object> map = cart.get(i);//从购物车中取出来的商品
            //创建订单条目项
            OrderItem orderItem = new OrderItem();
            orderItem.setBuycount((Integer) map.get("buycount"));
            //设置商品Product
            Product product = new Product();
            product.setProductid((Integer) map.get("productid"));
            product.setName((String) map.get("name"));
            product.setPhoto((String) map.get("photo"));
            product.setPrice((Float) map.get("price"));
            orderItem.setProduct(product);

            //设置所属Order
            orderItem.setOrder(order);
            orderItemList.add(orderItem);
        }
        //设置订单条目列表
        order.setOrderItemList(orderItemList);

//          3.调用Service层来添加订单
        orderService.addOrder(order);
        //4.清空购物车
        cart.remove(cart);
        session.removeAttribute("cart");
        session.removeAttribute("totalprice");
//          5.转向paying.jsp页面（刷新重复提交的问题）
        String msg = "<script>window.location.href='"+request.getContextPath()+"/home/index.jsp';</script>";
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/home/msg.jsp").forward(request,response);
    }
}
