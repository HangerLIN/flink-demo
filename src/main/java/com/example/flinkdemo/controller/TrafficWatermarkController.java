package com.example.flinkdemo.controller;

import com.example.flinkdemo.model.TrafficEvent;
import com.example.flinkdemo.model.TrafficAnalysisResult;
import com.example.flinkdemo.service.AdvancedWatermarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交通数据水印控制器 - 演示Flink高级水印特性
 */
@Slf4j
@RestController
@RequestMapping("/api/watermark")
@RequiredArgsConstructor
public class TrafficWatermarkController {

    private final AdvancedWatermarkService watermarkService;

    /**
     * 首页 - 提供水印功能概述
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "Flink高级水印示例");
        response.put("description", "这些端点演示了Flink的高级水印功能，包括水印对齐、空闲检测和积压处理");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("/api/watermark/basic", "基本水印和事件时间窗口");
        endpoints.put("/api/watermark/alignment", "水印对齐功能示例");
        endpoints.put("/api/watermark/backlog", "积压处理期间的检查点调整");
        endpoints.put("/api/watermark/comprehensive", "综合演示所有高级水印特性");
        
        response.put("endpoints", endpoints);
        return ResponseEntity.ok(response);
    }

    /**
     * 演示基本的水印生成与事件时间窗口
     */
    @GetMapping("/basic")
    public ResponseEntity<List<TrafficAnalysisResult>> demonstrateBasicWatermark() {
        try {
            log.info("开始执行基本水印演示");
            List<TrafficAnalysisResult> results = watermarkService.demonstrateBasicWatermark();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("执行基本水印演示时出错", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 演示水印对齐功能
     */
    @GetMapping("/alignment")
    public ResponseEntity<List<TrafficAnalysisResult>> demonstrateWatermarkAlignment() {
        try {
            log.info("开始执行水印对齐演示");
            List<TrafficAnalysisResult> results = watermarkService.demonstrateWatermarkAlignment();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("执行水印对齐演示时出错", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 演示积压处理期间的检查点调整
     */
    @GetMapping("/backlog")
    public ResponseEntity<List<TrafficAnalysisResult>> demonstrateBacklogProcessing() {
        try {
            log.info("开始执行积压处理演示");
            List<TrafficAnalysisResult> results = watermarkService.demonstrateBacklogProcessing();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("执行积压处理演示时出错", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 综合演示所有高级水印特性
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<List<TrafficAnalysisResult>> comprehensiveWatermarkDemo() {
        try {
            log.info("开始执行综合水印演示");
            List<TrafficAnalysisResult> results = watermarkService.comprehensiveWatermarkDemo();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("执行综合水印演示时出错", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 