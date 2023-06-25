package com.cskaoyan.mall.ware.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.ware.api.OrderApiClient;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDTO;
import com.cskaoyan.mall.ware.api.dto.WareSkuDTO;
import com.cskaoyan.mall.ware.api.constant.TaskStatus;
import com.cskaoyan.mall.ware.converter.WareOrderTaskConverter;
import com.cskaoyan.mall.ware.mapper.WareOrderTaskDetailMapper;
import com.cskaoyan.mall.ware.mapper.WareOrderTaskMapper;
import com.cskaoyan.mall.ware.mapper.WareSkuMapper;
import com.cskaoyan.mall.ware.model.WareOrderTask;
import com.cskaoyan.mall.ware.model.WareOrderTaskDetail;
import com.cskaoyan.mall.ware.model.WareSku;
import com.cskaoyan.mall.ware.service.WareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Slf4j
@Service
public class WareServiceImpl implements WareService {

    @Autowired
    WareSkuMapper wareSkuMapper;

    @Autowired
    OrderApiClient orderApiClient;

    @Autowired
    WareOrderTaskConverter wareOrderTaskConverter;

    @Autowired
    WareOrderTaskMapper wareOrderTaskMapper;

    @Autowired
    WareOrderTaskDetailMapper wareOrderTaskDetailMapper;



    // 校验库存
    @Override
    public Boolean hasStock(Long skuId, Integer num) {
        Integer stock = getStockBySkuId(skuId);

        if (stock == null || stock < num) {
            return false;
        }
        return true;
    }

    public Integer getStockBySkuId(Long skuId) {
        Integer stock = wareSkuMapper.selectStockBySkuid(skuId.toString());
        return stock;
    }


    // 扣减库存
    @Override
    public void decreaseStock(Long orderId) {

        // 获取订单信息
        OrderInfoDTO orderInfoDTO = orderApiClient.getOrderInfoDTOByOrderId(orderId);
        // 将订单信息转化库存工作单
        WareOrderTask wareOrderTask = wareOrderTaskConverter.convertOrderInfoDTO(orderInfoDTO);
        wareOrderTask.setTaskStatus(TaskStatus.PAID.name());

        // 保存库存工作单以及明细
        this.saveWareOrderTask(wareOrderTask);

        // 检查是否需要拆单
        List<WareOrderTask> orderTaskList = this.checkOrderSplit(wareOrderTask);

        // 如果需要拆单
        if (orderTaskList != null && orderTaskList.size() >= 2) {
            for (WareOrderTask orderTask : orderTaskList) {
                this.lockStock(orderTask);
            }
        }
        // 如果不需要拆单
        else {
            this.lockStock(wareOrderTask);
        }
    }



    // 检查拆单
    @Override
    @Transactional
    public List<WareOrderTask> checkOrderSplit(WareOrderTask wareOrderTask) {
        List<WareOrderTaskDetail> details = wareOrderTask.getDetails();
        // 获取所有sku id
        List<String> skulist = new ArrayList<>();
        for (WareOrderTaskDetail detail : details) {
            skulist.add(detail.getSkuId());
        }
        // 获取仓库Id和skuIds的映射
        List<WareSkuDTO> wareSkuDTOList = getWareSkuDTO(skulist);

        if (wareSkuDTOList.size() == 1) {
            WareSkuDTO wareSkuDTO = wareSkuDTOList.get(0);
            wareOrderTask.setWareId(wareSkuDTO.getWareId());
        } else {
            //需要拆单
            String orderId = wareOrderTask.getOrderId();

            // 调用订单服务拆单接口
            List<WareOrderTaskDTO> wareOrderTaskDTOS = orderApiClient.orderSplit(orderId,wareSkuDTOList);

            List<WareOrderTask> wareOrderTaskList = wareOrderTaskConverter.convertWareOrderTaskDTO(wareOrderTaskDTOS);
            if (wareOrderTaskDTOS.size() >= 2) {
                for (WareOrderTask subOrderTask : wareOrderTaskList) {
                    subOrderTask.setTaskStatus(TaskStatus.DEDUCTED.name());
                    saveWareOrderTask(subOrderTask);
                }
                updateStatusWareOrderTaskByOrderId(wareOrderTask.getOrderId(), TaskStatus.SPLIT);
                return wareOrderTaskList;
            } else {
                throw new RuntimeException("拆单异常!!");
            }
        }
        return null;
    }


