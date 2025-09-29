import com.coder.entity.Order;
import org.apache.commons.collections.functors.ExceptionPredicate;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderlyMessageTest {
    @Test
    public void OrderlyProducer() throws Exception {
        // 创建顺序消息生产者实例，指定生产者组名为 "orderly-producer-group"
        DefaultMQProducer producer = new DefaultMQProducer("orderly-producer-group");

        // 设置 NameServer 地址，用于连接 RocketMQ 服务
        producer.setNamesrvAddr("172.18.23.195:9876");

        // 启动生产者实例
        producer.start();

        // 构造一个订单列表，里面有两个订单（111 和 112），每个订单有三个状态
        // 订单111: 下订单1 -> 物流1 -> 签收1
        // 订单112: 下订单2 -> 物流2 -> 拒收2
        List<Order> orderList = Arrays.asList(
                new Order(1, 111, 59.0, new Date(), "下订单1"),
                new Order(2, 111, 59.0, new Date(), "物流1"),
                new Order(3, 111, 59.0, new Date(), "签收1"),
                new Order(4, 112, 66.0, new Date(), "下订单2"),
                new Order(5, 112, 66.0, new Date(), "物流2"),
                new Order(6, 112, 66.0, new Date(), "拒收2")
        );

        // 遍历订单列表，将每个订单对象作为消息发送
        orderList.forEach(order -> {
            // 创建消息，主题为 "orderlyTopic"，消息体是订单对象的字符串表示
            Message message = new Message("orderlyTopic", order.toString().getBytes());
            try {
                // 发送顺序消息
                // 关键点：第三个参数 order.getOrderNumber() 会传给队列选择器，用来决定放到哪个队列
                producer.send(message, new MessageQueueSelector() {

                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        // list.size() = 队列数量，比如 4 个
                        int size = list.size();

                        // o 是你传进来的 orderNumber，这里是订单号 111 或 112
                        int i = (Integer) o;

                        // 用订单号对队列数量取模，算出队列下标
                        int index = i % size;

                        // 返回对应的队列，让该消息始终进入同一个队列
                        return list.get(index);
                    }
                }, order.getOrderNumber());  // 把订单号作为参数传给选择器
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 关闭生产者，释放资源
        producer.shutdown();
    }

    @Test
    public void OrderlyConsumer()throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("orderly-consumer-group");
        consumer.setNamesrvAddr("172.18.23.195:9876");
        consumer.subscribe("orderlyTopic", "*");
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                System.out.println("线程号："+Thread.currentThread().getId()+"收到内容为："+new String(list.get(0).getBody()));
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
}
