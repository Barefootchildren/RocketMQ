package com.coder.message;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "oneWayTopic",consumerGroup = "one-consumer-group")
public class OneMessageListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("收到内容为："+s);
    }
}
