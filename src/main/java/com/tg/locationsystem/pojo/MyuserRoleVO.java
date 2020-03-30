package com.tg.locationsystem.pojo;

import com.tg.locationsystem.entity.RolePermission;

import java.io.Serializable;
import java.util.Set;

/**
 * @author hyy
 * @ Date2020/3/30
 */
public class MyuserRoleVO  implements java.lang.Comparable{
    private Integer id;

    private Integer userId;

    private String roleId;

    private String remark;

    /**
     * 角色对应权限集合
     */
    private Set<RolePermissionVO> permissions;

    private String username;

    private String roleName;

    public MyuserRoleVO() {
    }

    @Override
    public String toString() {
        return "MyuserRoleVO{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleId='" + roleId + '\'' +
                ", remark='" + remark + '\'' +
                ", permissions=" + permissions +
                ", username='" + username + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public Set<RolePermissionVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<RolePermissionVO> permissions) {
        this.permissions = permissions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public int compareTo(Object o) {
        MyuserRoleVO m= (MyuserRoleVO) o;
        return this.getId()-m.getId();
    }
}
