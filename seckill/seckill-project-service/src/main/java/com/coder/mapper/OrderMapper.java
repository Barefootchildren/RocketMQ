package com.coder.mapper;

import com.coder.entity.Order;

//@Mapper
public interface OrderMapper {
    //秒杀成功后，生成订单信息
    int insert(Order record);
}
