package com.coder.listener;

import com.coder.service.GoodsService;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RocketMQMessageListener(topic = "seckillTopic",consumerGroup = "seckillGroup",
consumeMode = ConsumeMode.CONCURRENTLY,consumeThreadNumber = 48)
public class SecKillListener implements RocketMQListener<Message> {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public void onMessage(Message message) {
        String s = new String(message.getBody());
        int userId = Integer.parseInt(s.split("-")[0]);
        int goodsId = Integer.parseInt(s.split("-")[1]);
        while (true){
            Boolean b = redisTemplate.opsForValue().setIfAbsent("lock:" + goodsId, "",Duration.ofSeconds(30));
            if (b){
                try {
                    goodsService.realSeckill(userId, goodsId);
                    return;
                }finally {
                    redisTemplate.delete("lock:" + goodsId);
                }
            }
        }
    }
}
