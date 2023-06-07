package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * SpuPoster
 * </p>
 *

 */
@Data
@TableName("spu_poster")
public class SpuPoster extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//"商品id"
	@TableField("spu_id")
	private Long spuId;

	// "文件名称"
	@TableField("img_name")
	private String imgName;

	// "文件路径"
	@TableField("img_url")
	private String imgUrl;

}

