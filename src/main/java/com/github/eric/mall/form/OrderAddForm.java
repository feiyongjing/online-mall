package com.github.eric.mall.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class OrderAddForm {
    @NotNull(message = "下单方式不能是空")
    private Integer orderWay;
    @NotNull(message = "地址Id不能是空")
    private Integer shippingId;
    @NotEmpty(message = "商品Id列表不能是空")
    private Map<Integer,Integer> productIdAndNumber;
}
