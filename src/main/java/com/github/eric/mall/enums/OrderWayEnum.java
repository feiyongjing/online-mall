package com.github.eric.mall.enums;

import lombok.Getter;

/**
 * code 0是直接下单，1是购物车下单
 */
@Getter
public enum OrderWayEnum {
    DIRECTLY_ORDER(0),
    CART_ORDER(1),
    ;
    Integer code;

    OrderWayEnum(Integer code) {
        this.code = code;
    }
}
