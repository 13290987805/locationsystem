package com.tg.locationsystem.service.impl;

import com.tg.locationsystem.base.dao.IBaseDao;
import com.tg.locationsystem.base.service.impl.BaseServiceImpl;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.*;
import com.tg.locationsystem.pojo.MyuserRoleVO;
import com.tg.locationsystem.pojo.RolePermissionVO;
import com.tg.locationsystem.service.IMyUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

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
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public IBaseDao<Myuser> getBaseDao() {
        return myuserMapper;
    }

    @Override
    public Myuser getUserByName(String username) {
        Myuser user = myuserMapper.getUserByName(username);
        Set<MyuserRole> roles=myuserRoleMapper.getMyuserRoleSet(user.getId());
        Set<MyuserRoleVO> roleVOS=new TreeSet<>();
        for (MyuserRole role : roles) {
            MyuserRoleVO roleVO=new MyuserRoleVO();
            roleVO.setId(role.getId());
            roleVO.setUserId(role.getUserId());
            roleVO.setRoleId(role.getRoleId());
            roleVO.setRemark(role.getRemark());
            roleVO.setUsername(username);
            Role role1 = roleMapper.selectByPrimaryKey(Integer.parseInt(role.getRoleId()));
            if (role1!=null){
                roleVO.setRoleName(role1.getRoleName());
            }
            roleVOS.add(roleVO);
        }
        for (MyuserRoleVO role : roleVOS) {
            Set<RolePermission> rolepermissions=rolePermissionMapper.getRolePermissionSetByRoleId(role.getRoleId());
            Set<RolePermissionVO> rolePermissionVOSet=new TreeSet<>();
            for (RolePermission permission : rolepermissions) {
                RolePermissionVO permissionVO=new RolePermissionVO();
                permissionVO.setId(permission.getId());
                permissionVO.setRoleId(permission.getRoleId());
                permissionVO.setPermissionId(permission.getPermissionId());
                permissionVO.setRemark(permission.getRemark());
                Permission permission1 = permissionMapper.selectByPrimaryKey(Integer.parseInt(permission.getPermissionId()));
                if (permission1!=null){
                    permissionVO.setPermissionName(permission1.getPermissionName());
                }
                rolePermissionVOSet.add(permissionVO);
            }
            role.setPermissions(rolePermissionVOSet);
            //System.out.println("角色"+roles);
        }
        user.setRoles(roleVOS);
       // System.out.println("用户:"+user);
        return user;
    }
}
