package com.cskaoyan.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.product.client.SearchApiClient;
import com.cskaoyan.mall.product.dto.*;
import com.cskaoyan.mall.product.service.CategoryService;
import com.cskaoyan.mall.product.service.ProductDetailService;
import com.cskaoyan.mall.product.service.SkuService;
import com.cskaoyan.mall.product.service.SpuService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/13 21:40
 */
@Service
public class ProductDetailServiceImpl implements ProductDetailService {
    @Autowired
    SpuService spuService;

    @Autowired
    SkuService skuService;

    @Autowired
    CategoryService categoryService;


    @Autowired
    SearchApiClient searchApiClient;

    @Autowired
    RedissonClient redissonClient;
    @Override
    public ProductDetailDTO getItemBySkuId(Long skuId) {
        ProductDetailDTO productDetailDTO = new ProductDetailDTO();
//        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
//        if (!bloomFilter.contains(skuId)) {
//            return productDetailDTO;
//        }


        // 通过skuId 查询skuInfo

        SkuInfoDTO skuInfo = skuService.getSkuInfo(skuId);
        // 保存skuInfo
        productDetailDTO.setSkuInfo(skuInfo);

        // 销售属性-销售属性值回显并锁定

        List<SpuSaleAttributeInfoDTO> spuSaleAttrListCheckBySku
                = skuService.getSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());

        // 保存数据
        productDetailDTO.setSpuSaleAttrList(spuSaleAttrListCheckBySku);

        //根据spuId 查询map 集合属性
        // 销售属性-销售属性值回显并锁定

        Map<String, Long> skuValueIdsMap = spuService.getSkuValueIdsMap(skuInfo.getSpuId());

        String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
        // 保存valuesSkuJson
        productDetailDTO.setValuesSkuJson(valuesSkuJson);


        //获取商品最新价格

        BigDecimal skuPrice = skuService.getSkuPrice(skuId);
        productDetailDTO.setPrice(skuPrice);


        //获取分类信息

        CategoryHierarchyDTO categoryViewByCategory = categoryService.getCategoryViewByCategoryId(skuInfo.getThirdLevelCategoryId());

        //分类信息
        productDetailDTO.setCategoryHierarchy(categoryViewByCategory);

        //  获取海报数据

        //  spu海报数据
        List<SpuPosterDTO> spuPosterBySpuId = spuService.findSpuPosterBySpuId(skuInfo.getSpuId());
        productDetailDTO.setSpuPosterList(spuPosterBySpuId);

        //  获取sku平台属性，即规格数据

        List<PlatformAttributeInfoDTO> platformAttrInfoBySku = skuService.getPlatformAttrInfoBySku(skuId);
        List<SkuSpecification> skuAttrList = platformAttrInfoBySku.stream().map((baseAttrInfo) -> {

            SkuSpecification skuSpecification = new SkuSpecification();
            skuSpecification.setAttrName(baseAttrInfo.getAttrName());
            skuSpecification.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());

            return skuSpecification;
        }).collect(Collectors.toList());
        productDetailDTO.setSkuAttrList(skuAttrList);

        //更新商品incrHotScore
        //searchApiClient.incrHotScore(skuId);

        return productDetailDTO;
    }
}
