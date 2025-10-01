package com.coder.mapper;

import com.coder.entity.Goods;

import java.util.List;

public interface GoodsMapper {
    //查询参与秒杀的商品信息
    List<Goods> selectSeckillGoods();

    //更新库存
    int updateStock(Integer goodsId);
}
