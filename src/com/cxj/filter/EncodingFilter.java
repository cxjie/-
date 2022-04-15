package com.cxj.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter (
        filterName = "EncodingFilter",
        dispatcherTypes = {
        DispatcherType.REQUEST ,
        DispatcherType.FORWARD
}
        ,urlPatterns = {"/*"})
public class EncodingFilter implements Filter {

    public void destroy() {
    }

    public void doFilter(ServletRequest request , ServletResponse response, FilterChain chain) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");//处理post请求
        HttpServletRequest req = (HttpServletRequest) request;
        if(req.getMethod().equals("get")){
            EncodingServletRequest esr = new EncodingServletRequest(req);
            chain.doFilter(esr, response);//放行
        }else if(req.getMethod().equals("post")){
            request.setCharacterEncoding("utf-8");//处理post请求
            chain.doFilter(request, response);//放行
        }else{
            chain.doFilter(request, response);//放行
        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
