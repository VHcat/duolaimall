package com.cskaoyan.mall.ware.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 商品库存单元
 */
@Data
public class WareSku {

    @TableId(type = IdType.AUTO)
    private  String id ;

    @TableField
    private String skuId;

    @TableField
    private String warehouseId;

    // 库存数
    @TableField
    private Integer stock=0;

    @TableField
    private  String stockName;

    // 锁定库存数
    @TableField
    private Integer stockLocked;

    @TableField(exist = false)
    private  String warehouseName;
}
