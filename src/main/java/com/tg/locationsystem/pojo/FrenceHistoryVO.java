package com.tg.locationsystem.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/7/4
 */
public class FrenceHistoryVO implements Serializable {
    private Integer id;

    private String personIdcard;

    private Double x;

    private Double y;

    private String status;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private String reserved1String;

    private Integer userId;

    private Integer frenceId;

    private String frenceName;
    private String tagName;

    private String data;

    private String img;

    private String mapkey;

    private String isDeal;

    public String getImg() {
        return img;
    }

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String mapkey) {
        this.mapkey = mapkey;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlert_type() {
        return alert_type;
    }

    public void setAlert_type(String alert_type) {
        this.alert_type = alert_type;
    }

    private String alert_type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonIdcard() {
        return personIdcard;
    }

    public void setPersonIdcard(String personIdcard) {
        this.personIdcard = personIdcard;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getReserved1String() {
        return reserved1String;
    }

    public void setReserved1String(String reserved1String) {
        this.reserved1String = reserved1String;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFrenceId() {
        return frenceId;
    }

    public void setFrenceId(Integer frenceId) {
        this.frenceId = frenceId;
    }

    public String getFrenceName() {
        return frenceName;
    }

    public void setFrenceName(String frenceName) {
        this.frenceName = frenceName;
    }

    public String getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(String isDeal) {
        this.isDeal = isDeal;
    }

    public FrenceHistoryVO() {
    }

}
