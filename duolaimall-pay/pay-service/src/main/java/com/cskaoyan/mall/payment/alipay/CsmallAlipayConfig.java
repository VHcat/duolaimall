package com.cskaoyan.mall.payment.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建日期: 2023/03/17 14:39
 */
@SuppressWarnings("all")
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay.config")
public class CsmallAlipayConfig {

    String appId;

    String alipayUrl;

    String appPrivateKey;

    String alipayPublicKey;

    // 同步回调地址
    String returnPaymentUrl;

    // 异步回调地址
    String notifyPaymentUrl;

    String returnOrderUrl;

    public final static String format="json";
    public final static String charset="utf-8";
    public final static String sign_type="RSA2";


    // 支付宝客户端
    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {

        AlipayConfig alipayConfig = new AlipayConfig();

        alipayConfig.setServerUrl(alipayUrl);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(appPrivateKey);
        alipayConfig.setFormat(format);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset(charset);
        alipayConfig.setSignType(sign_type);
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        return alipayClient;
    }
}

