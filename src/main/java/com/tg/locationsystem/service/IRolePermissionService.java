package com.tg.locationsystem.service;

import com.tg.locationsystem.base.service.IBaseService;
import com.tg.locationsystem.entity.RolePermission;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/28
 */
public interface IRolePermissionService extends IBaseService<RolePermission> {
    int deleteByRoleId(Integer roleId);

    List<RolePermission> getPermissionsByRoleId(Integer roleId);
}
