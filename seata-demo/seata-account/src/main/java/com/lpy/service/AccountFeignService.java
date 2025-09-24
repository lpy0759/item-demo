package com.lpy.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "seata-account")
public interface AccountFeignService {
    @PostMapping(value = "/account/decrease")
    String decrease(@RequestParam("userId") Long userId,
                    @RequestParam("money") BigDecimal money);
}
