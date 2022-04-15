package com.cxj.servlet;


import com.cxj.domain.Address;
import com.cxj.domain.User;
import com.cxj.service.AddressService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/addressServlet")
public class AddressServlet extends HttpServlet {
    private AddressService addressService = new AddressService();


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            add(request, response);
        } else if ("paybefore".equals(action)) {
            paybefore(request, response);
        }else if ("delete".equals(action)) {
            delete(request, response);
        }
    }



    private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            /*
             1.获取addressid
             2.调用Service层中的deleteById方法进行删除
             3.先转向msg.jsp，再由msg.jsp转向addressServlet（防止刷新重复提交）
         */
        String addressid = request.getParameter("id");
        addressService.deleteById(Integer.parseInt(addressid));
        request.setAttribute("msg","<script>ale rt('删除成功！');window.location.href='"+request.getContextPath()+"/addressServlet?action=paybefore';</script>");
        request.getRequestDispatcher("/home/msg.jsp").forward(request,response);
    }


    private void paybefore (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /*
        1.判断用户是否登录
        2.没有登录，转向登录页面
        3.登录，成功从session中取出User对象
        4.通过userid查找该用户的所有记录，调用service中的findAll（int userid）
        5.重定向到pay.jsp页面
         */

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user==null){
            response.sendRedirect(request.getContextPath()+"/home/login.jsp");
            return;
        }
        int vipid = user.getVipid();
        request.setAttribute("addresslist",addressService.findAll(vipid));
        request.getRequestDispatcher("/person/pay.jsp").forward(request,response);
    }

    protected void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        0.判断用户是否登录
        1.首先取出参数
        2.验证
        3.封装到Address对象
        4.调用Service的add方法添加记录
        5.返回到pay.jsp页面(显示新的地址)，防止刷新重复提交
         */
        HttpSession session = request.getSession();
        User user=(User) session.getAttribute("user");
        if (user==null){
            response.sendRedirect(request.getContextPath()+"/home/login.jsp");
            return;
        }
        String receiver = request.getParameter("receiver");
        String phone = request.getParameter("phone");
        String postcode = request.getParameter("postcode");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String area = request.getParameter("area");
        String addressname = request.getParameter("addressname");
        int vipid = user.getVipid();
        //3.封装到Address对象
        Address address = new Address();
        address.setAddressname(addressname);
        address.setProvince(province);
        address.setCity(city);
        address.setArea(area);
        address.setPhone(phone);
        address.setPostcode(postcode);
        address.setReceiver(receiver);
        address.setVipid(vipid);

        addressService.add(address);

        request.setAttribute("msg","<script>alert('添加成功!');window.location.href='"+request.getContextPath()+"/addressServlet?action=paybefore';</script>");
        request.getRequestDispatcher("/home/msg.jsp").forward(request,response);
    }
}



