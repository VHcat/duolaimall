package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * SkuPlatformAttributeValue
 * </p>
 *
 */
@Data
@TableName("sku_platform_attr_value")
public class SkuPlatformAttributeValue extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"属性id（冗余)"
	@TableField("attr_id")
	private Long attrId;

	//"属性值id"
	@TableField("value_id")
	private Long valueId;

	//"skuid"
	@TableField("sku_id")
	private Long skuId;

}

