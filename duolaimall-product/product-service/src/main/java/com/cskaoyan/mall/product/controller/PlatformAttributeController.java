package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.PlatformAttributeValueDTO;
import com.cskaoyan.mall.product.query.PlatformAttributeParam;
import com.cskaoyan.mall.product.service.PlatformAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class PlatformAttributeController {

    @Autowired
    PlatformAttributeService platformAttributeService;

    /**
     * 根据分类Id 获取平台属性数据
     */
    @GetMapping("attrInfoList/{firstLevelCategoryId}/{secondLevelCategoryId}/{thirdLevelCategoryId}")
    public Result<List<PlatformAttributeInfoDTO>> attrInfoList(@PathVariable("firstLevelCategoryId") Long firstLevelCategoryId,
                                                               @PathVariable("secondLevelCategoryId") Long secondLevelCategoryId,
                                                               @PathVariable("thirdLevelCategoryId") Long thirdLevelCategoryId) {
        List<PlatformAttributeInfoDTO> platformAttrInfoList =
                platformAttributeService.getPlatformAttrInfoList(firstLevelCategoryId,
                        secondLevelCategoryId, thirdLevelCategoryId);
        return Result.ok(platformAttrInfoList);
    }

    /**
     * 保存平台属性方法
     * @param
     * @return
     */
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody PlatformAttributeParam platformAttributeParam) {
        // 前台数据都被封装到该对象中baseAttrInfo
        platformAttributeService.savePlatformAttrInfo(platformAttributeParam);
        return Result.ok();
    }

    @GetMapping("getAttrValueList/{attrId}")
    public Result<List<PlatformAttributeValueDTO>> getAttrValueList(@PathVariable("attrId") Long attrId) {
        PlatformAttributeInfoDTO platformAttrInfo = platformAttributeService.getPlatformAttrInfo(attrId);
        List<PlatformAttributeValueDTO> attrValueList = platformAttrInfo.getAttrValueList();
        return Result.ok(attrValueList);
    }
}
