package com.coder.service;

public interface GoodsService {
    /**
     * 真正处理秒杀的业务
     *
     * @param userId
     * @param goodsId
     */
    void realSeckill(Integer userId, Integer goodsId);
}
