package com.hg.service.role;

import com.hg.dao.BaseDao;
import com.hg.dao.role.RoleDao;
import com.hg.dao.role.RoleDaoImpl;
import com.hg.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author hougen
 * @program smbms
 * @description service层捕获异常，进行事务处理
 * @create 2022-09-27 23:05
 */
public class RoleServiceImpl implements RoleService{

    private RoleDao roleDao;

    public RoleServiceImpl() {
        this.roleDao = new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection conn = BaseDao.getConnection();
        List<Role> roleList = null;
        try{
            roleList = roleDao.getRoleList(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(conn,null,null);
        }
        return roleList;
    }

    @Test
    public void getRoleListTest(){
        List<Role> roleList = getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}


