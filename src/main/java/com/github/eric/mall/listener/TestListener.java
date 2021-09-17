package com.github.eric.mall.listener;

import com.alibaba.fastjson.JSON;
import com.github.eric.mall.service.OrderService;
import com.github.eric.onlinemallpay.generate.entity.PayInfo;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TestListener {


    @Autowired
    OrderService orderService;

    @RabbitListener(queues = "test-1")
    public void process(PayInfo payInfo, Channel channel, Message message) throws IOException {
//        PayInfo payInfo = JSON.parseObject(msg, PayInfo.class);
//        if (payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.getDesc())) {
//            try {
//                System.out.println("收到消息" + msg);
//                System.out.println("处理业务");
////                log.info("收到消息"+msg);
////                orderService.updateOrderStatusByOrderNo(payInfo.getOrderNo());
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//            } catch (Exception exception) {
//                if (message.getMessageProperties().getRedelivered()) {
////                    log.error("消息已重复处理次数达到上限,拒绝再次接收...");
////                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
//                    System.out.println("消息已重复处理次数达到上限, 先返回收到消息, 再重新发送消息到队尾" + msg);
////                    log.error("消息已重复处理次数达到上限, 先返回收到消息, 再重新发送消息到队尾");
//                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//                    channel.basicPublish(message.getMessageProperties().getReceivedExchange(),
//                            message.getMessageProperties().getReceivedRoutingKey(), MessageProperties.PERSISTENT_TEXT_PLAIN,
//                            JSON.toJSONBytes(msg));
//                } else {
//                    System.out.println("消息即将再次返回队列处理" + msg);
////                    log.error("消息即将再次返回队列处理"+msg);
//                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//                }
//            }
//
//        }

    }

}
