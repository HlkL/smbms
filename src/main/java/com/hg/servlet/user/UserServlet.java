package com.hg.servlet.user;

import com.google.gson.Gson;
import com.hg.pojo.Role;
import com.hg.pojo.User;
import com.hg.service.role.RoleService;
import com.hg.service.role.RoleServiceImpl;
import com.hg.service.user.UserService;
import com.hg.service.user.UserServiceImpl;
import com.hg.utils.Constants;
import com.hg.utils.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现Servlet复用
 *
 * @author HG
 */
@WebServlet(name = "UserServlet", value = "/UserServlet")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        System.out.println("method----> " + method);
        if ("savepwd".equals(method)) {
            this.updatePwd(request, response);
        } else if ("pwdmodify".equals(method)) {
            this.pwdModify(request, response);
        } else if ("query".equals(method)) {
            this.query(request, response);
        }else if ("add".equals(method)) {
            this.add(request, response);
        }else if ("getrolelist".equals(method)) {
            this.getRoleList(request, response);
        }else if ("ucexist".equals(method)) {
            this.ifUserCodeExist(request, response);
        }else if ("view".equals(method)) {
            this.viewUser(request, response,"userview.jsp");
        }else if ("deluser".equals(method)) {
            this.delUser(request, response);
        }else if ("modify".equals(method)) {
            this.viewUser(request, response,"usermodify.jsp");
        }else if ("modifyexe".equals(method)) {
            this.updateUser(request, response);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * 修改密码
     */
    private void updatePwd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从Session中获取id
        Object attribute = request.getSession().getAttribute(Constants.USER_SESSION);
        //从前端输入框获取新密码
        String newPassword = request.getParameter("newpassword");
        if (attribute != null && !StringUtils.isNullOrEmpty(newPassword)) {
            UserService userService = new UserServiceImpl();
            //密码修改成功
            if (userService.updatePwd(((User) attribute).getId(), newPassword)) {
                //删除当前Session,返回登录页面
                request.setAttribute("message", "密码修改成功,请重新登录!");
                request.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                request.setAttribute("message", "密码修改失败,检查密码是否输入正确!");
            }
        } else {
            request.setAttribute("message", "密码修改失败,检查密码是否输入正确!");
        }
        //刷新页面
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
//        request.getRequestDispatcher("pwdmodify.jsp").forward(request, response);
    }

    /**
     * 旧密码验证
     */
    private void pwdModify(HttpServletRequest request, HttpServletResponse response){
        //从Session中获取id
        Object attribute = request.getSession().getAttribute(Constants.USER_SESSION);
        //从前端输入框获取旧密码
        String oldPassword = request.getParameter("oldpassword");

        //将结果集存放到Map中
        Map<String, String> resultMap = new HashMap<String, String>();
        //Session失效
        if (attribute == null) {
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldPassword)) {
            resultMap.put("result", "error");
        } else {
            //从session中获取用户登录的旧密码
            String userOldPwd = ((User) attribute).getUserPassword();
            //与输入框的旧密码比较是否密码是否正确
            if (userOldPwd.equals(oldPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }
        try {
            //设置类型为json
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            //Gson谷歌json工具类
            Gson gson = new Gson();
            writer.write(gson.toJson(resultMap));
            //刷新页面
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户管理
     */
    private void query(HttpServletRequest request, HttpServletResponse response) {
        String queryUserName = request.getParameter("queryname");
        String userRole = request.getParameter("queryUserRole");
        String pageIndex = request.getParameter("pageIndex");

        //分页工具类
        PageSupport pageSupport = new PageSupport();
        UserService userService = new UserServiceImpl();
        RoleService roleService = new RoleServiceImpl();
        //存放用户 角色
        List<User> userList;
        List<Role> roleList;

        //选择的角色
        int queryUserRole = 0;
        //当前页面
        int currentPageNo = 0;

        if (queryUserName == null) {
            queryUserName = "";
        }
        if( userRole != null && !"".equals(userRole) ){
            //角色身份选择款赋值
            queryUserRole = Integer.parseInt(userRole);
        }
        if( pageIndex != null ){
            currentPageNo = Integer.parseInt(pageIndex);
        }

        //获取用户总数  进行分页操作
        int totalCount = userService.getUserCount(queryUserName,queryUserRole);
        //总页数操作
        //设置页面大小
        pageSupport.setPageSize(Constants.PAGESIZE);
        //设置当前页面
        pageSupport.setCurrentPageNo(currentPageNo);
        //设置总页数
        pageSupport.setTotalCount(totalCount);
        //总页数
        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        if( currentPageNo < 1 ){
            //当前页面<1
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            //当前页面<尾页
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, Constants.PAGESIZE);
        request.setAttribute("userList",userList);
        //获取角色列表展示
        roleList = roleService.getRoleList();
        request.setAttribute("roleList",roleList);
        //页面
        request.setAttribute("totalCount",totalCount);
        request.setAttribute("currentPageNo",currentPageNo);
        request.setAttribute("totalPageCount",totalPageCount);


        request.setAttribute("queryUserName",queryUserName);
        request.setAttribute("queryUserRole",queryUserRole);
        //返回前端
        try {
            request.getRequestDispatcher("userlist.jsp").forward(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加用户
     */
    private void add(HttpServletRequest request, HttpServletResponse response) {
        String userCode = request.getParameter("userCode");
        String userName = request.getParameter("userName");
        String userPassword = request.getParameter("userPassword");
        String gender = request.getParameter("gender");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String userRole = request.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.parseInt(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if(userService.addUser(user)){
            try {
                response.sendRedirect(request.getContextPath() + "/jsp/user.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                request.getRequestDispatcher("useradd.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取角色身份列表
     */
    private void getRoleList(HttpServletRequest request, HttpServletResponse response) {
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();

        //设置类型为json
        response.setContentType("application/json");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            //Gson谷歌json工具类
            Gson gson = new Gson();
            writer.write(gson.toJson(roleList));
            //刷新页面
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * userCode是否存在
     */
    private void ifUserCodeExist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userCode = request.getParameter("userCode");
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, "");
        boolean isExist = user != null;
        Map<String, String> resultMap = new HashMap<>();

        if( isExist ){
            resultMap.put("userCode","exist");
        }
        //设置类型为json
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        //Gson谷歌json工具类
        Gson gson = new Gson();
        writer.write(gson.toJson(resultMap));
        //刷新页面
        writer.flush();
        writer.close();
    }

    /**
     * 查看用户信息
     */
    private void viewUser(HttpServletRequest request, HttpServletResponse response,String url) throws ServletException, IOException {
        String uid = request.getParameter("uid");
        int id = Integer.parseInt(uid);
        UserService userService = new UserServiceImpl();
        User user = userService.viewUser(id);
//        将用户信息保存至 request中 让usermodify.jsp显示
        request.setAttribute("user",user);
        request.getRequestDispatcher(url).forward(request,response);
    }
    /**
     * 删除用户
     */
    private void delUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uid = request.getParameter("uid");
        int delId = Integer.parseInt(uid);

        Map<String, String> resultMap = new HashMap<>();
        UserService userService = new UserServiceImpl();
        if( userService.delUser(delId) ){
            resultMap.put("delResult","true");
        }else{
            resultMap.put("delResult","false");
        }
        //设置类型为json
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        //Gson谷歌json工具类
        Gson gson = new Gson();
        writer.write(gson.toJson(resultMap));
        //刷新页面
        writer.flush();
        writer.close();
    }
    /**
     * 修改用户信息
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从修改信息的表单中封装信息
        User user = new User();
        user.setUserName(request.getParameter("userName"));
        user.setGender(Integer.parseInt(request.getParameter("gender")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("birthday")));
        }catch (ParseException e){
            e.printStackTrace();
        }
        user.setPhone(request.getParameter("phone"));
        user.setAddress(request.getParameter("address"));
        user.setUserRole(Integer.parseInt(request.getParameter("userRole")));
        //注意这两个参数不在表单的填写范围内
        user.setModifyBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        String uid = request.getParameter("uid");
        int id = 0;
        try {
            id = Integer.parseInt(uid);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        UserService userService = new UserServiceImpl();

        if(userService.modify(id,user)){
            //如果执行成功了 网页重定向到 用户管理页面(即 查询全部用户列表)
            response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
        }else{
            request.getRequestDispatcher("usermodify.jsp").forward(request,response);
        }
    }
}