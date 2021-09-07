package com.github.eric.mall.service;

import com.github.eric.mall.dao.MyOrderItemMapper;
import com.github.eric.mall.generate.entity.OrderItem;
import com.github.eric.mall.generate.entity.OrderItemExample;
import com.github.eric.mall.generate.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    MyOrderItemMapper myOrderItemMapper;


    public List<OrderItem> getOrderItemListByOrderNos(List<Long> orderNos) {
        OrderItemExample orderItemExample=new OrderItemExample();
        orderItemExample.createCriteria().andOrderNoIn(orderNos);
        return orderItemMapper.selectByExample(orderItemExample);
    }

    public int insertOrderItemList(List<OrderItem> orderItemList) {
        return myOrderItemMapper.insertOrderItemList(orderItemList);
    }

    public List<OrderItem> getOrderItemListByOrderNo(Long orderNo) {
        OrderItemExample orderItemExample=new OrderItemExample();
        orderItemExample.createCriteria().andOrderNoEqualTo(orderNo);
        return orderItemMapper.selectByExample(orderItemExample);
    }
}
