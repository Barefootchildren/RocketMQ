package com.coder.consumer;

import com.alibaba.fastjson.JSONObject;
import com.coder.enity.AccountChangeEvent;
import com.coder.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = "topic_txmsg",consumerGroup = "consumer_group_txmsg")
public class TxmsgConsumer implements RocketMQListener<String> {
    @Autowired
    AccountInfoService accountInfoService;
    @Override
    public void onMessage(String s) {
        log.info("接收到的消息：{}",s);
        //解析消息
        JSONObject jsonObject = JSONObject.parseObject(s);
        String accountChangeString = jsonObject.getString("accountChange");
        //转成AccountChangeEvent
        AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChangeString, AccountChangeEvent.class);
        accountChangeEvent.setAccountNo("2");
        accountInfoService.addAccountInfoBalance(accountChangeEvent);
    }
}
