package com.hg.filter;

import com.hg.utils.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hougen
 * @program smbms
 * @description 系统过滤器, 过滤掉未登录直接访问主页的错误
 * @create 2022-09-24 00:40
 */
public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //ServletRequest强转HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取session中存放的user信息
        Object attribute = request.getSession().getAttribute(Constants.USER_SESSION);
        //用户注销登录,或者没有登录
        //使之不能直接访问主页,必须从登录页面访问
        if (attribute == null){
            response.sendRedirect("/smbms/error.jsp");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}


