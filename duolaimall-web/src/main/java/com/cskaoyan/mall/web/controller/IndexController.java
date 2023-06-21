package com.cskaoyan.mall.web.controller;

import com.cskaoyan.mall.common.result.Result;
import com.cskaoyan.mall.web.client.ProductApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/15 22:04
 */
@Controller
public class IndexController {

    @Autowired
    private ProductApiClient productFeignClient;

    @GetMapping({"/","index.html"})
    public String index(HttpServletRequest request){
        // 获取首页分类数据
        Result result = productFeignClient.getCategoryList();
        request.setAttribute("list",result.getData());
        return "index/index";
    }
}
