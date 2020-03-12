package com.tg.locationsystem.pojo;

import com.tg.locationsystem.entity.PersonType;
import com.tg.locationsystem.entity.Tag;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hyy
 * @ Date2019/6/28
 */
public class Location implements Serializable {

    private Integer id;
    @NotBlank(message = "人员名称不能为空")
    private String personName;
    @NotBlank(message = "人员性别不能为空")
    private String personSex;

    private Integer userId;
    @NotBlank(message = "人员类型不能为空")
    private PersonType personType;
    @NotBlank(message = "人员身高不能为空")
    private String personHeight;
    @NotBlank(message = "人员唯一标识不能为空")
    private String idCard;

    private String tagAddress;

    private String img;

    private Date reserved2Date;
    @NotBlank(message = "人员号码不能为空")
    private String personPhone;

    private Tag tag;

    public Location() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonSex() {
        return personSex;
    }

    public void setPersonSex(String personSex) {
        this.personSex = personSex;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPersonHeight() {
        return personHeight;
    }

    public void setPersonHeight(String personHeight) {
        this.personHeight = personHeight;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
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
        this.personPhone = personPhone;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
