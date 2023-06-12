package com.cskaoyan.mall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.TrademarkDTO;
import com.cskaoyan.mall.product.dto.TrademarkPageDTO;
import com.cskaoyan.mall.product.model.Trademark;
import com.cskaoyan.mall.product.query.TrademarkParam;
import com.cskaoyan.mall.product.service.TrademarkService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/08 14:03
 */
@RestController
@RequestMapping("/admin/product")
public class TrademarkController {
    @Resource
    TrademarkService trademarkService;

    // http://localhost/admin/product/baseTrademark/1/10
    // 查看品牌列表
    @GetMapping("/baseTrademark/{pageNo}/{pageSize}")
    public Result<TrademarkPageDTO> getTradeMarkDTOList(@PathVariable Long pageNo, @PathVariable Long pageSize) {
        Page<Trademark> trademarkPage = new Page<>(pageNo, pageSize);
        TrademarkPageDTO page = trademarkService.getPage(trademarkPage);
        return Result.ok(page);
    }

    // 保存品牌
    //http://localhost/admin/product/baseTrademark/save
    @PostMapping("/baseTrademark/save")
    public Result save(@RequestBody TrademarkParam trademarkParam) {
        trademarkService.save(trademarkParam);
        return Result.ok();
    }

    // http://localhost/admin/product/baseTrademark/remove/10
    // 删除品牌
    @DeleteMapping("/baseTrademark/remove/{tradeMarkId}")
    public Result deleteById(@PathVariable Long tradeMarkId) {
        trademarkService.removeById(tradeMarkId);
        return Result.ok();
    }


    // http://localhost/admin/product/baseTrademark/get/17
    // 查询品牌
    @GetMapping("/baseTrademark/get/{tradeMarkId}")
    public Result<TrademarkDTO> getTradeMarkDTO(@PathVariable Long tradeMarkId) {
        TrademarkDTO trademarkByTmId = trademarkService.getTrademarkByTmId(tradeMarkId);
        return Result.ok(trademarkByTmId);
    }

    // 修改品牌
    // http://localhost/admin/product/baseTrademark/update
    @PutMapping("/baseTrademark/update")
    public Result updateTradeMark(@RequestBody TrademarkParam trademarkParam) {
        trademarkService.updateById(trademarkParam);
        return Result.ok();
    }
}
