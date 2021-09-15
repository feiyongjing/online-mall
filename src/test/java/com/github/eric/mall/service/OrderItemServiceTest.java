package com.github.eric.mall.service;

import com.github.eric.mall.generate.entity.OrderItem;
import com.github.eric.mall.vo.CartProductVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OrderItemServiceTest extends AbstractUnitTest {
    public static final Long orderNo_1= OrderService.generateOrderNo();
    public static final Long orderNo_2= OrderService.generateOrderNo();
    public static final OrderItem orderItem_1 = buildOrderItem(2,orderNo_1,26,"Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机","http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg",BigDecimal.valueOf(6999.00),1,BigDecimal.valueOf(6999.00));
    public static final OrderItem orderItem_2= buildOrderItem(2,orderNo_2,29,"Haier/海尔HJ100-1HU1 10公斤滚筒洗衣机全自动带烘干家用大容量 洗烘一体","http://img.springboot.cn/173335a4-5dce-4afd-9f18-a10623724c4e.jpeg",BigDecimal.valueOf(4299.00),1,BigDecimal.valueOf(4299.00));

    @Autowired
    OrderItemService orderItemService;

    @Test
    void getOrderItemListByOrderNos() {
        List<OrderItem> orderItemList =Arrays.asList(orderItem_1,orderItem_2);
        List<OrderItem> orderItemListInDb = orderItemService.getOrderItemListByOrderNos(Arrays.asList(orderNo_1,orderNo_2));
        assertEquals(2,orderItemList.size());
        verifyData(orderItemList,orderItemListInDb,OrderItem::getUserId);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getOrderNo);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getProductId);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getProductName);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getProductImage);
        verifyData(orderItemList,orderItemListInDb, orderItem -> orderItem.getCurrentUnitPrice().setScale(2, ROUND_HALF_UP));
        verifyData(orderItemList,orderItemListInDb,OrderItem::getQuantity);
        verifyData(orderItemList,orderItemListInDb, orderItem -> orderItem.getTotalPrice().setScale(2, ROUND_HALF_UP));

    }

    @BeforeEach
    public void insertOrderItemList() {
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem_1);
        orderItemList.add(orderItem_2);
        int row = orderItemService.insertOrderItemList(orderItemList);
        assertEquals(2, row);
    }

    public static OrderItem buildOrderItem(Integer userId, Long orderNo, Integer productId, String productName, String productImage, BigDecimal currentUnitPrice, Integer quantity, BigDecimal totalPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(productId);
        orderItem.setProductName(productName);
        orderItem.setProductImage(productImage);
        orderItem.setCurrentUnitPrice(currentUnitPrice);
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(totalPrice);
        return orderItem;
    }


    @Test
    void getOrderItemListByOrderNo() {
        List<OrderItem> orderItemList = Collections.singletonList(orderItem_1);
        List<OrderItem> orderItemListInDb = orderItemService.getOrderItemListByOrderNo(orderNo_1);
        assertEquals(1,orderItemList.size());
        verifyData(orderItemList,orderItemListInDb,OrderItem::getUserId);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getOrderNo);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getProductId);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getProductName);
        verifyData(orderItemList,orderItemListInDb,OrderItem::getProductImage);
        verifyData(orderItemList,orderItemListInDb, orderItem -> orderItem.getCurrentUnitPrice().setScale(2, ROUND_HALF_UP));
        verifyData(orderItemList,orderItemListInDb,OrderItem::getQuantity);
        verifyData(orderItemList,orderItemListInDb, orderItem -> orderItem.getTotalPrice().setScale(2, ROUND_HALF_UP));
    }
}