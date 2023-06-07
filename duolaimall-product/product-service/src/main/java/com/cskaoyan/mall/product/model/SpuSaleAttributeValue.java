package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;


/**
 * <p>
 * SpuSaleAttributeValue
 * </p>
 *
 */
@Data
@TableName("spu_sale_attr_value")
public class SpuSaleAttributeValue extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//"商品id"
	@TableField("spu_id")
	private Long spuId;

	//"销售属性id"
	@TableField("spu_sale_attr_id")
	private Long spuSaleAttrId;

	// "销售属性值名称"
	@TableField("spu_sale_attr_value_name")
	private String spuSaleAttrValueName;

	// 是否是默认选中状态
	@TableField(exist = false)
	String isChecked;

}

