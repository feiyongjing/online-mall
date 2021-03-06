package com.github.eric.mall.controller;

import com.github.eric.mall.consts.OnlineMallConst;
import com.github.eric.mall.enums.ResponseEnum;
import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.generate.entity.User;
import com.github.eric.mall.service.OrderService;
import com.github.eric.mall.service.ProductService;
import com.github.eric.mall.service.ShippingService;
import com.github.eric.mall.vo.OrderVo;
import com.github.eric.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<OrderVo> addOrder(@Valid @RequestBody OrderAddForm orderAddForm, HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        return ResponseVo.success(orderService.addOrder(orderAddForm,user.getId()));
    }



    @GetMapping("/list")
    @ResponseBody
    public ResponseVo<PageInfo<OrderVo>> getAllOrder(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        return ResponseVo.success(orderService.getOrderList(user.getId(),pageNum,pageSize));
    }

    @GetMapping("/item")
    @ResponseBody
    public ResponseVo<OrderVo> getOrderByOrderNo(@RequestParam("orderNo") Long orderNo,
                                                 HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        return ResponseVo.success(orderService.getOrderByOrderNo(orderNo,user.getId()));
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseVo<OrderVo> deleteOrder(@RequestParam("orderNo") Long orderNo,
                                        HttpSession session) {
        User user = (User) session.getAttribute(OnlineMallConst.CURRENT_USER);
        orderService.deleteOrderByOrderNo(orderNo,user.getId());
        return ResponseVo.successByMsg("??????????????????");
    }

}
