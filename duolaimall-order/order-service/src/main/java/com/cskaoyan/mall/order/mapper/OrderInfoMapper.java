package com.cskaoyan.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.order.model.OrderInfo;
import org.apache.ibatis.annotations.Param;

public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 我的订单：查询我的订单分页列表
     */
    Page<OrderInfo> selectPageByUserId(@Param("pageParam") Page<OrderInfo> pageParam, @Param("userId") String userId);


}
