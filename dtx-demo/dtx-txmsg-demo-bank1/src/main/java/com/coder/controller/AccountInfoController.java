package com.coder.controller;

import com.coder.entity.AccountChangeEvent;
import com.coder.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@SuppressWarnings("all")
public class AccountInfoController {
    @Autowired
    private AccountInfoService accountInfoService;
    @GetMapping("/transfer")
    public String transfer(@RequestParam String accountNo, @RequestParam double amount){
        String tx_no = UUID.randomUUID().toString();
        AccountChangeEvent accountChangeEvent = new AccountChangeEvent(accountNo, amount, tx_no);
        accountInfoService.sendUpdateAccountBalance(accountChangeEvent);
        return "转账成功";
    }
}