package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

public class PersonType implements Serializable{
    private Integer id;

    private Integer userId;
    @NotBlank(message = "人员类型名称不能为空")
    private String typeName;

    private String logo;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String reservde1String;

    private Integer reserved2Int;

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo == null ? null : logo.trim();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReservde1String() {
        return reservde1String;
    }

    public void setReservde1String(String reservde1String) {
        this.reservde1String = reservde1String == null ? null : reservde1String.trim();
    }

    @Override
    public String toString() {
        return "PersonType{" +
                "id=" + id +
                ", userId=" + userId +
                ", typeName='" + typeName + '\'' +
                ", logo='" + logo + '\'' +
                ", createTime=" + createTime +
                ", reservde1String='" + reservde1String + '\'' +
                ", reserved2Int=" + reserved2Int +
                '}';
    }

    public Integer getReserved2Int() {
        return reserved2Int;
    }

    public void setReserved2Int(Integer reserved2Int) {
        this.reserved2Int = reserved2Int;
    }
}