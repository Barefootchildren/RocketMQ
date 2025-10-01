package com.coder.controller;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SecKillController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    //CAS java无锁的   原子性 安全的
    AtomicInteger userIdAt = new AtomicInteger(0);
    @GetMapping("seckill")
    public String doSecKill(Integer goodsId){
        int userId = userIdAt.incrementAndGet();
        String uk=userId+"-"+goodsId;
        Boolean b = redisTemplate.opsForValue().setIfAbsent(uk, 1);
        if(!b){
            return "您已经参加过该商品的抢购，请下次再来";
        }
        Long count = redisTemplate.opsForValue().decrement("goodsId:" + goodsId);
        if (count < 0){
            redisTemplate.opsForValue().increment("goodsId:"+goodsId);
            return "该商品已被抢光，你来晚了";
        }
        rocketMQTemplate.asyncSend("seckillTopic",uk, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("消息发送失败："+throwable.getMessage());
                System.out.println("用户的ID为："+userId+"商品ID为："+goodsId);
            }
        });
        return "恭喜您抢购成功";
    }
}
