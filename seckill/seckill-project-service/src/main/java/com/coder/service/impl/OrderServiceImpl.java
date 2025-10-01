package com.coder.service.impl;

import com.coder.entity.Order;
import com.coder.mapper.OrderMapper;
import com.coder.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public int insert(Order record) {
        return orderMapper.insert(record);
    }
}
