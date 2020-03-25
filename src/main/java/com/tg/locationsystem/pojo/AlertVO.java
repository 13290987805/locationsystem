package com.tg.locationsystem.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/8/9
 */
public class AlertVO implements Serializable {
    private  Integer id;
    private String tagAddress;

    private String data;

    private String alertType;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String addTime;

    private String isdeal;

    private String type;

    private String name;

    private String idCard;

    private double x;

    private double y;

    private String mapKey;



    public AlertVO() {
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getIsdeal() {
        return isdeal;
    }

    public void setIsdeal(String isdeal) {
        this.isdeal = isdeal;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
