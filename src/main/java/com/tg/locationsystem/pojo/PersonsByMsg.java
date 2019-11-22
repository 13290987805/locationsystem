package com.tg.locationsystem.pojo;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/7/20
 */
public class PersonsByMsg implements Serializable {
     private String msg;

    public PersonsByMsg() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
