package com.cskaoyan.mall.promo.client;

import com.cskaoyan.mall.user.dto.UserAddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-user")
public interface UserApiClient {

    /**
     * 根据用户id获取用户地址列表
     */
    @GetMapping("/api/user/inner/findUserAddressListByUserId/{userId}")
    public List<UserAddressDTO> findUserAddressListByUserId(@PathVariable("userId") String userId);
}
