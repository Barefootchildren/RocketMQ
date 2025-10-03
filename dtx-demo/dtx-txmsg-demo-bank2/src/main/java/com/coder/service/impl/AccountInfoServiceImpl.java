package com.coder.service.impl;

import com.coder.dao.AccountInfoDao;
import com.coder.enity.AccountChangeEvent;
import com.coder.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {
    @Autowired
    AccountInfoDao accountInfoDao;
    @Override
    @Transactional
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent) {
        log.info("bank2更新账户，用户id：{}，金额：{}",accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount());
        if (accountInfoDao.isExistTx(accountChangeEvent.getTxNo())>0){
            return;
        }
        accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount());
        accountInfoDao.addTx(accountChangeEvent.getTxNo());
    }
}
