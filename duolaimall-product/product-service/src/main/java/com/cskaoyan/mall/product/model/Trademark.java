package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * Trademark
 * </p>
 *
 */
@Data
@TableName("trademark")
public class Trademark extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"属性值"
	@TableField("tm_name")
	private String tmName;

	//"品牌logo的图片路径"
	@TableField("logo_url")
	private String logoUrl;

}

