package com.tg.locationsystem.entity;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

public class Myuser implements Serializable{
    private Integer id;
    @NotBlank(message = "用户名称不能为空")
    private String username;
    @NotBlank(message = "用户公司名称不能为空")
    private String companyName;
    @NotBlank(message = "用户密码不能为空")
    private String password;
    @NotBlank(message = "用户联系方式不能为空")
    private String phonenumber;
    @NotBlank(message = "用户地址不能为空")
    private String address;

    private String createUser;

    private Date reserved2Date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber == null ? null : phonenumber.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getReserved2Date() {
        return reserved2Date;
    }

    public void setReserved2Date(Date reserved2Date) {
        this.reserved2Date = reserved2Date;
    }

    @Override
    public String toString() {
        return "Myuser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", companyName='" + companyName + '\'' +
                ", password='" + password + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", address='" + address + '\'' +
                ", createUser='" + createUser + '\'' +
                ", reserved2Date=" + reserved2Date +
                '}';
    }
}