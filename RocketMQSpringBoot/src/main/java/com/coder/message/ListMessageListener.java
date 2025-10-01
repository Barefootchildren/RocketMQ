package com.coder.message;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = "listTopic",consumerGroup = "list-consumer-group")
public class ListMessageListener implements RocketMQListener<Object> {
    @Override
    public void onMessage(Object o) {
        List<Map<String,Object>> msgs=(List<Map<String,Object>>) o;
        msgs.forEach(msg->{
            System.out.println("收到内容为："+msg.get("content"));
        });
    }
}
