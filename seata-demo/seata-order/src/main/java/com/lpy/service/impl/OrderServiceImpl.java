package com.lpy.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lpy.entity.Order;
import com.lpy.mapper.OrderMapper;
import com.lpy.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    public void create(Order order) {
        log.info("------->下单开始");
        order.setStatus(0);
        save(order);
        log.info("------->下单结束");
    }
}

