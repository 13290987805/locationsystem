package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TagTest implements Serializable{
    private Integer id;

    private Integer x;

    private Integer y;

    private  int[] a;

    private String address;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public int[] getA() {
        return a;
    }

    public void setA(int[] a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "TagTest{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", a=" + Arrays.toString(a) +
                ", address='" + address + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}



























