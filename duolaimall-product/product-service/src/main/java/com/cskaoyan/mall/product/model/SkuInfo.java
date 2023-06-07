package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * SkuInfo
 * </p>
 *
 */
@Data
@TableName("sku_info")
public class SkuInfo extends BaseEntity {


    public SkuInfo(){}
	public SkuInfo(Long skuId){
		setId(skuId);
	}
	//	判断去重的话，自动调用equals 方法。
	public boolean equals(SkuInfo skuInfo){
		return getId().equals(skuInfo.getId());
	}

	private static final long serialVersionUID = 1L;

	//"商品id"
	@TableField("spu_id")
	private Long spuId;

	//"价格"
	@TableField("price")
	private BigDecimal price;

	//"sku名称"
	@TableField("sku_name")
	private String skuName;

	//"商品规格描述"
	@TableField("sku_desc")
	private String skuDesc;

	//"重量"
	@TableField("weight")
	private String weight;

	//"品牌(冗余)"
	@TableField("tm_id")
	private Long tmId;

	//"三级分类id（冗余)"
	@TableField("third_level_category_id")
	private Long thirdLevelCategoryId;

	//"默认显示图片(冗余)"
	@TableField("sku_default_img")
	private String skuDefaultImg;

	//"是否销售（1：是 0：否）"
	@TableField("is_sale")
	private Integer isSale;

	@TableField(exist = false)
	List<SkuImage> SkuImageList;

	@TableField(exist = false)
	List<SkuPlatformAttributeValue> skuPlatformAttributeValueList;

	@TableField(exist = false)
	List<SkuSaleAttributeValue> skuSaleAttributeValueList;
}

