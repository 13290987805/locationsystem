package com.tg.locationsystem.pojo;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2020/3/30
 */
public class RolePermissionVO  implements java.lang.Comparable{
    private Integer id;

    private String roleId;

    private String permissionId;

    private String remark;

    private String permissionName;

    public RolePermissionVO() {
    }


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

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return "RolePermissionVO{" +
                "id=" + id +
                ", roleId='" + roleId + '\'' +
                ", permissionId='" + permissionId + '\'' +
                ", remark='" + remark + '\'' +
                ", permissionName='" + permissionName + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        RolePermissionVO r= (RolePermissionVO) o;
        return this.getId()-((RolePermissionVO) o).getId();
    }
}
