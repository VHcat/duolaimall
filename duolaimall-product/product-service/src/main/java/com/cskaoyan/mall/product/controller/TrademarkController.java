package com.cskaoyan.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.product.dto.TrademarkPageDTO;
import com.cskaoyan.mall.product.model.Trademark;
import com.cskaoyan.mall.product.query.TrademarkParam;
import com.cskaoyan.mall.product.service.TrademarkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class TrademarkController {

    @Autowired
    private TrademarkService trademarkService;

    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit) {

        Page<Trademark> pageParam = new Page<>(page, limit);
        TrademarkPageDTO trademarkPage = trademarkService.getPage(pageParam);
        return Result.ok(trademarkPage);
    }

    @GetMapping("/baseTrademark/get/{id}")
    public Result get(@PathVariable Long id) {
        TrademarkDTO trademarkDTO = trademarkService.getTrademarkByTmId(id);
        return Result.ok(trademarkDTO);
    }

    @PostMapping("/baseTrademark/save")
    public Result save(@RequestBody TrademarkParam banner) {
        trademarkService.save(banner);
        return Result.ok();
    }


    @PutMapping("/baseTrademark/update")
    public Result updateById(@RequestBody TrademarkParam banner) {
        trademarkService.updateById(banner);
        return Result.ok();
    }


    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result remove(@PathVariable Long id) {
        trademarkService.removeById(id);
        return Result.ok();
    }


}
