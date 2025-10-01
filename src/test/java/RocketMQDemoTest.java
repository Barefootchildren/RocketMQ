import cn.hutool.db.DaoTemplate;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RocketMQDemoTest {
    @Test
    public void test01() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message msg = new Message("testTopic", "hello world".getBytes());
        SendResult send = producer.send(msg);
        System.out.println(send);
        producer.shutdown();
    }
    @Test
    public void test02() throws Exception{
        // 创建默认的MQ推送消费者实例，指定消费者组名为"consumer-group"
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");
        // 设置NameServer地址，用于连接RocketMQ服务
        consumer.setNamesrvAddr("172.18.23.195:9876");
        // 订阅主题"testTopic"，"*"表示订阅该主题下的所有消息
        consumer.subscribe("testTopic", "*");
        // 注册并发消息监听器，用于处理接收到的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            /**
             * 并发消费消息的方法
             * @param message 消息列表，包含待处理的消息
             * @param context 消费上下文信息
             * @return 消费状态，CONSUME_SUCCESS表示消费成功
             */
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> message, ConsumeConcurrentlyContext context) {
                // 获取消息体内容并转换为字符串
                String msg = new String(message.get(0).getBody());
                // 打印当前线程名和消息内容到控制台
                System.out.println(Thread.currentThread().getName()+":"+msg);
                // 返回消费成功状态
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例，开始接收和处理消息
        consumer.start();
        // 等待用户输入，保持程序运行状态
        System.in.read();

    }
    @Test
    public void testSendAsyncMessage()throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("async-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message msg=new Message("asyncTopic","这是一个异步消息".getBytes());
        producer.send(msg, new SendCallback() {
            public void onSuccess(SendResult sendResult){
                System.out.println("异步执行：消息发送成功");
            }
            public void onException(Throwable throwable){
                System.out.println("异步执行：消息发送失败");
            }
        });
        System.out.println("主线程执行");
        System.in.read();
        producer.shutdown();
    }
    @Test
    public void oneWaySend() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("oneway-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message msg=new Message("onewayTopic","这是一个单向消息".getBytes());
        producer.sendOneway(msg);
        producer.shutdown();
    }
    @Test
    public void testDelaySend() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("delay-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message message = new Message("delayTopic", "这是一条延迟消息".getBytes());
        message.setDelayTimeLevel(3);
        producer.send(message);
        System.out.println("延迟消息发送成功,当前时间："+ LocalDateTime.now());
        producer.shutdown();
    }
    @Test
    public void testBatchSend()throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("batch-produce-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("batchTopic", "这是第一条批量消息".getBytes()));
        messages.add(new Message("batchTopic", "这是第二条批量消息".getBytes()));
        messages.add(new Message("batchTopic", "这是第三条批量消息".getBytes()));
        System.out.println(producer.send(messages));
        producer.shutdown();
    }
    @Test
    public void sendMessageWithTag()throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("tag-produce-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message message = new Message("tagTopic", "tag1", "这是带标签1的消息".getBytes());
        Message message2 = new Message("tagTopic", "tag2", "这是带标签2的消息".getBytes());
        System.out.println(producer.send(message));
        System.out.println(producer.send(message2));
        producer.shutdown();
    }
    @Test
    public void sendMessageWithKey()throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("key-produce-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message message = new Message("keyTopic", "tag1","key1", "这是带标签1key1的消息".getBytes());
        System.out.println(producer.send(message));
        producer.shutdown();
    }
    @Test
    public void retryProducer()throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("retry-produce-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(2);
        producer.setRetryTimesWhenSendFailed(2);
        Message message = new Message("retryTopic", "tag1", "这是带标签1的消息".getBytes());
        System.out.println(producer.send(message));
        producer.shutdown();
    }
    @Test
    public void testAsyncConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("async-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("asyncTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                String msg=new String(list.get(0).getBody());
                System.out.println(Thread.currentThread().getName()+"异步消费："+msg);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void testOneWayConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("oneway-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("onewayTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                String s = new String(list.get(0).getBody());
                System.out.println(Thread.currentThread().getName()+"单向消费："+s);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void testDelayConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("delay-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("delayTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName()+"延迟消费："+new String(list.get(0).getBody())+"当前时间："+ LocalDateTime.now());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void testBatchConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("batch-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("batchTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName()+"批量消费："+new String(list.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void testTagConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag1-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("tagTopic", "tag1 || tag2");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName()+"收到标签消息："+new String(list.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void testTagConsumer2() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag2-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("tagTopic", "tag2");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName()+"收到标签消息："+new String(list.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void testKeyConsumer2() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("key-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("keyTopic", "tag1");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName()+"收到标签消息："+new String(list.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void retryConsumer()throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("retryTopic", "*");
        consumer.setMaxReconsumeTimes(2);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                System.out.println(new Date());
                System.out.println("消息重试次数："+messageExt.getReconsumeTimes());
                System.out.println(Thread.currentThread().getName()+"收到消息："+new String(messageExt.getBody()));
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        System.in.read();
    }
    @Test
    public void retryDeadConsumer()throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry-dead-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("%DLQ%retry-consumer-group", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = list.get(0);
                System.out.println(new Date());
                System.out.println(Thread.currentThread().getName()+"收到消息："+new String(messageExt.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
}
