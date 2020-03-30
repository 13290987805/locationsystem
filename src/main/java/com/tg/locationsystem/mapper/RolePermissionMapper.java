package com.tg.locationsystem.mapper;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.entity.RolePermission;


import java.util.Set;

public interface RolePermissionMapper extends IBaseDao<RolePermission>{
    int deleteByPrimaryKey(Integer id);

    int insert(RolePermission record);

    int insertSelective(RolePermission record);

    RolePermission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RolePermission record);

    int updateByPrimaryKey(RolePermission record);


    //Set<RolePermission> getRolePermissionSet(String roleName);

    Set<RolePermission> getRolePermissionSetByRoleId(String roleId);
}