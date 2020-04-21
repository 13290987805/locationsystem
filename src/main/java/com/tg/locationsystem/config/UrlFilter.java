package com.tg.locationsystem.config;


import com.tg.locationsystem.utils.SystemMap;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/11/7
 */
//{"/alert/*","/binding/*","/call/*","/frence/*","/goods/*","/image/*","/map/*","/myuser/*","/path/*","/person/*","/station/*","/tag/*"}
@WebFilter(filterName = "UrlFilter ", urlPatterns ="/*" )
public class UrlFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        System.out.println("----------------------->过滤器被创建");
    }

    /*
    * 拦截使用期限过期的全部url
    * */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String requestURI = req.getRequestURI();
        //System.out.println("--------------------->过滤器：请求地址"+requestURI);
        Object user_secret = req.getSession().getAttribute("user_secret");
        List<String> secretList = SystemMap.getSecretList();
        if (secretList==null||secretList.size()==0){
            filterChain.doFilter(servletRequest, servletResponse);

        }else {
            servletRequest.getRequestDispatcher("/myuser/secret").forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        System.out.println("----------------------->过滤器被销毁");
    }
}
