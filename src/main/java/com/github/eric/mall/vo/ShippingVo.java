package com.github.eric.mall.vo;

import lombok.Data;

@Data
public class ShippingVo {
    private Integer shippingId;

    public ShippingVo() {
    }

    public ShippingVo(Integer shippingId) {
        this.shippingId = shippingId;
    }
}
