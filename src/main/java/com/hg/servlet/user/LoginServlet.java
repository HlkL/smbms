package com.hg.servlet.user;

import com.hg.pojo.User;
import com.hg.service.user.UserServiceImpl;
import com.hg.utils.Constants;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

/**
 * 处理登录请求
 * @author HG
 */
@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {
    /**
     * servlet控制层,调用业务层代码
     * @param request 请求
     * @param response 响应
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("LoginServlet---start......");
        //获取用户名和密码
        String userName = request.getParameter("userCode");
        String userPassword = request.getParameter("userPassword");

        //调用业务层和数据库中的密码进行对比
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userName, userPassword);

        //数据库中有userName的数据
        if( user != null && user.getUserPassword().equals(userPassword) ){
            //将用户信息存放到session中
            request.getSession().setAttribute(Constants.USER_SESSION,user);
            //登录成功跳转到主页
            response.sendRedirect("jsp/frame.jsp");
        }else {
            //没有数据转发回登录页面
            //提示输入错误
            request.setAttribute("error","用户名或密码输入错误!");
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
