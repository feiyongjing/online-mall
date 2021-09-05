package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.OrderStatusEnum;
import com.github.eric.mall.enums.PaymentTypeEnum;
import com.github.eric.mall.enums.ProductStatusEnum;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.*;
import com.github.eric.mall.service.OrderService;
import com.github.eric.mall.service.ProductService;
import com.github.eric.mall.service.ShippingService;
import com.github.eric.mall.vo.OrderVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    ShippingService shippingService;
    @Autowired
    ProductService productService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<OrderVo> addOrder(@RequestBody OrderAddForm orderAddForm, HttpSession session) {

        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        // 收获地址校验
        Shipping shipping = shippingService.getById(orderAddForm.getShippingId());
        if (shipping == null || !Objects.equals(shipping.getUserId(), user.getId())) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        Long orderNo=generateOrderNo();
        List<OrderItem> orderItemList=new ArrayList<>();

        // 校验商品是否存在，库存是否充足
        Map<Integer, Product> productIdAndProductMap = productService
                .findByIdIn(new ArrayList<>(orderAddForm.getProductIdAndNumberMap().keySet()));
        for (Map.Entry<Integer, Integer> productIdAndNumber : orderAddForm.getProductIdAndNumberMap().entrySet()) {
            Integer productId = productIdAndNumber.getKey();
            Integer number = productIdAndNumber.getValue();
            Product product = productIdAndProductMap.get(productId);
            // 商品是否存在
            if (!productIdAndProductMap.containsKey(productId)) {
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
            }
            // 商品是否在售
            if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
            }
            // 库存是否充足
            if (product.getStock() < number) {
                return ResponseVo.error(ResponseEnum.PRODUCT_UNDER_STOCK);
            }
            orderItemList.add(buildOrderItem(user,orderNo,number,product));
        }
        // TODO 注意事务
        // 生成订单order和order-item 还有商品表的库存减少
        Order order=buildOrder(user,orderNo,orderAddForm.getShippingId(),orderItemList);
        orderService.addOrderAndOrderItem(order,orderItemList);


        // 判断是否是直接下单还是购物车下单
        if (orderAddForm.getOrderWay() == 1) {
            // 是直接下单

        }

        // 分页查询订单返回
    }

    private Order buildOrder(User user, Long orderNo,Integer shippingId,List<OrderItem> orderItemList) {
        Order order=new Order();
        order.setOrderNo(orderNo);
        order.setUserId(user.getId());
        order.setShippingId(shippingId);
        order.setPayment(orderItemList.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add));
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }


    // TODO 优化成分布式唯一id的生成
    private Long generateOrderNo() {
        return System.currentTimeMillis()+ new Random().nextInt(999);
    }

    private OrderItem buildOrderItem(User user, Long orderNo,Integer quantity, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(user.getId());
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }

//    @GetMapping("/list")
//    @ResponseBody
//    public ResponseVo<List<OrderVo>> getAllOrder(@RequestParam(value = "pageNum", required = false) Integer pageNum,
//                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize,
//                                                 HttpSession session) {
//
//    }
//
//    @GetMapping("/item")
//    @ResponseBody
//    public ResponseVo<OrderVo> getOrderByOrderNo(@RequestParam("orderNo") Integer orderNo) {
//
//    }
//
//    @PutMapping("/")
//    @ResponseBody
//    public ResponseVo<OrderVo> addOrder(@RequestParam("shippingId") Integer shippingId) {
//
//    }

}
