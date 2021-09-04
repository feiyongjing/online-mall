package com.github.eric.mall.controller;

import com.github.eric.mall.form.OrderAddForm;
import com.github.eric.mall.service.OrderService;
import com.github.eric.mall.vo.OrderVo;
import com.github.eric.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseVo<OrderVo> addOrder(@RequestBody OrderAddForm orderAddForm, HttpSession session){

        // 收获地址校验
        // 校验商品是否存在，库存是否充足
        // TODO 注意事务
        // 生成订单order和order-item 还有商品表的库存减少
        // 判断是否是直接下单还是购物车下单
        // 分页查询订单返回
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseVo<List<OrderVo>> getAllOrder(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                              HttpSession session){

    }

    @GetMapping("/item")
    @ResponseBody
    public ResponseVo<OrderVo> getOrderByOrderNo(@RequestParam("orderNo") Integer orderNo){

    }

    @PutMapping("/")
    @ResponseBody
    public ResponseVo<OrderVo> addOrder(@RequestParam("shippingId") Integer shippingId){

    }

}
