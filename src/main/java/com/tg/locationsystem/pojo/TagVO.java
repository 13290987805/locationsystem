package com.tg.locationsystem.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/7/16
 */
public class TagVO implements Serializable {
    private Integer id;

    private String address;

    private double x;

    private double y;

    private String used;

    private String tagTypename;

    private String username;

    private Integer userId;

    private Double z;

    private  String type;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastonline;

    private String isonline;
    private String electric;
    private String addressBroadcast;

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
        this.electric = electric;
    }

    public String getAddressBroadcast() {
        return addressBroadcast;
    }

    public void setAddressBroadcast(String addressBroadcast) {
        this.addressBroadcast = addressBroadcast;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public TagVO() {
    }

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
        this.address = address;
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

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getTagTypename() {
        return tagTypename;
    }

    public void setTagTypename(String tagTypename) {
        this.tagTypename = tagTypename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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
        this.isonline = isonline;
    }


}
