package com.cskaoyan.mall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/19 22:09
 */
@Controller
public class loginController {

    /**
     *
     * @return
     */
    @GetMapping("login.html")
    public String login(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "login";
    }

}
