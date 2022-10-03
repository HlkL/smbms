package com.hg.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @program: smbms
 * @description: 字符编码过滤器
 * @author: hougen
 * @create: 2022-09-22 23:10
 */
public class CharacterEncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("utf-8");
        servletResponse.setCharacterEncoding("utf-8");
        servletResponse.setContentType("text/html;charset=UTF-8");

        //让我们的请求继续走，如果不写，程序到这里就被拦截停止！
        filterChain.doFilter(servletRequest,servletResponse);
    }
    @Override
    public void destroy() {

    }
}


