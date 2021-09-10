package com.github.eric.mall.form;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserForm {

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotNull(message = "用户角色不能为空")
    private Integer role;

    public UserForm(@NotEmpty(message = "用户名不能为空") String username, @NotBlank(message = "密码不能为空") String password, @NotBlank(message = "邮箱不能为空") String email, @NotNull(message = "用户角色不能为空") Integer role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
