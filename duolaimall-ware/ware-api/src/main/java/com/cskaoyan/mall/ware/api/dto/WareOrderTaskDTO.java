package com.cskaoyan.mall.ware.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建日期: 2023/03/18 16:30
 *
 * @author ciggar
 */
@Data
public class WareOrderTaskDTO {

    private String orderId;

    private String consignee;

    private String consigneeTel;

    private String deliveryAddress;

    private String orderComment;

    private String paymentWay;

    private String taskStatus;

    private String orderBody;

    private String trackingNo;

    private String wareId;

    private String taskComment;

    private List<WareOrderTaskDetailDTO> details;
}
