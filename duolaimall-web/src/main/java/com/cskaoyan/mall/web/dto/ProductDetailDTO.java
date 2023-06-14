package com.cskaoyan.mall.web.dto;

import com.cskaoyan.mall.product.dto.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/13 09:19
 */
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

    // 获取完整的三级类目信息
    CategoryHierarchyDTO categoryHierarchy;
    // 获取sku所属的SPU商品的海报列表
    List<SpuPosterDTO> spuPosterList;
    // 获取sku商品对应的平台属性集合(规格参数)
    List<SkuSpecification> skuAttrList;
}
