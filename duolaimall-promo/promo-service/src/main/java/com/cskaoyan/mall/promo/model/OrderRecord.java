package com.cskaoyan.mall.promo.model;

import com.cskaoyan.mall.promo.api.dto.SeckillGoodsDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户秒杀成功，订单记录
 */
@Data
public class OrderRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;

	private SeckillGoods seckillGoods;

	private Integer num;

	private String orderStr;
}
