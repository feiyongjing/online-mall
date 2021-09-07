package com.github.eric.mall.service;

import com.github.eric.mall.dao.MyOrderMapper;
import com.github.eric.mall.enums.OrderStatusEnum;
import com.github.eric.mall.enums.OrderWayEnum;
import com.github.eric.mall.enums.PaymentTypeEnum;
import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.*;
import com.github.eric.mall.generate.mapper.OrderMapper;
import com.github.eric.mall.vo.OrderItemVo;
import com.github.eric.mall.vo.OrderVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    MyOrderMapper myOrderMapper;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CartService cartService;
    @Autowired
    ShippingService shippingService;
    @Autowired
    ProductService productService;

    @Transactional
    public OrderVo addOrder(OrderAddForm orderAddForm, Integer userId) {
        // 获取收获地址并校验
        Shipping shipping = shippingService.getById(orderAddForm.getShippingId(), userId);

        // 判断是否是直接下单还是购物车下单
        checkOrderWay(orderAddForm, userId);

        Long orderNo = generateOrderNo();
        List<OrderItem> orderItemList = new ArrayList<>();

        // 校验商品是否存在，库存是否充足
        Map<Integer, Product> productIdAndProductMap = productService
                .findByIdIn(new ArrayList<>(orderAddForm.getProductIdAndNumberMap().keySet()));
        checkOrderProduct(orderAddForm, userId, orderNo, orderItemList, productIdAndProductMap);
        // TODO 注意事务
        // 生成订单order和order-item 还有商品表的库存减少
        Order order = buildOrder(userId, orderNo, orderAddForm.getShippingId(), orderItemList);
        addOrderAndOrderItem(order, orderItemList, new ArrayList<>(productIdAndProductMap.values()), userId, orderAddForm);

        return getOrderVo(order, orderItemList, shipping);
    }

    private void checkOrderProduct(OrderAddForm orderAddForm, Integer userId, Long orderNo, List<OrderItem> orderItemList, Map<Integer, Product> productIdAndProductMap) {
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
            product.setStock(product.getStock() - number);
        }
    }

    private void checkOrderWay(OrderAddForm orderAddForm, Integer userId) {
        if (Objects.equals(orderAddForm.getOrderWay(), OrderWayEnum.CART_ORDER.getCode())) {
            // 是购物车下单
            Map<Integer, Cart> entries = cartService.getProductAndCartMap(userId);
            Map<Integer, Integer> productIdAndNumberMap = entries.values().stream()
                    .filter(Cart::getProductSelected)
                    .collect(Collectors.toMap(Cart::getProductId, Cart::getQuantity));
            if (productIdAndNumberMap.isEmpty()) {
                // 购物车中没有选中的商品
                throw new RuntimeException();
            }
            orderAddForm.setProductIdAndNumberMap(productIdAndNumberMap);
        }
    }


    public void addOrderAndOrderItem(Order order, List<OrderItem> orderItemList, List<Product> productList, Integer userId, OrderAddForm orderAddForm) {
        int row = orderMapper.insertSelective(order);
        if (row <= 0) {
            throw new RuntimeException();
        }
        row = orderItemService.insertOrderItemList(orderItemList);
        if (row <= 0) {
            throw new RuntimeException();
        }
        row = productService.batchUpdateByPrimaryKeys(productList);
        if (row <= 0) {
            throw new RuntimeException();
        }

        if (orderAddForm.getOrderWay().equals(OrderWayEnum.CART_ORDER.getCode())) {
            HashOperations<String, Integer, Cart> hashOperations = redisTemplate.opsForHash();
            hashOperations.delete(String.format(CART_REDIS_KEY_TEMPLATE, userId), productList.stream().map(Product::getId).toArray());
        }
    }

    private OrderVo getOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order, orderVo);

        List<OrderItemVo> orderItemVoList = new ArrayList<>();

        orderItemList.forEach(orderItem -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            BeanUtils.copyProperties(orderItem, orderItemVo);
            orderItemVoList.add(orderItemVo);
        });

        orderVo.setShippingId(shipping.getId());
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

    public PageInfo<OrderVo> getOrderList(Integer userId, Integer pageNum, Integer pageSize) {

        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andUserIdEqualTo(userId);
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectByExample(orderExample);

        Map<Long, Order> orderNoAndOrderMap = orders.stream().collect(Collectors.toMap(Order::getOrderNo, order -> order));

        List<OrderItem> orderItemList = orderItemService.getOrderItemListByOrderNos(new ArrayList<>(orderNoAndOrderMap.keySet()));

        Map<Long, List<OrderItem>> orderNoAndOrderItemMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderNo));

        Map<Integer, Shipping> shippingIdAndShippingMap = shippingService.
                getShippingListByIdsAndUser(orders.stream().map(Order::getShippingId).collect(Collectors.toList()), userId)
                .stream().collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orders) {
            if (!orderNoAndOrderItemMap.containsKey(order.getOrderNo())) {
                // 数据库数据出错
                throw new RuntimeException();
            }
            if (!shippingIdAndShippingMap.containsKey(order.getShippingId())) {
                // 数据库数据出差
                throw new RuntimeException();
            }
            OrderVo orderVo = getOrderVo(order, orderNoAndOrderItemMap.get(order.getOrderNo()), shippingIdAndShippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }

        PageInfo pageInfo = new PageInfo<>(orders);
        pageInfo.setList(orderVoList);

        return pageInfo;
    }

    public OrderVo getOrderByOrderNo(Long orderNo, Integer userId) {
        Order order = myOrderMapper.getByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            // 订单不存在
            throw new RuntimeException();
        }
        List<OrderItem> orderItemList = orderItemService.getOrderItemListByOrderNo(orderNo);
        Shipping shipping = shippingService.getById(order.getShippingId(), userId);
        return getOrderVo(order, orderItemList, shipping);
    }


    public void deleteOrderByOrderNo(Long orderNo, Integer userId) {
        Order order = myOrderMapper.getByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            // 订单不存在
            throw new RuntimeException();
        }

        if (Objects.equals(order.getStatus(), OrderStatusEnum.PAID.getCode())) {
            // 此订单已付款，无法被取消
            throw new RuntimeException();
        }
        if (Objects.equals(order.getStatus(), OrderStatusEnum.NO_PAY.getCode())) {
            // 此订单未付款，可以取消
            order.setStatus(OrderStatusEnum.CANCELED.getCode());
            order.setCloseTime(new Date());
            int row = orderMapper.updateByPrimaryKeySelective(order);
            if(row<=0){
                // 此订单取消失败
                throw new RuntimeException();
            }
        }
    }
}
