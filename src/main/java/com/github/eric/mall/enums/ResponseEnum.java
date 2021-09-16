package com.github.eric.mall.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    ERROR(-1, "服务端错误"),

    SUCCESS(0, "成功"),

    PASSWORD_ERROR(1, "密码错误"),

    USERNAME_EXIST(2, "用户名已存在"),

    PARAM_ERROR(3, "参数错误"),

    EMAIL_EXIST(4, "邮箱已存在"),

    NEED_LOGIN(10, "用户未登录, 请先登录"),

    USERNAME_OR_PASSWORD_ERROR(11, "用户名或密码错误"),

    PRODUCT_OFF_SALE_OR_DELETE(12, "商品下架或删除"),

    PRODUCT_NOT_EXIST(13, "商品不存在"),

    PRODUCT_STOCK_ERROR(14, "库存不正确"),

    CART_PRODUCT_NOT_EXIST(15, "购物车里无此商品"),

    DELETE_SHIPPING_FAIL(16, "删除收货地址失败"),

    SHIPPING_NOT_EXIST(17, "收货地址不存在"),

    CART_SELECTED_IS_EMPTY(18, "请选择商品后下单"),

    ORDER_NOT_EXIST(19, "订单不存在"),

    ORDER_STATUS_ERROR(20, "订单状态有误"),

    PRODUCT_UNDER_STOCK(21, "商品库存不足"),

    DELETE_SHIPPING_SUCCESS(22, "删除收货地址成功"),

    UPDATE_SHIPPING_SUCCESS(23, "修改收货地址成功"),

    UPDATE_SHIPPING_FAIL(24, "修改收货地址失败"),

    INSERT_SHIPPING_FAIL(25, "新建地址失败"),

    USER_REGISTER_FAIL(26, "注册失败"),

    USER_REGISTER_SUCCESS(27, "注册成功"),

    ORDER_PRODUCT_NOT_EXIST(27, "订单商品列表为空"),

    ADD_ORDER_FAIL(28, "下订单失败"),

    ORDER_FOR_PAY_CANCEL_FAIL(29, "此订单已付款，无法被取消"),

    CANCEL_ORDER_FAIL(29, "此订单取消失败"),

    UPDATE_ORDER_STATUS_FAIL(30,"修改订单支付状态失败"),

    ;

    Integer code;

    String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
