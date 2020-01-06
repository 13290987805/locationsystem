package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/7/30
 */
public class FrenceVO implements Serializable {

    private Integer id;

    private Integer userId;

    private String polyline;
    @NotBlank(message = "围栏通知号码不能为空")
    private String phone;
    @NotBlank(message = "围栏数据不能为空")
    private String data;
    @NotBlank(message = "围栏类型不能为空")
    private String type;

    private Date reserved2Date;
    @NotBlank(message = "围栏名称不能为空")
    private String name;

    private String mapKey;

    private String mapName;

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
        this.polyline = polyline;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        this.name = name;
    }

    public FrenceVO() {
    }

    public String getSetSwitch() {
        return setSwitch;
    }

    public void setSetSwitch(String setSwitch) {
        this.setSwitch = setSwitch;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
