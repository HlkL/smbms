package com.hg.dao.role;

import com.hg.dao.BaseDao;
import com.hg.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hougen
 * @program smbms
 * @description 实现roleDao接口 dao层抛出异常，让service层去捕获处理
 * @create 2022-09-27 22:42
 */
public class RoleDaoImpl implements RoleDao {

    @Override
    public List<Role> getRoleList(Connection conn) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Role> roleList = new ArrayList<>();
        if (conn != null) {
            String sql = "SELECT * FROM smbms_role";
            Object[] params = {};
            rs = BaseDao.execute(conn, pstm, rs, sql, params);

            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                role.setRoleName(rs.getString("roleName"));
                roleList.add(role);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return roleList;
    }
}


