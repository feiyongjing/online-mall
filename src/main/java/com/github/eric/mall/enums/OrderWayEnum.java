package com.github.eric.mall.enums;

import lombok.Getter;

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
