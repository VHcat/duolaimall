package com.cskaoyan.mall.payment;

import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.payment.client.OrderApiClient;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.security.RunAs;

/**
 * Created by 北海 on 2023-06-21 17:50
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MyTest {
    @Autowired
    OrderApiClient client;

    @Test
    public void test() {
        OrderInfoDTO orderInfoDTO = client.getOrderInfoDTO(1L);
    }
}
