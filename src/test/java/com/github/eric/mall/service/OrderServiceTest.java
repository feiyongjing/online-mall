package com.github.eric.mall.service;

import com.github.eric.mall.enums.OrderWayEnum;
import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.Order;
import com.github.eric.mall.generate.entity.OrderItem;
import com.github.eric.mall.generate.entity.Shipping;
import com.github.eric.mall.vo.CartProductVo;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.OrderItemVo;
import com.github.eric.mall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OrderServiceTest extends AbstractUnitTest {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @BeforeEach
    public void setData() {
        cartService.addCartProduct(new CartAddForm(26, true), 2);
        CartVo cartVo = cartService.addCartProduct(new CartAddForm(29, true), 2);
    }

    @Test
    void addOrder() {
        OrderVo orderVo = buildOrderVo();

        OrderAddForm orderAddForm=new OrderAddForm();
        orderAddForm.setOrderWay(OrderWayEnum.CART_ORDER.getCode());
        orderAddForm.setShippingId(5);
        OrderVo orderVoInDb = orderService.addOrder(orderAddForm,2);
        verifyOrderVo(orderVo, orderVoInDb);

    }

    private OrderVo buildOrderVo() {
        List<OrderItem> orderItemList= Arrays.asList(OrderItemServiceTest.orderItem_1,OrderItemServiceTest.orderItem_2);
        Order order=orderService.buildOrder(2, OrderService.generateOrderNo(),5,orderItemList);
        Shipping shipping= ShippingServiceTest.shipping;
        return orderService.getOrderVo(order,orderItemList,shipping);
    }

    private void verifyOrderVo(OrderVo orderVo, OrderVo orderVoInDb) {
        assertNotNull(orderVoInDb.getOrderNo());
        assertEquals(orderVo.getPayment().setScale(2, ROUND_HALF_UP),orderVoInDb.getPayment());
        assertEquals(orderVo.getPaymentType(),orderVoInDb.getPaymentType());
        assertEquals(orderVo.getPostage(),orderVoInDb.getPostage());
        assertEquals(orderVo.getStatus(),orderVoInDb.getStatus());
        assertEquals(orderVo.getShippingId(),orderVoInDb.getShippingId());

        assertEquals(orderVo.getShippingVo().getId(),orderVoInDb.getShippingVo().getId());
        assertEquals(orderVo.getShippingVo().getUserId(),orderVoInDb.getShippingVo().getUserId());
        assertEquals(orderVo.getShippingVo().getReceiverName(),orderVoInDb.getShippingVo().getReceiverName());
        assertEquals(orderVo.getShippingVo().getReceiverPhone(),orderVoInDb.getShippingVo().getReceiverPhone());
        assertEquals(orderVo.getShippingVo().getReceiverMobile(),orderVoInDb.getShippingVo().getReceiverMobile());
        assertEquals(orderVo.getShippingVo().getReceiverProvince(),orderVoInDb.getShippingVo().getReceiverProvince());
        assertEquals(orderVo.getShippingVo().getReceiverCity(),orderVoInDb.getShippingVo().getReceiverCity());
        assertEquals(orderVo.getShippingVo().getReceiverDistrict(),orderVoInDb.getShippingVo().getReceiverDistrict());
        assertEquals(orderVo.getShippingVo().getReceiverAddress(),orderVoInDb.getShippingVo().getReceiverAddress());
        assertEquals(orderVo.getShippingVo().getReceiverZip(),orderVoInDb.getShippingVo().getReceiverZip());

        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), OrderItemVo::getProductId);
        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), OrderItemVo::getProductName);
        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), OrderItemVo::getProductImage);
        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), orderItemVo -> orderItemVo.getCurrentUnitPrice().setScale(2, ROUND_HALF_UP));
        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), OrderItemVo::getQuantity);
        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), orderItemVo -> orderItemVo.getTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(orderVo.getOrderItemVoList(),orderVoInDb.getOrderItemVoList(), OrderItemVo::getCreateTime);
    }


    @Test
    void getOrderList() {
        addOrder();
        PageInfo<OrderVo> pageInfo = orderService.getOrderList(2,1,3);
        List<OrderVo> orderVoList = pageInfo.getList();
        assertEquals(1,pageInfo.getPageNum());
        assertEquals(3,pageInfo.getPageSize());
        assertEquals(1,orderVoList.size());
        verifyOrderVo(buildOrderVo(),orderVoList.get(0));

    }

    @Test
    void getOrderByOrderNo() {
//        OrderVo orderByOrderNo = orderService.getOrderByOrderNo();
    }

    @Test
    void deleteOrderByOrderNo() {
//        orderService.deleteOrderByOrderNo();
    }

    @Test
    void updateOrderStatusByOrderNo() {
//        orderService.updateOrderStatusByOrderNo();
    }
}