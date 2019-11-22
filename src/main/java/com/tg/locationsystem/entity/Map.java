package com.tg.locationsystem.entity;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class Map implements Serializable{
    private Integer id;

    private Integer userId;

    private String mapData;
    @NotBlank(message = "地图名称不能为空")
    private String mapName;

    private String mapKey;

    private String remark;
    @NotBlank(message = "像素长不能为空")
    private String pixelX;
    @NotBlank(message = "像素宽不能为空")
    private String pixelY;
    @NotBlank(message = "实际长不能为空")
    private String realityX;
    @NotBlank(message = "实际宽不能为空")
    private String realityY;

    private String proportion;

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
        this.mapData = mapData == null ? null : mapData.trim();
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName == null ? null : mapName.trim();
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey == null ? null : mapKey.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getPixelX() {
        return pixelX;
    }

    public void setPixelX(String pixelX) {
        this.pixelX = pixelX == null ? null : pixelX.trim();
    }

    public String getPixelY() {
        return pixelY;
    }

    public void setPixelY(String pixelY) {
        this.pixelY = pixelY == null ? null : pixelY.trim();
    }

    public String getRealityX() {
        return realityX;
    }

    public void setRealityX(String realityX) {
        this.realityX = realityX == null ? null : realityX.trim();
    }

    public String getRealityY() {
        return realityY;
    }

    public void setRealityY(String realityY) {
        this.realityY = realityY == null ? null : realityY.trim();
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion == null ? null : proportion.trim();
    }
}