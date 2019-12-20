package com.tg.locationsystem.pojo;



import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hyy
 * @ Date2019/7/5
 */
public class UpdatePassword implements Serializable {
    @NotBlank(message = "旧号码不能为空")
    private String oldPassword;
    @NotBlank(message = "新号码不能为空")
    private String newPassword;

    public UpdatePassword() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public UpdatePassword(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
