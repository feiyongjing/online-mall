package com.github.eric.mall.listener;

import com.alibaba.fastjson.JSON;
import com.github.eric.mall.service.OrderService;
import com.github.eric.onlinemallpay.config.RabbitmqConfig;
import com.github.eric.onlinemallpay.generate.entity.PayInfo;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TestListener {

    @Autowired
    OrderService orderService;

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_NAME_1})
    public void process(PayInfo payInfo, Channel channel, Message message) throws IOException {
        String msg = JSON.toJSONString(payInfo);
        System.out.println(RabbitmqConfig.QUEUE_NAME_1 + "收到消息" + msg);
        if (payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.getDesc())) {
            try {
                System.out.println("处理业务");
                int a= 1/0;
            } catch (Exception exception) {
                System.out.println("处理业务失败");
                throw new RuntimeException("消费失败");
            }
        }else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }

    }

}
