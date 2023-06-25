package com.cskaoyan.mall.ware.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cskaoyan.mall.common.model.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 库存工作单
 */

@Data
public class WareOrderTask extends BaseEntity {

    @TableField
    private String orderId;

    @TableField
    private String consignee;

    @TableField
    private String consigneeTel;

    @TableField
    private String deliveryAddress;

    @TableField
    private String orderComment;

    @TableField
    private String paymentWay;

    @TableField
    private String taskStatus;

    @TableField
    private String orderBody;

    @TableField
    private String trackingNo;

    @TableField
    private String wareId;

    @TableField
    private String taskComment;

    @TableField(exist = false)
    private List<WareOrderTaskDetail> details;

}
