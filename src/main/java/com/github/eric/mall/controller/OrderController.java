package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.*;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.*;
import com.github.eric.mall.service.CartService;
import com.github.eric.mall.service.OrderService;
import com.github.eric.mall.service.ProductService;
import com.github.eric.mall.service.ShippingService;
import com.github.eric.mall.vo.OrderItemVo;
import com.github.eric.mall.vo.OrderVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    ShippingService shippingService;
    @Autowired
    ProductService productService;
    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<OrderVo> addOrder(@RequestBody OrderAddForm orderAddForm, HttpSession session) {

        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        // 分页查询订单返回
        return ResponseVo.success(orderService.addOrder(orderAddForm,user.getId()));
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
