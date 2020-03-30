package com.tg.locationsystem.entity;



import com.tg.locationsystem.pojo.MyuserRoleVO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Myuser implements Serializable{
    private Integer id;
    @NotBlank(message = "用户名称不能为空")
    private String username;
    @NotBlank(message = "用户公司名称不能为空")
    private String companyName;
    //@NotBlank(message = "用户密码不能为空")
    private String password;
    @NotBlank(message = "用户联系方式不能为空")
    private String phonenumber;
    @NotBlank(message = "用户地址不能为空")
    private String address;

    private String createUser;

    private Date reserved2Date;

    private String systemName;

    private String logo;
    @Pattern(regexp = "^[0-9a-z]+\\w*@([0-9a-z]+\\.)+[0-9a-z]+$",message = "邮箱格式不正确")
    private String mail;

    private Integer parentId;

    /**
     * 用户对应的角色集合
     */
    private Set<MyuserRoleVO> roles;

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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getParentId() {
        return parentId;
    }

    public Set<MyuserRoleVO> getRoles() {
        return roles;
    }

    public void setRoles(Set<MyuserRoleVO> roles) {
        this.roles = roles;
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
                ", systemName='" + systemName + '\'' +
                ", logo='" + logo + '\'' +
                ", mail='" + mail + '\'' +
                ", parentId=" + parentId +
                ", roles=" + roles +
                '}';
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}










