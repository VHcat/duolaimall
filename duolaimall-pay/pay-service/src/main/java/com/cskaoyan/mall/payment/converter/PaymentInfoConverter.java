package com.cskaoyan.mall.payment.converter;

import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.pay.api.dto.PaymentInfoDTO;
import com.cskaoyan.mall.payment.model.PaymentInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 创建日期: 2023/03/17 14:48
 *
 * @author ciggar
 */
@Mapper(componentModel = "spring")
public interface PaymentInfoConverter {

    @Mapping(source = "id",target = "orderId")
    @Mapping(source = "tradeBody",target = "subject")
    @Mapping(source = "totalAmount",target = "totalAmount")
    @Mapping(source = "userId",target = "userId")
    @Mapping(source = "outTradeNo",target = "outTradeNo")
    PaymentInfo contvertOrderInfoDTO2PaymentInfo(OrderInfoDTO orderInfoDTO);


    PaymentInfoDTO convertPaymentInfoToDTO(PaymentInfo paymentInfo);
}
