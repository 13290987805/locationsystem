package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/9/10
 */
public class MapVO implements Serializable {
    private Integer id;

    private Integer userId;

    private String mapData;
    @NotBlank(message = "地图名称不能为空")
    private String mapName;

    private String mapKey;

    private String remark;

    private String mapdata;

    private String channel;

    private String askTime;

    private String sendTime;

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

    public String getMapData() {
        return mapData;
    }

    public void setMapData(String mapData) {
        this.mapData = mapData;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMapdata() {
        return mapdata;
    }

    public void setMapdata(String mapdata) {
        this.mapdata = mapdata;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public MapVO() {
    }
}
