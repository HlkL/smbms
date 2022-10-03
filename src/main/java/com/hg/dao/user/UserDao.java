package com.hg.dao.user;

import com.hg.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户数据库操作层
 *
 * @author HG
 */
public interface UserDao {
    /**
     * 数据库操作接口 得到登录的对象
     *
     * @param conn     数据库连接对象
     * @param userCode 用户
     * @param password 密码
     * @return 查询到用户
     * @throws SQLException
     */
    User getLoginUser(Connection conn, String userCode, String password) throws SQLException;

    /**
     * 修改当前用户密码
     *
     * @param conn     数据库连接对象
     * @param id       要修改密码用户的id
     * @param password 要修改的旧密码
     * @return 受影响的行数
     * @throws SQLException
     */
    int updatePwd(Connection conn, String password, int id) throws SQLException;

    /**
     * 根据用户名或者角色身份查询用户总数
     *
     * @param conn     数据库连接对象
     * @param userName 用户名字
     * @param userRole 用户角色
     * @return 用户总数
     * @throws SQLException
     */
    int queryTheNumberOfUsers(Connection conn, String userName, int userRole) throws SQLException;

    /**
     * 根据用户名或者角色身份查询得到当前页面所有的用户信息
     *
     * @param conn          数据连接对象
     * @param userName      用户名
     * @param userRole      角色身份
     * @param currentPageNo 当前页面编号
     * @param pageSize      每页用户信息数量
     * @return 当前页面所有用户的信息
     * @throws SQLException
     */
    List<User> getUserList(Connection conn, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException;

    /**
     * 添加用户
     *
     * @param conn 数据库连接对象
     * @param user 用户
     * @return 受影响的行数
     * @throws SQLException
     */
    int addUser(Connection conn, User user) throws SQLException;

    /**
     * 通过id删除用户
     *
     * @param conn 数据库连接对象
     * @param id 用户id
     * @return 受影响的行数
     * @throws SQLException
     */
    int delUser(Connection conn,int id) throws SQLException;

    /**
     * 查看用户信息
     *
     * @param conn 数据库连接对象
     * @param id 用户id
     * @return 用户
     * @throws SQLException
     */
    User viewUser(Connection conn,int id) throws SQLException;

    /**
     * 修改用户信息
     *
     * @param conn 数据库连接对象
     * @param id 用户id
     * @param user 新的用户信息
     * @return 受影响行数
     * @throws SQLException
     */
    int modify(Connection conn,int id,User user) throws SQLException;
}
