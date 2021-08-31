package com.github.eric.mall.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CartAddForm {
    @NotNull(message = "用户名不能为空")
    Integer productId;
    @NotEmpty(message = "用户名不能为空")
    Boolean selected;

}
