package com.hg.service.role;

import com.hg.pojo.Role;

import java.util.List;

/**
 * @author HG
 */
public interface RoleService {
    /**
     * 得到角色列表
     * @return
     */
    List<Role> getRoleList();
}
