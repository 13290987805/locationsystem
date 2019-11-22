package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/7/3
 */
public class Path implements Serializable {
    @NotBlank(message = "人员唯一标识不能为空")
    private String personidcard;
    @NotBlank(message = "起始时间不能为空")
    private String startTime;
    @NotBlank(message = "终止时间不能为空")
    private String endTime;


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

    public String getPersonidcard() {
        return personidcard;
    }

    public void setPersonidcard(String personidcard) {
        this.personidcard = personidcard;
    }

    public Path() {
    }
}
