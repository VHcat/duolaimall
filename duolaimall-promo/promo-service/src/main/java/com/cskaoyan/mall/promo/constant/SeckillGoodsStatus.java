package com.cskaoyan.mall.promo.constant;

/**
 * 创建日期: 2023/03/19 16:02
 *
 * @author ciggar
 */
public enum  SeckillGoodsStatus {

    UNCKECKED("未审核"),
    CHECKED_PASS("审核通过"),
    CHECKED_UNPASS("审核不通过"),
    FINISHED("已结束")
    ;
    String desc;

    SeckillGoodsStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
