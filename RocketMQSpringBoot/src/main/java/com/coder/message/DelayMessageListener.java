package com.coder.message;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RocketMQMessageListener(topic = "delayTopic",consumerGroup = "delay-consumer-group")
public class DelayMessageListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("当前时间："+new Date()+"收到内容为："+s);
    }
}
