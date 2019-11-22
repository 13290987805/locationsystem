package com.tg.locationsystem.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/7/10
 */
public class AllTagLocationResult implements Serializable {
    private Integer code;

    private String msg;

    private List<Location> personlocationList;

    private List<GoodsLocation> goodsLocations;

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

    public List<Location> getPersonlocationList() {
        return personlocationList;
    }

    public void setPersonlocationList(List<Location> personlocationList) {
        this.personlocationList = personlocationList;
    }

    public List<GoodsLocation> getGoodsLocations() {
        return goodsLocations;
    }

    public void setGoodsLocations(List<GoodsLocation> goodsLocations) {
        this.goodsLocations = goodsLocations;
    }

    public AllTagLocationResult() {
    }
}
