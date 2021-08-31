package com.github.eric.mall.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo implements Serializable {
    List<CartProductVo> cartProductVoList;

    Boolean selectedAll;
    BigDecimal cartTotalPrice;
    Integer cartTotalQuantity;

    public CartVo() {
    }

    public CartVo(List<CartProductVo> cartProductVoList, Boolean selectedAll, BigDecimal cartTotalPrice, Integer cartTotalQuantity) {
        this.cartProductVoList = cartProductVoList;
        this.selectedAll = selectedAll;
        this.cartTotalPrice = cartTotalPrice;
        this.cartTotalQuantity = cartTotalQuantity;
    }
}
