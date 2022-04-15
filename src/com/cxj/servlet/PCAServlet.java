package com.cxj.servlet;


import com.cxj.service.PCAService;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/pCAServlet")
public class PCAServlet extends HttpServlet {

    private PCAService pcaService = new PCAService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");//防止中文乱码
        request.setCharacterEncoding("UTF-8");//防止中文乱码
        String action = request.getParameter("action");
        if("getprovinces".equals(action)){
            getProvinces(request,response);
        }if("getcities".equals(action)){
            getCities(request,response);
        }if("getareas".equals(action)){
            getAreas(request,response);
        }
    }

    private void getAreas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /*

         */
        String cityid = request.getParameter("cityid");
        List<Map<String,Object>> list = pcaService.getAreas(cityid);
        JSONArray jsonArray = JSONArray.fromObject(list);
        System.out.println("jsonArray:"+jsonArray);
        response.getWriter().print(jsonArray.toString());
    }

    private void getCities(HttpServletRequest request, HttpServletResponse response) throws IOException {
      /*
         1.获取省级编号
         2.根据省级编号调用service层方法获取市列表
         3.封装到JSONArray中
         4.发送回页面
       */
      String provinceid = request.getParameter("provinceid");
      List<Map<String,Object>> list = pcaService.getCities(provinceid);
      JSONArray jsonArray = JSONArray.fromObject(list);
      System.out.println("jsonArray:"+jsonArray);
      response.getWriter().print(jsonArray.toString());
    }

    private void getProvinces(HttpServletRequest request, HttpServletResponse response) throws IOException {
      /*
        1.调用service层获取省份列表
        2.将List封装到JSONArray中
        3.返回给页面
       */
      // 1.调用service层获取省份列表
        List<Map<String,Object>> list = pcaService.getProvinces();
        //2.将List封装到JSONArray中
        JSONArray jsonArray = JSONArray.fromObject(list);
        System.out.println("list:"+list);
        System.out.println("jsonArray:"+jsonArray);
        //3.返回给页面
        response.getWriter().print(jsonArray.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");//防止中文乱码
        request.setCharacterEncoding("UTF-8");//防止中文乱码
        doPost(request,response);
    }
}
