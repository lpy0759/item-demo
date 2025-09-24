package com.lpy.controller;

import com.lpy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/account/decrease")
    public String decrease(@RequestParam("userId") Long userId,
                           @RequestParam("money") BigDecimal money) {
        accountService.decrease(userId, money);
        return "扣减账户余额成功";
    }
}
