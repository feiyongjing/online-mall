package com.github.eric.mall.vo;

import com.github.eric.mall.generate.entity.Shipping;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private Integer postage;

    private Integer status;

    private Date paymentTime;

    private Date sendTime;

    private Date endTime;

    private Date closeTime;

    private Date createTime;

    private Integer shippingId;

    private Shipping shippingVo;

    private List<OrderItemVo> orderItemVoList;
}
