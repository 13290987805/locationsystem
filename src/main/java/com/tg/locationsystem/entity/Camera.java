package com.tg.locationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

public class Camera implements Serializable{
    private Integer id;

    private Double x;

    private Double y;

    private Double z;

    /*
    * 品牌
    * */
    @NotBlank(message = "摄像头品牌不能为空")
    private String cameraBrand;

    /*
    型号
    * */
    @NotBlank(message = "摄像头型号不能为空")
    private String cameraModelNumber;

    /*
    * 流媒体地址
    * */
    //@NotBlank(message = "流媒体地址不能为空")
    private String cameraStreamMediaAddress;

    /*
    * 端口号
    * */
    @NotBlank(message = "端口号不能为空")
    private String cameraPort;

    /*
    * ip
    * */
    @NotBlank(message = "摄像头ip不能为空")
    private String cameraIp;

    /*
    * 账号
    * */
    @NotBlank(message = "摄像头账号不能为空")
    private String cameraUsername;

    /*
    * 密码
    * */
    @NotBlank(message = "摄像头密码不能为空")
    private String cameraPwd;

    /*
    * 所属地图
    * */
    @NotBlank(message = "所属地图key不能为空")
    private String mapKey;

    /*
    * 描述
    * */
    private String remark;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public String getCameraBrand() {
        return cameraBrand;
    }

    public void setCameraBrand(String cameraBrand) {
        this.cameraBrand = cameraBrand == null ? null : cameraBrand.trim();
    }

    public String getCameraModelNumber() {
        return cameraModelNumber;
    }

    public void setCameraModelNumber(String cameraModelNumber) {
        this.cameraModelNumber = cameraModelNumber == null ? null : cameraModelNumber.trim();
    }

    public String getCameraStreamMediaAddress() {
        return cameraStreamMediaAddress;
    }

    public void setCameraStreamMediaAddress(String cameraStreamMediaAddress) {
        this.cameraStreamMediaAddress = cameraStreamMediaAddress == null ? null : cameraStreamMediaAddress.trim();
    }

    public String getCameraPort() {
        return cameraPort;
    }

    public void setCameraPort(String cameraPort) {
        this.cameraPort = cameraPort == null ? null : cameraPort.trim();
    }

    public String getCameraIp() {
        return cameraIp;
    }

    public void setCameraIp(String cameraIp) {
        this.cameraIp = cameraIp == null ? null : cameraIp.trim();
    }

    public String getCameraUsername() {
        return cameraUsername;
    }

    public void setCameraUsername(String cameraUsername) {
        this.cameraUsername = cameraUsername == null ? null : cameraUsername.trim();
    }

    public String getCameraPwd() {
        return cameraPwd;
    }

    public void setCameraPwd(String cameraPwd) {
        this.cameraPwd = cameraPwd == null ? null : cameraPwd.trim();
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey == null ? null : mapKey.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}