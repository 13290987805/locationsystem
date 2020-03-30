package com.tg.locationsystem.entity;

import java.io.Serializable;

public class Role implements Serializable{
    private Integer id;

    private String remark;

    private String roleName;

    private Integer createUserid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getRoleName() {
        return roleName;
    }

    public Integer getCreateUserid() {
        return createUserid;
    }

    public void setCreateUserid(Integer createUserid) {
        this.createUserid = createUserid;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }
}