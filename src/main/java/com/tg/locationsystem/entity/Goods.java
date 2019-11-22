package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class Goods implements Serializable{
    private Integer id;
    @NotBlank(message = "物品名称不能为空")
    private String goodsName;

    private Integer userId;
    @NotNull(message = "物品类型不能为空")
    private Integer goodsTypeid;

    private String tagAddress;

    private String img;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;
    @NotBlank(message = "物品唯一标识不能为空")
    private String goodsIdcard;

    private String reserved2String;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoodsTypeid() {
        return goodsTypeid;
    }

    public void setGoodsTypeid(Integer goodsTypeid) {
        this.goodsTypeid = goodsTypeid;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress == null ? null : tagAddress.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getGoodsIdcard() {
        return goodsIdcard;
    }

    public void setGoodsIdcard(String goodsIdcard) {
        this.goodsIdcard = goodsIdcard == null ? null : goodsIdcard.trim();
    }

    public String getReserved2String() {
        return reserved2String;
    }

    public void setReserved2String(String reserved2String) {
        this.reserved2String = reserved2String == null ? null : reserved2String.trim();
    }
}