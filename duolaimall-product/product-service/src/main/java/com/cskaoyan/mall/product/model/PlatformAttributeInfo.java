package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * PlatformAttributeInfo
 * </p>
 *
 */
@Data
@TableName("platform_attr_info")
public class PlatformAttributeInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//"属性名称"
	@TableField("attr_name")
	private String attrName;

	//"分类id"
	@TableField("category_id")
	private Long categoryId;

	//"分类层级"
	@TableField("category_level")
	private Integer categoryLevel;

	//	平台属性值集合，这里注意一个平台属性，有多个属性取值
	@TableField(exist = false)
	private List<PlatformAttributeValue> attrValueList;

}

