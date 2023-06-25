package com.cskaoyan.mall.promo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 功能描述: 用户秒杀下单记录
 */
@Data
public class UserRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long skuId;

	private String userId;
}
