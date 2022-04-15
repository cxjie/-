package com.cxj.servlet;


import com.cxj.domain.Page;
import com.cxj.service.ProductService;
import com.cxj.utils.BaseCalculate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/productServlet")
public class ProductServlet extends HttpServlet {

    private ProductService productService = new ProductService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if("findIndex".equals(action)){
            findIndex(request,response);
        }else if("findAll".equals(action)){
            findAll(request,response);
        }else if("findbyid".equals(action)){
            findbyId(request,response);
        }else if("addcart".equals(action)){
            addCart(request,response);
        }else if("updateBuyCount".equals(action)){
            updateBuyCount(request,response);
        }else if("deletecartMore".equals(action)){
            deletecartMore(request,response);
        }else if("deletecart".equals(action)){
            deletecart(request,response);
        }else if("gotocart".equals(action)){
            gotoCart(request,response);
        }
    }

    private void gotoCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath()+"/home/cart.jsp");
    }
    private void deletecartMore(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        1.获取要删除的id
        2.判断ids是否为空，不为空，可以删除
          2.1获取购物车
          2.2循环购物车，取出每个商品，对比是否是要删除的商品，是：删除，然后更新购物车的总计
        3.返回购物车cart.jsp页面
         */
//        1.获取要删除的id
        String ids[] = request.getParameterValues("sel");
        // 2.判断ids是否为空，不为空，可以删除
        if (ids!=null&&ids.length>0){
            //2.1获取购物车
            HttpSession session = request.getSession();
            List<Map<String,Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
            float totalprice = (float) session.getAttribute("totalprice");
//        2.2循环购物车，取出每个商品，对比是否是要删除的商品，是：删除，然后更新购物车的总计
            for (int j=0;j<ids.length;j++){
                //取出要删除的第j个商品
                for (int i=0;i<cart.size();i++){
                    Map<String,Object> item = cart.get(i);
                    String itemproductid = item.get("productid").toString();
                    if(itemproductid.equals(ids[j])){
                        //找到要删除的商品
                        float total = (Float) item.get("total");
                        totalprice = BaseCalculate.substract(totalprice,total);
                        cart.remove(i);//删除该商品
                    }
                }
            }
            session.setAttribute("cart",cart);
            session.setAttribute("totalprice",totalprice);
        }
//        3.返回购物车cart.jsp页面
        response.sendRedirect(request.getContextPath()+"/home/cart.jsp");
    }

    private void deletecart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        1.获取要删除的id
        2.获取购物车
        3.循环购物车，找到该商品，从购物车中删除，同时从总计中减去该商品的total
        4.保存购物车到session域中
        5.返回cart.jsp页面
         */
//        1.获取要删除的id
        String id = request.getParameter("id");
//        2.获取购物车
        HttpSession session = request.getSession();
        List<Map<String,Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        float totalprice = (float) session.getAttribute("totalprice");
//        3.循环购物车，找到该商品，从购物车中删除，同时从总计中减去该商品的total
        for (int i= 0;i<cart.size();i++){
            Map<String,Object> item = cart.get(i);
            if(item.get("productid").toString().equals(id)){
                //找到要删除的商品
                //在删除之前，将购物车所有商品总价-该商品的总价
                float total = Float.parseFloat(item.get("total").toString());
                totalprice =BaseCalculate.substract(totalprice,total);
                cart.remove(i);
                break;
            }
        }
//        4.保存购物车和totalprice到session域中
        session.setAttribute("cart",cart);
        session.setAttribute("totalprice",totalprice);
//        5.返回cart.jsp页面
        response.sendRedirect(request.getContextPath()+"/home/cart.jsp");
    }
    private void updateBuyCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        1.获取参数id和buycount
        2.获取购物车
        3.循环购物车，找到商品，更新购买数量以及total
           同时在循环过程中，重新计算购物车的总价
        4.把购物车和购物车总计重新保存回session
        5.需要把total和totalprice封装成json的形式返回
         */
//        1.获取参数id和buycount
        String sid = request.getParameter("id");//要修改数量的商品id
        String sbuycount = request.getParameter("buycount");
        String id = sid;
        int buycount = Integer.parseInt(sbuycount);
        if (buycount<1){
            response.sendRedirect(request.getContextPath()+"/home/cart.jsp");
            return;
        }
//        2.获取购物车(先获取session，再取出购物车)
        HttpSession session = request.getSession();
        List<Map<String,Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        float idtotal = 0;//更新后，该商品的总价
        float totalprice = 0;//购物车中所有商品的总价
//        3.循环购物车，找到商品，更新购买数量以及total
        for (int i=0;i<cart.size();i++){
            Map<String,Object> item = cart.get(i);//item表示购物车中的商品
            if (item.get("productid").toString().equals(id)){
                //找到该商品
                item.put("buycount",buycount);
                float price = Float.parseFloat(item.get("price").toString());
                idtotal = BaseCalculate.multiply(price,buycount);
                item.put("total",idtotal);
                totalprice = BaseCalculate.add(totalprice,idtotal);
            }else {
                float total = Float.parseFloat(item.get("total").toString());
                totalprice =BaseCalculate.add(totalprice,total);
            }
        }
