package com.github.eric.mall.service;

import com.github.eric.mall.generate.entity.Order;
import com.github.eric.mall.generate.entity.OrderItem;
import com.github.eric.mall.generate.mapper.OrderItemMapper;
import com.github.eric.mall.generate.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;


    // TODO 注意添加事务
    public void addOrderAndOrderItem(Order order, List<OrderItem> orderItemList) {
        int row = orderMapper.insertSelective(order);
        orderItemMapper.
    }
}
