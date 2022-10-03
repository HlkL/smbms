package com.hg.dao.role;

import com.hg.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 角色数据库操作层
 *
 * @author HG
 */
public interface RoleDao {
    /**
     * 得到角色列表
     *
     * @param conn 数据库连接对象
     * @return 角色列表
     * @throws SQLException
     */
    List<Role> getRoleList(Connection conn) throws SQLException;
}
