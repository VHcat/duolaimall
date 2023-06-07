package com.cskaoyan.mall.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
     该类封装了商品详情页所需的所有数据
 */
@Data
public class ProductDetailDTO {

    // 商品sku 信息
    SkuInfoDTO skuInfo;

    // 获取指定的sku完整的销售属性值
    List<SpuSaleAttributeInfoDTO> spuSaleAttrList;

    // 获取spu中包含的所有的不同销售属性取值的组合
    String valuesSkuJson;

    // 获取sku商品的价格
    BigDecimal price;

    // 获取三级类目的完整类目视图
    CategoryHierarchyDTO categoryHierarchy;
    // 获取sku商品的海报列表
    List<SpuPosterDTO> spuPosterList;
    // 获取sku商品对应的平台属性集合(规格参数)
    List<SkuSpecification> skuAttrList;
}
