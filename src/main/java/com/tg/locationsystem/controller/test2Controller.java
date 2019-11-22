package com.tg.locationsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hyy
 * @ Date2019/11/7
 */
@Controller
@RequestMapping("/test2")
public class test2Controller {

    @RequestMapping("/failed")
    @ResponseBody
    public Map<String, String> requestFailed() {

        Map<String, String> map = new HashMap<>();
        map.put("code", "-1");
        map.put("msg", "请求错误");
        return map;
    }

}
