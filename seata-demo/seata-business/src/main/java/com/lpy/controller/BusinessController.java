package com.lpy.controller;

import com.lpy.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/purchase")
    public String purchase() {
        businessService.purchase(1L, 1L, 10, new BigDecimal(100));
        return "购买成功";
    }
}
