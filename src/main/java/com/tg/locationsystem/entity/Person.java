package com.tg.locationsystem.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable{
    private Integer id;
    @NotBlank(message = "人员名称不能为空")
    private String personName;
    @NotBlank(message = "人员性别不能为空")
    private String personSex;

    private Integer userId;
    @NotNull(message = "人员类型不能为空")
    private Integer personTypeid;
    @NotBlank(message = "人员身高不能为空")
    private String personHeight;
    @NotBlank(message = "人员唯一标识不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",message = "身份证号码不正确")
    private String idCard;

    private String tagAddress;

    private String img;

    private String personType;

    private Date reserved2Date;
    @NotBlank(message = "人员号码不能为空")
    @Pattern(regexp = "^[1][3,4,5,7,8][0-9]{9}$",message = "手机号码不正确")
    private String personPhone;

    private String seriesNum;
    @NotNull(message = "人员所属组织不能为空")
    private Integer depId;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", personName='" + personName + '\'' +
                ", personSex='" + personSex + '\'' +
                ", userId=" + userId +
                ", personTypeid=" + personTypeid +
                ", personHeight='" + personHeight + '\'' +
                ", idCard='" + idCard + '\'' +
                ", tagAddress='" + tagAddress + '\'' +
                ", img='" + img + '\'' +
                ", personType='" + personType + '\'' +
                ", reserved2Date=" + reserved2Date +
                ", personPhone='" + personPhone + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getSeriesNum() {
        return seriesNum;
    }

    public void setSeriesNum(String seriesNum) {
        this.seriesNum = seriesNum;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName == null ? null : personName.trim();
    }

    public String getPersonSex() {
        return personSex;
    }

    public void setPersonSex(String personSex) {
        this.personSex = personSex == null ? null : personSex.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPersonTypeid() {
        return personTypeid;
    }

    public void setPersonTypeid(Integer personTypeid) {
        this.personTypeid = personTypeid;
    }

    public String getPersonHeight() {
        return personHeight;
    }

    public void setPersonHeight(String personHeight) {
        this.personHeight = personHeight == null ? null : personHeight.trim();
    }

    public Integer getDepId() {
        return depId;
    }

    public void setDepId(Integer depId) {
        this.depId = depId;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress == null ? null : tagAddress.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType == null ? null : personType.trim();
    }

    public Date getReserved2Date() {
        return reserved2Date;
    }

    public void setReserved2Date(Date reserved2Date) {
        this.reserved2Date = reserved2Date;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone == null ? null : personPhone.trim();
    }

}