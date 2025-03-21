package com.example.flinkdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
把这部分的内容功能全部写入到 readme 中 需要分别包含活动链路 还有各个接口对应什么样的 flink 操作 实现了什么功能；最后推送到@https://github.com/HangerLIN/flink-demo.git [https://github.com/HangerLIN/flink-demo.git]
/**
 * 首页控制器
 */
@Controller
public class HomeController {

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
} 