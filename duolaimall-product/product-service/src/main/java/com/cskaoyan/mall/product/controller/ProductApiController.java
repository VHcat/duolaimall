package com.cskaoyan.mall.product.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private SkuService skuService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuService spuService;

    @Autowired
    private TrademarkService trademarkService;

    /**
     * 根据skuId获取sku信息
     */
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfoDTO getSkuInfo(@PathVariable("skuId") Long skuId) {
        SkuInfoDTO skuInfo = skuService.getSkuInfo(skuId);
        return skuInfo;
    }

    /**
     * 通过三级分类id查询分类信息
     */
    @GetMapping("inner/getCategoryView/{category3Id}")
    public CategoryHierarchyDTO getCategoryView(@PathVariable("category3Id")Long category3Id){
        return categoryService.getCategoryViewByCategoryId(category3Id);
    }

    /**
     * 获取sku最新价格
     * @param skuId
     * @return
     */
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
        return skuService.getSkuPrice(skuId);
    }

    /**
     * 根据spuId，skuId 查询销售属性集合
     */
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttributeInfoDTO> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId){
        return skuService.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    /**
     * 根据spuId 查询map 集合属性
     */
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        return spuService.getSkuValueIdsMap(spuId);
    }

    //  根据spuId 获取海报数据
    @GetMapping("inner/findSpuPosterBySpuId/{spuId}")
    public List<SpuPosterDTO> findSpuPosterBySpuId(@PathVariable Long spuId){
        return spuService.findSpuPosterBySpuId(spuId);
    }

    /**
     * 通过skuId 集合来查询数据
     * @param skuId
     * @return
     */
    @GetMapping("inner/getAttrList/{skuId}")
    public List<PlatformAttributeInfoDTO> getAttrList(@PathVariable("skuId") Long skuId){
        return skuService.getPlatformAttrInfoBySku(skuId);
    }

    /**
     * 获取全部分类信息
     * @return
     */
    @GetMapping("getCategoryList")
    public Result getBaseCategoryList(){
        List<FirstLevelCategoryNodeDTO> categoryTreeList = categoryService.getCategoryTreeList();
        return Result.ok(categoryTreeList);
    }


    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("inner/getTrademark/{tmId}")
    public TrademarkDTO getTrademark(@PathVariable("tmId")Long tmId){
        return trademarkService.getTrademarkByTmId(tmId);
    }


}
