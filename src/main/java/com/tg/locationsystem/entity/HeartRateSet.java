package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class HeartRateSet implements Serializable{
    private Integer id;
    @NotNull(message = "最大心率不能为空")
    private Integer maxData;
    @Min(value = 0,message = "最小值不能小于0")
    @NotNull(message = "最小心率不能为空")
    private Integer minData;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMaxData() {
        return maxData;
    }

    public void setMaxData(Integer maxData) {
        this.maxData = maxData;
    }

    public Integer getMinData() {
        return minData;
    }

    public void setMinData(Integer minData) {
        this.minData = minData;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public String toString() {
        return "HeartRateSet{" +
                "id=" + id +
                ", maxData=" + maxData +
                ", minData=" + minData +
                ", updateTime=" + updateTime +
                ", userId=" + userId +
                '}';
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}