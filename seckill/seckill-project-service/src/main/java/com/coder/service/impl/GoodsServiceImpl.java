package com.coder.service.impl;

import com.coder.entity.Order;
import com.coder.mapper.GoodsMapper;
import com.coder.mapper.OrderMapper;
import com.coder.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void realSeckill(Integer userId, Integer goodsId) {
        int i = goodsMapper.updateStock(goodsId);
        if(i > 0){
            Order order = new Order();
            order.setGoodsid(goodsId);
            order.setUserid(userId);
            order.setCreatetime(new Date());
            orderMapper.insert(order);
        }
    }
}
