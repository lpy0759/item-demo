package com.lpy.controller;

import com.lpy.entity.Order;
import com.lpy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    public String create(@RequestParam("userId") Long userId,
                         @RequestParam("productId") Long productId,
                         @RequestParam("count") Integer count,
                         @RequestParam("money") BigDecimal money) {
        Order order = new Order(null, userId, productId, count, money, 0);
        orderService.create(order);
        return "订单创建成功";
    }
}
