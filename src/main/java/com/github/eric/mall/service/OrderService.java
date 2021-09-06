package com.github.eric.mall.service;

import com.github.eric.mall.dao.MyOrderItemMapper;
import com.github.eric.mall.dao.MyProductMapper;
import com.github.eric.mall.enums.*;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.*;
import com.github.eric.mall.generate.mapper.OrderItemMapper;
import com.github.eric.mall.generate.mapper.OrderMapper;
import com.github.eric.mall.vo.OrderItemVo;
import com.github.eric.mall.vo.OrderVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.eric.mall.service.CartService.CART_REDIS_KEY_TEMPLATE;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    MyOrderItemMapper myOrderItemMapper;

    @Autowired
    MyProductMapper myProductMapper;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    OrderService orderService;
    @Autowired
    ShippingService shippingService;
    @Autowired
    ProductService productService;

    public OrderVo addOrder(OrderAddForm orderAddForm, Integer userId){
        // 收获地址校验
        Shipping shipping = shippingService.getById(orderAddForm.getShippingId());
        if (shipping == null || !Objects.equals(shipping.getUserId(), userId)) {
            // 地址不存在
            throw new RuntimeException();
        }

        // 判断是否是直接下单还是购物车下单
        if (Objects.equals(orderAddForm.getOrderWay(), OrderWayEnum.CART_ORDER.getCode())) {
            // 是购物车下单
            HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
            Map<Integer, Cart> entries = hashOperations.entries(String.format(CartService.CART_REDIS_KEY_TEMPLATE, user.getId()));
            if(entries.isEmpty()){
                // 购物车中没有商品
                throw new RuntimeException();
            }
            Map<Integer, Integer> productIdAndNumberMap = entries.values().stream()
                    .filter(Cart::getProductSelected)
                    .collect(Collectors.toMap(Cart::getProductId, Cart::getQuantity));
            orderAddForm.setProductIdAndNumberMap(productIdAndNumberMap);
        }

        Long orderNo = generateOrderNo();
        List<OrderItem> orderItemList = new ArrayList<>();

        // 校验商品是否存在，库存是否充足
        Map<Integer, Product> productIdAndProductMap = productService
                .findByIdIn(new ArrayList<>(orderAddForm.getProductIdAndNumberMap().keySet()));
        for (Map.Entry<Integer, Integer> productIdAndNumber : orderAddForm.getProductIdAndNumberMap().entrySet()) {
            Integer productId = productIdAndNumber.getKey();
            Integer number = productIdAndNumber.getValue();
            Product product = productIdAndProductMap.get(productId);
            // 商品是否存在
            if (!productIdAndProductMap.containsKey(productId)) {
                throw new RuntimeException();
            }
            // 商品是否在售
            if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
                throw new RuntimeException();
            }
            // 库存是否充足
            if (product.getStock() < number) {
                throw new RuntimeException();
            }
            orderItemList.add(buildOrderItem(userId, orderNo, number, product));
            // 对商品信息的库存减少
            product.setStock(product.getStock()-number);
        }
        // TODO 注意事务
        // 生成订单order和order-item 还有商品表的库存减少
        Order order = buildOrder(userId, orderNo, orderAddForm.getShippingId(), orderItemList);
        orderService.addOrderAndOrderItem(order, orderItemList, new ArrayList<>(productIdAndProductMap.values()),user.getId(),orderAddForm);

        return getOrderVo(order,orderItemList,shipping);
    }

    // TODO 注意添加事务
    @Transactional
    public void addOrderAndOrderItem(Order order, List<OrderItem> orderItemList, List<Product> productList, Integer userId, OrderAddForm orderAddForm) {
        int row = orderMapper.insertSelective(order);
        if (row <= 0) {
            throw new RuntimeException();
        }
        row = myOrderItemMapper.insertOrderItemList(orderItemList);
        if (row <= 0) {
            throw new RuntimeException();
        }
        row = myProductMapper.batchUpdateByPrimaryKeys(productList);
        if (row <= 0) {
            throw new RuntimeException();
        }

        if (orderAddForm.getOrderWay().equals(OrderWayEnum.CART_ORDER.getCode())) {
            HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
            hashOperations.delete(String.format(CART_REDIS_KEY_TEMPLATE, userId), productList.stream().map(Product::getId).toArray());
        }
    }
    private OrderVo getOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo=new OrderVo();
        BeanUtils.copyProperties(order,orderVo);

        List<OrderItemVo> orderItemVoList=new ArrayList<>();

        orderItemList.forEach(orderItem -> {
            OrderItemVo orderItemVo =new OrderItemVo();
            BeanUtils.copyProperties(orderItem,orderItemVo);
            orderItemVoList.add(orderItemVo);
        });

        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setShippingVo(shipping);

        return orderVo;
    }

    private Order buildOrder(Integer userId, Long orderNo, Integer shippingId, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(orderItemList.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }


    // TODO 优化成分布式唯一id的生成
    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(Integer userId, Long orderNo, Integer quantity, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }
}
