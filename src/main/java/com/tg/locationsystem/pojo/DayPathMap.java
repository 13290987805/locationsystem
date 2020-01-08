package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class DayPathMap implements Serializable {
    @NotBlank(message = "人员唯一标识不能为空")
    private String personidcard;
    @NotBlank(message = "日期不能为空")
    private String date;
    @NotBlank(message = "地图唯一标识不能为空")
    private String mapkey;

    public DayPathMap() {
    }

    public String getPersonidcard() {
        return personidcard;
    }

    public void setPersonidcard(String personidcard) {
        this.personidcard = personidcard;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMapkey() {
        return mapkey;
    }

    public void setMapkey(String mapkey) {
        this.mapkey = mapkey;
    }
}
