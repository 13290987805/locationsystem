package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/11/22
 */
public class CleconfigVO implements Serializable {
    @NotBlank(message = "地图唯一标识不能为空")
    private String mapKey;
    @NotBlank(message = "cle信道不能为空")
    private String channel;
    @NotBlank(message = "问询频率不能为空")
    private String askTime;
    @NotBlank(message = "广播频率不能为空")
    private String sendTime;

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
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

    public CleconfigVO() {
    }
}
