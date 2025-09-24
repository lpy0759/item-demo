package com.lpy.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-storage")
public interface StorageFeignService {
    @PostMapping(value = "/storage/decrease")
    String decrease(@RequestParam("productId") Long productId,
                    @RequestParam("count") Integer count);
}