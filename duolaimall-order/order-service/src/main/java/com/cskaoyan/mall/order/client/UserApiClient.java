package com.cskaoyan.mall.order.client;

import com.cskaoyan.mall.user.dto.UserAddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 创建日期: 2023/03/16 09:40
 *
 * @author ciggar
 */
@FeignClient(value = "service-user")
public interface UserApiClient {

    @GetMapping("/api/user/inner/findUserAddressListByUserId/{userId}")
    public List<UserAddressDTO> findUserAddressListByUserId(@PathVariable("userId") String userId);
}
