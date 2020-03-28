package com.tg.locationsystem.entity;


import java.io.Serializable;
import java.util.Set;

public class MyuserRole implements Serializable{
    private Integer id;

    private String username;

    private String roleName;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<RolePermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "MyuserRole{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roleName='" + roleName + '\'' +
                ", permissions=" + permissions +
                '}';
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }
}