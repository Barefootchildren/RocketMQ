package com.coder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.coder.dao.AccountInfoDao;
import com.coder.entity.AccountChangeEvent;
import com.coder.service.AccountInfoService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Resource
    AccountInfoDao accountInfoDao;

    @Resource
    RocketMQTemplate rocketMQTemplate;

    @Override
    public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountChange",accountChangeEvent);
        String jsonString = jsonObject.toJSONString();
        Message<String> build = MessageBuilder.withPayload(jsonString).build();
        rocketMQTemplate.sendMessageInTransaction("producer_group_txmsg_bank1","topic_txmsg",build,null);
    }

    @Override
    @Transactional
    public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        if(accountInfoDao.isExistTx(accountChangeEvent.getTxNo())>0){
            return;
        }
        accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount()*-1);
        accountInfoDao.addTx(accountChangeEvent.getTxNo());
        if (accountChangeEvent.getAmount()==3){
            throw new RuntimeException("人为制造异常");
        }
    }
}