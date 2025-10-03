package com.coder.listener;

import com.alibaba.fastjson.JSONObject;
import com.coder.dao.AccountInfoDao;
import com.coder.entity.AccountChangeEvent;
import com.coder.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQTransactionListener(txProducerGroup = "producer_group_txmsg_bank1")
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {

    @Autowired
    AccountInfoService accountInfoService;

    @Autowired
    AccountInfoDao accountInfoDao;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            String messageString = new String((byte[]) message.getPayload());
            JSONObject jsonObject = JSONObject.parseObject(messageString);
            String accountChange = jsonObject.getString("accountChange");
            AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChange, AccountChangeEvent.class);
            accountInfoService.doUpdateAccountBalance(accountChangeEvent);
            return RocketMQLocalTransactionState.COMMIT;
        }catch (Exception e){
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        String messageString = new String((byte[]) message.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String accountChange = jsonObject.getString("accountChange");
        AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChange, AccountChangeEvent.class);
        String txNo = accountChangeEvent.getTxNo();
        int existTx = accountInfoDao.isExistTx(txNo);
        if (existTx>0){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}