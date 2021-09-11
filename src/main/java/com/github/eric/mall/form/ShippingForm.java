package com.github.eric.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ShippingForm {
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    @NotBlank(message = "收货人固定电话不能为空")
    private String receiverPhone;
    @NotBlank(message = "收货人移动电话不能为空")
    private String receiverMobile;
    @NotBlank(message = "省份不能为空")
    private String receiverProvince;
    @NotBlank(message = "城市不能为空")
    private String receiverCity;
    @NotBlank(message = "区县不能为空")
    private String receiverDistrict;
    @NotBlank(message = "详细地址不能为空")
    private String receiverAddress;
    @NotBlank(message = "邮编不能为空")
    private String receiverZip;

    public ShippingForm(@NotBlank(message = "收货人姓名不能为空") String receiverName, @NotBlank(message = "收货人固定电话不能为空") String receiverPhone, @NotBlank(message = "收货人移动电话不能为空") String receiverMobile, @NotBlank(message = "省份不能为空") String receiverProvince, @NotBlank(message = "城市不能为空") String receiverCity, @NotBlank(message = "区县不能为空") String receiverDistrict, @NotBlank(message = "详细地址不能为空") String receiverAddress, @NotBlank(message = "邮编不能为空") String receiverZip) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverMobile = receiverMobile;
        this.receiverProvince = receiverProvince;
        this.receiverCity = receiverCity;
        this.receiverDistrict = receiverDistrict;
        this.receiverAddress = receiverAddress;
        this.receiverZip = receiverZip;
    }
}
