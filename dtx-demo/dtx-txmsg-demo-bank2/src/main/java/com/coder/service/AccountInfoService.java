package com.coder.service;

import com.coder.enity.AccountChangeEvent;

public interface AccountInfoService {

    //更新账户，增加金额
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent);
}