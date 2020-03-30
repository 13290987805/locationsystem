package com.tg.locationsystem.entity;

import java.io.Serializable;

public class RolePermission implements Serializable{
    private Integer id;

    private String roleId;

    private String permissionId;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", roleId='" + roleId + '\'' +
                ", permissionId='" + permissionId + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}