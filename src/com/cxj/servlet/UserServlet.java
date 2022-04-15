package com.cxj.servlet;


import com.cxj.domain.Activate;
import com.cxj.domain.User;
import com.cxj.exception.UserException;
import com.cxj.service.UserService;
import com.cxj.utils.EmailUtils;
import com.cxj.utils.MD5;
import com.cxj.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/userServlet")
public class UserServlet extends HttpServlet {

    private UserService userService = new UserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if("regist".equals(action)){
            regist(request,response);
        }else if("login".equals(action)){
            login(request,response);
        }else if("activate".equals(action)){
            activate(request,response);
        }else if("validateUsername".equals(action)){
            validateUsername(request,response);
        }else if("exit".equals(action)){
            exit(request,response);
        }
    }

    private void exit(HttpServletRequest request, HttpServletResponse response) throws IOException {
      /*
         1.将session失效
         2.转向productservlet中，action=findIndex
       */
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect(request.getContextPath()+"/productServlet?action=findIndex");
    }

    private void validateUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {

        /*
           1.获取用户名
           2.调用service的validateUsername(name)，检查用户名是否被占用
           3.返回结果
        */

        String username = request.getParameter("username");
        boolean result = userService.validateUsername(username);
        response.getWriter().print(result);
    }

    private void activate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*
           1.获取激活码
           2.进行激活，调用service方法
           3.激活成功，转向登陆页面
           4.激活失败，转向msg.jsp
         */
        String code = request.getParameter("code");
        try {
            userService.activate(code);
            //激活成功
            String msg = "<script>alert('激活成功，你现在可以登陆了');window.location.href='"+request.getContextPath()+"/home/login.jsp';</script>";
            request.setAttribute("msg",msg);
            request.getRequestDispatcher("/msg.jsp").forward(request,response);
            return ;
        } catch (UserException e) {
            //激活失败
            request.setAttribute("msg",e.getMessage());
            request.getRequestDispatcher("/msg.jsp").forward(request,response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
     /*
         1.获取参数
         2.调用service层的login方法进行登录
         3.登陆成功，修改最近一次登陆时间，转到index.jsp
           登陆失败，转到login.jsp
      */

     String username = request.getParameter("username");
     String password = request.getParameter("password");
     password = MD5.md5(password);
        try {
            User user = userService.login(username,password);
//            if(user.isStatus())
            //修改最近一次登陆时间,调用service层的方法
            userService.updateLastLoginTime(user);
            //登录成功，保存用户登陆成功的信息
            request.getSession().setAttribute("user",user);
            response.sendRedirect(request.getContextPath()+"/home/index.jsp");//重定向
        } catch (UserException e) {
            request.setAttribute("error",e.getMessage());
            request.getRequestDispatcher("/home/login.jsp").forward(request,response);
        }
    }

    private void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      /*
          1.获取参数
          2.验证参数的正确性
          3.对密码进行加密
          3.封装到user对象中
          4.调用service层的regist方法进行注册
          5.调用service层的addActivate()存入激活码
          6.向用户发送一封激活邮件
          7.跳转到login.jsp(防止重复刷新)
      */
      //1.获取参数
      String username = request.getParameter("username");
      String password = request.getParameter("password");
      String password2 = request.getParameter("password2");
      String email = request.getParameter("email");
      //2.验证参数的正确性,回显（将正确的显示出来，不需要重新填写）
        if(username==null||username.trim().isEmpty()){
            request.setAttribute("error","用户名不能为空");
            request.setAttribute("email",email);
            request.getRequestDispatcher("/home/regist.jsp").forward(request,response);
            return;
        }
        if(password==null||password.trim().isEmpty()){
            request.setAttribute("username",username);
            request.setAttribute("email",email);
            request.setAttribute("error","密码不能为空");
            request.getRequestDispatcher("/home/regist.jsp").forward(request,response);
            return;
        }
        if(!password.equals(password2)){
            request.setAttribute("username",username);
            request.setAttribute("email",email);
            request.setAttribute("error","两次密码不一致");
            request.getRequestDispatcher("/home/regist.jsp").forward(request,response);
            return;
        }
        //验证邮箱的正则表达式
        //验证邮箱的正则表达式
        String regex="^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        Pattern p=Pattern.compile(regex);
        Matcher m=p.matcher(email);
        if(!m.find()){//返回true表示符合规则，false表示不符合规则
            request.setAttribute("username",username);
            request.setAttribute("error","邮箱格式不正确！");
            request.getRequestDispatcher("/home/regist.jsp").forward(request,response);
            return;
        }

        //对密码进行加密
        password = MD5.md5(password);
        //3.封装到user对象中
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setLastlogintime(new Date());
        user.setStatus(false);
        //4.调用service层的regist方法进行注册
        userService.regist(user);

        //5.1生成激活码
        String code = UUID.randomUUID().toString().replace("-","");
        //5.2将激活码调用service层的addActivate(Activate)方法，定义一个Activate对象，将值封装到这个方法中
        Activate activate = new Activate();
        activate.setCode(code);
        Date now = new Date();//过期时间是一天后
        now = Utils.checkOption("next",now);
        activate.setExpiredate(now);
        activate.setVipid(user.getVipid());
        userService.addActivate(activate);

        //6.发送激活邮件
        EmailUtils emailUtils = new EmailUtils();
        emailUtils.sendActivateMail(email,code);

        //5.跳转到login。jsp(防止重复刷新)
        request.setAttribute("msg","<script>alert('注册成功，请前往邮箱激活后登录！');window.location.href='/shop/home/login.jsp'</script>");
        request.getRequestDispatcher("/home/msg.jsp").forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
