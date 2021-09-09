package com.github.eric.mall.listener;

import com.alibaba.fastjson.JSON;
import com.github.eric.mall.service.OrderService;
import com.github.eric.onlinemallpay.generate.entity.PayInfo;
import com.lly835.bestpay.enums.OrderStatusEnum;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "payNotify")
public class PayMsgListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void process(String msg){
        PayInfo payInfo = JSON.parseObject(msg, PayInfo.class);
        if(payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.getDesc())){

            orderService.updateOrderStatusByOrderNo(payInfo.getOrderNo());
        }

    }
}
