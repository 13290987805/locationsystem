package com.tg.locationsystem.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class Station implements Serializable{
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
        this.addr = addr == null ? null : addr.trim();
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
        this.masterLagDelay = masterLagDelay == null ? null : masterLagDelay.trim();
    }

    public String getAntDelayRx() {
        return antDelayRx;
    }

    public void setAntDelayRx(String antDelayRx) {
        this.antDelayRx = antDelayRx == null ? null : antDelayRx.trim();
    }

    public String getAntDelayTx() {
        return antDelayTx;
    }

    public void setAntDelayTx(String antDelayTx) {
        this.antDelayTx = antDelayTx == null ? null : antDelayTx.trim();
    }

    public String getIsmaster() {
        return ismaster;
    }

    public void setIsmaster(String ismaster) {
        this.ismaster = ismaster == null ? null : ismaster.trim();
    }

    public String getMasterAddr() {
        return masterAddr;
    }

    public void setMasterAddr(String masterAddr) {
        this.masterAddr = masterAddr == null ? null : masterAddr.trim();
    }

    public String getMasteranchoraddress() {
        return masteranchoraddress;
    }

    public void setMasteranchoraddress(String masteranchoraddress) {
        this.masteranchoraddress = masteranchoraddress == null ? null : masteranchoraddress.trim();
    }

    public String getMasteranchorAddress() {
        return masteranchorAddress;
    }

    public void setMasteranchorAddress(String masteranchorAddress) {
        this.masteranchorAddress = masteranchorAddress == null ? null : masteranchorAddress.trim();
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
        this.dimension = dimension == null ? null : dimension.trim();
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
        this.rfdistance = rfdistance == null ? null : rfdistance.trim();
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", sid=" + sid +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", addr='" + addr + '\'' +
                ", reserved2Date=" + reserved2Date +
                ", userId=" + userId +
                ", masterLagDelay='" + masterLagDelay + '\'' +
                ", antDelayRx='" + antDelayRx + '\'' +
                ", antDelayTx='" + antDelayTx + '\'' +
                ", ismaster='" + ismaster + '\'' +
                ", masterAddr='" + masterAddr + '\'' +
                ", masteranchoraddress='" + masteranchoraddress + '\'' +
                ", masteranchorAddress='" + masteranchorAddress + '\'' +
                ", stationStatus=" + stationStatus +
                ", dimension='" + dimension + '\'' +
                ", mapId=" + mapId +
                ", rfdistance='" + rfdistance + '\'' +
                '}';
    }
}