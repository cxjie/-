package com.cxj.filter;


import com.cxj.domain.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter
        (filterName = "LoginFilter",
                dispatcherTypes = {DispatcherType.REQUEST,DispatcherType.FORWARD},
                urlPatterns = {"/person/*"},
                servletNames = {"AddressServlet","OrderServlet"})
public class LoginFilter  implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        /*
        1、判断用户是否登录
        2、登录则继续
        3、未登录，转到登录页面
         */
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user==null){
            req.getRequestDispatcher("/home/login.jsp").forward(req,response);
            return;
        }
        else {

            chain.doFilter(request, response);//继续执行
        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
