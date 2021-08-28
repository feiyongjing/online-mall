package com.github.eric.mall.form;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserForm {

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "用户角色不能为空")
    private Integer role;


}
