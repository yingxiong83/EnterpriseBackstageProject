package com.itheima.ssm.service;

import com.itheima.ssm.domain.Permission;
import com.itheima.ssm.domain.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll() throws Exception;

    void save(Role role) throws Exception;

    Role findById(String id) throws Exception;

    List<Permission> findNoPermissions(String id) throws Exception;

    void addPermissionToRole(String[] permissionId, String roleId) throws Exception;

    void delete(String roleId) throws Exception;
}
