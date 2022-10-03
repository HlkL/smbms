package com.hg.service.user;

import com.hg.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author HG
 */
public interface UserService {
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 数据库查询到的user对象
     */
    User login(String username, String password);

    /**
     * 根据用户id修改密码
     *
     * @param id       用户id
     * @param password 要修改的密码
     * @return 修改结果
     */
    boolean updatePwd(int id, String password);

    /**
     * 查询记录数
     *
     * @param userName 用户名
     * @param userRole 角色身份
     * @return 记录的数量
     */
    int getUserCount(String userName, int userRole);

    /**
     * 根据条件查询用户列表
     *
     * @param userName      用户名
     * @param userRole      角色身份
     * @param currentPageNo 当前页面
     * @param pageSize      页面大小
     * @return 用户列表
     */
    List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);

    /**
     * 添加用户
     * @param user 用户
     * @return 是否添加成功
     */
    boolean addUser(User user);

    /**
     * 通过id删除用户
     *
     * @param id 用户id
     * @return 受影响的行数
     */
    boolean delUser(int id);

    /**
     * 查看用户信息
     *
     * @param id 用户id
     * @return 用户
     */
    User viewUser(int id);

    /**
     * 修改用户信息
     *
     * @param id 用户id
     * @param user 新的用户信息
     */
    boolean modify(int id,User user);
}
