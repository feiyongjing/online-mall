package com.github.eric.mall.listener;

import com.alibaba.fastjson.JSON;
import com.github.eric.mall.service.OrderService;
import com.github.eric.onlinemallpay.config.RabbitmqConfig;
import com.github.eric.onlinemallpay.generate.entity.PayInfo;
import com.lly835.bestpay.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayMsgListener {

    @Autowired
    OrderService orderService;

    @RabbitListener(queues = {RabbitmqConfig.PAY_NOTIFY_QUEUE})
    public void process(PayInfo payInfo) {
        String msg = JSON.toJSONString(payInfo);
        System.out.println(RabbitmqConfig.PAY_NOTIFY_QUEUE + "收到消息" + msg);
        if (payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.getDesc())) {
            try {
                System.out.println("处理业务");
                orderService.updateOrderStatusByOrderNo(payInfo.getOrderNo());
            } catch (Exception exception) {
                System.out.println("处理业务失败");
                throw new RuntimeException("消费失败");
            }
        }
    }
}
