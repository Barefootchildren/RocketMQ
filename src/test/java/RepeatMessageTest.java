import cn.hutool.bloomfilter.BitMapBloomFilter;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.util.List;

public class RepeatMessageTest {
    private static BitMapBloomFilter bloomFilter = new BitMapBloomFilter(1000);
    @Test
    public void testRepeatMessageProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("repeat-group");
        producer.setNamesrvAddr("172.18.23.195:9876");
        producer.start();
        Message message = new Message("repeatTopic", "tag1","key1","这是重复消息".getBytes());
        Message message2 = new Message("repeatTopic", "tag1","key1","这是重复消息".getBytes());
        System.out.println(producer.send(message));
        System.out.println(producer.send(message2));
        producer.shutdown();
    }
    @Test
    public void testRepeatMessageConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("repeat-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("repeatTopic", "tag1");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                String keys = list.get(0).getKeys();
                if(bloomFilter.contains(keys)){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                bloomFilter.add(keys);
                System.out.println(Thread.currentThread().getName()+"消费："+new String(list.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
}
