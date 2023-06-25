package com.cskaoyan.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.mq.constant.MqTopicConst;
import com.cskaoyan.mall.mq.producer.BaseProducer;
import com.cskaoyan.mall.order.client.CartApiClient;
import com.cskaoyan.mall.order.client.ProductApiClient;
import com.cskaoyan.mall.order.client.WareApiClient;
import com.cskaoyan.mall.order.constant.OrderStatus;
import com.cskaoyan.mall.order.constant.OrderType;
import com.cskaoyan.mall.order.converter.OrderDetailConverter;
import com.cskaoyan.mall.order.converter.OrderInfoConverter;
import com.cskaoyan.mall.order.dto.OrderDetailDTO;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.order.mapper.OrderDetailMapper;
import com.cskaoyan.mall.order.mapper.OrderInfoMapper;
import com.cskaoyan.mall.order.model.OrderDetail;
import com.cskaoyan.mall.order.model.OrderInfo;
import com.cskaoyan.mall.order.query.OrderInfoParam;
import com.cskaoyan.mall.order.service.OrderService;
import com.cskaoyan.mall.ware.api.constant.TaskStatus;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDTO;
import com.cskaoyan.mall.ware.api.dto.WareSkuDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 创建日期: 2023/03/16 10:13
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    ProductApiClient productApiClient;

    @Autowired
    CartApiClient cartApiClient;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    BaseProducer baseProducer;

    @Autowired
    OrderInfoConverter orderInfoConverter;

    @Autowired
    WareApiClient wareApiClient;

    @Autowired
    OrderDetailConverter detailConverter;

    /**
     * 订单确认页：生成交易流水号，防止重复提交
     */
    @Override
    public String getTradeNo(String userId) {
        // order:trade:code:1
        String key = buildKey(userId);
        String uuidCode = UUID.randomUUID().toString().replaceAll("-", "");

        // 保存到redis中
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(uuidCode);
        return uuidCode;
    }

    /**
     * 提交订单：检查下单码
     * 返回false表示不是重复提交，返回true表示是重复提交
     */
    public boolean checkTradeCode(String userId, String tradeCodeNo) {

        String key = buildKey(userId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (tradeCodeNo.equals(bucket.get())) {
            // 相等，说明不是重复提交
            return false;
        }
        // 不相等
        return true;
    }

    /**
     * 提交订单：删除下单码
     */
    @Override
    public void deleteTradeNo(String userId) {
        String key = buildKey(userId);

        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }


    /**
     * 提交订单: 校验价格
     */
    @Override
    public Boolean checkPrice(Long skuId, BigDecimal skuPrice) {
        // 调用商品服务获取当前sku商品的最新价格，和skuPrice比较是否相同
        BigDecimal curPrice = productApiClient.getSkuPrice(skuId);

        if (curPrice.compareTo(skuPrice) != 0) {
            // 价格发生变化了
            return true;
        }

        return false;
    }

    /**
     * 提交订单: 更新用户购物车商品价格
     */
    @Override
    public void refreshPrice(Long skuId, String userId) {
        // 又要调用 购物车服务（需要在购物车服务中自己实现）
        cartApiClient.refreshCartPrice(userId,skuId);
    }


    /**
     * 提交订单: 保存订单以及订单详情
     */
    @Override
    @Transactional
    public String saveOrderInfo(OrderInfo orderInfo) {

        // 设置orderInfo的订单总金额，支付状态(UNPAIED), outTradeNo, 订单创建时间
        // 以及tradeBody(主要包含订单中每个sku商品的名称，以空格分隔)
        buildOrderInfo(orderInfo, false);

        // 保存订单以及订单明细到数据库
        // 保存订单
        orderInfoMapper.insert(orderInfo);

        // 保存订单详情
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        });


        // 获取订单明细列表，获取订单中包含的所有skuid集合
        List<Long> skuIds = orderDetailList.stream()
                .map(orderDetail -> orderDetail.getSkuId())
                .collect(Collectors.toList());
        cartApiClient.removeCartProductsInOrder(orderInfo.getUserId().toString(), skuIds);


        // 发送订单超时自动取消消息（延迟消息）
        //  消息体中放 orderId
        baseProducer.sendDelayMessage(MqTopicConst.DELAY_ORDER_TOPIC, orderInfo.getId(), 5);

        return orderInfo.getOutTradeNo();
    }

    @Override
    public OrderInfoDTO getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo == null) return null;
        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);

        List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper);
        orderInfo.setOrderDetailList(orderDetails);

        // 转化
        OrderInfoDTO orderInfoDTO = orderInfoConverter.convertOrderInfoToOrderInfoDTO(orderInfo);

        return orderInfoDTO;
    }


    /**
     * 我的订单：获取《我的订单》 列表
     */
    @Override
    public Page<OrderInfoDTO> getPage(Page<OrderInfo> pageParam, String userId) {

        // 使用了自己定义的sql语句
        Page<OrderInfo> orderInfoPage = orderInfoMapper.selectPageByUserId(pageParam, userId);

        Page<OrderInfoDTO> orderInfoDTOPage = orderInfoConverter.orderInfoPageToPageDTO(orderInfoPage);

        orderInfoDTOPage.getRecords().stream().forEach(orderInfo -> {
            String orderStatus = orderInfo.getOrderStatus();
            // 中文的订单状态
            String statusDesc = OrderStatus.getStatusDescByStatus(orderStatus);
            orderInfo.setOrderStatusName(statusDesc);
        });
        return orderInfoDTOPage;
    }



    private void buildOrderInfo(OrderInfo orderInfo, boolean isSplit) {

        // 计算总金额
        orderInfo.sumTotalAmount();

        // 订单状态为未支付状态
        if (!isSplit) {
            orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        }


        // 在支付时使用的订单交易编号
        String outTradeNo = "CSKAOYAN" + System.currentTimeMillis() + new Random().nextInt(1000);
        // 订单号
        orderInfo.setOutTradeNo(outTradeNo);
        // 创建时间
        orderInfo.setCreateTime(new Date());

        // 获取订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        StringBuffer stringBuffer = new StringBuffer();
        for (OrderDetail orderDetail : orderDetailList) {
            String skuName = orderDetail.getSkuName() + "  ";
            stringBuffer.append(skuName);
        }

        String tradeBody = stringBuffer.toString().length() > 100 ? stringBuffer.toString().substring(0,100) : stringBuffer.toString();
        orderInfo.setTradeBody(tradeBody);
    }

    // 构建Redis中订单交易流水号的key
    private String buildKey(String userId) {
        return RedisConst.ORDER_TRADE_CODE_PREFIX + userId;
    }

    /**
     * 支付回调，支付成功，修改订单状态
     */
    @Override
    public void successPay(Long orderId) {
        // 1. 修改订单状态


        // 2. 调用仓储服务扣减库存


    }


    /**
     * 修改订单状态
     */
    private void updateOrderStatus(Long orderId,String orderStatus) {
        OrderInfo orderInfo =new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderType(null);
        orderInfo.setOrderStatus(orderStatus);
        orderInfoMapper.updateById(orderInfo);
    }



    /**
     * 支付回调：库存扣减完成，修改订单状态
     */
    @Override
    public void successLockStock(String orderId, String taskStatus) {
        if (TaskStatus.DEDUCTED.name().equals(taskStatus)) {
            updateOrderStatus(Long.valueOf(orderId),OrderStatus.WAIT_DELEVER.name());
        }else {
            updateOrderStatus(Long.valueOf(orderId),OrderStatus.STOCK_EXCEPTION.name());
        }
    }


    /**
     * 支付回调：拆单
     */
    @Override
    public List<WareOrderTaskDTO> orderSplit(String orderId, List<WareSkuDTO> wareSkuDTOList) {

        List<WareOrderTaskDTO> orderTaskDTOS = new ArrayList<>();
        if (StringUtils.isBlank(orderId) || CollectionUtils.isEmpty(wareSkuDTOList)) return orderTaskDTOS;

        // 1. 获取原订单信息
        OrderInfoDTO originOrderInfoDTO = this.getOrderInfo(Long.valueOf(orderId));
        List<OrderDetailDTO> orderDetailDTOS = originOrderInfoDTO.getOrderDetailList();

        for (WareSkuDTO wareSkuDTO : wareSkuDTOList) {
            // 仓库Id
            String wareId = wareSkuDTO.getWareId();
            // 仓库中包含的原始订单中的商品id集合
            List<String> skuIds = wareSkuDTO.getSkuIds();

            // 2. 创建新的子订单
            OrderInfo subOrderInfo = orderInfoConverter.copyOrderInfo(originOrderInfoDTO);

            // 3. 给子订单赋值
            subOrderInfo.setId(null);
            subOrderInfo.setParentOrderId(originOrderInfoDTO.getId());
            subOrderInfo.setWareId(wareId);

            // 4. 设置子订单明细
            List<OrderDetail> newOrderDetailList = new ArrayList<>();
            for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
                Long skuId = orderDetailDTO.getSkuId();
                // 如果当前订单商品的skuId，包含在了当前子订单所属仓库的skuId集合
                if (skuIds.contains(skuId.toString())) {
                    newOrderDetailList.add(detailConverter.convertOrderDetailToDTO(orderDetailDTO));
                }
            }
            subOrderInfo.setOrderDetailList(newOrderDetailList);

            // 5. 保存子订单到数据库
            saveSubOrderInfo(subOrderInfo);

            // 6. 把subOrderInfo转化为WareOrderTaskDTO
            WareOrderTaskDTO wareOrderTaskDTO = orderInfoConverter.convertOrderInfoToWareOrderTaskDTO(subOrderInfo);
            orderTaskDTOS.add(wareOrderTaskDTO);
        }
        // 7. 修改原始订单状态
        updateOrderStatus(Long.valueOf(orderId),OrderStatus.SPLIT.name());

        return orderTaskDTOS;

    }

    // 保存拆单后的子订单
    private void saveSubOrderInfo(OrderInfo subOrderInfo) {
        buildOrderInfo(subOrderInfo, true);

        // 保存订单以及订单明细到数据库
        // 保存订单
        orderInfoMapper.insert(subOrderInfo);

        // 保存订单详情
        List<OrderDetail> orderDetailList = subOrderInfo.getOrderDetailList();
        orderDetailList.forEach(orderDetail -> {
            orderDetail.setOrderId(subOrderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        });
    }


    /**
     * 订单超时取消
     */
    @Override
    public void execExpiredOrder(Long orderId) {

    }

    @Transactional
    @Override
    public Long saveSeckillOrder(OrderInfoParam orderInfoParam) {
        OrderInfo orderInfo = orderInfoConverter.convertOrderInfoParam(orderInfoParam);
        buildSeckillOrder(orderInfo);
        orderInfoMapper.insert(orderInfo);

        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }

        return orderInfo.getId();
    }


    @Transactional
    private void buildSeckillOrder(OrderInfo orderInfo) {

        orderInfo.setOrderType(OrderType.PROMO_ORDER.name());

        orderInfo.sumTotalAmount();

        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        String outTradeNo = "CSKAOYAN_SECK" + System.currentTimeMillis() + new Random().nextInt(1000);
        // 订单号
        orderInfo.setOutTradeNo(outTradeNo);
        // 创建时间
        orderInfo.setCreateTime(new Date());

        OrderDetail orderDetail = orderInfo.getOrderDetailList().get(0);
        orderInfo.setTradeBody(orderDetail.getSkuName());
    }

}
