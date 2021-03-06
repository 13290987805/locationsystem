package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.RolePermission;
import com.tg.locationsystem.entity.Station;
import com.tg.locationsystem.mapper.RolePermissionMapper;
import com.tg.locationsystem.service.IRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyy
 * @ Date2020/3/28
 */
@Service
public class RolePermissionService  extends BaseServiceImpl<RolePermission> implements IRolePermissionService{
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Override
    public IBaseDao<RolePermission> getBaseDao() {
        return rolePermissionMapper;
    }

    @Override
    public int deleteByRoleId(Integer roleId) {
        return rolePermissionMapper.deleteByRoleId(roleId) ;
    }

    @Override
    public List<RolePermission> getPermissionsByRoleId(Integer roleId) {
        return rolePermissionMapper.getPermissionsByRoleId(roleId);
    }
}
