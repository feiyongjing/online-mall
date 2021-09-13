package com.github.eric.mall.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CartAddForm {
    @NotNull(message = "商品Id不能为空")
    Integer productId;
    @NotEmpty(message = "购物车商品状态不能为空")
    Boolean selected;

    public CartAddForm() {
    }

    public CartAddForm(@NotNull(message = "商品Id不能为空") Integer productId, @NotEmpty(message = "购物车商品状态不能为空") Boolean selected) {
        this.productId = productId;
        this.selected = selected;
    }
}
