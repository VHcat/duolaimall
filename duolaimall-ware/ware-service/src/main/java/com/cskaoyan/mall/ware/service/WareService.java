package com.cskaoyan.mall.ware.service;

import com.cskaoyan.mall.ware.model.WareOrderTask;

import java.util.List;

/**
 * @param
 * @return
 */
public interface WareService {


    // 校验库存
    Boolean hasStock(Long skuId, Integer num);

    // 扣减库存
    void decreaseStock(Long orderId);

    // 检查拆单
    public List<WareOrderTask> checkOrderSplit(WareOrderTask wareOrderTask);

    // 扣减库存
    public void lockStock(WareOrderTask wareOrderTask);
}
