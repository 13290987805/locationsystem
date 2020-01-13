package com.tg.locationsystem.entity;

import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

public class Frence {
    private Integer id;

    private Integer userId;

    private String polyline;
    @NotBlank(message = "围栏通知号码不能为空")
    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$",message = "手机号码不正确")
    private String phone;
    @NotBlank(message = "围栏数据不能为空")
    private String data;
    @NotBlank(message = "围栏类型不能为空")
    private String type;

    private Date reserved2Date;
    @NotBlank(message = "围栏名称不能为空")
    private String name;
    @NotBlank(message = "围栏所属地图不能为空")
    private String mapKey;

    private String setSwitch;

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

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline == null ? null : polyline.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getReserved2Date() {
        return reserved2Date;
    }

    public void setReserved2Date(Date reserved2Date) {
        this.reserved2Date = reserved2Date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey == null ? null : mapKey.trim();
    }

    public String getSetSwitch() {
        return setSwitch;
    }

    @Override
    public String toString() {
        return "Frence{" +
                "id=" + id +
                ", userId=" + userId +
                ", polyline='" + polyline + '\'' +
                ", phone='" + phone + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                ", reserved2Date=" + reserved2Date +
                ", name='" + name + '\'' +
                ", mapKey='" + mapKey + '\'' +
                ", setSwitch='" + setSwitch + '\'' +
                '}';
    }

    public void setSetSwitch(String setSwitch) {
        this.setSwitch = setSwitch;
    }
}

















