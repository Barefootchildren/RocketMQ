package com.coder.message;

import com.coder.entity.Order;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "orderlyTopic",consumerGroup = "orderly-consumer-group",consumeMode = ConsumeMode.ORDERLY)
public class OrderlyMessageListener implements RocketMQListener<Order> {
    @Override
    public void onMessage(Order order) {
        System.out.println(Thread.currentThread().getName()+"收到信息："+order);
    }
}
