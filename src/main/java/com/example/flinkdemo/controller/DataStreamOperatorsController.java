package com.example.flinkdemo.controller;

import com.example.flinkdemo.model.SensorReading;
import com.example.flinkdemo.service.DataStreamOperatorsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataStream流处理算子演示控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/datastream")
@RequiredArgsConstructor
public class DataStreamOperatorsController {

    private final DataStreamOperatorsService dataStreamOperatorsService;

    /**
     * 基础转换算子示例 - Map, FlatMap, Filter
     */
    @GetMapping("/basic-transform")
    public Map<String, Object> basicTransformDemo() throws Exception {
        log.info("执行DataStream基础转换算子演示");
        List<SensorReading> result = dataStreamOperatorsService.basicTransformDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "DataStream基础转换算子(Map/Filter/FlatMap)");
        response.put("description", "应用Map将温度从摄氏度转为华氏度，Filter过滤高温值，FlatMap为每个传感器生成多条记录");
        response.put("result", result);
        
        return response;
    }

    /**
     * KeyBy和聚合算子示例 - keyBy, reduce
     */
    @GetMapping("/keyby-reduce")
    public Map<String, Object> keyByReduceDemo() throws Exception {
        log.info("执行DataStream KeyBy和Reduce算子演示");
        List<SensorReading> result = dataStreamOperatorsService.keyByReduceDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "DataStream KeyBy和Reduce算子");
        response.put("description", "按传感器ID分组，计算每个传感器的最高温度");
        response.put("result", result);
        
        return response;
    }

    /**
     * 窗口算子示例 - window, windowAll
     */
    @GetMapping("/window")
    public Map<String, Object> windowDemo() throws Exception {
        log.info("执行DataStream窗口算子演示");
        List<Tuple3<String, Double, Long>> result = dataStreamOperatorsService.windowDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "DataStream窗口算子(Window)");
        response.put("description", "按传感器ID分组，应用5秒滚动窗口计算窗口内的平均温度");
        response.put("result", result);
        
        return response;
    }

    /**
     * 多流转换算子示例 - union, connect
     */
    @GetMapping("/multi-stream")
    public Map<String, Object> multiStreamDemo() throws Exception {
        log.info("执行DataStream多流转换算子演示");
        List<Tuple3<String, Object, String>> result = dataStreamOperatorsService.multiStreamDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "DataStream多流转换算子(Union/Connect)");
        response.put("description", "使用Union合并同类型流，Connect连接不同类型流，并使用CoMap分别处理");
        response.put("result", result);
        
        return response;
    }
} 