package com.cskaoyan.mall.promo.controller;

import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.common.constant.ResultCodeEnum;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.common.util.AuthContext;
import com.cskaoyan.mall.common.util.DateUtil;
import com.cskaoyan.mall.common.util.MD5;
import com.cskaoyan.mall.mq.constant.MqTopicConst;
import com.cskaoyan.mall.mq.producer.BaseProducer;
import com.cskaoyan.mall.order.dto.OrderDetailDTO;
import com.cskaoyan.mall.order.dto.OrderTradeDTO;
import com.cskaoyan.mall.order.query.OrderInfoParam;
import com.cskaoyan.mall.promo.client.OrderApiClient;
import com.cskaoyan.mall.promo.client.UserApiClient;
import com.cskaoyan.mall.promo.converter.SeckillGoodsConverter;
import com.cskaoyan.mall.promo.model.OrderRecord;
import com.cskaoyan.mall.promo.model.SeckillGoods;
import com.cskaoyan.mall.promo.model.UserRecord;
import com.cskaoyan.mall.promo.service.PromoService;
import com.cskaoyan.mall.promo.util.LocalCacheHelper;
import com.cskaoyan.mall.user.dto.UserAddressDTO;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 创建日期: 2023/03/19 16:11
 *
 * @author ciggar
 */

@RestController
@RequestMapping("api/promo/seckill")
public class PromoApiController {

    @Autowired
    PromoService promoService;

    @Autowired
    BaseProducer baseProducer;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    UserApiClient userApiClient;

    @Autowired
    OrderApiClient orderApiClient;


    /**
     * 返回全部列表
     *
     * @return
     */

    @GetMapping("/findAll")
    public Result findAll() {
        return Result.ok(promoService.findAll());
    }


    /**
     * 获取实体
     *
     * @param skuId
     * @return
     */
    @GetMapping("/getSeckillGoods/{skuId}")
    public Result getSeckillGoods(@PathVariable("skuId") Long skuId) {
        return Result.ok(promoService.getSeckillGoodsDTO(skuId));
    }

    /**
     * @param skuId
     * @param request
     * 功能描述: 获取下单码
     */
    @GetMapping("auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") Long skuId, HttpServletRequest request) {

        String userId = AuthContext.getUserId(request);
        RMap<String, SeckillGoods> goodsRMap = redissonClient.getMap(RedisConst.PROMO_SECKILL_GOODS);
        SeckillGoods seckillGoods = goodsRMap.get(skuId.toString());
        if (seckillGoods != null) {
            // 判断请求时间是否正在秒杀活动的范围内
            Date now = new Date();
            if (DateUtil.dateCompare(now, seckillGoods.getStartTime())
                    && DateUtil.dateCompare(seckillGoods.getEndTime(), now)) {
                // 生成下单码并返回
                String skuIdStr = MD5.encrypt(userId + skuId);
                return Result.ok(skuIdStr);
            }
        }
        return Result.fail().message("获取下单码失败");
    }


    /**
     * @param skuId
     * @param request
     * 功能描述: 秒杀下单排队
     */
    @PostMapping("auth/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") Long skuId,String skuIdStr, HttpServletRequest request) throws Exception {
        String userId = AuthContext.getUserId(request);

        // 先判断下单码是否正确
        String newSkuIdStr = MD5.encrypt(userId + skuId);
        if (!newSkuIdStr.equals(skuIdStr) ) {
            // 下单码不正确
            return Result.build(null, ResultCodeEnum.SECKILL_ILLEGAL);
        }

        // 判断秒杀商品的状态标志位(是否已经售罄)
        String flag = (String) LocalCacheHelper.get(skuId.toString());
        if (flag == null) return Result.build(null, ResultCodeEnum.SECKILL_ILLEGAL);
        if ("0".equals(flag)) return Result.build(null, ResultCodeEnum.SECKILL_FINISH);

        // 准备发消息排队
        UserRecord userRecord = new UserRecord();
        userRecord.setSkuId(skuId);
        userRecord.setUserId(userId);

        // 发送消息排队
        baseProducer.sendMessage(MqTopicConst.SECKILL_GOODS_QUEUE_TOPIC, userRecord);

        return Result.ok();
    }

    /**
     * @param skuId
     * @param request
     * 功能描述: 前端轮训，秒杀，检查下单状态
     */
    @GetMapping(value = "auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userId = AuthContext.getUserId(request);
        return  promoService.checkOrder(skuId, userId);
    }

    @Autowired
    SeckillGoodsConverter goodsConverter;

    /**
     * 秒杀确认订单
     * @param request
     * @return
     */
    @GetMapping("auth/trade")
    public Result trade(HttpServletRequest request) {

        String userId = AuthContext.getUserId(request);

        // 获取redis中暂存的 带下单的数据 OrderRecord对象
        RMap<String, OrderRecord> orderRecordRMap = redissonClient.getMap(RedisConst.PROMO_SECKILL_ORDERS);
        OrderRecord orderRecord = orderRecordRMap.get(userId.toString());

        //获取用户的地址列表
        List<UserAddressDTO> addressListByUserId = userApiClient.findUserAddressListByUserId(userId);

        OrderDetailDTO orderDetailDTO
                = goodsConverter.secondKillGoodsToOrderDetailDTO(orderRecord.getSeckillGoods(), orderRecord.getNum());

        ArrayList<OrderDetailDTO> orderDetailDTOs = new ArrayList<>();
        orderDetailDTOs.add(orderDetailDTO);

        OrderTradeDTO orderTradeDTO = new OrderTradeDTO();
        orderTradeDTO.setDetailArrayList(orderDetailDTOs);
        orderTradeDTO.setUserAddressList(addressListByUserId);
        orderTradeDTO.setTotalNum(1);
        orderTradeDTO.setTotalAmount(orderDetailDTO.getOrderPrice());
        return Result.ok(orderTradeDTO);

    }

    /**
     * @param orderInfo
     * @param request
     * 功能描述: 提交订单(真正的下单)
     */
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfoParam orderInfo, HttpServletRequest request) {

        String userId = AuthContext.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));

        Long skuId = orderInfo.getOrderDetailList().get(0).getSkuId();

        RMap<String, String> map = redissonClient.getMap(RedisConst.PROMO_SUBMITTING);
        // 保证下单标记，只有第一次能够成功
        if (map.putIfAbsent(userId,skuId.toString()) != null) {
            // 说明用户已经有，提交订单的标记
            return Result.fail().message("请勿重复提交订单");
        }

        // 调用订单服务生成秒杀订单
        Long orderId = orderApiClient.submitOrder(orderInfo);
        if (orderId == null) {
            // 删除下单标记，让用户可以继续去下单
            String remove = map.remove(userId);
            return Result.fail().message("下单失败，请稍后重试");
        }

        // 删除 redis中暂存的带下单的数据
        RMap<String, OrderRecord> orderRecordRMap = redissonClient.getMap(RedisConst.PROMO_SECKILL_ORDERS);
        orderRecordRMap.remove(userId);

        // 在redis中，记录一下用户的下单信息
        //  userId   orderId
        RMap<String, String> userOrderMap = redissonClient.getMap(RedisConst.PROMO_SUBMIT_ORDER);
        userOrderMap.put(userId, orderId.toString());

        return Result.ok(orderId);
    }

}
