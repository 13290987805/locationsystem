package com.tg.locationsystem.pojo;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2020/3/24
 */
public class DepVo implements Serializable {

    private String name;

    private int id;

    private int pid;

    public DepVo() {
    }

    public DepVo(String name, int id, int pid) {
        this.name = name;
        this.id = id;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
