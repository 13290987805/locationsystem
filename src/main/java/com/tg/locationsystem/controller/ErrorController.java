package com.tg.locationsystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyy
 * @ Date2020/3/28
 */
@RestController
public class ErrorController {
    @RequestMapping("/Unauthorized")
    public String login() {
        return "没有权限2";
    }
}
