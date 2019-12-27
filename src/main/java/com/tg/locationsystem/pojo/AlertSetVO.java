package com.tg.locationsystem.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/12/20
 */
public class AlertSetVO implements Serializable {

    private String sosAlert;

    private String heartAlert;

    private String cutAlert;

    private String batteryAlert;

    @NotNull(message = "最大心率不能为空")
    private Integer heart_maxData;
    @Min(value = 0,message = "最小值不能小于0")
    @NotNull(message = "最小心率不能为空")
    private Integer heart_minData;
    private Integer timeInterval;
    @NotNull(message = "请打开开关")
    private String EleCallSwitch;

    public AlertSetVO() {
    }


    public String getSosAlert() {
        return sosAlert;
    }

    public void setSosAlert(String sosAlert) {
        this.sosAlert = sosAlert;
    }

    public String getHeartAlert() {
        return heartAlert;
    }

    public void setHeartAlert(String heartAlert) {
        this.heartAlert = heartAlert;
    }

    public String getCutAlert() {
        return cutAlert;
    }

    public void setCutAlert(String cutAlert) {
        this.cutAlert = cutAlert;
    }

    public String getBatteryAlert() {
        return batteryAlert;
    }

    public void setBatteryAlert(String batteryAlert) {
        this.batteryAlert = batteryAlert;
    }


    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Integer getHeart_maxData() {
        return heart_maxData;
    }

    public void setHeart_maxData(Integer heart_maxData) {
        this.heart_maxData = heart_maxData;
    }

    public Integer getHeart_minData() {
        return heart_minData;
    }

    public void setHeart_minData(Integer heart_minData) {
        this.heart_minData = heart_minData;
    }

    public String getEleCallSwitch() {
        return EleCallSwitch;
    }

    public void setEleCallSwitch(String eleCallSwitch) {
        EleCallSwitch = eleCallSwitch;
    }
}
