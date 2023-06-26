package com.cskaoyan.mall.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

/**
 * <p>
 * SpuImage
 * </p>
 *
 */
@Data
@TableName("spu_image")
public class SpuImage extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	//"商品id"
	@TableField("spu_id")
	private Long spuId;

	//"图片名称"
	@TableField("img_name")
	private String imgName;

	//"图片路径"
	@TableField("img_url")
	private String imgUrl;

}

