package com.cxj.servlet;


import com.cxj.domain.Page;
import com.cxj.domain.Product;
import com.cxj.service.CategoryService;
import com.cxj.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@WebServlet("/adminProductServlet")
public class AdminProductServlet extends HttpServlet {

    private CategoryService categoryService = new CategoryService();
    private ProductService productService =new ProductService() ;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if("addBefore".equals(action)){
            addBefore(request,response);
        }else if("add".equals(action)){
            add(request,response);
        }else if("findAll".equals(action)){
            findAll(request,response);
        }else if("find".equals(action)){
            find(request,response);
        }else if("updateBefore".equals(action)){
            updateBefore(request,response);
        }else if("update".equals(action)){
            update(request,response);
        }else if("delete".equals(action)){
            delete(request,response);
        } else if("deleteMore".equals(action)){
            deleteMore(request,response);
        }
    }

    private void deleteMore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       /*
           1.获取所有的checkbox的值
           2.判断是否为空，转向list页面
           3.调用productService层的deleteMore方法
           4.转向msg.jsp  list(防止刷新)
        */
       String ids[] = request.getParameterValues("sel");
       if(ids==null || ids.length==0){
           findAll(request,response);
           return;
       }
       productService.deleteMore(ids);
       request.setAttribute("msg","<script>alert('删除成功！');window.location.href='/shop/adminProductServlet?action=findAll'</script>");
        request.getRequestDispatcher("/admin/msg.jsp").forward(request,response);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
      /*
          1.获取id
          2.调用productService的delete进行删除
          3。转向msg.jsp  list
          4.
      */
      String id = request.getParameter("id");
      try{
          int productid = Integer.parseInt(id);
          productService.delete(productid);
          request.setAttribute("msg","<script>alert('删除成功！');window.location.href='/shop/adminProductServlet?action=findAll'</script>");
          request.getRequestDispatcher("/admin/msg.jsp").forward(request,response);
      }catch(Exception e){
          throw new RuntimeException();
      }

    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       /*
           1.获取表单数据
           2.验证表单数据的正确性
           3.将表单数据封装到product对象
           4.调用productService进行修改update
           5.刷新重复修改问题
        */
        //1.获取表单数据
        String productid = request.getParameter("productid");
        String name = request.getParameter("name");
        String price = request.getParameter("price");
        String markprice = request.getParameter("markprice");
        String photo = request.getParameter("photo");
        String categoryid = request.getParameter("categoryid");
        String quality = request.getParameter("quality");
        String hit = request.getParameter("hit");
        String time = request.getParameter("time");
        String content = request.getParameter("content");

        // 3.封装到Product对象
        Product product = new Product();
        product.setProductid(Integer.parseInt(productid));
        product.setName(name);
        product.setPrice(Float.parseFloat(price));
        product.setMarkprice(Float.parseFloat(markprice));
        product.setPhoto(photo);
        product.setQuality(Integer.parseInt(quality));
        try {
            product.setTime(new SimpleDateFormat("yyyy-mm-dd").parse(time));
        } catch (ParseException e) {
            throw new RuntimeException();
        }
        product.setHit(Integer.parseInt(hit));
        product.setCategoryid(Integer.parseInt(categoryid));
        product.setContent(content);
        //4.调用productService进行修改update
        productService.update(product);
        //5.刷新重复修改问题
        request.setAttribute("msg","<script>alert('修改成功！');window.location.href='/shop/adminProductServlet?action=findAll'</script>");
        request.getRequestDispatcher("/admin/msg.jsp").forward(request,response);
    }

    private void updateBefore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      /*
          1.获取参数id
          2.根据id读取记录，调用productService的findById方法
          3.读取所有的分类记录，调用categoryService的findAll方法
          4.(2,3)中读取的记录存入request中
          5.请求转发到updateProduct.jsp
       */

      String id = request.getParameter("id");
      request.setAttribute("item",productService.findById(Integer.parseInt(id)));
      request.setAttribute("clist",categoryService.findAll());
      request.getRequestDispatcher("/admin/updateProduct.jsp").forward(request,response);
    }

    private void find(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      /*
          1.获取id
          2.调用productService获取id这一条记录findById
          3.存入request
          4.转发到productDes.jsp页面
       */
      String id = request.getParameter("id");
      request.setAttribute("item",productService.findById(Integer.parseInt(id)));
      request.getRequestDispatcher("/admin/productDes.jsp").forward(request,response);
    }

    private void findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    /*
           1.获取当前页码，如果没有当前页码，默认为第一页
           2.调用productService层的findAll方法，来获取Page对象
           3.把数据保存到request
           4.请求转发到productList.jsp
         */
       //1.获取当前页码
       String current = request.getParameter("currentPage");
       //接收查询参数
        String skey = request.getParameter("skey");
        String svalue = request.getParameter("svalue");
       int currentPage = 1;//如果没有当前页码，默认为第一页
        try{
            currentPage = Integer.parseInt(current);
        }catch(Exception e){
            currentPage = 1;
        }

        //2.调用productService层的findAll方法，来获取Page对象
        Page page = productService.findAll(currentPage,skey,svalue);
        request.setAttribute("page",page);
        request.setAttribute("skey",skey);
        request.setAttribute("svalue",svalue);
        request.getRequestDispatcher("/admin/productList.jsp").forward(request,response);

    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    /*
       1.获取表单数据
       2.验证表单的正确性
       3.封装到Product对象
       4.调用ProductService层来添加一条记录
       5.返回到添加页面（禁止重复提交）
     */
        //1.获取表单数据
          String name = request.getParameter("name");
          String price = request.getParameter("price");
          String markprice = request.getParameter("markprice");
          String photo = request.getParameter("photo");
          String categoryid = request.getParameter("categoryid");
          String quality = request.getParameter("quality");
          String hit = request.getParameter("hit");
          String time = request.getParameter("time");
          String content = request.getParameter("content");

        // 3.封装到Product对象
        Product product = new Product();
        product.setName(name);
        product.setPrice(Float.parseFloat(price));
        product.setMarkprice(Float.parseFloat(markprice));
        product.setPhoto(photo);
        product.setQuality(Integer.parseInt(quality));
        try {
            product.setTime(new SimpleDateFormat("yyyy-mm-dd").parse(time));
        } catch (ParseException e) {
            throw new RuntimeException();
        }
        product.setHit(Integer.parseInt(hit));
        product.setCategoryid(Integer.parseInt(categoryid));
        product.setContent(content);

        //4.调用service 层的方法进行修改
        productService.add(product);
        //5.禁止刷新重复提交，先将他转向msg.jsp
        request.setAttribute("msg","<script>alert('发布成功！');window.location.href='/shop/adminProductServlet?action=findAll'</script>");
        request.getRequestDispatcher("/admin/msg.jsp").forward(request,response);
    }

    private void addBefore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
            1.读取商品分类信息，调用CategoryService中的findAll()方法
            2.将数据存入request域
            3.请求转发到addProduct.jsp页面
         */
        //1.读取商品分类信息，调用CategoryService中的findAll()方法
        List<Map<String,Object>> list = categoryService.findAll();
        //2.将数据存入request域
        request.setAttribute("list",list);
        //3.请求转发到addProduct.jsp页面
        request.getRequestDispatcher("/admin/addProduct.jsp").forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
