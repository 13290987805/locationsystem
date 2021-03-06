package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/9/17
 */
public class PathMap implements Serializable {
    @NotBlank(message = "人员唯一标识不能为空")
    private String personidcard;
    @NotBlank(message = "起始时间不能为空")
    private String startTime;
    @NotBlank(message = "终止时间不能为空")
    private String endTime;
    @NotBlank(message = "地图唯一标识不能为空")
    private String mapkey;
    public PathMap() {
    }

    public String getPersonidcard() {
        return personidcard;
    }

    public void setPersonidcard(String personidcard) {
        this.personidcard = personidcard;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String mapkey) {
        this.mapkey = mapkey;
    }
}