    // 获取仓库id和skuId的映射
    public List<WareSkuDTO> getWareSkuDTO(List<String> skuIdlist) {

        QueryWrapper<WareSku> queryWrapper = new QueryWrapper();
        queryWrapper.in("sku_id", skuIdlist);
        // 查询所有的sku所属的仓库id
        List<WareSku> wareSkuList = wareSkuMapper.selectList(queryWrapper);

        Map<String, List<String>> wareSkuMap = new HashMap<>();
        // 将sku id合并到对应的仓库id的list中
        for (WareSku wareSku : wareSkuList) {
            // 查询是否已经有该仓库
            List<String> skulistOfWare = wareSkuMap.get(wareSku.getWarehouseId());
            if (skulistOfWare == null) {
                // 没有就创建仓库对应的list
                skulistOfWare = new ArrayList<>();
            }
            // 向该仓库中添加sku id
            skulistOfWare.add(wareSku.getSkuId());
            // 1 - 33  | 2 - 35 36
            wareSkuMap.put(wareSku.getWarehouseId(), skulistOfWare);
        }
        List<WareSkuDTO> wareSkuDTOs = wareSkuMap.keySet().stream().map(key -> {
            WareSkuDTO wareSkuDTO = new WareSkuDTO();
            wareSkuDTO.setWareId(key);
            wareSkuDTO.setSkuIds(wareSkuMap.get(key));
            return wareSkuDTO;
        }).collect(Collectors.toList());

        return wareSkuDTOs;
    }

    public List<Map<String, Object>> convertWareSkuMapList(Map<String, List<String>> wareSkuMap) {
        List<Map<String, Object>> wareSkuMapList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : wareSkuMap.entrySet()) {
            Map<String, Object> skuWareMap = new HashMap<>();
            String wareid = entry.getKey();
            skuWareMap.put("wareId", wareid);
            List<String> skuids = entry.getValue();
            skuWareMap.put("skuIds", skuids);
            wareSkuMapList.add(skuWareMap);
        }
        return wareSkuMapList;
    }

    // 修改库存工作清单 状态
    public void updateStatusWareOrderTaskByOrderId(String orderId, TaskStatus taskStatus) {
        QueryWrapper<WareOrderTask> queryWrapper = new QueryWrapper();
        queryWrapper.in("order_id", orderId);
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setTaskStatus(taskStatus.name());
        wareOrderTaskMapper.update(wareOrderTask, queryWrapper);
    }


    @Override
    @Transactional
    public void lockStock(WareOrderTask wareOrderTask) {

        List<WareOrderTaskDetail> wareOrderTaskDetails = wareOrderTask.getDetails();
        String comment = "";
        for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetails) {

            WareSku wareSku = new WareSku();
            wareSku.setWarehouseId(wareOrderTask.getWareId());
            wareSku.setStockLocked(wareOrderTaskDetail.getSkuNum());
            wareSku.setSkuId(wareOrderTaskDetail.getSkuId());

            int availableStock = wareSkuMapper.selectStockBySkuidForUpdate(wareSku); //查询可用库存 加行级写锁 注意索引避免表锁
            if (availableStock - wareOrderTaskDetail.getSkuNum() < 0) {
                comment += "减库存异常：名称：" + wareOrderTaskDetail.getSkuName() + "，实际可用库存数" + availableStock + ",要求库存" + wareOrderTaskDetail.getSkuNum();
            }
        }

        if (comment.length() > 0) {   //库存超卖 记录日志，返回错误状态
            wareOrderTask.setTaskComment(comment);
            wareOrderTask.setTaskStatus(TaskStatus.OUT_OF_STOCK.name());
            updateStatusWareOrderTaskByOrderId(wareOrderTask.getOrderId(), TaskStatus.OUT_OF_STOCK);

        } else {   //库存正常  进行减库存
            for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetails) {

                WareSku wareSku = new WareSku();
                wareSku.setWarehouseId(wareOrderTask.getWareId());
                wareSku.setStockLocked(wareOrderTaskDetail.getSkuNum());
                wareSku.setSkuId(wareOrderTaskDetail.getSkuId());

                wareSkuMapper.incrStockLocked(wareSku); //  加行级写锁 注意索引避免表锁

            }
            wareOrderTask.setTaskStatus(TaskStatus.DEDUCTED.name());
            updateStatusWareOrderTaskByOrderId(wareOrderTask.getOrderId(), TaskStatus.DEDUCTED);
        }

        // 远程调用顶订单服务，修改订单状态
        orderApiClient.successLockStock(wareOrderTask.getOrderId(),wareOrderTask.getTaskStatus());

        return;
    }

    @Transactional
    private WareOrderTask saveWareOrderTask(WareOrderTask wareOrderTask) {
        wareOrderTask.setCreateTime(new Date());

        QueryWrapper<WareOrderTask> queryWrapper = new QueryWrapper();
        queryWrapper.in("order_id", wareOrderTask.getOrderId());
        WareOrderTask wareOrderTaskOrigin = wareOrderTaskMapper.selectOne(queryWrapper);
        if (wareOrderTaskOrigin != null) {
            return wareOrderTaskOrigin;
        }

        wareOrderTaskMapper.insert(wareOrderTask);

        List<WareOrderTaskDetail> wareOrderTaskDetails = wareOrderTask.getDetails();
        for (WareOrderTaskDetail wareOrderTaskDetail : wareOrderTaskDetails) {
            wareOrderTaskDetail.setTaskId(wareOrderTask.getId());
            wareOrderTaskDetailMapper.insert(wareOrderTaskDetail);
        }
        return wareOrderTask;
    }
}
