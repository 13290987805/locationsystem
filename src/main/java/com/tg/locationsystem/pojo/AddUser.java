package com.tg.locationsystem.pojo;



import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hyy
 * @ Date2020/3/30
 */
public class AddUser {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotNull(message="角色不能为空")
    private String roleIds;

    public AddUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }
}
