package com.tg.locationsystem.pojo;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/7/16
 */
public class QueryTagCondition implements Serializable {
    private String address;

    public QueryTagCondition(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public QueryTagCondition() {
    }
}
