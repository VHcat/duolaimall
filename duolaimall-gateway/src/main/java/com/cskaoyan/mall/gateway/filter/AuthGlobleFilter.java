package com.cskaoyan.mall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.cskaoyan.mall.common.constant.ResultCodeEnum;
import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.common.util.IpUtil;
import com.cskaoyan.mall.user.consts.UserConstants;
import com.cskaoyan.mall.user.dto.UserLoginInfoDTO;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class AuthGlobleFilter implements GlobalFilter, Ordered {

    // 存储配置好的需要登录的请求 path
    @Value("${authUrls.url}")
    private String authUrls;

    @Autowired
    private RedissonClient redissonClient;

    // 匹配路径的工具类
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取当前拦截到的请求
        ServerHttpRequest request = exchange.getRequest();

        //获取当前请求的请求路径
        String path = request.getURI().getPath();

        // 服务内部调用的请求
        if (antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            out(response, ResultCodeEnum.PERMISSION);
        }


        // 从请求中获取Token
        String tokenStr = getUserInfoFromRequestByName(request, "token");
        String userId = "";
        if (!StringUtils.isEmpty(tokenStr)) {
            userId = getUserLoginInfoFromRedis(tokenStr, request);
        }

        if ("-1".equals(userId)) {
            // 用户的登录信息被盗用
            ServerHttpResponse response = exchange.getResponse();
            // 向响应体中写入响应码
            return out(response, ResultCodeEnum.PERMISSION);
        }
        // 做登录身份认证的匹配
        for (String loginPath :authUrls.split(",")) {
            if (!antPathMatcher.match(loginPath, path)) continue;
            // 当前请求和需要登录的请求的url匹配上了
            if (!StringUtils.isEmpty(userId)) {
               //  需要登录，用户也已经登陆过
                break;
            }

            // 需要登录，又没有登录
            if (path.startsWith("/api")) {
               // 说明该请求，是一个ajax发起的异步请求，不返回重定向的响应，返回特殊的响应码
                ServerHttpResponse response = exchange.getResponse();
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }

            // 针对获取页面的请求，返回重定向的响应
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION, "http://www.csmall.com/login?orignalUrl=" + request.getURI());
            return response.setComplete();
        }

        // 请求需要登录且已登录 或者 请求不需要登录

        // 如果用户登录过了，将用户的userId放入请求头，转发给服务
        ServerWebExchange newExchange = null;
        if (!StringUtils.isEmpty(userId)) {
            // 向请求中添加请求头
            ServerHttpRequest newRequest = request.mutate().header("userId", userId).build();
            // 请求放入exchange中
            newExchange = exchange.mutate().request(newRequest).build();
        }

        String userTempId = getUserInfoFromRequestByName(request, "userTempId");

        if (!StringUtils.isEmpty(userTempId)) {
            ServerHttpRequest newRequest = request.mutate().header("userTempId", userTempId).build();
            newExchange = exchange.mutate().request(newRequest).build();
        }
        if (newExchange != null) {
            exchange = newExchange;
        }
        return chain.filter(exchange);

    }

    private String getUserLoginInfoFromRedis(String tokenStr, ServerHttpRequest request) {

        RBucket<UserLoginInfoDTO> bucket
                = redissonClient.getBucket(UserConstants.USER_LOGIN_KEY_PREFIX + tokenStr);

        UserLoginInfoDTO userLoginInfoDTO = bucket.get();
        // 判断用户是否登陆过
        if (userLoginInfoDTO == null) {
            // 没有登录过
            return "";
        }
        // 获取用户登录成功时，发起请求的ip地址
        String userLoginIp = userLoginInfoDTO.getIp();
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        if (!gatwayIpAddress.equals(userLoginIp)) {
            // 如果不相等
            return "-1";
        }

        return userLoginInfoDTO.getUserId();
    }


    private String getUserInfoFromRequestByName(ServerHttpRequest request, String name) {
        // 先从请求头中获取
        List<String> headerValues = request.getHeaders().get(name);
        if (!CollectionUtils.isEmpty(headerValues)) {
            return headerValues.get(0);
        }

        // 在从Cookie中获取
        HttpCookie tokenCookie = request.getCookies().getFirst(name);
        if (tokenCookie != null) {
            return tokenCookie.getValue();
        }

        //获取到了Cookie
        return null;

    }


    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 根据指定的响应码构造Result对象
        Result<Object> result = Result.build(null, resultCodeEnum);
        // 转化成Json字符串
        String resultJson = JSON.toJSONString(result);
        byte[] bytes = resultJson.getBytes();

        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
