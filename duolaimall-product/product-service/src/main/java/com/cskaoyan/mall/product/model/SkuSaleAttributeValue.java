package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * SkuSaleAttributeValue
 * </p>
 *
 */
@Data
@TableName("sku_sale_attr_value")
public class SkuSaleAttributeValue extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"库存单元id"
	@TableField("sku_id")
	private Long skuId;

	//"spu_id(冗余)"
	@TableField("spu_id")
	private Long spuId;

	//"销售属性值id"
	@TableField("spu_sale_attr_value_id")
	private Long spuSaleAttrValueId;

}

