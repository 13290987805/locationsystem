package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/11/21
 */
public class AddMapVO implements Serializable {
    @NotBlank(message = "地图名称不能为空")
    private String mapName;
    private String remark;
    @NotBlank(message = "像素长不能为空")
    private String pixelX;
    @NotBlank(message = "像素宽不能为空")
    private String pixelY;
    @NotBlank(message = "实际长不能为空")
    private String realityX;
    @NotBlank(message = "实际宽不能为空")
    private String realityY;

    public AddMapVO() {
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPixelX() {
        return pixelX;
    }

    public void setPixelX(String pixelX) {
        this.pixelX = pixelX;
    }

    public String getPixelY() {
        return pixelY;
    }

    public void setPixelY(String pixelY) {
        this.pixelY = pixelY;
    }

    public String getRealityX() {
        return realityX;
    }

    public void setRealityX(String realityX) {
        this.realityX = realityX;
    }

    public String getRealityY() {
        return realityY;
    }

    public void setRealityY(String realityY) {
        this.realityY = realityY;
    }
}
