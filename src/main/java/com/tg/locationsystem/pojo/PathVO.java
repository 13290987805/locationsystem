package com.tg.locationsystem.pojo;

import com.tg.locationsystem.entity.TagHistory;

import java.io.Serializable;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/8/1
 */
public class PathVO implements Serializable {
    private Integer pathCode;

    private String msg;
    private String path;
    private  String startTime;
    private  String endTime;
    private String img;

    private List<TagHistory> tagHistoryList;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getPathCode() {
        return pathCode;
    }

    public void setPathCode(Integer pathCode) {
        this.pathCode = pathCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<TagHistory> getTagHistoryList() {
        return tagHistoryList;
    }

    public void setTagHistoryList(List<TagHistory> tagHistoryList) {
        this.tagHistoryList = tagHistoryList;
    }


    public PathVO() {
    }
}
