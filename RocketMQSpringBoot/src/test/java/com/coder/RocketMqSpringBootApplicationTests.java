package com.coder;

import com.coder.entity.Order;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@SpringBootTest
class RocketMqSpringBootApplicationTests {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Test
    void sendMessage() {
        SendResult sendResult = rocketMQTemplate.syncSend("simpleTopic", "hello world");
        System.out.println("消息发送状态："+sendResult.getSendStatus());
        System.out.println("消息ID"+sendResult.getMsgId());
    }
    @Test
    void testObjectMsg(){
        Order order = new Order(UUID.randomUUID().toString(), "订单1", 59.0, new Date(), "订单描述1");
        System.out.println(rocketMQTemplate.syncSend("objectTopic", order));
    }
    @Test
    void testListMessage(){
        List<Order> orderList = new ArrayList<>();
        orderList.add(new Order(UUID.randomUUID().toString(),"订单1",111.1,new Date(),"普通配送"));
        orderList.add(new Order(UUID.randomUUID().toString(),"订单2",222.1,new Date(),"加急配送"));
        orderList.add(new Order(UUID.randomUUID().toString(),"订单3",333.1,new Date(),"延时配送"));
        orderList.add(new Order(UUID.randomUUID().toString(),"订单4",444.1,new Date(),"普通配送"));
        System.out.println(rocketMQTemplate.syncSend("listTopic", orderList));
    }
    @Test
    void testAsyncMessage() throws IOException {
        rocketMQTemplate.asyncSend("asyncTopic", "这是一条异步消息", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("消息发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("消息发送失败");
            }
        });
        System.in.read();
    }
    @Test
    void testOnWay(){
        rocketMQTemplate.sendOneWay("oneWayTopic", "这是一条单向消息");
    }
    @Test
    void testDelay(){
        Message<String> message = MessageBuilder.withPayload("这是一条延迟消息").build();
        SendResult sendResult = rocketMQTemplate.syncSend("delayTopic", message, 2000, 4);
        System.out.println("消息发送状态为："+sendResult.getSendStatus()+"当前时间为："+new Date());

    }
    @Test
    void testOrderly(){
        List<Order> orderList = Arrays.asList(
                new Order("1", "111", 59.0, new Date(), "下订单"),
                new Order("2", "111", 59.0, new Date(), "物流"),
                new Order("3", "111", 59.0, new Date(), "签收"),
                new Order("4", "112", 66.0, new Date(), "下订单"),
                new Order("5", "112", 66.0, new Date(), "物流"),
                new Order("6", "112", 66.0, new Date(), "拒收")
        );
        orderList.forEach(order -> {
            rocketMQTemplate.syncSendOrderly("orderlyTopic",order,order.getOrderName());
            System.out.println("订单 "+order.getOrderId()+" 信息已发送");
        });
    }
    @Test
    void sendMessageByTag(){
        rocketMQTemplate.syncSend("tagTopic:tag1", "这是tag1的消息");
    }
    @Test
    void sendMessageWithKey(){
        Message<String> message = MessageBuilder.withPayload("key消息").setHeader(RocketMQHeaders.KEYS, "key").build();
        System.out.println(rocketMQTemplate.syncSend("keyTopic", message));
    }
    @Test
    void testMsgModel(){
        for (int i = 0; i < 10; i++) {
            rocketMQTemplate.syncSend("clusterTopic","我是消息："+i);
            System.out.println("成功发送消息"+i);
        }
    }
}
