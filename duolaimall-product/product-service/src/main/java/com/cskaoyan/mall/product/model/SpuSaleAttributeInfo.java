package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SpuSaleAttrbuteInfo
 * </p>
 *
 */
@Data
@TableName("spu_sale_attr_info")
public class SpuSaleAttributeInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//"商品id"
	@TableField("spu_id")
	private Long spuId;

	//"销售属性id"
	@TableField("sale_attr_id")
	private Long saleAttrId;

	//"销售属性名称(冗余)"
	@TableField("sale_attr_name")
	private String saleAttrName;

	// 销售属性值对象集合
	@TableField(exist = false)
	List<SpuSaleAttributeValue> spuSaleAttrValueList;

}

