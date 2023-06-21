package com.cskaoyan.mall.search.service;

import com.cskaoyan.mall.search.dto.SearchResponseDTO;
import com.cskaoyan.mall.search.param.SearchParam;

import java.io.IOException;

public interface SearchService {

    /**
     * 上架商品列表
     * @param skuId
     */
    void upperGoods(Long skuId);

    /**
     * 下架商品列表
     * @param skuId
     */
    void lowerGoods(Long skuId);

    /**
     * 更新热点
     * @param skuId
     */
    void incrHotScore(Long skuId);

    /**
     * 搜索列表
     * @param searchParam
     * @return
     * @throws IOException
     */
    SearchResponseDTO search(SearchParam searchParam) throws IOException;
}
