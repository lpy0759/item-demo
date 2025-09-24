package com.lpy.controller;

import com.lpy.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/storage/decrease")
    public String decrease(@RequestParam("productId") Long productId,
                           @RequestParam("count") Integer count) {
        storageService.decrease(productId, count);
        return "扣减库存成功";
    }
}

