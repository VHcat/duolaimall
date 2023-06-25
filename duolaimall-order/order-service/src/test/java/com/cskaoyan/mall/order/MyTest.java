package com.cskaoyan.mall.order;

import com.cskaoyan.mall.order.mapper.OrderInfoMapper;
import com.cskaoyan.mall.order.model.OrderInfo;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by 北海 on 2023-06-22 7:46
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MyTest {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Test
    public void test() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(100000L);
        orderInfo.setOrderStatus("abc");

        orderInfoMapper.updateById(orderInfo);

    }
}
