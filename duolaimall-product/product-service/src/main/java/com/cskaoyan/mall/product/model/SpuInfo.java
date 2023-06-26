package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SpuInfo
 * </p>
 *
 */
@Data
@TableName("spu_info")
public class SpuInfo extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"商品名称"
	@TableField("spu_name")
	private String spuName;

	//"商品描述(后台简述）"
	@TableField("description")
	private String description;

	//"三级分类id"
	@TableField("third_level_category_id")
	private Long thirdLevelCategoryId;

	//"品牌id"
	@TableField("tm_id")
	private Long tmId;

	// 销售属性集合
	@TableField(exist = false)
	private List<SpuSaleAttributeInfo> spuSaleAttributeInfoList;

	// 商品的图片集合
	@TableField(exist = false)
	private List<SpuImage> SpuImageList;

	// 商品的海报图片集合
	@TableField(exist = false)
	private List<SpuPoster> SpuPosterList;
}

