package com.lpy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lpy.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
