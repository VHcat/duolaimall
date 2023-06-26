package com.cskaoyan.mall.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class SkuInfoDTO {

    private Long id;


    private Long spuId;


    private BigDecimal price;


    private String skuName;


    private String skuDesc;


    private String weight;


    private Long tmId;


    private Long thirdLevelCategoryId;


    private String skuDefaultImg;


    private Integer isSale;


    List<SkuImageDTO> skuImageList;

    List<SkuPlatformAttributeValueDTO> skuPlatformAttributeValueList;

    List<SkuSaleAttributeValueDTO> skuSaleAttributeValueList;
}
