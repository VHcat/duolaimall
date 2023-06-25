package com.cskaoyan.mall.order.dto;

import com.cskaoyan.mall.user.dto.UserAddressDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建日期: 2023/03/15 15:09
 */
@Data
public class OrderTradeDTO {

    // TODO 修改 userAddressList
    List<UserAddressDTO> userAddressList;
    List<OrderDetailDTO> detailArrayList;
    Integer totalNum;
    BigDecimal totalAmount;

    // 交易订单号，防止重复提交订单
    String tradeNo;
}
