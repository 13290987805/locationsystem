package com.tg.locationsystem.pojo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/9/23
 */
public class StationJson implements Serializable {
    private Integer id;

    private Integer sid;
    @NotNull(message = "基站x坐标")
    private Float x;
    @NotNull(message = "基站y坐标")
    private Float y;
    @NotNull(message = "基站z坐标")
    private Float z;
    @NotBlank(message = "基站唯一标识")
    private String addr;

    private Date reserved2Date;

    private Integer userId;

    private String masterLagDelay;

    private String antDelayRx;

    private String antDelayTx;

    private String ismaster;

    private String masterAddr;

    private String masteranchoraddress;

    private String masteranchorAddress;

    private Integer stationStatus;

    private String dimension;

    private Integer mapId;

    private String rfdistance;

    private String mapName;

    private Integer LAY_TABLE_INDEX;

    public StationJson() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Date getReserved2Date() {
        return reserved2Date;
    }

    public void setReserved2Date(Date reserved2Date) {
        this.reserved2Date = reserved2Date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMasterLagDelay() {
        return masterLagDelay;
    }

    public void setMasterLagDelay(String masterLagDelay) {
        this.masterLagDelay = masterLagDelay;
    }

    public String getAntDelayRx() {
        return antDelayRx;
    }

    public void setAntDelayRx(String antDelayRx) {
        this.antDelayRx = antDelayRx;
    }

    public String getAntDelayTx() {
        return antDelayTx;
    }

    public void setAntDelayTx(String antDelayTx) {
        this.antDelayTx = antDelayTx;
    }

    public String getIsmaster() {
        return ismaster;
    }

    public void setIsmaster(String ismaster) {
        this.ismaster = ismaster;
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr;
    }

    public String getMasteranchoraddress() {
        return masteranchoraddress;
    }

    public void setMasteranchoraddress(String masteranchoraddress) {
        this.masteranchoraddress = masteranchoraddress;
    }

    public String getMasteranchorAddress() {
        return masteranchorAddress;
    }

    public void setMasteranchorAddress(String masteranchorAddress) {
        this.masteranchorAddress = masteranchorAddress;
    }

    public Integer getStationStatus() {
        return stationStatus;
    }

    public void setStationStatus(Integer stationStatus) {
        this.stationStatus = stationStatus;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }

    public String getRfdistance() {
        return rfdistance;
    }

    public void setRfdistance(String rfdistance) {
        this.rfdistance = rfdistance;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getLAY_TABLE_INDEX() {
        return LAY_TABLE_INDEX;
    }

    public void setLAY_TABLE_INDEX(Integer LAY_TABLE_INDEX) {
        this.LAY_TABLE_INDEX = LAY_TABLE_INDEX;
    }
}
