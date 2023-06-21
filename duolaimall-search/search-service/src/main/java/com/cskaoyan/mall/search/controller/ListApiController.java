package com.cskaoyan.mall.search.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.search.dto.SearchResponseDTO;
import com.cskaoyan.mall.search.param.SearchParam;
import com.cskaoyan.mall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/list")
public class ListApiController {

    @Autowired
    private SearchService searchService;


    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        searchService.upperGoods(skuId);

        return Result.ok();
    }

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 更新商品incrHotScore
     *
     * @param skuId
     * @return
     */
    @GetMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId) {
        // 调用服务层
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    /**
     * 搜索商品
     * @param searchParam
     * @return
     * @throws IOException
     */
    @PostMapping()
    public Result list(@RequestBody SearchParam searchParam) throws IOException {
        SearchResponseDTO response = searchService.search(searchParam);
        return Result.ok(response);
    }
}
