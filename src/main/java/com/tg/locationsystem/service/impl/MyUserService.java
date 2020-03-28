package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.MyuserRole;
import com.tg.locationsystem.entity.RolePermission;
import com.tg.locationsystem.mapper.MyuserMapper;
import com.tg.locationsystem.mapper.MyuserRoleMapper;
import com.tg.locationsystem.mapper.RolePermissionMapper;
import com.tg.locationsystem.service.IMyUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author hyy
 * @ Date2019/6/26
 */
@Service
public class MyUserService extends BaseServiceImpl<Myuser> implements IMyUserService{
    @Autowired
    private MyuserMapper myuserMapper;
    @Autowired
    private MyuserRoleMapper myuserRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Override
    public IBaseDao<Myuser> getBaseDao() {
        return myuserMapper;
    }

    @Override
    public Myuser getUserByName(String username) {
        Myuser user = myuserMapper.getUserByName(username);
        Set<MyuserRole> roles=myuserRoleMapper.getMyuserRoleSet(user.getUsername());

        for (MyuserRole role : roles) {
            Set<RolePermission> permissions=rolePermissionMapper.getRolePermissionSet(role.getRoleName());

            role.setPermissions(permissions);
            //System.out.println("角色"+roles);
        }
        user.setRoles(roles);
       // System.out.println("用户:"+user);
        return user;
    }
}
