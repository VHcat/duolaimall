//
//
package com.cskaoyan.mall.product.query;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * CategoryHierarchy
 * </p>
 *
 */
@Data
public class CategoryTrademarkParam {
	
	//"三级分类编号"
	private Long category3Id;

	//"品牌id"
	private List<Long> trademarkIdList;

}

