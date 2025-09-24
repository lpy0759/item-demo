package com.lpy.service;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BusinessService {

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private StorageFeignService storageFeignService;

    @Autowired
    private AccountFeignService accountFeignService;

    @GlobalTransactional(name = "fsp-create-order", rollbackFor = Exception.class)
    public void purchase(Long userId, Long productId, Integer count, BigDecimal money) {
        log.info("------->交易开始");

        //本地方法的本地事务提交了，全局事务回滚了，问题就出现了
        //远程方法
        //减库存
        storageFeignService.decrease(productId, count);

        //减余额
        accountFeignService.decrease(userId, money);

        //下订单
        orderFeignService.create(userId, productId, count, money);

        log.info("------->交易结束");
    }
}
