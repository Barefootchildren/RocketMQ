package com.coder.config;

import com.coder.entity.Goods;
import com.coder.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DataSync {
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @PostConstruct
    public void initData(){
        List<Goods> goodsList = goodsMapper.selectSeckillGoods();
        if(CollectionUtils.isEmpty(goodsList)){
            return;
        }
        goodsList.forEach(goods -> {
            redisTemplate.opsForValue().set("goodsId:"+goods.getGoodsId(),goods.getTotalStocks().toString());
        });
    }
}
