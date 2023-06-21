package com.cskaoyan.mall.user.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.common.util.IpUtil;
import com.cskaoyan.mall.user.consts.UserCodeEnum;
import com.cskaoyan.mall.user.consts.UserConstants;
import com.cskaoyan.mall.user.dto.UserLoginDTO;
import com.cskaoyan.mall.user.query.UserInfoParam;
import com.cskaoyan.mall.user.service.UserService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/passport")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 登录
     */
    @PostMapping("login")
    public Result login(@RequestBody UserInfoParam userInfo, HttpServletRequest request) {
        // 生成uuid字符串，作为Redis存储登录会话的key
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        // 从当前请求中，获取请求发起的IP地址
        String ipAddress = IpUtil.getIpAddress(request);
        UserLoginDTO loginInfo = userService.login(userInfo, ipAddress, token);
        if (loginInfo != null) {
            // 登录匹配成功
            return Result.ok(loginInfo);
        }

        return Result.build(null, UserCodeEnum.USER_LOGIN_CHECK_FAIL);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("logout")
    public Result logout(HttpServletRequest request){
        RBucket<String> token = redissonClient.getBucket(UserConstants.USER_LOGIN_KEY_PREFIX + request.getHeader("token"));
        token.delete();
        return Result.ok();
    }
}
