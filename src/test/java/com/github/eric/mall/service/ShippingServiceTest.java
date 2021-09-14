package com.github.eric.mall.service;

import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.ShippingForm;
import com.github.eric.mall.generate.entity.Shipping;
import com.github.eric.mall.vo.ResponseVo;
import com.github.eric.mall.vo.ShippingVo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ShippingServiceTest extends AbstractUnitTest {

    public static Shipping shipping = buildShipping();
    @Autowired
    ShippingService shippingService;

    @Test
    void addShipping() {
        ShippingForm shippingForm = new ShippingForm("收货人1","18766655522", "18766655544",
                "浙江","杭州","萧山区",
                "详细地址1","邮编1");
        ResponseVo<ShippingVo> responseVo = shippingService.addShipping(shippingForm, 2);
        assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }


    @Test
    void deleteByShippingId() {
        ResponseVo<String> responseVo = shippingService.deleteByShippingId(5, 2);
        assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
        assertEquals(ResponseEnum.DELETE_SHIPPING_SUCCESS.getDesc(),responseVo.getData());
    }

    @Test
    void updateByShippingId() {
        ShippingForm shippingForm = new ShippingForm("收货人1","18766655522", "18766655544",
                "浙江","杭州","萧山区",
                "详细地址1","邮编1");
        ResponseVo<String> responseVo = shippingService.updateByShippingId(shippingForm, 5,2);
        assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
        assertEquals(ResponseEnum.UPDATE_SHIPPING_SUCCESS.getDesc(),responseVo.getData());
    }

    @Test
    void getShippingByIdAndUserId(){
        Shipping shippingInDb = shippingService.getShippingByIdAndUserId(5, 2);

        assertEquals(shipping.getId(),shippingInDb.getId());
        assertEquals(shipping.getUserId(),shippingInDb.getUserId());
        assertEquals(shipping.getReceiverName(),shippingInDb.getReceiverName());
        assertEquals(shipping.getReceiverPhone(),shippingInDb.getReceiverPhone());
        assertEquals(shipping.getReceiverMobile(),shippingInDb.getReceiverMobile());
        assertEquals(shipping.getReceiverProvince(),shippingInDb.getReceiverProvince());
        assertEquals(shipping.getReceiverCity(),shippingInDb.getReceiverCity());
        assertEquals(shipping.getReceiverDistrict(),shippingInDb.getReceiverDistrict());
        assertEquals(shipping.getReceiverAddress(),shippingInDb.getReceiverAddress());
        assertEquals(shipping.getReceiverZip(),shippingInDb.getReceiverZip());

    }

    private static Shipping buildShipping() {
        Shipping shipping=new Shipping();
        shipping.setId(5);
        shipping.setUserId(2);
        shipping.setReceiverName("aaa");
        shipping.setReceiverPhone("039");
        shipping.setReceiverMobile("12453333333");
        shipping.setReceiverProvince("xx省");
        shipping.setReceiverCity("xx市");
        shipping.setReceiverDistrict("xx区");
        shipping.setReceiverAddress("xxx");
        shipping.setReceiverZip("111111");
        return shipping;
    }

    @Test
    void getShippingList() {
        ResponseVo<PageInfo<Shipping>> responseVo = shippingService.getShippingList(1,3,2);
        assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
        PageInfo<Shipping> pageInfo=responseVo.getData();
        List<Shipping> list = pageInfo.getList();

        assertEquals(1,pageInfo.getPageNum());
        assertEquals(3,pageInfo.getPageSize());
        assertEquals(Collections.singletonList(5),list.stream().map(Shipping::getId).collect(Collectors.toList()));
        assertEquals(Collections.singletonList(2),list.stream().map(Shipping::getUserId).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("aaa"),list.stream().map(Shipping::getReceiverName).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("039"),list.stream().map(Shipping::getReceiverPhone).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("12453333333"),list.stream().map(Shipping::getReceiverMobile).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xx省"),list.stream().map(Shipping::getReceiverProvince).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xx市"),list.stream().map(Shipping::getReceiverCity).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xx区"),list.stream().map(Shipping::getReceiverDistrict).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xxx"),list.stream().map(Shipping::getReceiverAddress).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("111111"),list.stream().map(Shipping::getReceiverZip).collect(Collectors.toList()));

    }

    @Test
    void getShippingListByIdsAndUser() {
        List<Shipping> shippingList = shippingService.getShippingListByIdsAndUser(Arrays.asList(5), 2);

        assertEquals(Collections.singletonList(5),shippingList.stream().map(Shipping::getId).collect(Collectors.toList()));
        assertEquals(Collections.singletonList(2),shippingList.stream().map(Shipping::getUserId).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("aaa"),shippingList.stream().map(Shipping::getReceiverName).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("039"),shippingList.stream().map(Shipping::getReceiverPhone).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("12453333333"),shippingList.stream().map(Shipping::getReceiverMobile).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xx省"),shippingList.stream().map(Shipping::getReceiverProvince).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xx市"),shippingList.stream().map(Shipping::getReceiverCity).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xx区"),shippingList.stream().map(Shipping::getReceiverDistrict).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("xxx"),shippingList.stream().map(Shipping::getReceiverAddress).collect(Collectors.toList()));
        assertEquals(Collections.singletonList("111111"),shippingList.stream().map(Shipping::getReceiverZip).collect(Collectors.toList()));

    }
}