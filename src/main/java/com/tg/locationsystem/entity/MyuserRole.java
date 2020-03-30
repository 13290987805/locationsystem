package com.tg.locationsystem.entity;


import java.io.Serializable;
import java.util.Set;

public class MyuserRole implements Serializable{
    private Integer id;

    private Integer userId;

    private String roleId;

    private String remark;

    /**
     * 角色对应权限集合
     */
    private Set<RolePermission> permissions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<RolePermission> permissions) {
        this.permissions = permissions;
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

    @Override
    public String toString() {
        return "MyuserRole{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleId='" + roleId + '\'' +
                ", remark='" + remark + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}