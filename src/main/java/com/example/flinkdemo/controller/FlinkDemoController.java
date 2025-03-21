package com.example.flinkdemo.controller;

import com.example.flinkdemo.service.BatchProcessingService;
import com.example.flinkdemo.service.StreamProcessingService;
import com.example.flinkdemo.service.TableSqlService;
import com.example.flinkdemo.model.TrafficResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.Row;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flink演示控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/flink")
@RequiredArgsConstructor
public class FlinkDemoController {

    private final BatchProcessingService batchService;
    private final StreamProcessingService streamService;
    private final TableSqlService tableSqlService;

    /**
     * 批处理演示 - 基本转换
     */
    @GetMapping("/batch/transform")
    public Map<String, Object> batchTransformDemo() throws Exception {
        log.info("执行批处理基本转换演示");
        List<String> result = batchService.basicTransformationDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "批处理基本转换（Map, FlatMap, Filter）");
        response.put("description", "将文本分割为单词，转为小写，并过滤长度大于4的单词");
        response.put("result", result);
        
        return response;
    }

    /**
     * 批处理演示 - 单词计数
     */
    @GetMapping("/batch/wordcount")
    public Map<String, Object> wordCountDemo() throws Exception {
        log.info("执行批处理单词计数演示");
        List<Tuple2<String, Integer>> result = batchService.wordCountDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "批处理单词计数（WordCount）");
        response.put("description", "统计文本中每个单词出现的次数");
        response.put("result", result);
        
        return response;
    }

    /**
     * 批处理演示 - 分组聚合
     */
    @GetMapping("/batch/aggregate")
    public Map<String, Object> groupAndReduceDemo() throws Exception {
        log.info("执行批处理分组聚合演示");
        List<Tuple2<String, Integer>> result = batchService.groupAndReduceDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "批处理分组聚合（Group and Reduce）");
        response.put("description", "按产品类别统计销售总额");
        response.put("result", result);
        
        return response;
    }

    /**
     * 流处理演示 - 处理时间窗口
     */
    @GetMapping("/stream/processing-time")
    public Map<String, Object> processingTimeWindowDemo() throws Exception {
        log.info("执行流处理时间窗口演示");
        List<Tuple2<Integer, Integer>> result = streamService.processingTimeWindowDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "流处理处理时间窗口（Processing Time Window）");
        response.put("description", "使用5秒的滚动窗口统计各传感器的车流量");
        response.put("result", result);
        
        return response;
    }

    /**
     * 流处理演示 - 事件时间窗口与Watermark
     */
    @GetMapping("/stream/event-time")
    public Map<String, Object> eventTimeWindowDemo() throws Exception {
        log.info("执行流处理事件时间窗口与Watermark演示");
        List<Tuple2<Integer, Integer>> result = streamService.eventTimeWindowWithWatermarkDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "流处理事件时间窗口与Watermark（Event Time Window with Watermark）");
        response.put("description", "使用5秒的事件时间滚动窗口统计各传感器的车流量，允许2秒的延迟");
        response.put("result", result);
        
        return response;
    }

    /**
     * Table API演示
     */
    @GetMapping("/table/api")
    public Map<String, Object> tableApiDemo() throws Exception {
        log.info("执行Table API演示");
        List<Tuple2<Integer, Double>> result = tableSqlService.tableApiDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Table API");
        response.put("description", "使用Table API按传感器ID分组，计算平均速度");
        response.put("result", result);
        
        return response;
    }

    /**
     * SQL演示
     */
    @GetMapping("/table/sql")
    public Map<String, Object> sqlDemo() throws Exception {
        log.info("执行SQL演示");
        List<Row> result = tableSqlService.sqlDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "SQL");
        response.put("description", "使用SQL按传感器ID分组，计算车流量总和和平均速度");
        response.put("result", result.toString());
        
        return response;
    }

    /**
     * Table API和SQL结合演示
     */
    @GetMapping("/table/combined")
    public Map<String, Object> tableAndSqlDemo() throws Exception {
        log.info("执行Table API和SQL结合演示");
        List<Row> result = tableSqlService.tableAndSqlDemo();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Table API与SQL结合");
        response.put("description", "先使用Table API过滤速度大于等于60的数据，再使用SQL统计每个传感器的数据量和平均速度");
        response.put("result", result.toString());
        
        return response;
    }

    /**
     * 速度阈值过滤演示 - 带参数
     */
    @GetMapping("/table/speed-filter")
    public List<TrafficResultDTO> speedFilterDemo(double threshold) throws Exception {
        log.info("执行速度阈值过滤演示，阈值: {}", threshold);
        return tableSqlService.speedFilterDemoFormatted(threshold);
    }
} 