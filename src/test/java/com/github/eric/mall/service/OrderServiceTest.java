package com.github.eric.mall.service;

import com.github.eric.mall.enums.OrderWayEnum;
import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.exception.ResultException;
import com.github.eric.mall.form.CartAddForm;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.*;
import com.github.eric.mall.vo.CartProductVo;
import com.github.eric.mall.vo.CartVo;
import com.github.eric.mall.vo.OrderItemVo;
import com.github.eric.mall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OrderServiceTest extends AbstractUnitTest {

    public static final Long ORDER_NO = 777L;
    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    RedisTemplate redisTemplate;

    @BeforeEach
    public void setData() {
        cartService.addCartProduct(new CartAddForm(26, true), 2);
        CartVo cartVo = cartService.addCartProduct(new CartAddForm(29, true), 2);
    }

    @AfterEach
    void clearRedisData() {
        HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
        redisTemplate.delete(String.format(CartService.CART_REDIS_KEY_TEMPLATE, 2));
    }

    @Test
    void addOrder() {
        // 通过购物车下订单
        OrderVo orderVo_1 = buildOrderVo(OrderItemServiceTest.orderItem_1, OrderItemServiceTest.orderItem_2);

        OrderAddForm orderAddForm_1 = new OrderAddForm();
        orderAddForm_1.setOrderWay(OrderWayEnum.CART_ORDER.getCode());
        orderAddForm_1.setShippingId(5);
        OrderVo orderVoInDb = orderService.addOrder(orderAddForm_1, 2);
        verifyOrderVo(orderVo_1, orderVoInDb);

        // 直接下订单
        OrderItem orderItem_1 = OrderItemServiceTest.buildOrderItem(2, OrderService.generateOrderNo(), 28, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", BigDecimal.valueOf(1999.00), 1, BigDecimal.valueOf(1999.00));
        OrderItem orderItem_2 = OrderItemServiceTest.buildOrderItem(2, OrderService.generateOrderNo(), 29, "Haier/海尔HJ100-1HU1 10公斤滚筒洗衣机全自动带烘干家用大容量 洗烘一体", "http://img.springboot.cn/173335a4-5dce-4afd-9f18-a10623724c4e.jpeg", BigDecimal.valueOf(4299.00), 1, BigDecimal.valueOf(4299.00));
        OrderVo orderVo_2 = buildOrderVo(orderItem_1, orderItem_2);

        OrderAddForm orderAddForm_2 = new OrderAddForm();
        Map<Integer, Integer> productIdAndNumberMap = new HashMap<>();
        productIdAndNumberMap.put(28, 1);
        productIdAndNumberMap.put(29, 1);
        orderAddForm_2.setOrderWay(OrderWayEnum.DIRECTLY_ORDER.getCode());
        orderAddForm_2.setShippingId(5);
        orderAddForm_2.setProductIdAndNumberMap(productIdAndNumberMap);
        OrderVo orderVoInDb_2 = orderService.addOrder(orderAddForm_2, 2);
        verifyOrderVo(orderVo_2, orderVoInDb_2);

    }

    private OrderVo buildOrderVo(OrderItem orderItem_1, OrderItem orderItem_2) {
        List<OrderItem> orderItemList = Arrays.asList(orderItem_1, orderItem_2);
        Order order = orderService.buildOrder(2, OrderService.generateOrderNo(), 5, orderItemList);
        Shipping shipping = ShippingServiceTest.shipping;
        return orderService.getOrderVo(order, orderItemList, shipping);
    }

    private void verifyOrderVo(OrderVo orderVo, OrderVo orderVoInDb) {
        assertNotNull(orderVoInDb.getOrderNo());
        assertEquals(orderVo.getPayment().setScale(2, ROUND_HALF_UP), orderVoInDb.getPayment());
        assertEquals(orderVo.getPaymentType(), orderVoInDb.getPaymentType());
        assertEquals(orderVo.getPostage(), orderVoInDb.getPostage());
        assertEquals(orderVo.getStatus(), orderVoInDb.getStatus());
        assertEquals(orderVo.getShippingId(), orderVoInDb.getShippingId());

        assertEquals(orderVo.getShippingVo().getId(), orderVoInDb.getShippingVo().getId());
        assertEquals(orderVo.getShippingVo().getUserId(), orderVoInDb.getShippingVo().getUserId());
        assertEquals(orderVo.getShippingVo().getReceiverName(), orderVoInDb.getShippingVo().getReceiverName());
        assertEquals(orderVo.getShippingVo().getReceiverPhone(), orderVoInDb.getShippingVo().getReceiverPhone());
        assertEquals(orderVo.getShippingVo().getReceiverMobile(), orderVoInDb.getShippingVo().getReceiverMobile());
        assertEquals(orderVo.getShippingVo().getReceiverProvince(), orderVoInDb.getShippingVo().getReceiverProvince());
        assertEquals(orderVo.getShippingVo().getReceiverCity(), orderVoInDb.getShippingVo().getReceiverCity());
        assertEquals(orderVo.getShippingVo().getReceiverDistrict(), orderVoInDb.getShippingVo().getReceiverDistrict());
        assertEquals(orderVo.getShippingVo().getReceiverAddress(), orderVoInDb.getShippingVo().getReceiverAddress());
        assertEquals(orderVo.getShippingVo().getReceiverZip(), orderVoInDb.getShippingVo().getReceiverZip());

        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), OrderItemVo::getProductId);
        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), OrderItemVo::getProductName);
        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), OrderItemVo::getProductImage);
        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), orderItemVo -> orderItemVo.getCurrentUnitPrice().setScale(2, ROUND_HALF_UP));
        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), OrderItemVo::getQuantity);
        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), orderItemVo -> orderItemVo.getTotalPrice().setScale(2, ROUND_HALF_UP));
        verifyData(orderVo.getOrderItemVoList(), orderVoInDb.getOrderItemVoList(), OrderItemVo::getCreateTime);
    }


    @Test
    void getOrderList() {
        OrderItem orderItem_1 = OrderItemServiceTest.buildOrderItem(2, ORDER_NO, 26, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", BigDecimal.valueOf(6999.00), 1, BigDecimal.valueOf(6999.00));
        OrderItem orderItem_2 = OrderItemServiceTest.buildOrderItem(2, ORDER_NO, 27, "Midea/美的 BCD-535WKZM(E)冰箱双开门对开门风冷无霜智能电家用", "http://img.springboot.cn/ac3e571d-13ce-4fad-89e8-c92c2eccf536.jpeg", BigDecimal.valueOf(3299.00), 1, BigDecimal.valueOf(3299.00));
        OrderVo orderVo_1 = buildOrderVo(orderItem_1, orderItem_2);

        PageInfo<OrderVo> pageInfo = orderService.getOrderList(2, 1, 3);
        List<OrderVo> orderVoList = pageInfo.getList();
        assertEquals(1, pageInfo.getPageNum());
        assertEquals(3, pageInfo.getPageSize());
        assertEquals(1, orderVoList.size());
        verifyOrderVo(orderVo_1, orderVoList.get(0));

    }

    @Test
    void getOrderByOrderNo() {
        OrderItem orderItem_1 = OrderItemServiceTest.buildOrderItem(2, ORDER_NO, 26, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", BigDecimal.valueOf(6999.00), 1, BigDecimal.valueOf(6999.00));
        OrderItem orderItem_2 = OrderItemServiceTest.buildOrderItem(2, ORDER_NO, 27, "Midea/美的 BCD-535WKZM(E)冰箱双开门对开门风冷无霜智能电家用", "http://img.springboot.cn/ac3e571d-13ce-4fad-89e8-c92c2eccf536.jpeg", BigDecimal.valueOf(3299.00), 1, BigDecimal.valueOf(3299.00));
        OrderVo orderVo_1 = buildOrderVo(orderItem_1, orderItem_2);
        OrderVo orderVoInDb = orderService.getOrderByOrderNo(ORDER_NO, 2);
        verifyOrderVo(orderVo_1, orderVoInDb);

    }

    @Test
    public void checkProductInfo() {
        Map<Integer, Product> productIdAndProductMap = new HashMap<>();
        Product product_1 = ProductServiceTest.buildProduct(26, 100002, "Apple iPhone 7 Plus (A1661) 128G 玫瑰金色 移动联通电信4G手机", "iPhone 7，现更以红色呈现。", "http://img.springboot.cn/241997c4-9e62-4824-b7f0-7425c3c28917.jpeg", "241997c4-9e62-4824-b7f0-7425c3c28917.jpeg,b6c56eb0-1748-49a9-98dc-bcc4b9788a54.jpeg,92f17532-1527-4563-aa1d-ed01baa0f7b2.jpeg,3adbe4f7-e374-4533-aa79-cc4a98c529bf.jpeg", "<p><img alt=\"10000.jpg\" src=\"http://img.springboot.cn/00bce8d4-e9af-4c8d-b205-e6c75c7e252b.jpg\" width=\"790\" height=\"553\"><br></p><p><img alt=\"20000.jpg\" src=\"http://img.springboot.cn/4a70b4b4-01ee-46af-9468-31e67d0995b8.jpg\" width=\"790\" height=\"525\"><br></p><p><img alt=\"30000.jpg\" src=\"http://img.springboot.cn/0570e033-12d7-49b2-88f3-7a5d84157223.jpg\" width=\"790\" height=\"365\"><br></p><p><img alt=\"40000.jpg\" src=\"http://img.springboot.cn/50515c02-3255-44b9-a829-9e141a28c08a.jpg\" width=\"790\" height=\"525\"><br></p><p><img alt=\"50000.jpg\" src=\"http://img.springboot.cn/c138fc56-5843-4287-a029-91cf3732d034.jpg\" width=\"790\" height=\"525\"><br></p><p><img alt=\"60000.jpg\" src=\"http://img.springboot.cn/c92d1f8a-9827-453f-9d37-b10a3287e894.jpg\" width=\"790\" height=\"525\"><br></p><p><br></p><p><img alt=\"TB24p51hgFkpuFjSspnXXb4qFXa-1776456424.jpg\" src=\"http://img.springboot.cn/bb1511fc-3483-471f-80e5-c7c81fa5e1dd.jpg\" width=\"790\" height=\"375\"><br></p><p><br></p><p><img alt=\"shouhou.jpg\" src=\"http://img.springboot.cn/698e6fbe-97ea-478b-8170-008ad24030f7.jpg\" width=\"750\" height=\"150\"><br></p><p><img alt=\"999.jpg\" src=\"http://img.springboot.cn/ee276fe6-5d79-45aa-8393-ba1d210f9c89.jpg\" width=\"790\" height=\"351\"><br></p>", BigDecimal.valueOf(6999.00), 96, ProductStatusEnum.ON_SALE.getCode());
        Product product_2 = ProductServiceTest.buildProduct(27, 100006, "Midea/美的 BCD-535WKZM(E)冰箱双开门对开门风冷无霜智能电家用", "送品牌烤箱，五一大促", "http://img.springboot.cn/ac3e571d-13ce-4fad-89e8-c92c2eccf536.jpeg", "ac3e571d-13ce-4fad-89e8-c92c2eccf536.jpeg,4bb02f1c-62d5-48cc-b358-97b05af5740d.jpeg,36bdb49c-72ae-4185-9297-78829b54b566.jpeg", "<p><img alt=\"miaoshu.jpg\" src=\"http://img.springboot.cn/9c5c74e6-6615-4aa0-b1fc-c17a1eff6027.jpg\" width=\"790\" height=\"444\"><br></p><p><img alt=\"miaoshu2.jpg\" src=\"http://img.springboot.cn/31dc1a94-f354-48b8-a170-1a1a6de8751b.jpg\" width=\"790\" height=\"1441\"><img alt=\"miaoshu3.jpg\" src=\"http://img.springboot.cn/7862594b-3063-4b52-b7d4-cea980c604e0.jpg\" width=\"790\" height=\"1442\"><img alt=\"miaoshu4.jpg\" src=\"http://img.springboot.cn/9a650563-dc85-44d6-b174-d6960cfb1d6a.jpg\" width=\"790\" height=\"1441\"><br></p>", BigDecimal.valueOf(3299.00), 99,ProductStatusEnum.ON_SALE.getCode());
        Product product_3 = ProductServiceTest.buildProduct(28, 100012, "4+64G送手环/Huawei/华为 nova 手机P9/P10plus青春", "NOVA青春版1999元", "http://img.springboot.cn/0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg", "0093f5d3-bdb4-4fb0-bec5-5465dfd26363.jpeg,13da2172-4445-4eb5-a13f-c5d4ede8458c.jpeg,58d5d4b7-58d4-4948-81b6-2bae4f79bf02.jpeg", "<p><img alt=\"11TB2fKK3cl0kpuFjSsziXXa.oVXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/5c2d1c6d-9e09-48ce-bbdb-e833b42ff664.jpg\" width=\"790\" height=\"966\"><img alt=\"22TB2YP3AkEhnpuFjSZFpXXcpuXXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/9a10b877-818f-4a27-b6f7-62887f3fb39d.jpg\" width=\"790\" height=\"1344\"><img alt=\"33TB2Yyshk.hnpuFjSZFpXXcpuXXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/7d7fbd69-a3cb-4efe-8765-423bf8276e3e.jpg\" width=\"790\" height=\"700\"><img alt=\"TB2diyziB8kpuFjSspeXXc7IpXa_!!1777180618.jpg\" src=\"http://img.springboot.cn/1d7160d2-9dba-422f-b2a0-e92847ba6ce9.jpg\" width=\"790\" height=\"393\"><br></p>", BigDecimal.valueOf(1999.00), 100,ProductStatusEnum.ON_SALE.getCode());
        Product product_4 = ProductServiceTest.buildProduct(29, 100008, "Haier/海尔HJ100-1HU1 10公斤滚筒洗衣机全自动带烘干家用大容量 洗烘一体", "门店机型 德邦送货", "http://img.springboot.cn/173335a4-5dce-4afd-9f18-a10623724c4e.jpeg", "173335a4-5dce-4afd-9f18-a10623724c4e.jpeg,42b1b8bc-27c7-4ee1-80ab-753d216a1d49.jpeg,2f1b3de1-1eb1-4c18-8ca2-518934931bec.jpeg", "<p><img alt=\"1TB2WLZrcIaK.eBjSspjXXXL.XXa_!!2114960396.jpg\" src=\"http://img.springboot.cn/ffcce953-81bd-463c-acd1-d690b263d6df.jpg\" width=\"790\" height=\"920\"><img alt=\"2TB2zhOFbZCO.eBjSZFzXXaRiVXa_!!2114960396.jpg\" src=\"http://img.springboot.cn/58a7bd25-c3e7-4248-9dba-158ef2a90e70.jpg\" width=\"790\" height=\"1052\"><img alt=\"3TB27mCtb7WM.eBjSZFhXXbdWpXa_!!2114960396.jpg\" src=\"http://img.springboot.cn/2edbe9b3-28be-4a8b-a9c3-82e40703f22f.jpg\" width=\"790\" height=\"820\"><br></p>", BigDecimal.valueOf(4299.00), 100,ProductStatusEnum.ON_SALE.getCode());
        Product product_5 = ProductServiceTest.buildProduct(30,100001,"商品1","商品1限时9折","商品1主图","商品1副图","商品1详情",BigDecimal.valueOf(2222.00),77,ProductStatusEnum.OFF_SALE.getCode());
        Product product_6 = ProductServiceTest.buildProduct(31,100001,"商品2","商品2限时9折","商品2主图","商品2副图","商品2详情",BigDecimal.valueOf(3333.00),88,ProductStatusEnum.DELETE.getCode());

        productIdAndProductMap.put(26, product_1);
        productIdAndProductMap.put(27, product_2);
        productIdAndProductMap.put(28, product_3);
        productIdAndProductMap.put(29, product_4);
        productIdAndProductMap.put(30, product_5);
        productIdAndProductMap.put(31, product_6);

        verifyException(ResultException.class, ResponseEnum.PRODUCT_NOT_EXIST.getDesc()
        ,()-> orderService.checkProductInfo(productIdAndProductMap,32,1,new Product()));

        verifyException(ResultException.class, ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc()
                ,()-> orderService.checkProductInfo(productIdAndProductMap,30,10,product_5));
        verifyException(ResultException.class, ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc()
                ,()-> orderService.checkProductInfo(productIdAndProductMap,31,10,product_6));

        verifyException(ResultException.class, ResponseEnum.PRODUCT_STOCK_ERROR.getDesc()
                ,()-> orderService.checkProductInfo(productIdAndProductMap,26,100,product_1));

        assertDoesNotThrow(()-> orderService.checkProductInfo(productIdAndProductMap,26,10,product_1));

    }

    @Test
    void deleteOrderByOrderNo() {
        orderService.deleteOrderByOrderNo(ORDER_NO, 2);
    }

    @Test
    void updateOrderStatusByOrderNo() {
        orderService.updateOrderStatusByOrderNo(ORDER_NO);
    }
}