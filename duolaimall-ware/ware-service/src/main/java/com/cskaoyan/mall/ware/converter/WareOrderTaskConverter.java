package com.cskaoyan.mall.ware.converter;

import com.cskaoyan.mall.order.dto.OrderDetailDTO;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDTO;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDetailDTO;
import com.cskaoyan.mall.ware.model.WareOrderTask;
import com.cskaoyan.mall.ware.model.WareOrderTaskDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WareOrderTaskConverter {

    @Mapping(source = "id",target = "orderId")
    @Mapping(source = "tradeBody",target = "orderBody" )
    @Mapping(source = "orderDetailList",target = "details")
    WareOrderTask convertOrderInfoDTO(OrderInfoDTO orderInfoDTO);

    WareOrderTaskDetail convertOrderDetailDTO(OrderDetailDTO orderDetailDTO);

    WareOrderTask converWareOrderTask(WareOrderTaskDTO wareOrderTaskDTO);

    WareOrderTaskDetail convertOrderTaskDetailDTO(WareOrderTaskDetailDTO wareOrderTaskDetailDTO);

    List<WareOrderTask> convertWareOrderTaskDTO(List<WareOrderTaskDTO> wareOrderTaskDTOS);


}
