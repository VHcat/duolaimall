package com.cskaoyan.mall.order.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.order.model.OrderDetail;
import com.cskaoyan.mall.order.model.OrderInfo;
import com.cskaoyan.mall.order.query.OrderDetailParam;
import com.cskaoyan.mall.order.query.OrderInfoParam;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDTO;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderInfoConverter {

    OrderInfoDTO convertOrderInfoToOrderInfoDTO(OrderInfo orderInfo);

    OrderInfo convertOrderInfoParam(OrderInfoParam orderInfoParam);

    OrderDetail convertOrderDetailParam(OrderDetailParam orderDetailParam);

    @Mapping(source = "id",target = "orderId")
    @Mapping(source = "tradeBody",target = "orderBody" )
    @Mapping(source = "orderDetailList",target = "details")
    WareOrderTaskDTO convertOrderInfoToWareOrderTaskDTO(OrderInfo orderInfo);

    WareOrderTaskDetailDTO convertDetail(OrderDetail orderDetail);

    OrderInfo copyOrderInfo(OrderInfoDTO orderInfo);

    Page<OrderInfoDTO> orderInfoPageToPageDTO(Page<OrderInfo> orderInfoPage);

}
