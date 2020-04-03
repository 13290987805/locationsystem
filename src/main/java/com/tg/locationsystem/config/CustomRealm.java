package com.tg.locationsystem.config;

import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.MyuserRole;
import com.tg.locationsystem.entity.RolePermission;
import com.tg.locationsystem.pojo.MyuserRoleVO;
import com.tg.locationsystem.pojo.RolePermissionVO;
import com.tg.locationsystem.service.IMyUserService;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Permissions;

/**
 * @author hyy
 * @ Date2020/3/27
 */
public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private IMyUserService myUserService;

    /*
    *授权
    * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String name = (String) principalCollection.getPrimaryPrincipal();
        //根据用户名去数据库查询用户信息
        Myuser user = myUserService.getUserByName(name);
        if (user==null){
            return null;
        }
        //System.out.println("用户:"+user);
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (MyuserRoleVO role : user.getRoles()) {
            //添加角色
            simpleAuthorizationInfo.addRole(role.getRoleName());
            //添加权限
            for (RolePermissionVO permissions : role.getPermissions()) {
                //System.out.println("权限:"+permissions);
                simpleAuthorizationInfo.addStringPermission(permissions.getPermissionName());
            }
        }
        return simpleAuthorizationInfo;
    }

    /*
    * 登录
    * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //加这一步的目的是在Post请求的时候会先进认证，然后在到请求
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        //获取用户信息
        String name = authenticationToken.getPrincipal().toString();
        Myuser user = myUserService.getUserByName(name);
        if (user == null) {
            //这里返回后会报出对应异常
            return null;
        } else {
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name, user.getPassword().toString(), getName());
            return simpleAuthenticationInfo;
        }
    }
    }

