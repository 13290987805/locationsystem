package com.tg.locationsystem.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/26
 */
public class ResultBean implements Serializable {
    private Integer code;

    private String msg;

    private List<?> data;

    private  Integer size;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public ResultBean(Integer code, String msg, List<?> data, Integer size) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.size = size;
    }

    public ResultBean() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
