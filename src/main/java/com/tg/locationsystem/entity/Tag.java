package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class Tag implements Serializable{
    private Integer id;
    @NotBlank(message = "标签序列号不能为空")
    private String address;

    private Double x;

    private Double y;

    private String used;

    private Integer userId;
    @NotNull(message = "标签类型不能为空")
    private Integer tagTypeid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastonline;

    private String isonline;

    private Double z;

    private String electric;

    private String addressBroadcast;

    private String mapKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used == null ? null : used.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTagTypeid() {
        return tagTypeid;
    }

    public void setTagTypeid(Integer tagTypeid) {
        this.tagTypeid = tagTypeid;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getLastonline() {
        return lastonline;
    }

    public void setLastonline(Date lastonline) {
        this.lastonline = lastonline;
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline == null ? null : isonline.trim();
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public String getElectric() {
        return electric;
    }

    public void setElectric(String electric) {
        this.electric = electric == null ? null : electric.trim();
    }

    public String getAddressBroadcast() {
        return addressBroadcast;
    }

    public void setAddressBroadcast(String addressBroadcast) {
        this.addressBroadcast = addressBroadcast == null ? null : addressBroadcast.trim();
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", used='" + used + '\'' +
                ", userId=" + userId +
                ", tagTypeid=" + tagTypeid +
                ", lastonline=" + lastonline +
                ", isonline='" + isonline + '\'' +
                ", z=" + z +
                ", electric='" + electric + '\'' +
                ", addressBroadcast='" + addressBroadcast + '\'' +
                ", mapKey='" + mapKey + '\'' +
                '}';
    }

    public Tag(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Tag() {
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey == null ? null : mapKey.trim();
    }
}