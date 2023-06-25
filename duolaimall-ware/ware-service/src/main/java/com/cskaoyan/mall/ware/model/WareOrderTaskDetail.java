package com.cskaoyan.mall.ware.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 库存工作单明细
 */
@Data
public class WareOrderTaskDetail {

    @TableField
    private String skuId;

    @TableField
    private String skuName;

    @TableField
    private Integer skuNum;

    @TableField
    private Long taskId;
}
