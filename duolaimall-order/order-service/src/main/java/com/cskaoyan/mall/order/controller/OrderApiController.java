package com.cskaoyan.mall.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cskaoyan.mall.cart.api.dto.CartInfoDTO;
import com.cskaoyan.mall.common.constant.ResultCodeEnum;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.common.util.AuthContext;
import com.cskaoyan.mall.order.client.CartApiClient;
import com.cskaoyan.mall.order.client.UserApiClient;
import com.cskaoyan.mall.order.client.WareApiClient;
import com.cskaoyan.mall.order.converter.CartInfoConverter;
import com.cskaoyan.mall.order.converter.OrderInfoConverter;
import com.cskaoyan.mall.order.dto.OrderDetailDTO;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.order.dto.OrderTradeDTO;
import com.cskaoyan.mall.order.model.OrderInfo;
import com.cskaoyan.mall.order.query.OrderDetailParam;
import com.cskaoyan.mall.order.query.OrderInfoParam;
import com.cskaoyan.mall.order.service.OrderService;
import com.cskaoyan.mall.user.dto.UserAddressDTO;
import com.cskaoyan.mall.ware.api.dto.WareOrderTaskDTO;
import com.cskaoyan.mall.ware.api.dto.WareSkuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired
    UserApiClient userApiClient;

    @Autowired
    CartApiClient cartApiClient;


    @Autowired
    WareApiClient wareApiClient;

    @Autowired
    OrderService orderService;

    @Autowired
    CartInfoConverter cartInfoConverter;

    @Autowired
    OrderInfoConverter orderInfoConverter;



    /**
     * 确认订单
     * 获取结算页信息
     */
    @GetMapping("/auth/trade")
    public OrderTradeDTO getTradeInfo(HttpServletRequest request){

        // 获取到用户Id
        String userId = AuthContext.getUserId(request);

        // 用户的地址列表
        List<UserAddressDTO> addressListByUserId = userApiClient.findUserAddressListByUserId(userId);

        // 获取购物车选中的商品列表
        List<CartInfoDTO> cartCheckedList = cartApiClient.getCartCheckedList(userId);
        // 订单明细列表
        List<OrderDetailDTO> orderDetailDTOs
                = cartInfoConverter.convertCartInfoDTOToOrderDetailDTOList(cartCheckedList);


        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderDetailList(orderDetailDTOs);
        // 计算订单总金额
        orderInfoDTO.sumTotalAmount();


        OrderTradeDTO orderTradeDTO = new OrderTradeDTO();
        //  设置地址列表
        orderTradeDTO.setUserAddressList(addressListByUserId);
        // 设置订单明细
        orderTradeDTO.setDetailArrayList(orderDetailDTOs);
        // 设置总的订单商品条目数量 = detailDTOList.size()
        orderTradeDTO.setTotalNum(orderDetailDTOs.size());

        // 设置总金额
        orderTradeDTO.setTotalAmount(orderInfoDTO.getTotalAmount());

        String tradeNo = orderService.getTradeNo(userId);
        // 生成订单流水号 orderService.getTradeNo(userId)
        // 设置订单流水号
        orderTradeDTO.setTradeNo(tradeNo);

        return orderTradeDTO;
    }


    /**
     * 提交订单
     */
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfoParam orderInfoParam, String tradeNo, HttpServletRequest request) {

        // 获取到用户Id
        String userId = AuthContext.getUserId(request);
        orderInfoParam.setUserId(Long.parseLong(userId));

        // 验证是否重复下单 orderService.checkTradeCode(userId, tradeNo);
        // 如果重复，则返回Result.fail().message("请不要重复提交订单")
        boolean isDuplicate = orderService.checkTradeCode(userId,  tradeNo);
        if (isDuplicate) {
            // 是重复提交
            return Result.fail().message("请不要重复提交订单");
        }


        /*
            校验库存：
            a. 遍历订单商品条目，调用wareApiClient.hasStock(skuId, skuNum)判断订单商品条目是否有库存
            b. 如果有一个订单商品条目的库存不足，则返回Result.fail().message(orderDetail.getSkuName()+ ":库存不足！")

         */

        /*
              校验价格(因为添加购物车的价格，可能和当前商品的价格不一样)
              a. 遍历订单商品条目，调用orderService.checkPrice 方法判断商品价格是否发生改变(
                 调用商品服务获取当前的最新价格 orderService.refreshPrice)
              b. 如果发现商品当前价格与购物车中的价格不符，则更新购物车中的商品价格，并返回
                 Result.fail().message(orderDetail.getSkuName() + "价格有变动！")
          */
        // 获取订单条目列表
        List<OrderDetailParam> orderDetailList = orderInfoParam.getOrderDetailList();

        for (int i = 0; i < orderDetailList.size(); i++) {
            OrderDetailParam orderDetail = orderDetailList.get(i);
            // 判断库存
            Result result = wareApiClient.hasStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!ResultCodeEnum.SUCCESS.getCode().equals(result.getCode())) {
                return Result.fail().message(orderDetail.getSkuName()+ ":库存不足！");
            }

            // 判断价格是否发生了变化
            Boolean isChanged = orderService.checkPrice(orderDetail.getSkuId(), orderDetail.getOrderPrice());
            if (isChanged) {

                // 更新一下购物车中商品的价格
                orderService.refreshPrice(orderDetail.getSkuId(), userId);
                return Result.fail().message(orderDetail.getSkuName() + "价格有变动！");
            }
        }

        OrderInfo orderInfo = orderInfoConverter.convertOrderInfoParam(orderInfoParam);

        // 验证通过，保存订单！orderService.saveOrderInfo(orderInfo)
        String outTradeNo = orderService.saveOrderInfo(orderInfo);

        // 已经下单了，所以可以删除tradeNo orderService.deleteTradeNo(userId);
        orderService.deleteTradeNo(userId);

        return Result.ok(orderInfo.getId());
    }


    /**
     * 我的订单：获取《我的订单》 列表
     */
    @GetMapping("auth/{page}/{limit}")
    public Result<Page<OrderInfoDTO>> index(@PathVariable Long page, @PathVariable Long limit, HttpServletRequest request) {
        // 获取到用户Id
        String userId = AuthContext.getUserId(request);

        // 构造分页参数，调用orderService.getPage(pageParam, userId) 获取我的订单分页数据
        Page<OrderInfo> orderInfoPage = new Page<>(page, limit);
        Page<OrderInfoDTO> pageInfo = orderService.getPage(orderInfoPage, userId);
        return Result.ok(pageInfo);
    }


    /**
     * 秒杀提交订单，秒杀订单不需要做前置判断，直接下单
     * @param orderInfo
     * @return
     */
    @PostMapping("inner/seckill/submitOrder")
    public Long submitOrder(@RequestBody OrderInfoParam orderInfoParam) {
        Long orderId = orderService.saveSeckillOrder(orderInfoParam);
        return orderId;
    }

    @GetMapping("/inner/getOrderInfo/{orderId}")
    public OrderInfoDTO getOrderInfoDTO(@PathVariable(value = "orderId") String orderId) {
        boolean isOrderId = true;
        long orderIdL = 0;
        try {
            orderIdL = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            isOrderId = false;
        }

        // isOrderId 为true orderId
        if (isOrderId = true) {
            OrderInfoDTO orderInfo = orderService.getOrderInfo(orderIdL);
            return orderInfo;
        }
        // isOrderId 为false outTradeNo
        return null;
    }


    /**
     * 支付回调，支付成功，修改订单状态
     */
    @PostMapping("/inner/success/{orderId}")
    public Result successPay(@PathVariable Long orderId){
        orderService.successPay(orderId);
        return Result.ok();
    }


    /**
     * 支付回调：拆单
     */
    @PostMapping("/inner/orderSplit/{orderId}")
    public List<WareOrderTaskDTO> orderSplit(@PathVariable(value = "orderId") String orderId, @RequestBody List<WareSkuDTO> wareSkuDTOList){

        List<WareOrderTaskDTO> wareOrderTaskDTOS = orderService.orderSplit(orderId,wareSkuDTOList);

        return wareOrderTaskDTOS;
    }

    /**
     * 库存回调：库存扣减完成，修改订单状态
     */
    @PostMapping("/inner/successLockStock/{orderId}/{taskStatus}")
    public Result successLockStock(@PathVariable(value = "orderId") String orderId, @PathVariable(value = "taskStatus") String taskStatus){

        orderService.successLockStock(orderId,taskStatus);

        return Result.ok();
    }

}
