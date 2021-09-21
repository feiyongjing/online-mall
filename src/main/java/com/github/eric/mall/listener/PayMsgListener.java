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
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PayMsgListener {

    @Autowired
    OrderService orderService;


    //    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = RabbitmqConfig.QUEUE_NAME_2, durable = "true"),
//            exchange = @Exchange(value = RabbitmqConfig.TEST_EXCHANGE_HAHA, ignoreDeclarationExceptions = "true"),
//            key = RabbitmqConfig.TEST_TOPIC_ROUTING_KEY_2)
//    )
//    @RabbitListener(queues = {RabbitmqConfig.QUEUE_NAME_2})
    public void process(PayInfo payInfo, Channel channel, Message message) throws IOException {
//        System.out.println(RabbitmqConfig.QUEUE_NAME_2 + "收到消息" + JSON.toJSONString(payInfo));
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        if (payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.getDesc())) {
//            try {
//                System.out.println("收到消息"+JSON.toJSONString(payInfo));
////                log.info("收到消息"+msg);
//                orderService.updateOrderStatusByOrderNo(payInfo.getOrderNo());
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//            }catch (Exception exception){
//                if (message.getMessageProperties().getRedelivered()) {
////                    log.error("消息已重复处理次数达到上限,拒绝再次接收...");
////                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false); // 拒绝消息
//                    System.out.println("消息已重复处理次数达到上限, 先返回收到消息, 再重新发送消息到队尾"+JSON.toJSONString(payInfo));
////                    log.error("消息已重复处理次数达到上限, 先返回收到消息, 再重新发送消息到队尾");
//                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//                    channel.basicPublish(message.getMessageProperties().getReceivedExchange(),
//                            message.getMessageProperties().getReceivedRoutingKey(), MessageProperties.PERSISTENT_TEXT_PLAIN,
//                            JSON.toJSONBytes(payInfo));
//                } else {
//                    System.out.println("消息即将再次返回队列处理"+JSON.toJSONString(payInfo));
////                    log.error("消息即将再次返回队列处理"+msg);
//                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//                }
//            }
//
//        }

    }
}
