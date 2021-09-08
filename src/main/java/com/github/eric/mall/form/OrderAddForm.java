package com.github.eric.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class OrderAddForm {
    @NotNull(message = "下单方式不能是空")
    private Integer orderWay;
    @NotNull(message = "地址Id不能是空")
    private Integer shippingId;

    private Map<Integer,Integer> productIdAndNumberMap;
}
