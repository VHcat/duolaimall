//
//
package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * CategoryHierarchy
 * </p>
 *
 */
@Data
public class CategoryHierarchy implements Serializable {

	private static final long serialVersionUID = 1L;

	 //"一级分类编号"
	@TableField("first_level_category_id")
	private Long firstLevelCategoryId;

	//"一级分类名称"
	@TableField("first_level_category_name")
	private String firstLevelCategoryName;

	//"二级分类编号"
	@TableField("second_level_category_id")
	private Long secondLevelCategoryId;

	//"二级分类名称"
	@TableField("second_level_category_name")
	private String secondLevelCategoryName;

	//"三级分类编号"
	@TableField("third_level_category_id")
	private Long thirdLevelCategoryId;

	//"三级分类名称"
	@TableField("third_level_category_name")
	private String thirdLevelCategoryName;

}