//        同时在循环过程中，重新计算购物车的总价
//        4.把购物车和购物车总计重新保存回session
        session.setAttribute("cart",cart);
        session.setAttribute("totaloprice",totalprice);
//        5.需要把total和totalprice封装成json的形式返回
        String jsonstr = "{\"total\":"+idtotal+",\"totalprice\":"+totalprice+"}";
        response.getWriter().print(jsonstr);
    }

    private void addCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*
        1.获取商品id和购买数量
        2.根据id，获取商品信息（Service的findbyid方法）
        3.从session中去除购物车
        4.不存在，创建购物车，然后把该商品放入购物车中
        5.存在，购物车中没有该商品，直接放入购物车中
                购物车中有该商品，需要取出该商品，商品数量加上购买数量，更新总计
         */
//        1.获取商品id和购买数量
        int id = Integer.parseInt(request.getParameter("productid"));
        int buycount = Integer.parseInt(request.getParameter("buycount"));
//        2.根据id，获取商品信息（Service的findbyid方法）
        Map<String,Object> map = productService.findById(id);
//        3.从session中去除购物车
        List<Map<String,Object>> cart = null;
        HttpSession session = request.getSession();
        cart = (List<Map<String, Object>>) session.getAttribute("cart");
        float price =Float.parseFloat(map.get("price").toString());//该商品单价
        float totalprice = 0;
//        4.不存在，创建购物车，然后把该商品放入购物车中
        if (cart==null){
            //不存在，创建购物车
            cart = new ArrayList<Map<String, Object>>();
            //把商品数量和该商品总价放入map
            map.put("buycount",buycount);
            float total = BaseCalculate.multiply(price,buycount);
            map.put("total",total);
            //把map放入购物车
            cart.add(map);
            totalprice = total;
        }else {
            //5.购物车存在,通过循环
            boolean incart = false;//标记该商品是否在购物车中
            for (int i=0;i<cart.size();i++){
                Map<String,Object> item = cart.get(i);//购物车中的商品
                if(item.get("productid").equals(map.get("productid"))){
                    //购物车中有该商品,更新buycount和total
                    buycount += Integer.parseInt(item.get("buycount").toString());
                    item.put("buycount",buycount);
                    float total = BaseCalculate.multiply(price,buycount);
                    item.put("total",total);
                    totalprice = BaseCalculate.add(totalprice,total);
                    incart = true;
                }else {
                    totalprice = BaseCalculate.add(totalprice,Float.parseFloat(item.get("total").toString()));

                }
            }
            if(!incart){//没有在购物车中
                //把商品数量和该商品总价放入map
                map.put("buycount",buycount);
                float total = BaseCalculate.multiply(price,buycount);
                map.put("total",total);
                totalprice = BaseCalculate.add(totalprice,total);

                //把map放入购物车
                cart.add(map);

            }
        }
        session.setAttribute("totalprice",totalprice);
        session.setAttribute("cart",cart);//把购物车放入到session中
        response.sendRedirect(request.getContextPath()+"/home/cart.jsp");
    }

    private void findbyId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      /*
          1.获取id，根据id查找该条记录(service层)
          2.把该条记录保存到request中
          3.请求转发到productdetails.jsp
     */
       String sid = request.getParameter("id");
       request.setAttribute("item",productService.findById(Integer.parseInt(sid)));
       request.getRequestDispatcher("/home/productdetails.jsp").forward(request,response);
    }

    private void findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      //用于分页的参数
        String current = request.getParameter("current");
        int currentPage;
        try{
            currentPage = Integer.parseInt(current);
        }catch(Exception e){
            currentPage =1;
        }
        //用于查询的参数
        String skey= "name";
        String svalue= request.getParameter("svalue");
        //用于排序的参数
        String sortkey = request.getParameter("sortkey");
        String sort = request.getParameter("sort");

        //调用Service层来获取数据
        Page page = productService.findAll(currentPage,skey,svalue,sortkey,sort);
        //存入request中
        request.setAttribute("sortkey",sortkey);
        request.setAttribute("sort",sort);
        request.setAttribute("svalue",svalue);
        request.setAttribute("page",page);
        //请求转发到productList.jsp页面
        request.getRequestDispatcher("/home/productList.jsp").forward(request,response);
    }

    private void findIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      /*
          1.调用service层的方法获取0-12条记录
          2.存入到request中
          3.请求转发到myindex.jsp页面
       */
      request.setAttribute("list",productService.findIndex());
      request.getRequestDispatcher("/home/myindex.jsp").forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
