//
//
package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * CategoryHierarchy
 * </p>
 *
 */
@Data
@TableName("category_trademark")
public class CategoryTrademark extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//"三级分类编号"
	@TableField("third_level_category_id")
	private Long thirdLevelCategoryId;

	//"品牌id"
	@TableField("trademark_id")
	private Long trademarkId;

	@TableField(exist = false)
	private Trademark trademark;
}

