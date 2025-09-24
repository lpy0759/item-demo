package com.lpy.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lpy.entity.Order;

public interface OrderService extends IService<Order> {
    void create(Order order);
}
