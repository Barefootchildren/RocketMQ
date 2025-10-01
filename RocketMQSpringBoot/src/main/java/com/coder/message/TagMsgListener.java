package com.coder.message;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "tagTopic",consumerGroup = "tagGroup",selectorType = SelectorType.TAG,selectorExpression = "tag1")
public class TagMsgListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("收到内容为："+s);

    }
}
