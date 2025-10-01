package com.coder.message;

import com.coder.entity.Order;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "objectTopic",consumerGroup = "object-consumer-group")
public class ObjectMessageListener implements RocketMQListener<Order> {
    @Override
    public void onMessage(Order order) {
        System.out.println("收到内容为："+order);
    }
}
