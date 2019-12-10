package com.tg.locationsystem.config;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author hyy
 * @ Date2019/11/7
 */
//{"/alert/*","/binding/*","/call/*","/frence/*","/goods/*","/image/*","/map/*","/myuser/*","/path/*","/person/*","/station/*","/tag/*"}
//@WebFilter(filterName = "UrlFilter ", urlPatterns ="/*" )
public class UrlFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        System.out.println("----------------------->过滤器被创建");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String requestURI = req.getRequestURI();
        //System.out.println("--------------------->过滤器：请求地址"+requestURI);
        Object user = req.getSession().getAttribute("user");
        if (user!=null||requestURI.contains("Login")||
                requestURI.contains("toLogin")||requestURI.contains("Login2")){
            filterChain.doFilter(servletRequest, servletResponse);
        }else {
            servletRequest.getRequestDispatcher("/myuser/toLogin").forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        System.out.println("----------------------->过滤器被销毁");
    }
}
