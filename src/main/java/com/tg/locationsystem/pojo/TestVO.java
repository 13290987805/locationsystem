package com.tg.locationsystem.pojo;

import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/11/27
 */
public class TestVO implements Serializable{

    private String address;

    private String map_key;

    private double x;

    private double y;

    private String person_name;

    public TestVO() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getMap_key() {
        return map_key;
    }

    public void setMap_key(String map_key) {
        this.map_key = map_key;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    @Override
    public String toString() {
        return "TestVO{" +
                "address='" + address + '\'' +
                ", map_key='" + map_key + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", person_name='" + person_name + '\'' +
                '}';
    }
}
