package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * ThirdLevelCategory
 * </p>
 *
 */
@Data
@TableName("third_level_category")
public class ThirdLevelCategory extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"三级分类名称"
	@TableField("name")
	private String name;

	//"二级分类编号"
	@TableField("second_level_category_id")
	private Long secondLevelCategoryId;

}

