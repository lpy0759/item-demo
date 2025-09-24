package com.lpy.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "seata-order")
public interface OrderFeignService {
    @PostMapping(value = "/order/create")
    String create(@RequestParam("userId") Long userId,
                  @RequestParam("productId") Long productId,
                  @RequestParam("count") Integer count,
                  @RequestParam("money") BigDecimal money);
}
