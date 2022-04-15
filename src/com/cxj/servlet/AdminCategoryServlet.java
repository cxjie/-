package com.cxj.servlet;


import com.cxj.domain.Category;
import com.cxj.domain.Page;
import com.cxj.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/adminCategoryServlet")
public class AdminCategoryServlet extends HttpServlet {

    private CategoryService categoryService = new CategoryService();


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
            1.获取action的值，根据action值调用不同的方法

         */
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            add(request, response);
        } else if ("findAll".equals(action)) {
            findAll(request, response);
        } else if ("delete".equals(action)) {
            deleteById(request, response);
        } else if ("deleteMore".equals(action)) {
            deleteMore(request, response);
        } else if ("updateBefore".equals(action)) {
            updateBefore(request, response);
        } else if ("update".equals(action)) {
            update(request, response);
        }


    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
           1.获取要修改的所有信息
           2.验证
           3.调用service层进行修改
           4.禁止刷新重复提交，先将他转向msg.jsp
         */
        //1.获取表单数据
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String sort = request.getParameter("sort");
        //2.进行验证表单数据的正确性
        if (name == null || name.trim().isEmpty()) {
            //name为空
            request.setAttribute("msg", "分类名称不能为空!");
            request.getRequestDispatcher("/admin/updateCategory.jsp").forward(request, response);
            return;
        }
        if (sort == null || sort.trim().isEmpty()) {
            //sort为空
            request.setAttribute("msg", "分类排序号不能为空!");
            request.getRequestDispatcher("/admin/updateCategory.jsp").forward(request, response);
            return;
        }
        String regex = "^[1-9]+[0-9]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sort);
        if (!m.find()) {//m.find()返回true表示符合规则，false表示不符合规则
            request.setAttribute("msg", "分类排序号必须为正整数！");
            request.getRequestDispatcher("/admin/updateCategory.jsp").forward(request, response);
            return;
        }
        //2.1判断name是否已存在，存在返回提示错误，不存在继续添加
        //调用service中的方法validateName(name)，如果存在返回true,不存在返回false
        if (categoryService.validateName(name)) {
            request.setAttribute("msg", "该分类名称已被占用");
            request.getRequestDispatcher("admin/addCategory.jsp").forward(request, response);
            return;
        }

        //3.封装到Category对象中
        Category category = new Category();
        category.setCategoryid(Integer.parseInt(id));
        category.setName(name);
        category.setSort(Integer.parseInt(sort));
        //4.调用service 层的方法进行修改
        categoryService.update(category);
        //5.禁止刷新重复提交，先将他转向msg.jsp
        request.setAttribute("msg", "<script>alert('修改成功！');window.location.href='" + request.getContextPath() + "/adminCategoryServlet?action=findAll'</script>");
        request.getRequestDispatcher("/admin/msg.jsp").forward(request, response);
    }

    private void updateBefore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    /*
        1.获取要修改的Id
        2.通过service层把该条记录读取出来
        3.存入到request
        4.请求转发到updateCategory.jsp
     */
        String id = request.getParameter("id");
        request.setAttribute("item", categoryService.findById(Integer.parseInt(id)));
        request.getRequestDispatcher("/admin/updateCategory.jsp").forward(request, response);


    }

    private void deleteMore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
            1.获取要删除的id数组
            2.判断id是否为空
            3.调用service层删除
            4.调用findall方法重新显示删除后的记录
         */
        String[] ids = request.getParameterValues("sel");
        if (ids != null) {
            categoryService.deleteMore(ids);
            findAll(request, response);
        }
    }

    private void deleteById(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    /*
       1.获取要删除的id
       2.调用service层来deleteById进行删除
       3.调用findAll方法显示新的列表
     */
        String str = request.getParameter("id");
        categoryService.deleteById(Integer.parseInt(str));
        findAll(request, response);

    }

    private void findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*
           0.获取当前页码，如果没有当前页码，默认为第一页
           1.调用service层的findALL方法，来获取Page对象
           2.把数据保存到request
           3.请求转发到categoryList.jsp
         */
        //0.获取当前页码，如果没有当前，默认为第一页
        int currentPage = 1;
        String c = request.getParameter("currentPage");
        try {
            currentPage = Integer.parseInt(c);
        } catch (Exception e) {
            currentPage = 1;
        }
        //1.直接调用service层的findAll方法，来获取数据
        Page page = categoryService.findAll(currentPage);
        // 2.把数据保存到request
        request.setAttribute("page", page);
        //3.请求转发categoryList.jsp页面
        request.getRequestDispatcher("/admin/categoryList.jsp").forward(request, response);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
            1.获取表单数据
            2.进行验证表单数据的正确,判断name是否已存在
            3.封装到Category对象中
            4.调用service 层的方法进行添加
            5.转到本身页面（以后再改）
         */
        //1.获取表单数据
        String name = request.getParameter("name");
        String sort = request.getParameter("sort");
        //2.进行验证表单数据的正确性
        if (name == null || name.trim().isEmpty()) {
            //name为空
            request.setAttribute("msg", "分类名称不能为空!");
            request.getRequestDispatcher("/admin/addCategory.jsp").forward(request, response);
            return;
        }
        if (sort == null || sort.trim().isEmpty()) {
            //sort为空
            request.setAttribute("msg", "分类排序号不能为空!");
            request.getRequestDispatcher("/admin/addCategory.jsp").forward(request, response);
            return;
        }
        String regex = "^[1-9]+[0-9]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sort);
        if (!m.find()) {//m.find()返回true表示符合规则，false表示不符合规则
            request.setAttribute("msg", "分类排序号必须为正整数！");
            request.getRequestDispatcher("/admin/addCategory.jsp").forward(request, response);
            return;
        }
        //2.1判断name是否已存在，存在返回提示错误，不存在继续添加
        //调用service中的方法validateName(name)，如果存在返回true,不存在返回false
        if (categoryService.validateName(name)) {
            request.setAttribute("msg", "该分类名称已被占用");
            request.getRequestDispatcher("admin/addCategory.jsp").forward(request, response);
            return;
        }

        //3.封装到Category对象中
        Category category = new Category();
        category.setName(name);
        category.setSort(Integer.parseInt(sort));
        //4.调用service 层的方法进行添加
        categoryService.add(category);
        //5.转到本身页面
        request.setAttribute("msg", "<script>alert('添加成功！');window.location.href='/shop/adminCategoryServlet?action=findAll'</script>");
        request.getRequestDispatcher("/admin/msg.jsp").forward(request, response);

    }


}
