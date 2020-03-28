package com.tg.locationsystem.entity;

import java.io.Serializable;

public class RolePermission implements Serializable{
    private Integer id;

    private String roleName;

    private String permission;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }

    public void setPermission(String permission) {
        this.permission = permission == null ? null : permission.trim();
    }
}