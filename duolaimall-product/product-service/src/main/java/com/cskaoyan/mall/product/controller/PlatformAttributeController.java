package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.PlatformAttributeValueDTO;
import com.cskaoyan.mall.product.query.PlatformAttributeParam;
import com.cskaoyan.mall.product.service.PlatformAttributeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/08 14:05
 */
@RestController
@RequestMapping("/admin/product")
public class PlatformAttributeController {
    @Resource
    PlatformAttributeService platformAttributeService;

    @GetMapping("/attrInfoList/{firstLevelCategoryId}/{secondLevelCategoryId}/{thirdLevelCategoryId}")
    public Result getAttrInfoList(@PathVariable Long firstLevelCategoryId,
                                  @PathVariable Long secondLevelCategoryId,
                                  @PathVariable Long thirdLevelCategoryId) {
        List<PlatformAttributeInfoDTO> platformAttrInfoList
                = platformAttributeService.getPlatformAttrInfoList(firstLevelCategoryId, secondLevelCategoryId, thirdLevelCategoryId);
        return Result.ok(platformAttrInfoList);
    }

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody PlatformAttributeParam platformAttributeParam) {
        platformAttributeService.savePlatformAttrInfo(platformAttributeParam);
        return Result.ok();
    }

    // 平台属性值回显
    @GetMapping("/getAttrValueList/{attrId}")
    public Result<List<PlatformAttributeValueDTO>> getAttrInfoDTO(@PathVariable Long attrId) {
        PlatformAttributeInfoDTO platformAttrInfo = platformAttributeService.getPlatformAttrInfo(attrId);
        List<PlatformAttributeValueDTO> attrValueList = platformAttrInfo.getAttrValueList();
        return Result.ok(attrValueList);
    }
}
