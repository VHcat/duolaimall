package com.cskaoyan.mall.order.constant;

/**
 * 创建日期: 2023/03/15 11:31
 *
 * @author ciggar
 * 订单状态
 */
public enum  OrderStatus {

    UNPAID("未支付"),
    PAID("已支付"),
    NOTIFIED_WARE("已通知仓储"),
    WAIT_DELEVER("待发货"),
    STOCK_EXCEPTION("库存异常"),
    DELEVERED("已发货"),
    CLOSED("已关闭"),
    COMMENT("已评价"),
    PAY_FAIL("支付失败"),
    SPLIT("已拆分")
    ;
    String desc;

    OrderStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    public static String getStatusDescByStatus(String status) {
        OrderStatus arrObj[] = OrderStatus.values();
        for (OrderStatus obj : arrObj) {
            if (obj.name().equals(status)) {
                return obj.getDesc();
            }
        }
        return "";
    }

}
