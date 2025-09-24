package com.lpy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lpy.entity.Account;
import com.lpy.mapper.AccountMapper;
import com.lpy.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public void decrease(Long userId, BigDecimal money) {
        log.info("------->account-service中扣减账户余额开始");

        // 模拟超时异常，验证分布式事务
        // try { TimeUnit.SECONDS.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }

        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        Account account = accountMapper.selectOne(wrapper);

        if (account != null && account.getResidue().compareTo(money) >= 0) {
            account.setResidue(account.getResidue().subtract(money));
            account.setUsed(account.getUsed().add(money));
            accountMapper.updateById(account);
        } else {
            throw new RuntimeException("账户余额不足");
        }

        log.info("------->account-service中扣减账户余额结束");
    }
}
