package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class AlertSet {
    private Integer id;

    private Integer userId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String sosAlert;

    private String heartAlert;

    private String cutAlert;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSosAlert() {
        return sosAlert;
    }

    public void setSosAlert(String sosAlert) {
        this.sosAlert = sosAlert == null ? null : sosAlert.trim();
    }

    public String getHeartAlert() {
        return heartAlert;
    }

    public void setHeartAlert(String heartAlert) {
        this.heartAlert = heartAlert == null ? null : heartAlert.trim();
    }

    public String getCutAlert() {
        return cutAlert;
    }

    public void setCutAlert(String cutAlert) {
        this.cutAlert = cutAlert == null ? null : cutAlert.trim();
    }
}