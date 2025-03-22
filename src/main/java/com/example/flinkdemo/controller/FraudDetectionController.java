package com.example.flinkdemo.controller;

import com.example.flinkdemo.model.*;
import com.example.flinkdemo.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 欺诈检测控制器
 * 处理交易欺诈检测和流量异常监控的API接口
 */
@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    /**
     * 检测交易欺诈
     * @param transactions 交易事件列表
     * @return 检测结果
     */
    @PostMapping("/detect")
    public ResponseEntity<FraudDetectionResponse> detectFraud(@RequestBody List<TransactionEvent> transactions) {
        log.info("收到欺诈检测请求，交易数量: {}", transactions.size());
        
        // 调用欺诈检测服务
        FraudDetectionResponse response = fraudDetectionService.detectFraud(transactions);
        return ResponseEntity.ok(response);
    }

    /**
     * 生成测试交易数据
     * @param count 生成数量
     * @return 测试数据
     */
    @GetMapping("/generate-test-data")
    public ResponseEntity<List<TransactionEvent>> generateTestData(@RequestParam(defaultValue = "50") int count) {
        log.info("生成测试数据，数量: {}", count);
        List<TransactionEvent> testData = fraudDetectionService.generateTestTransactions(count);
        return ResponseEntity.ok(testData);
    }

    /**
     * 服务健康检查
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "FraudDetectionService");
        
        return ResponseEntity.ok(health);
    }

    /**
     * 超简化的测试接口 - 仅返回简单响应
     * @return 测试响应
     */
    @PostMapping("/test-detect")
    public ResponseEntity<Map<String, Object>> testDetect(@RequestBody List<TransactionEvent> transactions) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "测试响应成功");
        response.put("timestamp", System.currentTimeMillis());
        response.put("count", transactions.size());
        return ResponseEntity.ok(response);
    }
} 