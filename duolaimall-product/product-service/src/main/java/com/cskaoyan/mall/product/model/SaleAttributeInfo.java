package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * SaleAttrributeInfo
 * </p>
 *
 */
@Data
@TableName("sale_attr_info")
public class SaleAttributeInfo extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"销售属性名称"
	@TableField("name")
	private String name;

}

